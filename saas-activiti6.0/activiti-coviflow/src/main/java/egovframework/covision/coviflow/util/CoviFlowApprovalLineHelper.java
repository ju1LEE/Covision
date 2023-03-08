/**
 * @Class Name : CoviFlowApprovalLineHelper.java
 * @Description : 
 * @Modification Information 
 * @ 2016.12.01 최초생성
 *
 * @author 코비젼 연구소
 * @since 2016. 12.01
 * @version 1.0
 * @see Copyright (C) by Covision All right reserved.
 */
package egovframework.covision.coviflow.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class CoviFlowApprovalLineHelper {
	
	@SuppressWarnings("unchecked")
	public static JSONArray getActiveTask(JSONArray ous, String taskid)throws Exception {
		JSONArray ret = new JSONArray();
		
		for(int i = 0; i < ous.size(); i++)
		{
			JSONObject ou = (JSONObject)ous.get(i);
			if(ou.containsKey("taskid")){
				String ti = (String)ou.get("taskid");
				Object personObj = ou.get("person");
				JSONArray persons = new JSONArray();
				if(personObj instanceof JSONObject){
					JSONObject jsonObj = (JSONObject)personObj;
					persons.add(jsonObj);
				} else {
					persons = (JSONArray)personObj;
				}
				
				JSONObject person = (JSONObject)persons.get(0);
				JSONObject taskObject = (JSONObject)person.get("taskinfo");
				//전달 처리
				if(taskObject.containsKey("kind")){
					if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
						person = (JSONObject)persons.get(persons.size()-1);
						taskObject = (JSONObject)person.get("taskinfo");
					}
				}
				
				String status = "";
				if(taskObject.containsKey("status")){
					status = (String)taskObject.get("status");
					if((status.equalsIgnoreCase("pending")||status.equalsIgnoreCase("reserved"))&&!ti.equalsIgnoreCase(taskid)){
						ret.add(ou);
					}
				}
				
			}
		}
		
		return ret;
	}
	
	/*
	 * 알림의 대상을 추출함
	 * */
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<HashMap<String,String>> getCancelledMessageReceivers(JSONObject appvLine, int divisionIdx, int stepIdx, String actionMode) throws Exception {
		/*
		 * 기안취소, 회수의 경우는 기안자를 제외(첫번째)
		 * 승인 취소는 승인 취소자를 제외(현재 step의 전단계) + 현결재자(2021/04/15)
		 * */
		ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		if(divisions != null){
			JSONObject d = (JSONObject)divisions.get(divisionIdx);
			
			Object stepO = d.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}	
			
			if(steps != null){
				LoopSteps : for(int j = 0; j < steps.size(); j++)
				{
					
					JSONObject s = (JSONObject)steps.get(j);
					
					String unitType = "";
					if(s.containsKey("unittype")){
						unitType = (String)s.get("unittype");	
					}
					
					//jsonarray와 jsonobject 분기 처리
					Object ouObj = s.get("ou");
					JSONArray ouArray = new JSONArray();
					if(ouObj instanceof JSONArray){
						ouArray = (JSONArray)ouObj;
					} else {
						ouArray.add((JSONObject)ouObj);
					}
					
					if(ouArray != null){
						LoopOus : for(int k = 0; k < ouArray.size(); k++)
						{
							//알림대상
							HashMap<String,String> receiver = new HashMap<String,String>();
							
							JSONObject ouObject = (JSONObject)ouArray.get(k);
							
							if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
								//알림대상 add
								if(d.containsKey("processID")){
									receiver.put("piid", d.get("processID").toString());
								}
								
								if(ouObject.containsKey("wiid")){
									receiver.put("wiid", ouObject.get("wiid").toString());
								}
								
								Object personObj = ouObject.get("person");
								JSONArray persons = new JSONArray();
								if(personObj instanceof JSONObject){
									JSONObject jsonObj = (JSONObject)personObj;
									persons.add(jsonObj);
								} else {
									persons = (JSONArray)personObj;
								}
								
								if(persons != null){
									LoopPersons : for(int l = 0; l < persons.size(); l++)
									{
										JSONObject person = (JSONObject)persons.get(l);
										JSONObject taskinfo = new JSONObject();
										if(person.containsKey("taskinfo")){
											taskinfo = (JSONObject)person.get("taskinfo");
											String status = "";
											if(taskinfo.containsKey("status")){
												status = (String)taskinfo.get("status");
												
												if(!status.equalsIgnoreCase("inactive")){
													
													if(actionMode.equalsIgnoreCase("APPROVECANCEL")
															&& j < (stepIdx-1)){//승인취소 (취소 담당자 빼기)
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);	
														
													} else if((actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW"))
															&& j != 0){//회수, 기안취소
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);	
														
													}
													
												} else if(status.equalsIgnoreCase("inactive")){
													if(actionMode.equalsIgnoreCase("APPROVECANCEL")
															&& j == stepIdx){//승인취소 - 현결재자
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);	
														
													}
												}
											}
										}
									}
								}
								
							} else if (unitType.equalsIgnoreCase("ou")){
								//unit일 경우에도 알림의 대상 추출이 필요한가?
								
								JSONObject ouTaskInfo = (JSONObject)ouObject.get("taskinfo");
								
								Object personObj = ouObject.get("person");
								JSONArray persons = new JSONArray();
								if(personObj instanceof JSONObject){
									JSONObject jsonObj = (JSONObject)personObj;
									persons.add(jsonObj);
								} else {
									persons = (JSONArray)personObj;
								}
								
								if(persons != null){
									LoopPersons : for(int l = 0; l < persons.size(); l++)
									{
										receiver = new HashMap<String,String>();

										//알림대상 add
										if(ouTaskInfo.containsKey("piid")){
											receiver.put("piid", ouTaskInfo.get("piid").toString());
										}
										
										JSONObject person = (JSONObject)persons.get(l);
										JSONObject taskinfo = new JSONObject();
										if(person.containsKey("taskinfo")){
											taskinfo = (JSONObject)person.get("taskinfo");
											String status = "";
											if(taskinfo.containsKey("status")){
												status = (String)taskinfo.get("status");
												
												if(!status.equalsIgnoreCase("inactive")){
													
													if(actionMode.equalsIgnoreCase("APPROVECANCEL")
															&& j < (stepIdx-1)){//승인취소 (취소 담당자 빼기)
														receiver.put("wiid", (String)person.get("wiid"));
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);	
														
													} else if((actionMode.equalsIgnoreCase("ABORT")||actionMode.equalsIgnoreCase("WITHDRAW"))
															&& j != 0){//회수, 기안취소
														receiver.put("wiid", (String)person.get("wiid"));
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);
													}
												} else if(status.equalsIgnoreCase("inactive")){
													if(actionMode.equalsIgnoreCase("APPROVECANCEL")
															&& j == stepIdx){//승인취소 - 현결재자
														receiver.put("wiid", (String)person.get("wiid"));
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);	
														
													}
												}
											}
										}
									}
								}
							} else if (unitType.equalsIgnoreCase("role")){
								//role일 경우에도 알림의 대상 추출이 필요한가?
							}
						}
					}
				}
			}
			
		}
		
		return receivers;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<HashMap<String,String>> getCompletedMessageReceivers(JSONObject appvLine, Boolean isDistribution, String processID) throws Exception {
		ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		if(divisions != null){
			LoopDivisions : for(int i = 0; i < divisions.size(); i++)
			{
				JSONObject d = (JSONObject)divisions.get(i);
				
				if(isDistribution){
					if(d.containsKey("processID") && d.get("processID").toString().equalsIgnoreCase(processID)){
						Object stepO = d.get("step");
						JSONArray steps = new JSONArray();
						if(stepO instanceof JSONObject){
							JSONObject stepJsonObj = (JSONObject)stepO;
							steps.add(stepJsonObj);
						} else {
							steps = (JSONArray)stepO;
						}	
						
						if(steps != null){
							LoopSteps : for(int j = 0; j < steps.size(); j++)
							{
								JSONObject s = (JSONObject)steps.get(j);
								
								String unitType = "";
								if(s.containsKey("unittype")){
									unitType = (String)s.get("unittype");	
								}
								
								//jsonarray와 jsonobject 분기 처리
								Object ouObj = s.get("ou");
								JSONArray ouArray = new JSONArray();
								if(ouObj instanceof JSONArray){
									ouArray = (JSONArray)ouObj;
								} else {
									ouArray.add((JSONObject)ouObj);
								}
								
								if(ouArray != null){
									LoopOus : for(int k = 0; k < ouArray.size(); k++)
									{
										//알림대상
										HashMap<String,String> receiver = new HashMap<String,String>();
										
										JSONObject ouObject = (JSONObject)ouArray.get(k);
										
										//알림대상 add
										if(d.containsKey("processID")){
											receiver.put("piid", d.get("processID").toString());
										}
										
										if(ouObject.containsKey("wiid")){
											receiver.put("wiid", ouObject.get("wiid").toString());
										}
										
										if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
											Object personObj = ouObject.get("person");
											JSONArray persons = new JSONArray();
											if(personObj instanceof JSONObject){
												JSONObject jsonObj = (JSONObject)personObj;
												persons.add(jsonObj);
											} else {
												persons = (JSONArray)personObj;
											}
											
											if(persons != null){
												LoopPersons : for(int l = 0; l < persons.size(); l++)
												{
													JSONObject person = (JSONObject)persons.get(l);
													JSONObject taskinfo = new JSONObject();
													if(person.containsKey("taskinfo")){
														taskinfo = (JSONObject)person.get("taskinfo");
														String status = "";
														if(taskinfo.containsKey("status")){
															status = (String)taskinfo.get("status");
															
															//if(!status.equalsIgnoreCase("inactive")&&!status.equalsIgnoreCase("pending")){
															if(status.equalsIgnoreCase("completed")){
																receiver.put("userId", (String)person.get("code"));
																receiver.put("type", "UR");
																receivers.add(receiver);
															}
														}
													}
												}
											}
											
										} else if (unitType.equalsIgnoreCase("ou")){
											//unit일 경우에도 알림의 대상 추출이 필요한가?
											
										} else if (unitType.equalsIgnoreCase("role")){
											//role일 경우에도 알림의 대상 추출이 필요한가?
										}
									}
								}
							}
						}
						break;
					}else{
						continue;
					}
				}else{
					Object stepO = d.get("step");
					JSONArray steps = new JSONArray();
					if(stepO instanceof JSONObject){
						JSONObject stepJsonObj = (JSONObject)stepO;
						steps.add(stepJsonObj);
					} else {
						steps = (JSONArray)stepO;
					}	
					
					if(steps != null){
						LoopSteps : for(int j = 0; j < steps.size(); j++)
						{
							JSONObject s = (JSONObject)steps.get(j);
							
							String unitType = "";
							if(s.containsKey("unittype")){
								unitType = (String)s.get("unittype");	
							}
							
							//jsonarray와 jsonobject 분기 처리
							Object ouObj = s.get("ou");
							JSONArray ouArray = new JSONArray();
							if(ouObj instanceof JSONArray){
								ouArray = (JSONArray)ouObj;
							} else {
								ouArray.add((JSONObject)ouObj);
							}
							
							if(ouArray != null){
								LoopOus : for(int k = 0; k < ouArray.size(); k++)
								{
									//알림대상
									HashMap<String,String> receiver = new HashMap<String,String>();
									
									JSONObject ouObject = (JSONObject)ouArray.get(k);
									
									if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
										//알림대상 add
										if(d.containsKey("processID")){
											receiver.put("piid", d.get("processID").toString());
										}
										
										if(ouObject.containsKey("wiid")){
											receiver.put("wiid", ouObject.get("wiid").toString());
										}
										
										Object personObj = ouObject.get("person");
										JSONArray persons = new JSONArray();
										if(personObj instanceof JSONObject){
											JSONObject jsonObj = (JSONObject)personObj;
											persons.add(jsonObj);
										} else {
											persons = (JSONArray)personObj;
										}
										
										if(persons != null){
											LoopPersons : for(int l = 0; l < persons.size(); l++)
											{
												JSONObject person = (JSONObject)persons.get(l);
												JSONObject taskinfo = new JSONObject();
												if(person.containsKey("taskinfo")){
													taskinfo = (JSONObject)person.get("taskinfo");
													String status = "";
													if(taskinfo.containsKey("status")){
														status = (String)taskinfo.get("status");
														
														//if(!status.equalsIgnoreCase("inactive")&&!status.equalsIgnoreCase("pending")){
														if(status.equalsIgnoreCase("completed")){
															receiver.put("userId", (String)person.get("code"));
															receiver.put("type", "UR");
															receivers.add(receiver);
														}
													}
												}
											}
										}
										
									} else if (unitType.equalsIgnoreCase("ou")){
										//unit일 경우에도 알림의 대상 추출이 필요한가?
										
										JSONObject ouTaskInfo = (JSONObject)ouObject.get("taskinfo");
										
										Object personObj = ouObject.get("person");
										JSONArray persons = new JSONArray();
										if(personObj instanceof JSONObject){
											JSONObject jsonObj = (JSONObject)personObj;
											persons.add(jsonObj);
										} else {
											persons = (JSONArray)personObj;
										}
										
										if(persons != null){
											LoopPersons : for(int l = 0; l < persons.size(); l++)
											{
												receiver = new HashMap<String,String>();

												//알림대상 add
												if(ouTaskInfo.containsKey("piid")){
													receiver.put("piid", ouTaskInfo.get("piid").toString());
												}
												
												JSONObject person = (JSONObject)persons.get(l);
												JSONObject taskinfo = new JSONObject();
												if(person.containsKey("taskinfo")){
													taskinfo = (JSONObject)person.get("taskinfo");
													String status = "";
													if(taskinfo.containsKey("status")){
														status = (String)taskinfo.get("status");
														
														if(!status.equalsIgnoreCase("inactive")&&!status.equalsIgnoreCase("rejected")){
															receiver.put("wiid", (String)person.get("wiid"));
															receiver.put("userId", (String)person.get("code"));
															receiver.put("type", "UR");
															receivers.add(receiver);
														}
													}
												}
											}
										}
										
									} else if (unitType.equalsIgnoreCase("role")){
										//role일 경우에도 알림의 대상 추출이 필요한가?
									}
								}
							}
						}
					}
				}
			}
		}
		return receivers;
	}
	
	// 의견알림추가 - 의견추가시 본인에게도 알림발송이되어, unitType = ou 일때도 compelete만 추출하는 함수 추가
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<HashMap<String,String>> getCompletedMessageReceivers_AddOu(JSONObject appvLine, Boolean isDistribution, String processID) throws Exception {
		ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		if(divisions != null){
			LoopDivisions : for(int i = 0; i < divisions.size(); i++)
			{
				JSONObject d = (JSONObject)divisions.get(i);
				
				if(isDistribution){
					if(d.containsKey("processID") && d.get("processID").toString().equalsIgnoreCase(processID)){
						Object stepO = d.get("step");
						JSONArray steps = new JSONArray();
						if(stepO instanceof JSONObject){
							JSONObject stepJsonObj = (JSONObject)stepO;
							steps.add(stepJsonObj);
						} else {
							steps = (JSONArray)stepO;
						}	
						
						if(steps != null){
							LoopSteps : for(int j = 0; j < steps.size(); j++)
							{
								JSONObject s = (JSONObject)steps.get(j);
								
								String unitType = "";
								if(s.containsKey("unittype")){
									unitType = (String)s.get("unittype");	
								}
								
								//jsonarray와 jsonobject 분기 처리
								Object ouObj = s.get("ou");
								JSONArray ouArray = new JSONArray();
								if(ouObj instanceof JSONArray){
									ouArray = (JSONArray)ouObj;
								} else {
									ouArray.add((JSONObject)ouObj);
								}
								
								if(ouArray != null){
									LoopOus : for(int k = 0; k < ouArray.size(); k++)
									{
										//알림대상
										HashMap<String,String> receiver = new HashMap<String,String>();
										
										JSONObject ouObject = (JSONObject)ouArray.get(k);
										
										//알림대상 add
										if(d.containsKey("processID")){
											receiver.put("piid", d.get("processID").toString());
										}
										
										if(ouObject.containsKey("wiid")){
											receiver.put("wiid", ouObject.get("wiid").toString());
										}
										
										if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
											Object personObj = ouObject.get("person");
											JSONArray persons = new JSONArray();
											if(personObj instanceof JSONObject){
												JSONObject jsonObj = (JSONObject)personObj;
												persons.add(jsonObj);
											} else {
												persons = (JSONArray)personObj;
											}
											
											if(persons != null){
												LoopPersons : for(int l = 0; l < persons.size(); l++)
												{
													JSONObject person = (JSONObject)persons.get(l);
													JSONObject taskinfo = new JSONObject();
													if(person.containsKey("taskinfo")){
														taskinfo = (JSONObject)person.get("taskinfo");
														String status = "";
														if(taskinfo.containsKey("status")){
															status = (String)taskinfo.get("status");
															
															//if(!status.equalsIgnoreCase("inactive")&&!status.equalsIgnoreCase("pending")){
															if(status.equalsIgnoreCase("completed")){
																receiver.put("userId", (String)person.get("code"));
																receiver.put("type", "UR");
																receivers.add(receiver);
															}
														}
													}
												}
											}
											
										} else if (unitType.equalsIgnoreCase("ou")){
											//unit일 경우에도 알림의 대상 추출이 필요한가?
											
										} else if (unitType.equalsIgnoreCase("role")){
											//role일 경우에도 알림의 대상 추출이 필요한가?
										}
									}
								}
							}
						}
						break;
					}else{
						continue;
					}
				}else{
					Object stepO = d.get("step");
					JSONArray steps = new JSONArray();
					if(stepO instanceof JSONObject){
						JSONObject stepJsonObj = (JSONObject)stepO;
						steps.add(stepJsonObj);
					} else {
						steps = (JSONArray)stepO;
					}	
					
					if(steps != null){
						LoopSteps : for(int j = 0; j < steps.size(); j++)
						{
							JSONObject s = (JSONObject)steps.get(j);
							
							String unitType = "";
							if(s.containsKey("unittype")){
								unitType = (String)s.get("unittype");	
							}
							
							//jsonarray와 jsonobject 분기 처리
							Object ouObj = s.get("ou");
							JSONArray ouArray = new JSONArray();
							if(ouObj instanceof JSONArray){
								ouArray = (JSONArray)ouObj;
							} else {
								ouArray.add((JSONObject)ouObj);
							}
							
							if(ouArray != null){
								LoopOus : for(int k = 0; k < ouArray.size(); k++)
								{
									//알림대상
									HashMap<String,String> receiver = new HashMap<String,String>();
									
									JSONObject ouObject = (JSONObject)ouArray.get(k);
									
									if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
										//알림대상 add
										if(d.containsKey("processID")){
											receiver.put("piid", d.get("processID").toString());
										}
										
										if(ouObject.containsKey("wiid")){
											receiver.put("wiid", ouObject.get("wiid").toString());
										}
										
										Object personObj = ouObject.get("person");
										JSONArray persons = new JSONArray();
										if(personObj instanceof JSONObject){
											JSONObject jsonObj = (JSONObject)personObj;
											persons.add(jsonObj);
										} else {
											persons = (JSONArray)personObj;
										}
										
										if(persons != null){
											LoopPersons : for(int l = 0; l < persons.size(); l++)
											{
												JSONObject person = (JSONObject)persons.get(l);
												JSONObject taskinfo = new JSONObject();
												if(person.containsKey("taskinfo")){
													taskinfo = (JSONObject)person.get("taskinfo");
													String status = "";
													if(taskinfo.containsKey("status")){
														status = (String)taskinfo.get("status");
														
														//if(!status.equalsIgnoreCase("inactive")&&!status.equalsIgnoreCase("pending")){
														if(status.equalsIgnoreCase("completed")){
															receiver.put("userId", (String)person.get("code"));
															receiver.put("type", "UR");
															receivers.add(receiver);
														}
													}
												}
											}
										}
										
									} else if (unitType.equalsIgnoreCase("ou")){
										//unit일 경우에도 알림의 대상 추출이 필요한가?
										
										JSONObject ouTaskInfo = (JSONObject)ouObject.get("taskinfo");
										
										Object personObj = ouObject.get("person");
										JSONArray persons = new JSONArray();
										if(personObj instanceof JSONObject){
											JSONObject jsonObj = (JSONObject)personObj;
											persons.add(jsonObj);
										} else {
											persons = (JSONArray)personObj;
										}
										
										if(persons != null){
											LoopPersons : for(int l = 0; l < persons.size(); l++)
											{
												receiver = new HashMap<String,String>();

												//알림대상 add
												if(ouTaskInfo.containsKey("piid")){
													receiver.put("piid", ouTaskInfo.get("piid").toString());
												}
												
												JSONObject person = (JSONObject)persons.get(l);
												JSONObject taskinfo = new JSONObject();
												if(person.containsKey("taskinfo")){
													taskinfo = (JSONObject)person.get("taskinfo");
													String status = "";
													if(taskinfo.containsKey("status")){
														status = (String)taskinfo.get("status");
														
														//if(!status.equalsIgnoreCase("inactive")&&!status.equalsIgnoreCase("rejected")){
														if(status.equalsIgnoreCase("completed")){
															receiver.put("wiid", (String)person.get("wiid"));
															receiver.put("userId", (String)person.get("code"));
															receiver.put("type", "UR");
															receivers.add(receiver);
														}
													}
												}
											}
										}
		
									} else if (unitType.equalsIgnoreCase("role")){
										//role일 경우에도 알림의 대상 추출이 필요한가?
									}
								}
							}
						}
					}
				}
			}
		}
		return receivers;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<HashMap<String,String>> getCompletedPendingMessageReceivers(JSONObject appvLine, String actionUserCode) throws Exception {
		ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		if(divisions != null){
			LoopDivisions : for(int i = 0; i < divisions.size(); i++)
			{
				JSONObject d = (JSONObject)divisions.get(i);
				
				Object stepO = d.get("step");
				JSONArray steps = new JSONArray();
				if(stepO instanceof JSONObject){
					JSONObject stepJsonObj = (JSONObject)stepO;
					steps.add(stepJsonObj);
				} else {
					steps = (JSONArray)stepO;
				}	
				
				if(steps != null){
					LoopSteps : for(int j = 0; j < steps.size(); j++)
					{
						JSONObject s = (JSONObject)steps.get(j);
						
						String unitType = "";
						if(s.containsKey("unittype")){
							unitType = (String)s.get("unittype");	
						}
						
						//jsonarray와 jsonobject 분기 처리
						Object ouObj = s.get("ou");
						JSONArray ouArray = new JSONArray();
						if(ouObj instanceof JSONArray){
							ouArray = (JSONArray)ouObj;
						} else {
							ouArray.add((JSONObject)ouObj);
						}
						
						if(ouArray != null){
							LoopOus : for(int k = 0; k < ouArray.size(); k++)
							{
								//알림대상
								HashMap<String,String> receiver = new HashMap<String,String>();
								
								JSONObject ouObject = (JSONObject)ouArray.get(k);
								
								//알림대상 add
								if(d.containsKey("processID")){
									receiver.put("piid", d.get("processID").toString());
								}
								
								if(ouObject.containsKey("wiid")){
									receiver.put("wiid", ouObject.get("wiid").toString());
								}
								
								if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
									Object personObj = ouObject.get("person");
									JSONArray persons = new JSONArray();
									if(personObj instanceof JSONObject){
										JSONObject jsonObj = (JSONObject)personObj;
										persons.add(jsonObj);
									} else {
										persons = (JSONArray)personObj;
									}
									
									if(persons != null){
										LoopPersons : for(int l = 0; l < persons.size(); l++)
										{
											JSONObject person = (JSONObject)persons.get(l);
											JSONObject taskinfo = new JSONObject();
											if(person.containsKey("taskinfo")){
												taskinfo = (JSONObject)person.get("taskinfo");
												String status = "";
												if(taskinfo.containsKey("status")){
													status = (String)taskinfo.get("status");
													
													if(!status.equalsIgnoreCase("inactive") && !person.get("code").toString().equalsIgnoreCase(actionUserCode)){
														receiver.put("userId", (String)person.get("code"));
														receiver.put("type", "UR");
														receivers.add(receiver);
													}
												}
											}
										}
									}
									
								} else if (unitType.equalsIgnoreCase("ou")){
									//unit일 경우에도 알림의 대상 추출이 필요한가?
									
								} else if (unitType.equalsIgnoreCase("role")){
									//role일 경우에도 알림의 대상 추출이 필요한가?
								}
							}
						}
					}
				}
			}
		}
		
		return receivers;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getDivisions(JSONObject appvLine) throws Exception{
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		return divisions;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<HashMap<String,String>> getMessageReceivers(JSONObject appvLine) throws Exception {
		ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		if(divisions != null){
			LoopDivisions : for(int i = 0; i < divisions.size(); i++)
			{
				JSONObject d = (JSONObject)divisions.get(i);
				
				Object stepO = d.get("step");
				JSONArray steps = new JSONArray();
				if(stepO instanceof JSONObject){
					JSONObject stepJsonObj = (JSONObject)stepO;
					steps.add(stepJsonObj);
				} else {
					steps = (JSONArray)stepO;
				}	
				
				if(steps != null){
					LoopSteps : for(int j = 0; j < steps.size(); j++)
					{
						
						JSONObject s = (JSONObject)steps.get(j);
						
						String unitType = "";
						if(s.containsKey("unittype")){
							unitType = (String)s.get("unittype");	
						}
						
						//jsonarray와 jsonobject 분기 처리
						Object ouObj = s.get("ou");
						JSONArray ouArray = new JSONArray();
						if(ouObj instanceof JSONArray){
							ouArray = (JSONArray)ouObj;
						} else {
							ouArray.add((JSONObject)ouObj);
						}
						
						if(ouArray != null){
							LoopOus : for(int k = 0; k < ouArray.size(); k++)
							{
								//알림대상
								HashMap<String,String> receiver = new HashMap<String,String>();
								
								JSONObject ouObject = (JSONObject)ouArray.get(k);
								
								if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){

									//알림대상 add
									if(d.containsKey("processID")){
										receiver.put("piid", d.get("processID").toString());
									}
									if(ouObject.containsKey("wiid")){
										receiver.put("wiid", ouObject.get("wiid").toString());
									}
									
									Object personObj = ouObject.get("person");
									JSONArray persons = new JSONArray();
									if(personObj instanceof JSONObject){
										JSONObject jsonObj = (JSONObject)personObj;
										persons.add(jsonObj);
									} else {
										persons = (JSONArray)personObj;
									}
									
									if(persons != null){
										LoopPersons : for(int l = 0; l < persons.size(); l++)
										{
											JSONObject person = (JSONObject)persons.get(l);
											JSONObject personTaskInfo = (JSONObject)person.get("taskinfo");
											
											if(personTaskInfo.containsKey("kind") && ((String)personTaskInfo.get("kind")).equalsIgnoreCase("authorize")  && personTaskInfo.containsKey("result") && ((String)personTaskInfo.get("result")).equalsIgnoreCase("authorized")){
												break LoopSteps;
											} else {
											receiver.put("userId", (String)person.get("code"));
											receiver.put("type", "UR");
											receivers.add(receiver);
										}
									}
									}
									
								} else if (unitType.equalsIgnoreCase("ou")){
									//unit일 경우에도 알림의 대상 추출이 필요한가?
									
									JSONObject ouTaskInfo = (JSONObject)ouObject.get("taskinfo");
									
									Object personObj = ouObject.get("person");
									JSONArray persons = new JSONArray();
									if(personObj instanceof JSONObject){
										JSONObject jsonObj = (JSONObject)personObj;
										persons.add(jsonObj);
									} else {
										persons = (JSONArray)personObj;
									}
									
									if(persons != null){
										LoopPersons : for(int l = 0; l < persons.size(); l++)
										{
											receiver = new HashMap<String,String>();
											JSONObject person = (JSONObject)persons.get(l);
											//알림대상 add
											if(ouTaskInfo.containsKey("piid")){
												receiver.put("piid", ouTaskInfo.get("piid").toString());
											}
											
											receiver.put("wiid", (String)person.get("wiid"));
											receiver.put("userId", (String)person.get("code"));
											receiver.put("type", "UR");
											receivers.add(receiver);
										}
									}
								} else if (unitType.equalsIgnoreCase("role")){
									//role일 경우에도 알림의 대상 추출이 필요한가?
								}
							}
						}
					}
				}
			}
		}
		
		receivers.remove(receivers.size()-1);
		
		return receivers;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<HashMap<String,String>> getDivisionMessageReceivers(JSONObject division) throws Exception {
		ArrayList<HashMap<String,String>> receivers = new ArrayList<HashMap<String,String>>();
				
		Object stepO = division.get("step");
		JSONArray steps = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			steps.add(stepJsonObj);
		} else {
			steps = (JSONArray)stepO;
		}
		
		if(steps != null){
			LoopSteps : for(int j = 0; j < steps.size() - 1; j++)
			{
				JSONObject s = (JSONObject)steps.get(j);
				
				String unitType = "";
				if(s.containsKey("unittype")){
					unitType = (String)s.get("unittype");	
				}
				
				//jsonarray와 jsonobject 분기 처리
				Object ouObj = s.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONArray){
					ouArray = (JSONArray)ouObj;
				} else {
					ouArray.add((JSONObject)ouObj);
				}
				
				if(ouArray != null){
					LoopOus : for(int k = 0; k < ouArray.size(); k++)
					{
						//알림대상
						HashMap<String,String> receiver = new HashMap<String,String>();
						
						JSONObject ouObject = (JSONObject)ouArray.get(k);
						
						if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
							//알림대상 add
							if(division.containsKey("processID")){
								receiver.put("piid", division.get("processID").toString());
							}
							
							if(ouObject.containsKey("wiid")){
								receiver.put("wiid", ouObject.get("wiid").toString());
							}
							
							Object personObj = ouObject.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
							
							if(persons != null){
								LoopPersons : for(int l = 0; l < persons.size(); l++)
								{
									JSONObject person = (JSONObject)persons.get(l);
									JSONObject personTaskInfo = (JSONObject)person.get("taskinfo");

									if(personTaskInfo.containsKey("kind") && ((String)personTaskInfo.get("kind")).equalsIgnoreCase("authorize")  && personTaskInfo.containsKey("result") && ((String)personTaskInfo.get("result")).equalsIgnoreCase("authorized")){
										break LoopSteps;
									} else {
									receiver.put("userId", (String)person.get("code"));
									receiver.put("type", "UR");
									receivers.add(receiver);
								}
							}
							}
							
						} else if (unitType.equalsIgnoreCase("ou")){
							//unit일 경우에도 알림의 대상 추출이 필요한가?
							
							JSONObject ouTaskInfo = (JSONObject)ouObject.get("taskinfo");
							
							Object personObj = ouObject.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
							
							if(persons != null){
								LoopPersons : for(int l = 0; l < persons.size(); l++)
								{
									receiver = new HashMap<String,String>();
									
									//알림대상 add
									if(ouTaskInfo.containsKey("piid")){
										receiver.put("piid", ouTaskInfo.get("piid").toString());
									}
									
									JSONObject person = (JSONObject)persons.get(l);
									receiver.put("wiid", (String)person.get("wiid"));
									receiver.put("userId", (String)person.get("code"));
									receiver.put("type", "UR");
									receivers.add(receiver);
								}
							}
							
						} else if (unitType.equalsIgnoreCase("role")){
							//role일 경우에도 알림의 대상 추출이 필요한가?
						}
					}
				}
			}
		}
		
		return receivers;
	}
	
	//전결 여부 판단
	@SuppressWarnings("unchecked")
	public static Boolean isAuthorized(JSONObject step) throws Exception{
		Boolean bRet = false;
		String routeType = (String)step.get("routetype");
		String unitType = (String)step.get("unittype");
		String allotType = "";
		
		if(step.containsKey("allottype"))
			allotType = (String)step.get("allottype"); // 동시결재인 경우 false로 return
		
		if(routeType.equalsIgnoreCase("approve")&&unitType.equalsIgnoreCase("person")&&!allotType.equalsIgnoreCase("parallel")){
			JSONObject ou = (JSONObject)step.get("ou");
			
			Object personObj = ou.get("person");
			JSONArray persons = new JSONArray();
			if(personObj instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)personObj;
				persons.add(jsonObj);
			} else {
				persons = (JSONArray)personObj;
			}
			
			JSONObject person = (JSONObject)persons.get(0);
			JSONObject personTask = (JSONObject)person.get("taskinfo");
			
			if(personTask.containsKey("kind")){
				String kind = (String)personTask.get("kind");
				if(kind.equalsIgnoreCase("authorize")){
					bRet = true;
				}	
			}
		}
		return bRet;
	}
	
	//List에 중복으로 add되는 부분을 remove 처리
	//List -> JSONArray로 바꿀 것
	public static List<Integer> removeDupeFromExecIds(List<Integer> execIds) throws Exception{
		Set<Integer> hs = new HashSet<>();
		hs.addAll(execIds);
		execIds.clear();
		execIds.addAll(hs);
		
		return execIds;
	}
	
	public static List<Integer> removeDupeFromExecIds(List<Integer> execIds, int id) throws Exception{
		Set<Integer> hs = new HashSet<>();
		hs.addAll(execIds);
		execIds.clear();
		execIds.addAll(hs);
		
		Integer tempId = new Integer(id);
		List<Integer> ret = new ArrayList<>();
		for(int i = 0;i < execIds.size();i++){
			if(!tempId.equals(execIds.get(i)))
			{
				ret.add(execIds.get(i));
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Integer> getExecIdsFromOuTask(JSONObject step) throws Exception{
		Object ouObj = step.get("ou");
		JSONArray ous = new JSONArray();
		if(ouObj instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)ouObj;
			ous.add(jsonObj);
		} else {
			ous = (JSONArray)ouObj;
		}
		
		List<Integer> processIdList = new ArrayList<Integer>();
		
		for(int i = 0; i < ous.size(); i++)
		{
			JSONObject ou = (JSONObject)ous.get(i);
			if(ou.containsKey("taskinfo")){
				JSONObject taskinfo = (JSONObject)ou.get("taskinfo");
				if(taskinfo.containsKey("execid")){
					processIdList.add(Integer.parseInt(taskinfo.get("execid").toString()));
				}	
			}
		}
		
		return processIdList;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setPersonTask(JSONObject ou, int personIndex, HashMap<String, String> attrs) throws Exception {
		
		if(ou.containsKey("person")){
			Object personObj = ou.get("person");
			JSONArray persons = new JSONArray();
			if(personObj instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)personObj;
				persons.add(jsonObj);
			} else {
				persons = (JSONArray)personObj;
			}
			
			JSONObject person = (JSONObject)persons.get(personIndex);
			if(person.containsKey("taskinfo")){
				JSONObject taskObject = new JSONObject();
				taskObject = (JSONObject)person.get("taskinfo");
				
				for (Map.Entry<String, String> entry : attrs.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    taskObject.put(key, value);
				}	
			}
		}
		
		return ou;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setPersonTask(JSONObject ou, int personIndex, String attr, Object value) throws Exception {
		
		if(ou.containsKey("person")){
			Object personObj = ou.get("person");
			JSONArray persons = new JSONArray();
			if(personObj instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)personObj;
				persons.add(jsonObj);
			} else {
				persons = (JSONArray)personObj;
			}
			
			JSONObject person = (JSONObject)persons.get(personIndex);
			if(person.containsKey("taskinfo")){
				JSONObject taskObject = new JSONObject();
				taskObject = (JSONObject)person.get("taskinfo");
				taskObject.put(attr, value);
			}
		}
		
		return ou;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setPersonTask(JSONObject appvLine, int divisionIndex, int stepIndex, int personIndex, String attr, Object value) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		
		JSONObject stepObj = (JSONObject)stepArray.get(stepIndex);
		Object personO = ((JSONObject)stepObj.get("ou")).get("person");
		JSONArray personArray = new JSONArray();
		if(personO instanceof JSONObject){
			JSONObject personJsonObj = (JSONObject)personO;
			personArray.add(personJsonObj);
		} else {
			personArray = (JSONArray)personO;
		}
		
		JSONObject person = (JSONObject)personArray.get(personIndex);
		if(person.containsKey("taskinfo")){
			JSONObject taskObject = new JSONObject();
			taskObject = (JSONObject)person.get("taskinfo");
			taskObject.put(attr, value);
		}
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPendingPerson(JSONObject ou) throws Exception {
		JSONObject ret = new JSONObject();
		
		JSONObject person = new JSONObject();
		String personSize = "";
		String isPerson = "false";
		String personIndex = "";
		String approvalStep = "";
		String deputyCode = "";
		String deputyName = "";
		
		if(ou.containsKey("person")){
			Object personObj = ou.get("person");
			JSONArray persons = new JSONArray();
			if(personObj instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)personObj;
				persons.add(jsonObj);
			} else {
				persons = (JSONArray)personObj;
			}
			
			for(int i = 0; i < persons.size(); i++)
			{
				person = (JSONObject)persons.get(i);
				
				if(person.containsKey("taskinfo")){
					JSONObject taskObject = new JSONObject();
					taskObject = (JSONObject)person.get("taskinfo");
					
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("substitute") && taskObject.get("status").toString().equalsIgnoreCase("pending")){
							deputyCode = (String)person.get("code");
							deputyName = (String)person.get("name");		
						}	
					}
					
					if(taskObject.get("status").toString().equalsIgnoreCase("pending")||taskObject.get("status").toString().equalsIgnoreCase("reserved")){
						personSize = Integer.toString(persons.size());
						isPerson = "true";
						personIndex = Integer.toString(i);
						approvalStep = personSize + "_" + Integer.toString(i + 1);
						break;
					}
				}
			}
			
		}
		
		ret.put("personSize", personSize);
		ret.put("isPerson", isPerson);
		ret.put("personIndex", personIndex);
		ret.put("approvalStep", approvalStep);
		ret.put("deputyCode", deputyCode);
		ret.put("deputyName", deputyName);
		ret.put("person", person);
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getRejectedPerson(JSONObject step) throws Exception {
		JSONObject ret = new JSONObject();
		
		JSONObject person = new JSONObject();
		String personSize = "";
		String isPerson = "false";
		String personIndex = "";
		String approvalStep = "";
		String approverCode = "";
		String approverName = "";
		String deputyCode = "";
		String deputyName = "";
		
		Object ouObj = step.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONArray){
			ouArray = (JSONArray)ouObj;
		} else {
			ouArray.add((JSONObject)ouObj);
		}
		
		if(ouArray != null){
			LoopOus : for(int k = 0; k < ouArray.size(); k++)
			{
				JSONObject ou = (JSONObject)ouArray.get(k);
				if(ou.containsKey("person")){
					Object personObj = ou.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					for(int i = 0; i < persons.size(); i++)
					{
						person = (JSONObject)persons.get(i);
						
						if(person.containsKey("taskinfo")){
							JSONObject taskObject = new JSONObject();
							taskObject = (JSONObject)person.get("taskinfo");
							
							if(taskObject.containsKey("kind")){
								if(taskObject.get("kind").toString().equalsIgnoreCase("substitute")){
									deputyCode = (String)person.get("code");
									deputyName = (String)person.get("name");		
								}	
							}
							
							if(taskObject.get("status").toString().equalsIgnoreCase("rejected")){
								personSize = Integer.toString(persons.size());
								isPerson = "true";
								personIndex = Integer.toString(i);
								approvalStep = personSize + "_" + Integer.toString(i + 1);
								
								approverCode = (String)person.get("code");
								approverName = (String)person.get("name");
								break;
							}
						}
					}
				}
			}
		}
		
		ret.put("personSize", personSize);
		ret.put("isPerson", isPerson);
		ret.put("personIndex", personIndex);
		ret.put("approvalStep", approvalStep);
		ret.put("approverCode", approverCode);
		ret.put("approverName", approverName);
		ret.put("deputyCode", deputyCode);
		ret.put("deputyName", deputyName);
		ret.put("person", person);
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPendingOu(JSONObject step, String execId) throws Exception {
		JSONObject ret = new JSONObject();
		
		Object ouObj = step.get("ou");
		JSONArray ous = new JSONArray();
		if(ouObj instanceof JSONObject){
			JSONObject ouJsonObj = (JSONObject)ouObj;
			ous.add(ouJsonObj);
		} else {
			ous = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ous.size(); i++)
		{
			JSONObject ou = (JSONObject)ous.get(i);
			JSONObject taskObject = (JSONObject)ou.get("taskinfo");
			
			if(taskObject.containsKey("execid")){
				if(taskObject.get("execid").equals(execId)){
					if(taskObject.get("status").toString().equalsIgnoreCase("pending")||taskObject.get("status").toString().equalsIgnoreCase("reserved")){
						ret = ou;
						break;
					}	
				}
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static String isPendingOu(JSONObject appvLine, int divisionIndex, int stepIndex) throws Exception {
		String ret = "false";
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		Object ouObj = stepObject.get("ou");
		JSONArray ous = new JSONArray();
		if(ouObj instanceof JSONObject){
			JSONObject ouJsonObj = (JSONObject)ouObj;
			ous.add(ouJsonObj);
		} else {
			ous = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ous.size(); i++)
		{
			JSONObject ou = (JSONObject)ous.get(i);
			
			JSONObject taskObject = new JSONObject();
			if(ou.containsKey("person")){
				Object personObj = ou.get("person");
				JSONArray persons = new JSONArray();
				if(personObj instanceof JSONObject){
					JSONObject jsonObj = (JSONObject)personObj;
					persons.add(jsonObj);
				} else {
					persons = (JSONArray)personObj;
				}
				
				JSONObject personObject = (JSONObject)persons.get(0);
				taskObject = (JSONObject)personObject.get("taskinfo");
				//전달일 경우
				if(taskObject.containsKey("kind")){
					if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
						JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
						taskObject = (JSONObject)forwardedPerson.get("taskinfo");
					}
				}
				
			} else if(ou.containsKey("role")) {
				JSONObject role = new JSONObject();
				role = (JSONObject)ou.get("role");
				taskObject = (JSONObject)role.get("taskinfo");
			} else {
				taskObject = (JSONObject)ou.get("taskinfo");
			}
		      
			if(taskObject.get("status").toString().equalsIgnoreCase("pending")||taskObject.get("status").toString().equalsIgnoreCase("reserved")){
				ret = "true";
				break;
			}	
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getOuAssignees(JSONObject step) throws Exception{
		List<String> assignees = new ArrayList<String>();
		
		Object ouObj = step.get("ou");
		JSONArray ous = new JSONArray();
		if(ouObj instanceof JSONObject){
			ous.add((JSONObject)ouObj);
		} else {
			ous = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ous.size(); i++)
		{
			JSONObject ou = (JSONObject)ous.get(i);
			assignees.add((String)ou.get("code"));
		}
		
		return assignees;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getAssignees(JSONObject step) throws Exception{
		List<String> assignees = new ArrayList<String>();
		
		Object ouObj = step.get("ou");
		JSONArray ous = new JSONArray();
		if(ouObj instanceof JSONObject){
			ous.add((JSONObject)ouObj);
		} else {
			ous = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ous.size(); i++)
		{
			JSONObject ou = (JSONObject)ous.get(i);
			
			if(ou.containsKey("person")){
				Object personObj = ou.get("person");
				JSONArray persons = new JSONArray();
				if(personObj instanceof JSONObject){
					JSONObject jsonObj = (JSONObject)personObj;
					persons.add(jsonObj);
				} else {
					persons = (JSONArray)personObj;
				}
				
				JSONObject person = (JSONObject)persons.get(0);
				JSONObject taskObject = (JSONObject)person.get("taskinfo");
				//전달일 경우
				if(taskObject.containsKey("kind")){
					if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
						person = (JSONObject)persons.get(persons.size()-1);
					}
				}
				
				assignees.add((String)person.get("code"));
				
			} else if(ou.containsKey("role")) {
				JSONObject role = new JSONObject();
				role = (JSONObject)ou.get("role");
				assignees.add((String)role.get("code"));
			} else {
				assignees.add((String)ou.get("code"));
			}
		}
		
		return assignees;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getReceiveDivision(JSONObject appvLine) throws Exception {
		JSONObject ret = new JSONObject();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = new JSONObject();
		String divisionSize = "";
		String divisionIndex = "";
		String isDivision = "false";
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			JSONObject taskObject = (JSONObject)d.get("taskinfo");
			String divisionType = (String)d.get("divisiontype");
		      
			//division의 taskinfo가 active인 경우
			if(taskObject.get("status").toString().equalsIgnoreCase("pending") 
					&& taskObject.get("result").toString().equalsIgnoreCase("pending")
					&& !divisionType.equalsIgnoreCase("send")){
				isDivision = "true";
				division = d;
				divisionSize =  Integer.toString(divisions.size());
				divisionIndex = Integer.toString(i);
				break;
			}
		}
		
		ret.put("isDivision", isDivision);
		ret.put("division", division);
		ret.put("divisionSize", divisionSize);
		ret.put("divisionIndex", divisionIndex);
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean HasReceiveDivision(JSONObject appvLine) throws Exception {
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		boolean isDivision = false;
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			String divisionType = (String)d.get("divisiontype");
		      
			//division의 taskinfo가 active인 경우
			if(divisionType.equalsIgnoreCase("receive")){
				isDivision = true;
				break;
			}			      
		}
		
		return isDivision;
	}
	
	// 미처리 수신부서 여부 판단
	@SuppressWarnings("unchecked") 
	public static boolean hasReceiveInactiveStep(JSONObject appvLine) throws Exception {
		boolean ret = false;
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = new JSONObject();
		
		if(divisions.size() > 1) {
			// 수신부서만 체크함, i=1 부터 시작
			for(int i = 1; i < divisions.size(); i++)
			{
				JSONObject d = (JSONObject)divisions.get(i);
				JSONObject taskObject = (JSONObject)d.get("taskinfo");
			      
				//division의 taskinfo가 inactive인 경우
				if(taskObject.get("status").toString().equalsIgnoreCase("inactive")){
					division = d;
					break;
				}			      
			}
			
			Object stepO = division.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
	
			for(int i = 0; i < steps.size(); i++)
			{
				JSONObject s = (JSONObject)steps.get(i);
				
				if(s.containsKey("unittype")){
					//jsonarray와 jsonobject 분기 처리
					JSONObject ouObject = new JSONObject();
					Object ouObj = s.get("ou");
					if(ouObj instanceof JSONArray){
						JSONArray ouArray = (JSONArray)ouObj;
						ouObject = (JSONObject)ouArray.get(0);
					} else {
						ouObject = (JSONObject)ouObj;
					}
						
					JSONObject taskObject = new JSONObject();
					if(ouObject.containsKey("person")){
						Object personObj = ouObject.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
						
						//전달 처리
						if(taskObject.containsKey("kind")){
							if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
								JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
								taskObject = (JSONObject)forwardedPerson.get("taskinfo");
							}
						}
					} else if(ouObject.containsKey("role")) {
						JSONObject role = new JSONObject();
						role = (JSONObject)ouObject.get("role");
						taskObject = (JSONObject)role.get("taskinfo");
					} else {
						taskObject = (JSONObject)ouObject.get("taskinfo");
					}
				      
					if(taskObject.get("status").toString().equalsIgnoreCase("inactive")){
						ret = true;
						break;
					}
				}
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean HasPerson(JSONObject appvLine, int divisionIdx, int stepIdx, int ouIdx) throws Exception {
		boolean isPerson = false;
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)divisionObj;
			divisions.add(jsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		JSONObject division = (JSONObject)divisions.get(divisionIdx);
		Object stepObj = division.get("step");
		JSONArray steps = new JSONArray();
		if(stepObj instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)stepObj;
			steps.add(jsonObj);
		} else {
			steps = (JSONArray)stepObj;
		}
		
		JSONObject step = (JSONObject)steps.get(stepIdx);
		Object ouObj = step.get("ou");
		JSONArray ous = new JSONArray();
		if(ouObj instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)ouObj;
			ous.add(jsonObj);
		} else {
			ous = (JSONArray)ouObj;
		}
		
		JSONObject ou = (JSONObject)ous.get(ouIdx);
		if(ou.containsKey("person")){
			isPerson = true;
		}
		
		return isPerson;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPendingStep(JSONObject appvLine) throws Exception {
		JSONObject ret = new JSONObject();
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = new JSONObject();
		String divisionSize = "";
		String divisionIndex = "";
		String isStep = "false";
		String stepSize = "";
		String stepIndex = "";
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			JSONObject taskObject = (JSONObject)d.get("taskinfo");
		      
			divisionSize =  Integer.toString(divisions.size());
			//division의 taskinfo가 active인 경우
			if(taskObject.get("status").toString().equalsIgnoreCase("pending")){
				division = d;
				divisionIndex = Integer.toString(i);
			}			      
		}
		
		Object stepO = division.get("step");
		JSONArray steps = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			steps.add(stepJsonObj);
		} else {
			steps = (JSONArray)stepO;
		}
		
		JSONObject step = new JSONObject();
		String approvalStep = "";
		String approverCode = "";
		String approverName = "";
		String approverSIPAddress = "";
		String deputyCode = "";
		String deputyName = "";
		String unitType = "";
		String routeType = "";
		String allotType = "";
		
		LoopSteps : for(int i = 0; i < steps.size(); i++)
		{
			unitType = "";
			routeType = "";
			allotType = "";
			
			JSONObject s = (JSONObject)steps.get(i);
			unitType = (String)s.get("unittype");
			routeType = (String)s.get("routetype");
			if(s.containsKey("allottype")){
				allotType = (String)s.get("allottype");
			}
			
			//jsonarray와 jsonobject 분기 처리
			Object ouObj = s.get("ou");
			JSONArray ouArray = new JSONArray();
			if(ouObj instanceof JSONArray){
				ouArray = (JSONArray)ouObj;
			} else {
				ouArray.add((JSONObject)ouObj);
			}
			
			JSONObject stepTaskObj = new JSONObject();
			if(s.containsKey("taskinfo")) stepTaskObj = (JSONObject)s.get("taskinfo");
			
			LoopOus : for(int j = 0; j < ouArray.size(); j++)
			{
				approvalStep = "";
				approverCode = "";
				approverName = "";
				approverSIPAddress = "";
				deputyCode = "";
				deputyName = "";
				
				JSONObject ouObject = (JSONObject)ouArray.get(j);
				JSONObject taskObject = new JSONObject();
				if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
				//if(ouObject.containsKey("person")){	
					Object personObj = ouObject.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
					
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					approverCode = (String)personObject.get("code");
					approverName = (String)personObject.get("name");
					if(personObject.containsKey("sipaddress")){
						approverSIPAddress = (String)personObject.get("sipaddress");
					}
					
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("substitute")){
							deputyCode = (String)personObject.get("code");
							deputyName = (String)personObject.get("name");		
						}
						
						//전달 처리
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)forwardedPerson.get("taskinfo");
							approverCode = (String)forwardedPerson.get("code");
							approverName = (String)forwardedPerson.get("name");
							if(forwardedPerson.containsKey("sipaddress")){
								approverSIPAddress = (String)forwardedPerson.get("sipaddress");
							}
						}
					}
					
				} else if(ouObject.containsKey("role")) {
					JSONObject role = new JSONObject();
					role = (JSONObject)ouObject.get("role");
					taskObject = (JSONObject)role.get("taskinfo");
					approverCode = (String)ouObject.get("code");
					approverName = (String)ouObject.get("name");
				} else {
					taskObject = (JSONObject)ouObject.get("taskinfo");
					approverCode = (String)ouObject.get("code");
					approverName = (String)ouObject.get("name");
				}
			      
				//if(j == ouArray.size()-1){
					if(taskObject.get("status").toString().equalsIgnoreCase("pending")||taskObject.get("status").toString().equalsIgnoreCase("reserved")
							|| (!stepTaskObj.isEmpty() && stepTaskObj.get("status").toString().equalsIgnoreCase("pending"))){
						step = s;
						isStep = "true";
						stepSize = Integer.toString(steps.size());
						stepIndex = Integer.toString(i);
						approvalStep = stepSize + "_" + Integer.toString(i + 1);
						break LoopSteps;
						//break LoopOus;
					}else {
						
						if(allotType.equalsIgnoreCase("parallel")&&
								s.containsKey("taskinfo")){
							
							JSONObject stepTask = (JSONObject)s.get("taskinfo");
							if(stepTask.get("status").toString().equalsIgnoreCase("pending")){
								step = s;
								isStep = "true";
								stepSize = Integer.toString(steps.size());
								stepIndex = Integer.toString(i);
								approvalStep = stepSize + "_" + Integer.toString(i + 1);
								break LoopSteps;	
							}
						}
					}
				//}
			}
		}
		
		ret.put("isStep", isStep);
		ret.put("step", step);
		ret.put("approvalStep", approvalStep);
		ret.put("approverCode", approverCode);
		ret.put("approverName", approverName);
		ret.put("approverSIPAddress", approverSIPAddress);
		ret.put("deputyCode", deputyCode);
		ret.put("deputyName", deputyName);
		ret.put("unitType", unitType);
		ret.put("routeType", routeType);
		ret.put("allotType", allotType);
		ret.put("divisionSize", divisionSize);
		ret.put("divisionIndex", divisionIndex);
		ret.put("stepSize", stepSize);
		ret.put("stepIndex", stepIndex);
		
		return ret;
	}
	


	@SuppressWarnings("unchecked")
	public static JSONArray getReviewerStep(JSONObject apvLineObj) throws Exception {
		JSONArray ret = new JSONArray();
		
		JSONObject root = (JSONObject)apvLineObj.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		for(int z = 0; z < divisions.size(); z++)
		{
			JSONObject division = (JSONObject)divisions.get(z);
			
			Object stepO = division.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
			
			String unitType = "";
			
			for(int i = 0; i < steps.size(); i++)
			{
				unitType = "";
				
				JSONObject s = (JSONObject)steps.get(i);
				
				// index
				s.put("stepIndex", i);
				s.put("divisionIndex", z);
				
				unitType = (String)s.get("unittype");
				
				//jsonarray와 jsonobject 분기 처리
				Object ouObj = s.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ouObj instanceof JSONArray){
					ouArray = (JSONArray)ouObj;
				} else {
					ouArray.add((JSONObject)ouObj);
				}
				
				for(int j = 0; j < ouArray.size(); j++)
				{
					
					JSONObject ouObject = (JSONObject)ouArray.get(j);
					JSONObject taskObject = new JSONObject();
					if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
					//if(ouObject.containsKey("person")){	
						Object personObj = ouObject.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
						
						//전달 처리
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)forwardedPerson.get("taskinfo");
						}
						
					} else if(ouObject.containsKey("role")) {
						JSONObject role = new JSONObject();
						role = (JSONObject)ouObject.get("role");
						taskObject = (JSONObject)role.get("taskinfo");
					} else {
						taskObject = (JSONObject)ouObject.get("taskinfo");
					}
					
					if(taskObject.get("status").toString().equalsIgnoreCase("inactive") && taskObject.get("kind").toString().equalsIgnoreCase("review")){
						ret.add(s);
					}
					
				}
			    		      
			}
		}
		
		return ret;
	}

	/*
	 * 결재선의 pending 다음 단계를 모두 가져 옴
	 * */
	@SuppressWarnings("unchecked")
	public static JSONArray getInactiveStep(JSONObject appvLine) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = new JSONObject();
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			JSONObject taskObject = (JSONObject)d.get("taskinfo");
		      
			if(taskObject.get("status").toString().equalsIgnoreCase("pending")){
				division = d;
				break;
			}			      
		}
		
		Object stepO = division.get("step");
		JSONArray steps = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			steps.add(stepJsonObj);
		} else {
			steps = (JSONArray)stepO;
		}

		JSONArray stepsOut = new JSONArray();
		
		for(int i = 0; i < steps.size(); i++)
		{
			JSONObject s = (JSONObject)steps.get(i);
			
			if(s.containsKey("unittype")){
				if(s.get("unittype").equals("person")){
					//jsonarray와 jsonobject 분기 처리
					JSONObject ouObject = new JSONObject();
					Object ouObj = s.get("ou");
					if(ouObj instanceof JSONArray){
						JSONArray ouArray = (JSONArray)ouObj;
						ouObject = (JSONObject)ouArray.get(0);
					} else {
						ouObject = (JSONObject)ouObj;
					}
						
					JSONObject taskObject = new JSONObject();
					if(ouObject.containsKey("person")){
						Object personObj = ouObject.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						//JSONObject personObject = (JSONObject)ouObject.get("person");
						taskObject = (JSONObject)personObject.get("taskinfo");
						
						//전달 처리
						if(taskObject.containsKey("kind")){
							if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
								JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
								taskObject = (JSONObject)forwardedPerson.get("taskinfo");
							}
						}
					} else if(ouObject.containsKey("role")) {
						JSONObject role = new JSONObject();
						role = (JSONObject)ouObject.get("role");
						taskObject = (JSONObject)role.get("taskinfo");
					} else {
						taskObject = (JSONObject)ouObject.get("taskinfo");
					}
				      
					if(taskObject.get("status").toString().equalsIgnoreCase("inactive")){
						stepsOut.add(s);
					}	
				}
			}
		}
		
		return stepsOut;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getInactiveDivision(JSONObject appvLine) throws Exception {
		JSONObject ret = new JSONObject();
		int divisionIndex = 0;
		
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = new JSONObject();
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			JSONObject taskObject = (JSONObject)d.get("taskinfo");
		      
			if(taskObject.get("status").toString().equalsIgnoreCase("inactive")){
				division = d;
				divisionIndex = i;
				
				break;
			}
		}
		
		ret.put("division", division);
		ret.put("divisionIndex", divisionIndex);
		
		return ret;
	}
	
	/*
	 * 결재선의 rejectee = y 단계의 wiid 값을 모두 가져옴
	 * */
	@SuppressWarnings("unchecked")
	public static List<String> getRejecteeStep(JSONObject appvLine, int divisionIdx) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		JSONObject division = (JSONObject)divisions.get(divisionIdx);
		
		Object stepO = division.get("step");
		JSONArray steps = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			steps.add(stepJsonObj);
		} else {
			steps = (JSONArray)stepO;
		}

		List<String> listOut = new ArrayList<String>();
		
		for(int i = 0; i < steps.size(); i++)
		{
			JSONObject s = (JSONObject)steps.get(i);
			
			if(s.containsKey("unittype")){
				//jsonarray와 jsonobject 분기 처리
				Object ou = s.get("ou");
				JSONArray ouArray = new JSONArray();
				if(ou instanceof JSONObject){
					JSONObject jsonObj = (JSONObject)ou;
					ouArray.add(jsonObj);
				} else {
					ouArray = (JSONArray)ou;
				}
				
				for(int k = 0; k < ouArray.size(); k++)
				{
					JSONObject ouObject = (JSONObject)ouArray.get(k);
					JSONObject taskObject = new JSONObject();
					
					if(ouObject.containsKey("person")){
						Object personObj = ouObject.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						for(int j = 0; j < persons.size(); j++)
						{
							JSONObject personObject = (JSONObject)persons.get(j);
							taskObject = (JSONObject)personObject.get("taskinfo");
							
							if(taskObject.containsKey("rejectee")){
								if(taskObject.get("rejectee").toString().equalsIgnoreCase("y")){
									if(ouObject.containsKey("wiid")){
										listOut.add(ouObject.get("wiid").toString());
									}
								}	
							}		
						}
						
						// 지정반려하는 중간에 부서협조/합의가 있는 경우 처리
						if(s.get("unittype").equals("ou")){
							JSONObject ouTaskObject = (JSONObject)ouObject.get("taskinfo");
							if(ouTaskObject.containsKey("piid")){
								CoviFlowWorkHelper.processCancel(Integer.parseInt(ouTaskObject.get("piid").toString()));
								CoviFlowWorkHelper.workitemCancel(Integer.parseInt(ouTaskObject.get("piid").toString()));
							}
						}
					} else if(ouObject.containsKey("role")) {
						JSONObject role = new JSONObject();
						role = (JSONObject)ouObject.get("role");
						taskObject = (JSONObject)role.get("taskinfo");
						
						if(taskObject.containsKey("rejectee")){
							if(taskObject.get("rejectee").toString().equalsIgnoreCase("y")){
								if(ouObject.containsKey("wiid")){
									listOut.add(ouObject.get("wiid").toString());
								}
							}	
						}
					} else {
						taskObject = (JSONObject)ouObject.get("taskinfo");
						
						if(taskObject.containsKey("rejectee")){
							if(taskObject.get("rejectee").toString().equalsIgnoreCase("y")){
								if(ouObject.containsKey("wiid")){
									listOut.add(ouObject.get("wiid").toString());
								}
							}	
						}
					}
				}
			}
		}
		
		return listOut;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getRejecteeStepForOU(JSONObject appvLine, int divisionIdx) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		JSONObject division = (JSONObject)divisions.get(divisionIdx);
		
		Object stepO = division.get("step");
		JSONArray steps = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			steps.add(stepJsonObj);
		} else {
			steps = (JSONArray)stepO;
		}

		List<String> listOut = new ArrayList<String>();
		
		for(int i = 0; i < steps.size(); i++)
		{
			JSONObject s = (JSONObject)steps.get(i);
			
			if(s.containsKey("unittype")){
				if(s.get("unittype").equals("ou")){
					//jsonarray와 jsonobject 분기 처리
					Object ou = s.get("ou");
					JSONArray ouArray = new JSONArray();
					if(ou instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)ou;
						ouArray.add(jsonObj);
					} else {
						ouArray = (JSONArray)ou;
					}
					
					for(int k = 0; k < ouArray.size(); k++)
					{
						JSONObject ouObject = (JSONObject)ouArray.get(k);
						JSONObject taskObject = new JSONObject();
						
						if(ouObject.containsKey("person")){
							Object personObj = ouObject.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
							
							for(int j = 0; j < persons.size(); j++)
							{
								JSONObject personObject = (JSONObject)persons.get(j);
								taskObject = (JSONObject)personObject.get("taskinfo");
								
								if(taskObject.containsKey("rejectee")){
									if(taskObject.get("rejectee").toString().equalsIgnoreCase("y")){
										if(personObject.containsKey("wiid")){
											listOut.add(personObject.get("wiid").toString());
										}
									}	
								}		
							}
							
						} else if(ouObject.containsKey("role")) {
							JSONObject role = new JSONObject();
							role = (JSONObject)ouObject.get("role");
							taskObject = (JSONObject)role.get("taskinfo");
							
							if(taskObject.containsKey("rejectee")){
								if(taskObject.get("rejectee").toString().equalsIgnoreCase("y")){
									if(ouObject.containsKey("wiid")){
										listOut.add(ouObject.get("wiid").toString());
									}
								}	
							}
						} else {
							taskObject = (JSONObject)ouObject.get("taskinfo");
							
							if(taskObject.containsKey("rejectee")){
								if(taskObject.get("rejectee").toString().equalsIgnoreCase("y")){
									if(ouObject.containsKey("wiid")){
										listOut.add(ouObject.get("wiid").toString());
									}
								}	
							}
						}
					}
				}
			}
		}
		
		return listOut;
	}
		
	public static String getRootAttr(JSONObject appvLine, String attrName) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		return root.get(attrName).toString();
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setRootAttr(JSONObject appvLine, String attrName, String attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		root.put(attrName, attrValue);
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setDivision(JSONObject appvLine, JSONObject division) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		divisions.add(division);
		root.put("division", divisions);
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static String getDivisionAttr(JSONObject appvLine, int index, String attrName) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(index);
		return division.get(attrName).toString();
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setDivisionAttr(JSONObject appvLine, int index, String attrName, String attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(index);
		division.put(attrName, attrValue);
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static String getDivisionTask(JSONObject appvLine, int index, String attrName) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(index);
		JSONObject taskObject = (JSONObject)division.get("taskinfo");
		return taskObject.get(attrName).toString();
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setDivisionTask(JSONObject appvLine, int index, String attrName, String attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(index);
		JSONObject taskObject = (JSONObject)division.get("taskinfo");
		taskObject.put(attrName, attrValue);
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setDivisionTask(JSONObject appvLine, int index, HashMap<String, String> attrs) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(index);
		JSONObject taskObject = (JSONObject)division.get("taskinfo");
		for (Map.Entry<String, String> entry : attrs.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    taskObject.put(key, value);
		}
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static String getStepAttr(JSONObject appvLine, int divisionIndex, int stepIndex, String attrName) throws Exception {
		String ret = "";
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		if(stepObject.containsKey(attrName)){
			ret = stepObject.get(attrName).toString(); 
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static String getStepTask(JSONObject appvLine, int divisionIndex, int stepIndex, String attrName) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		JSONObject taskObject = (JSONObject)stepObject.get("taskinfo");
		return taskObject.get(attrName).toString();
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setStepTask(JSONObject appvLine, int divisionIndex, int stepIndex, String attrName, String attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		if(stepObject.containsKey("taskinfo")){
			JSONObject taskObject = (JSONObject)stepObject.get("taskinfo");
			taskObject.put(attrName, attrValue);
		}
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getLegacyApproverInfo(String apvLine, String apvMode) throws Exception{
		JSONObject approverInfo = new JSONObject();
		
		JSONParser parser = new JSONParser();
		
		JSONObject root = (JSONObject)parser.parse(apvLine.toString());
		root  = (JSONObject)root.get("steps");
		
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		if(apvMode.equalsIgnoreCase("DRAFT")){
			JSONObject d = (JSONObject)divisions.get(0);
			
			Object stepO = d.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
			
			JSONObject s = (JSONObject)steps.get(0);
			
			String unitType = "";
			if(s.containsKey("unittype")){
				unitType = (String)s.get("unittype");	
			}
			
			//jsonarray와 jsonobject 분기 처리
			JSONObject ouObj = (JSONObject)s.get("ou");
			
			JSONObject person = (JSONObject)ouObj.get("person");
			JSONObject taskinfo = new JSONObject();
			if(person.containsKey("taskinfo")){
				taskinfo = (JSONObject)person.get("taskinfo");
				
				approverInfo.put("approverID", (String)person.get("code"));
				approverInfo.put("comment", taskinfo.containsKey("comment") ? CoviFlowWorkHelper.base64Decode((String)((JSONObject)taskinfo.get("comment")).get("#text")) : "");
			}
		}else{
			if(divisions != null){
				LoopDivisions : for(int i = 0; i < divisions.size(); i++)
				{
					JSONObject d = (JSONObject)divisions.get(i);
					
					Object stepO = d.get("step");
					JSONArray steps = new JSONArray();
					if(stepO instanceof JSONObject){
						JSONObject stepJsonObj = (JSONObject)stepO;
						steps.add(stepJsonObj);
					} else {
						steps = (JSONArray)stepO;
					}	
					
					if(steps != null){
						LoopSteps : for(int j = 0; j < steps.size(); j++)
						{
							JSONObject s = (JSONObject)steps.get(j);
							
							String unitType = "";
							if(s.containsKey("unittype")){
								unitType = (String)s.get("unittype");	
							}
							
							//jsonarray와 jsonobject 분기 처리
							Object ouObj = s.get("ou");
							JSONArray ouArray = new JSONArray();
							if(ouObj instanceof JSONArray){
								ouArray = (JSONArray)ouObj;
							} else {
								ouArray.add((JSONObject)ouObj);
							}
							
							if(ouArray != null){
								LoopOus : for(int k = 0; k < ouArray.size(); k++)
								{
									
									JSONObject ouObject = (JSONObject)ouArray.get(k);
									
									if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
										Object personObj = ouObject.get("person");
										JSONArray persons = new JSONArray();
										if(personObj instanceof JSONObject){
											JSONObject jsonObj = (JSONObject)personObj;
											persons.add(jsonObj);
										} else {
											persons = (JSONArray)personObj;
										}
										
										if(persons != null){
											LoopPersons : for(int l = 0; l < persons.size(); l++)
											{
												JSONObject person = (JSONObject)persons.get(l);
												JSONObject taskinfo = new JSONObject();
												if(person.containsKey("taskinfo")){
													taskinfo = (JSONObject)person.get("taskinfo");
													String status = "";
													if(taskinfo.containsKey("status")){
														status = (String)taskinfo.get("status");
														
														if((status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("rejected")) && ouObject.containsKey("taskid")){
															approverInfo.put("approverID", (String)person.get("code"));
															approverInfo.put("comment", taskinfo.containsKey("comment") ? CoviFlowWorkHelper.base64Decode((String)((JSONObject)taskinfo.get("comment")).get("#text")) : "");
															approverInfo.put("taskid", (String)ouObject.get("taskid").toString());
														}
													}
												}
											}
										}
										
									} else if (unitType.equalsIgnoreCase("ou")){
										//unit일 경우에도 대상 추출이 필요한가?
										
									} else if (unitType.equalsIgnoreCase("role")){
										//role일 경우에도 대상 추출이 필요한가?
									}
								}
							}
						}
					}
				}
			}
		}
		
		return approverInfo;
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject setStepTask(JSONObject appvLine, int divisionIndex, int stepIndex, HashMap<String, String> attrs) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		if(stepObject.containsKey("taskinfo")){
			JSONObject taskObject = (JSONObject)stepObject.get("taskinfo");
			
			for (Map.Entry<String, String> entry : attrs.entrySet()) {
			    String key = entry.getKey();
			    Object value = entry.getValue();
			    taskObject.put(key, value);
			}
		}
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject removeStepTask(JSONObject appvLine, int divisionIndex, int stepIndex) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		
		stepArray.remove(stepIndex);
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject removePersonTask(JSONObject appvLine, int divisionIndex, int stepIndex, int personIndex) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		
		JSONObject stepObj = (JSONObject)stepArray.get(stepIndex);
		Object personO = ((JSONObject)stepObj.get("ou")).get("person");
		JSONArray personArray = new JSONArray();
		if(personO instanceof JSONObject){
			JSONObject personJsonObj = (JSONObject)personO;
			personArray.add(personJsonObj);
		} else {
			personArray = (JSONArray)personO;
		}
		
		personArray.remove(personIndex);
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static String getOuTask(JSONObject appvLine, int divisionIndex, int stepIndex, String attrName) throws Exception {
		String ret = "";
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		String unitType = "";
		if(stepObject.containsKey("unittype")){
			unitType = (String)stepObject.get("unittype");
		}
		Object ouObj = stepObject.get("ou");
		if(ouObj instanceof JSONObject){
			JSONObject ouObject = (JSONObject)stepObject.get("ou");
			
			JSONObject taskObject = new JSONObject();
			if(ouObject.containsKey("person")&&unitType.equalsIgnoreCase("person")){
				Object personObj = ouObject.get("person");
				JSONArray persons = new JSONArray();
				if(personObj instanceof JSONObject){
					JSONObject jsonObj = (JSONObject)personObj;
					persons.add(jsonObj);
				} else {
					persons = (JSONArray)personObj;
				}
				
				JSONObject personObject = (JSONObject)persons.get(0);
				taskObject = (JSONObject)personObject.get("taskinfo");
				
				//전달 처리
				if(taskObject.containsKey("kind")){
					if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
						JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
						taskObject = (JSONObject)forwardedPerson.get("taskinfo");
					}
				}
				
			} else if(ouObject.containsKey("role")) {
				JSONObject role = new JSONObject();
				role = (JSONObject)ouObject.get("role");
				taskObject = (JSONObject)role.get("taskinfo");
			} else {
				taskObject = (JSONObject)ouObject.get("taskinfo");
			}
					
			ret = taskObject.get(attrName).toString();
		} 
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getOuIndex(JSONObject appvLine, int divisionIndex, int stepIndex) throws Exception {
		JSONObject ret = new JSONObject();
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		ret = (JSONObject)stepArray.get(stepIndex);

		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static String getOuTask(JSONObject step, String attrName) throws Exception {
		String ret = "";
		String unitType = "";
		if(step.containsKey("unittype")){
			unitType = (String)step.get("unittype");
		}
		Object ouObj = step.get("ou");
		JSONObject ouObject = new JSONObject();
		if(ouObj instanceof JSONObject){
			ouObject = (JSONObject)ouObj;
		} else {
			JSONArray ouArray = (JSONArray)ouObj;
			ouObject = (JSONObject)ouArray.get(0);
		}
		
		JSONObject taskObject = new JSONObject();
		if(ouObject.containsKey("person")&&unitType.equalsIgnoreCase("person")){
			Object personObj = ouObject.get("person");
			JSONArray persons = new JSONArray();
			if(personObj instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)personObj;
				persons.add(jsonObj);
			} else {
				persons = (JSONArray)personObj;
			}
			
			JSONObject personObject = (JSONObject)persons.get(0);
			taskObject = (JSONObject)personObject.get("taskinfo");
			
			//전달 처리
			if(taskObject.containsKey("kind")){
				if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
					JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
					taskObject = (JSONObject)forwardedPerson.get("taskinfo");
				}
			}
			
		} else if(ouObject.containsKey("role")) {
			JSONObject role = new JSONObject();
			role = (JSONObject)ouObject.get("role");
			taskObject = (JSONObject)role.get("taskinfo");
		} else {
			taskObject = (JSONObject)ouObject.get("taskinfo");
		}
		
		if(taskObject.containsKey(attrName)){
			ret = taskObject.get(attrName).toString();	
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static String getOuTaskForOU(JSONObject appvLine, int divisionIndex, int stepIndex, String execId, String attrName) throws Exception {
		String ret = "";
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			JSONObject taskObject = new JSONObject();
			taskObject = (JSONObject)o.get("taskinfo");
			
			if(taskObject.containsKey("execid")){
				if(taskObject.get("execid").toString().equalsIgnoreCase(execId)){
					ret = taskObject.get(attrName).toString();
				}	
			}				
		}
				
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setOuTaskForOU(JSONObject appvLine, int divisionIndex, int stepIndex, String execId, HashMap<String, String> attrs) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			JSONObject taskObject = new JSONObject();
			taskObject = (JSONObject)o.get("taskinfo");
			
			if(taskObject.containsKey("execid")){
				if(taskObject.get("execid").toString().equalsIgnoreCase(execId)){
					for (Map.Entry<String, String> entry : attrs.entrySet()) {
					    String key = entry.getKey();
					    Object value = entry.getValue();
					    taskObject.put(key, value);
					}
					break;
				}	
			} else {
				for (Map.Entry<String, String> entry : attrs.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    taskObject.put(key, value);
				}
				break;
			}
				
		}
				
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setOuTaskForOU(JSONObject appvLine, int divisionIndex, int stepIndex, String execId, String attrName, Object attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			JSONObject taskObject = new JSONObject();
			taskObject = (JSONObject)o.get("taskinfo");
			
			if(taskObject.containsKey("execid")){
				if(taskObject.get("execid").toString().equalsIgnoreCase(execId)){
					taskObject.put(attrName, attrValue);
					break;
				}	
			} else {
				taskObject.put(attrName, attrValue);
				break;
			}
				
		}
				
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setOuTask(JSONObject appvLine, int divisionIndex, int stepIndex, String taskId, String attrName, Object attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		String unitType = "";
		if(stepObject.containsKey("unittype")){
			unitType = (String)stepObject.get("unittype");
		}
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			
			if(StringUtils.isNotBlank(taskId)){
				
				if(o.containsKey("taskid")){
					if(o.get("taskid").toString().equalsIgnoreCase(taskId)){
						
						JSONObject taskObject = new JSONObject();
						
						if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
							Object personObj = o.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
						
							JSONObject personObject = (JSONObject)persons.get(0);
							taskObject = (JSONObject)personObject.get("taskinfo");
							
							//전달 처리
							if(taskObject.containsKey("kind")){
								if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
									JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
									taskObject = (JSONObject)forwardedPerson.get("taskinfo");
								}
							}
							
						} else if(o.containsKey("role")) {
							JSONObject role = new JSONObject();
							role = (JSONObject)o.get("role");
							taskObject = (JSONObject)role.get("taskinfo");
						} else {
							taskObject = (JSONObject)o.get("taskinfo");
						}
								
						taskObject.put(attrName, attrValue);
					}	
				}
				 
				
			} else {
				JSONObject taskObject = new JSONObject();
				
				if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
					Object personObj = o.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
				
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)forwardedPerson.get("taskinfo");
						}
					}
					
				} else if(o.containsKey("role")) {
					JSONObject role = new JSONObject();
					role = (JSONObject)o.get("role");
					taskObject = (JSONObject)role.get("taskinfo");
				} else {
					taskObject = (JSONObject)o.get("taskinfo");
				}
						
				taskObject.put(attrName, attrValue);	
			}
		}
				
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setOuTask(JSONObject appvLine, int divisionIndex, int stepIndex, int personIndex, String taskId, String attrName, Object attrValue) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		String unitType = "";
		if(stepObject.containsKey("unittype")){
			unitType = (String)stepObject.get("unittype");
		}
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			
			if(StringUtils.isNotBlank(taskId)){
				
				if(o.containsKey("taskid")){
					if(o.get("taskid").toString().equalsIgnoreCase(taskId)){
						JSONObject taskObject = new JSONObject();
						
						if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
							Object personObj = o.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
						
							JSONObject personObject = (JSONObject)persons.get(personIndex);
							taskObject = (JSONObject)personObject.get("taskinfo");
							
						} else if(o.containsKey("role")) {
							JSONObject role = new JSONObject();
							role = (JSONObject)o.get("role");
							taskObject = (JSONObject)role.get("taskinfo");
						} else {
							taskObject = (JSONObject)o.get("taskinfo");
						}
								
						taskObject.put(attrName, attrValue);
					}	
				}
				 				
			} else {
				JSONObject taskObject = new JSONObject();
				
				if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
					Object personObj = o.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
				
					JSONObject personObject = (JSONObject)persons.get(personIndex);
					taskObject = (JSONObject)personObject.get("taskinfo");
					
				} else if(o.containsKey("role")) {
					JSONObject role = new JSONObject();
					role = (JSONObject)o.get("role");
					taskObject = (JSONObject)role.get("taskinfo");
				} else {
					taskObject = (JSONObject)o.get("taskinfo");
				}
						
				taskObject.put(attrName, attrValue);	
			}
				
		}
				
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setOuTask(JSONObject appvLine, int divisionIndex, int stepIndex, String taskId, HashMap<String, String> attrs) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		String unitType = "";
		if(stepObject.containsKey("unittype")){
			unitType = (String)stepObject.get("unittype");
		}

		String routeType = "";
		if(stepObject.containsKey("routetype")){
			routeType = (String)stepObject.get("routetype");
		}
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			
			if(StringUtils.isNotBlank(taskId)){
				
				if(o.containsKey("taskid")){
					if(o.get("taskid").toString().equalsIgnoreCase(taskId)){
						JSONObject taskObject = new JSONObject();
						
						if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
							Object personObj = o.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
						
							JSONObject personObject = (JSONObject)persons.get(0);
							taskObject = (JSONObject)personObject.get("taskinfo");
							
							if(taskObject.containsKey("kind")){
								//전달 처리
								if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
									JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
									taskObject = (JSONObject)forwardedPerson.get("taskinfo");
								}
								//후결 처리
								else if(taskObject.get("kind").toString().equalsIgnoreCase("review")){
									if(stepArray.size() > stepIndex + 1){
										// 병렬인 경우 예외처리
										if(((JSONObject)stepArray.get(stepIndex + 1)).containsKey("allottype") && ((JSONObject)stepArray.get(stepIndex + 1)).get("allottype").toString().equalsIgnoreCase("parallel")) {
											Object pendingO = ((JSONObject)stepArray.get(stepIndex + 1)).get("ou");
											JSONArray ous = new JSONArray();
											if(pendingO instanceof JSONObject){
												JSONObject ouJsonObj = (JSONObject)pendingO;
												ous.add(ouJsonObj);
											} else {
												ous = (JSONArray)pendingO;
											}
											
											//해당 taskid에 대한 처리
											JSONObject pendingOu = (JSONObject)ous.get(i);
											
											Object pendingP = pendingOu.get("person");
											JSONArray persons_arr = new JSONArray();
											if(pendingP instanceof JSONObject){
												JSONObject personJsonObj = (JSONObject)pendingP;
												persons_arr.add(personJsonObj);
											} else {
												persons_arr = (JSONArray)pendingP;
											}
											
											JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject)persons_arr.get(0)).get("taskinfo");
											// JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou")).get("person")).get("taskinfo");
											
											if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
												appvLine = CoviFlowApprovalLineHelper.setStepTask(appvLine, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
												appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
											}
										}
										else {										
											JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou")).get("person")).get("taskinfo");
											
											if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
												appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
											}
										}
									}
									break;
								}
								//공람 처리
								else if(taskObject.get("kind").toString().equalsIgnoreCase("confirm")&&routeType.equalsIgnoreCase("review")){
									if(stepArray.size() > stepIndex + 1){
										// 병렬인 경우 예외처리
										if(((JSONObject)stepArray.get(stepIndex + 1)).containsKey("allottype") && ((JSONObject)stepArray.get(stepIndex + 1)).get("allottype").toString().equalsIgnoreCase("parallel")) {
											Object pendingO = ((JSONObject)stepArray.get(stepIndex + 1)).get("ou");
											JSONArray ous = new JSONArray();
											if(pendingO instanceof JSONObject){
												JSONObject ouJsonObj = (JSONObject)pendingO;
												ous.add(ouJsonObj);
											} else {
												ous = (JSONArray)pendingO;
											}
											
											//해당 taskid에 대한 처리
											JSONObject pendingOu = (JSONObject)ous.get(i);
											
											Object pendingP = pendingOu.get("person");
											JSONArray persons_arr = new JSONArray();
											if(pendingP instanceof JSONObject){
												JSONObject personJsonObj = (JSONObject)pendingP;
												persons_arr.add(personJsonObj);
											} else {
												persons_arr = (JSONArray)pendingP;
											}
											
											JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject)persons_arr.get(0)).get("taskinfo");
											// JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou")).get("person")).get("taskinfo");
											
											if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
												appvLine = CoviFlowApprovalLineHelper.setStepTask(appvLine, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
												appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
											}
										}
										else {										
											JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou")).get("person")).get("taskinfo");
											
											if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
												appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
											}
										}
									}
									break;
								}								
							}
							
						} else if(o.containsKey("role")) {
							JSONObject role = new JSONObject();
							role = (JSONObject)o.get("role");
							taskObject = (JSONObject)role.get("taskinfo");
						} else {
							taskObject = (JSONObject)o.get("taskinfo");
						}
								
						for (Map.Entry<String, String> entry : attrs.entrySet()) {
						    String key = entry.getKey();
						    Object value = entry.getValue();
						    taskObject.put(key, value);
						}
					}
				}
				
			} else {
				JSONObject taskObject = new JSONObject();
				
				if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
					Object personObj = o.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
				
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					
					if(taskObject.containsKey("kind")){
						//전달 처리
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)forwardedPerson.get("taskinfo");
						}
						// 후결 처리 
						else if(taskObject.get("kind").toString().equalsIgnoreCase("review")){
							if(stepArray.size() > stepIndex + 1){
								// 병렬인 경우 예외처리
								if(((JSONObject)stepArray.get(stepIndex + 1)).containsKey("allottype") && ((JSONObject)stepArray.get(stepIndex + 1)).get("allottype").toString().equalsIgnoreCase("parallel")) {
									Object pendingO = ((JSONObject)stepArray.get(stepIndex + 1)).get("ou");
									JSONArray ous = new JSONArray();
									if(pendingO instanceof JSONObject){
										JSONObject ouJsonObj = (JSONObject)pendingO;
										ous.add(ouJsonObj);
									} else {
										ous = (JSONArray)pendingO;
									}
									
									//해당 taskid에 대한 처리
									JSONObject pendingOu = (JSONObject)ous.get(i);
									
									// 후결 뒤에 person/ou 오는 여부에 따라 분기함.
									JSONObject nextStepTaskInfo = new JSONObject();
									if(pendingOu.containsKey("person")){
										Object pendingP = pendingOu.get("person");
										JSONArray persons_arr = new JSONArray();
										if(pendingP instanceof JSONObject){
											JSONObject personJsonObj = (JSONObject)pendingP;
											persons_arr.add(personJsonObj);
										} else {
											persons_arr = (JSONArray)pendingP;
										}
										
										nextStepTaskInfo = (JSONObject) ((JSONObject)persons_arr.get(0)).get("taskinfo");
										// JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou")).get("person")).get("taskinfo");
									}
									else {
										nextStepTaskInfo = (JSONObject) pendingOu.get("taskinfo");
									}
									
									if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
										appvLine = CoviFlowApprovalLineHelper.setStepTask(appvLine, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
										appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
									}
								}
								else {
									// 후결 뒤에 person/ou 오는 여부에 따라 분기함.
									JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou");
									
									if(nextStepTaskInfo.containsKey("person")){
										nextStepTaskInfo = (JSONObject)((JSONObject)nextStepTaskInfo.get("person")).get("taskinfo");
										
										if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
											appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
										}
									} else {
										nextStepTaskInfo = (JSONObject)nextStepTaskInfo.get("taskinfo");
										
										if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
											appvLine = CoviFlowApprovalLineHelper.setStepTask(appvLine, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
											appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
										}
									}
								}
							}
							break;
						}
						// 공람 처리 
						else if(taskObject.get("kind").toString().equalsIgnoreCase("confirm")&&routeType.equalsIgnoreCase("review")){
							if(stepArray.size() > stepIndex + 1){
								// 병렬인 경우 예외처리
								if(((JSONObject)stepArray.get(stepIndex + 1)).containsKey("allottype") && ((JSONObject)stepArray.get(stepIndex + 1)).get("allottype").toString().equalsIgnoreCase("parallel")) {
									Object pendingO = ((JSONObject)stepArray.get(stepIndex + 1)).get("ou");
									JSONArray ous = new JSONArray();
									if(pendingO instanceof JSONObject){
										JSONObject ouJsonObj = (JSONObject)pendingO;
										ous.add(ouJsonObj);
									} else {
										ous = (JSONArray)pendingO;
									}
									
									//해당 taskid에 대한 처리
									JSONObject pendingOu = (JSONObject)ous.get(i);
									
									// 공람 뒤에 person/ou 오는 여부에 따라 분기함.
									JSONObject nextStepTaskInfo = new JSONObject();
									if(pendingOu.containsKey("person")){
										Object pendingP = pendingOu.get("person");
										JSONArray persons_arr = new JSONArray();
										if(pendingP instanceof JSONObject){
											JSONObject personJsonObj = (JSONObject)pendingP;
											persons_arr.add(personJsonObj);
										} else {
											persons_arr = (JSONArray)pendingP;
										}
										
										nextStepTaskInfo = (JSONObject) ((JSONObject)persons_arr.get(0)).get("taskinfo");
										// JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou")).get("person")).get("taskinfo");
									}
									else {
										nextStepTaskInfo = (JSONObject) pendingOu.get("taskinfo");
									}
									
									if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
										appvLine = CoviFlowApprovalLineHelper.setStepTask(appvLine, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
										appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
									}
								}
								else {
									// 공람 뒤에 person/ou 오는 여부에 따라 분기함.
									JSONObject nextStepTaskInfo = (JSONObject) ((JSONObject)stepArray.get(stepIndex + 1)).get("ou");
									
									if(nextStepTaskInfo.containsKey("person")){
										nextStepTaskInfo = (JSONObject)((JSONObject)nextStepTaskInfo.get("person")).get("taskinfo");
										
										if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
											appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
										}
									} else {
										nextStepTaskInfo = (JSONObject)nextStepTaskInfo.get("taskinfo");
										
										if(!nextStepTaskInfo.get("status").toString().equalsIgnoreCase("completed")){
											appvLine = CoviFlowApprovalLineHelper.setStepTask(appvLine, divisionIndex, stepIndex + 1, CoviFlowVariables.APPV_PENDING);
											appvLine = CoviFlowApprovalLineHelper.setOuTask(appvLine, divisionIndex, stepIndex + 1, "", CoviFlowVariables.APPV_PENDING);
										}
									}
								}
							}
							break;
						}						
					}
					
				} else if(o.containsKey("role")) {
					JSONObject role = new JSONObject();
					role = (JSONObject)o.get("role");
					taskObject = (JSONObject)role.get("taskinfo");
				} else {
					taskObject = (JSONObject)o.get("taskinfo");
				}
						
				for (Map.Entry<String, String> entry : attrs.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    taskObject.put(key, value);
				}	
			}
		}
				
		return appvLine;
	}
	
	// ou만 있는 Division 가져오기(부서 수신함)
	@SuppressWarnings("unchecked")
	public static JSONObject getClearDivision_OU(JSONObject appvLine) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			JSONObject taskObject = (JSONObject)d.get("taskinfo");
			String divisionType = (String)d.get("divisiontype");
			
			//division의 taskinfo가 active인 경우
			if(taskObject.get("status").toString().equalsIgnoreCase("pending") 
					&& taskObject.get("result").toString().equalsIgnoreCase("pending")
					&& !divisionType.equalsIgnoreCase("send")){
				Object stepsObj = d.get("step");
				JSONArray steps = new JSONArray();
				if(stepsObj instanceof JSONObject){
					JSONObject stepJsonObj = (JSONObject)stepsObj;
					steps.add(stepJsonObj);
				} else {
					steps = (JSONArray)stepsObj;
				}
				
				/*"step": {
		          "unittype": "ou",
		          "routetype": "receive",
		          "name": "담당부서수신",
		          "ou": {
		            "code": "RD02",
		            "name": "연구2팀;R&D team 2;;;;;;;;",
		            "taskinfo": {
		              "status": "inactive",
		              "result": "inactive",
		              "kind": "normal"
		            }
		          }*/
				steps.add(0, steps.get(0));
				
				JSONObject divObj = (JSONObject)divisions.get(i);
				JSONObject stepObj = (JSONObject)steps.get(0);
				JSONObject ouObj = (JSONObject)stepObj.get("ou");
				// 왜 person의 taskinfo를 쓴거지? 수신부서이므로 ou것 또는 신규생성으로 변경 (일반 진행시 ou에 taskinfo없고, 담당자지정시 있음)
				//JSONObject personObj = (JSONObject)ouObj.get("person");
				//JSONObject taskinfoObj = (JSONObject)personObj.get("taskinfo");
				JSONObject taskinfoObj = new JSONObject();
				if(ouObj.containsKey("taskinfo")) {
					taskinfoObj = (JSONObject)ouObj.get("taskinfo");
				}
				JSONObject divTaskInfo = new JSONObject();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
				}
				
				stepObj.put("unittype", "ou");
				stepObj.put("routetype", "receive");
				
				divTaskInfo.put("status", "pending");
				divTaskInfo.put("result", "pending");
				divTaskInfo.put("kind", "receive");
				divTaskInfo.put("datereceived", sdf.format(new Date()));
				divObj.put("taskinfo", divTaskInfo);
				
				if(ouObj.containsKey("pfid")) {
					ouObj.remove("pfid");	
				}
				if(ouObj.containsKey("wiid")) {
					ouObj.remove("wiid");	
				}
				if(ouObj.containsKey("widescid")) {
					ouObj.remove("widescid");	
				}
				if(ouObj.containsKey("taskid")) {
					ouObj.remove("taskid");	
				}
				
				if(taskinfoObj.containsKey("datereceived")) {
					taskinfoObj.remove("datereceived");	
				}
				if(taskinfoObj.containsKey("datecompleted")) {
					taskinfoObj.remove("datecompleted");	
				}
				if(taskinfoObj.containsKey("customattribute1")) {
					taskinfoObj.remove("customattribute1");	
				}					
				
				taskinfoObj.put("result", "inactive");
				taskinfoObj.put("status", "inactive");
				taskinfoObj.put("kind", "normal");
				if(taskinfoObj.containsKey("routetype")) {
					taskinfoObj.put("routetype", "receive");	
				}
				
				ouObj.put("taskinfo", taskinfoObj);
				ouObj.remove("person");
				
				for(int j = steps.size()-1; j > 0; j--) {
					steps.remove(steps.get(j));
				}
			}
		}
				
		return appvLine;
	}
	
	// person만 있는 Division 가져오기(개인 미결함, 수신)
	@SuppressWarnings("unchecked")
	public static JSONObject getClearDivision_Person(JSONObject appvLine) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		for(int i = 0; i < divisions.size(); i++)
		{
			JSONObject d = (JSONObject)divisions.get(i);
			JSONObject taskObject = (JSONObject)d.get("taskinfo");
			String divisionType = (String)d.get("divisiontype");
			
			//division의 taskinfo가 active인 경우
			if(taskObject.get("status").toString().equalsIgnoreCase("pending") 
					&& taskObject.get("result").toString().equalsIgnoreCase("pending")
					&& !divisionType.equalsIgnoreCase("send")){
				Object stepsObj = d.get("step");
				JSONArray steps = new JSONArray();
				if(stepsObj instanceof JSONObject){
					JSONObject stepJsonObj = (JSONObject)stepsObj;
					steps.add(stepJsonObj);
				} else {
					steps = (JSONArray)stepsObj;
				}
				
				/*"step": {
		          "unittype": "person",
		          "routetype": "receive",
		          "name": "담당결재",
		          "ou": {
		            "code": "RD01",
		            "name": "연구1팀;R&D team 1;;;;;;;;",
		            "person": {
		              "code": "gypark",
		              "name": "박경연;;;;;;;;;",
		              "position": "P600&사원;;;;;;;;;",
		              "title": "T300&팀원;;;;;;;;;",
		              "level": "L600&사원;;;;;;;;;",
		              "oucode": "RD01",
		              "ouname": "연구1팀;R&D team 1;;;;;;;;",
		              "taskinfo": {
		                "status": "inactive",
		                "result": "inactive",
		                "kind": "normal"
		              }
		            }
		          }
		        }*/
				steps.add(0, steps.get(0));
				
				JSONObject divObj = (JSONObject)divisions.get(i);
				JSONObject stepObj = (JSONObject)steps.get(0);
				JSONObject ouObj = (JSONObject)stepObj.get("ou");
				
				Object personsObj = ouObj.get("person");
				JSONArray persons = new JSONArray();
				if(personsObj instanceof JSONObject){
					JSONObject personJsonObj = (JSONObject)personsObj;
					persons.add(personJsonObj);
				} else {
					persons = (JSONArray)personsObj;
				}
				persons.add(0, persons.get(0));
				JSONObject personObj = (JSONObject)persons.get(0);
				JSONObject taskinfoObj = (JSONObject)personObj.get("taskinfo");
				
				JSONObject divTaskInfo = new JSONObject();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
				}
				
				stepObj.put("unittype", "person");
				stepObj.put("routetype", "receive");
				
				divTaskInfo.put("status", "pending");
				divTaskInfo.put("result", "pending");
				divTaskInfo.put("kind", "receive");
				divTaskInfo.put("datereceived", sdf.format(new Date()));
				divObj.put("taskinfo", divTaskInfo);
				
				if(ouObj.containsKey("pfid")) {
					ouObj.remove("pfid");	
				}
				if(ouObj.containsKey("wiid")) {
					ouObj.remove("wiid");	
				}
				if(ouObj.containsKey("widescid")) {
					ouObj.remove("widescid");	
				}
				if(ouObj.containsKey("taskid")) {
					ouObj.remove("taskid");	
				}
				
				if(taskinfoObj.containsKey("datereceived")) {
					taskinfoObj.remove("datereceived");	
				}
				if(taskinfoObj.containsKey("datecompleted")) {
					taskinfoObj.remove("datecompleted");	
				}
				if(taskinfoObj.containsKey("customattribute1")) {
					taskinfoObj.remove("customattribute1");	
				}					
				
				if(taskinfoObj.containsKey("result")) {
					taskinfoObj.put("result", "inactive");	
				}
				if(taskinfoObj.containsKey("status")) {
					taskinfoObj.put("status", "inactive");	
				}
				if(taskinfoObj.containsKey("routetype")) {
					taskinfoObj.put("routetype", "receive");	
				}
				if(taskinfoObj.containsKey("kind")) {
					taskinfoObj.put("kind", "charge");	
				}
				
				personObj.put("taskinfo", taskinfoObj);
				
				for(int j = steps.size()-1; j > 0; j--) {
					steps.remove(steps.get(j));
				}
			}
		}
				
		return appvLine;
	}
	
	// 참조 datereceived 지우기
	@SuppressWarnings("unchecked")
	public static JSONObject getClearCCInfo(JSONObject appvLine) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object ccinfoObj = root.get("ccinfo");
		JSONArray ccinfos = new JSONArray();

		if(ccinfoObj!=null) {
			if(ccinfoObj instanceof JSONObject){
				JSONObject ccinfoJsonObj = (JSONObject)ccinfoObj;
				ccinfos.add(ccinfoJsonObj);
			} else {
				ccinfos = (JSONArray)ccinfoObj;
			}
			
			for(int i = 0; i < ccinfos.size(); i++)
			{
				JSONObject c = (JSONObject)ccinfos.get(i);
				c.put("datereceived", "");
			}
		}
		
		return appvLine;
	}
	
	// 부서합의/협조 ou만 남기기
	@SuppressWarnings("unchecked")
	public static JSONObject getClearOU_Person(JSONObject appvLine, int divisionIndex, int stepIndex) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		/*{
        "ou": {
          "code": "RD01",
          "taskinfo": {
            "result": "inactive",
            "kind": "normal",
            "status": "inactive"
          },
          "name": "연구1팀;R&amp;D team 1;;;;;;;;"
        },
      },*/
		
		JSONObject o = (JSONObject)ouArray.get(0);
		JSONObject taskObject_step = (JSONObject)stepObject.get("taskinfo");
		JSONObject taskObject_ou = (JSONObject)o.get("taskinfo");
		
		o.remove("person");
		
		if(o.containsKey("pfid")){
			o.remove("pfid");	
		}
		
		if(o.containsKey("wiid")){
			o.remove("wiid");	
		}
		
		if(o.containsKey("widescid")){
			o.remove("widescid");	
		}
		
		if(o.containsKey("taskid")){
			o.remove("taskid");	
		}
		
		if(taskObject_ou.containsKey("result")) {
			taskObject_step.put("result", "pending");
			taskObject_ou.put("result", "pending");	
		}
		if(taskObject_ou.containsKey("status")) {
			taskObject_step.put("status", "pending");	
			taskObject_ou.put("status", "pending");
		}
		
		//remove 처리
		if(taskObject_ou.containsKey("datereceived")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
			}
			
			taskObject_step.put("datereceived", sdf.format(new Date()));	
			taskObject_ou.put("datereceived", sdf.format(new Date()));
		}
		
		if(taskObject_ou.containsKey("datecompleted")){
			taskObject_ou.remove("datecompleted");	
		}
		if(taskObject_ou.containsKey("comment")){
			taskObject_ou.remove("comment");	
		}
		if(taskObject_ou.containsKey("comment_fileinfo")){
			taskObject_ou.remove("comment_fileinfo");	
		}
		if(taskObject_ou.containsKey("piid")){
			taskObject_ou.remove("piid");	
		}
		if(taskObject_ou.containsKey("pdescid")){
			taskObject_ou.remove("pdescid");	
		}
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getClearDivision_JF(String JFID, int divisionIndex, JSONObject apvLineObj, int processID) throws Exception {
		JSONObject root = (JSONObject)apvLineObj.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		String processDescID = ((JSONObject) divisions.get(divisionIndex)).get("processDescID").toString();
		divisions.remove(divisionIndex);

		// 담당업무함 추가
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(CoviFlowPropHelper.getInstace().getPropertyValue("useTimeZone").equalsIgnoreCase("true")) {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	
		}
		
		String jfId = "";
		String jfName = "";
		if(StringUtils.isNotBlank(JFID)){
			jfId = JFID.split("@")[0];
			jfName = JFID.split("@")[1];
		}
		
		//업무담당함에 대해서 division을 추가할 것
		JSONObject division = new JSONObject();
		JSONObject divTaskInfo = new JSONObject();
		JSONObject step = new JSONObject();
		JSONObject ou = new JSONObject();
		JSONObject role = new JSONObject();
		JSONObject roleTaskInfo = new JSONObject();
		
		//role > taskinfo
		roleTaskInfo.put("status", "pending");
		roleTaskInfo.put("result", "pending");
		roleTaskInfo.put("kind", "normal");
		roleTaskInfo.put("datereceived", sdf.format(new Date()));
		
		//role
		role.put("taskinfo", roleTaskInfo);
		role.put("code", jfId);
		role.put("name", jfName);
		role.put("oucode", jfId);
		role.put("ouname", jfName);
		
		//ou
		ou.put("role", role);
		ou.put("code", jfId);
		ou.put("name", jfName);
		
		//step
		step.put("ou", ou);
		step.put("unittype", "person");
		step.put("routetype", "receive");
		step.put("name", "담당결재");
		
		//division > taskinfo
		divTaskInfo.put("status", "pending");
		divTaskInfo.put("result", "pending");
		divTaskInfo.put("kind", "receive");
		divTaskInfo.put("datereceived", sdf.format(new Date()));
		
		//division
		division.put("processID", processID);
		
		division.put("taskinfo", divTaskInfo);
		division.put("step", step);
		division.put("divisiontype", "receive");
		division.put("name", "담당결재");
		
		division.put("processDescID", processDescID);
		
		apvLineObj = CoviFlowApprovalLineHelper.setDivision(apvLineObj, division);
		
		return apvLineObj;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject clearStep(JSONObject appvLine, int divisionIndex, int stepIndex, String type) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		String unitType = "";
		if(stepObject.containsKey("unittype")){
			unitType = (String)stepObject.get("unittype");
		}
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			//불필요한 데이터는(taskid, wiid, widescid, pfid, datecompleted, datereceived, comment)  remove
			
			// id값을 제외한 date, comment 정보만 삭제할 수 있도록 조건 분기
			if(!type.equalsIgnoreCase("NoID")){
				if(o.containsKey("taskid")){
					o.remove("taskid");	
				}
				
				if(o.containsKey("wiid")){
					o.remove("wiid");	
				}
				
				if(o.containsKey("widescid")){
					o.remove("widescid");	
				}
				
				if(o.containsKey("pfid")){
					o.remove("pfid");	
				}
			}
			
			if(type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("NoID")){
				JSONObject taskObject = new JSONObject();
				if(o.containsKey("person")&&unitType.equalsIgnoreCase("person")){
					Object personObj = o.get("person");
					JSONArray persons = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						persons.add(jsonObj);
					} else {
						persons = (JSONArray)personObj;
					}
				
					JSONObject personObject = (JSONObject)persons.get(0);
					taskObject = (JSONObject)personObject.get("taskinfo");
					
					//전달 처리
					if(taskObject.containsKey("kind")){
						if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
							JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
							taskObject = (JSONObject)forwardedPerson.get("taskinfo");
						}
					}
					
				} else if(o.containsKey("role")) {
					JSONObject role = new JSONObject();
					role = (JSONObject)o.get("role");
					taskObject = (JSONObject)role.get("taskinfo");
				} else {
					taskObject = (JSONObject)o.get("taskinfo");
				}
				
				//remove 처리
				if(taskObject.containsKey("datereceived")){
					taskObject.remove("datereceived");	
				}
				
				if(taskObject.containsKey("datecompleted")){
					taskObject.remove("datecompleted");	
				}
				
				if(taskObject.containsKey("comment")){
					taskObject.remove("comment");	
				}
				if(taskObject.containsKey("comment_fileinfo")){
					taskObject.remove("comment_fileinfo");	
				}
			}
		}
				
		return appvLine;
	}
	
	public static String getCCAttr(JSONObject appvLine, String attrName) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		JSONObject ccObject = (JSONObject)root.get("ccinfo");
		return ccObject.get(attrName).toString();
	}

	public static String getCCOu(JSONObject appvLine, String attrName) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		JSONObject ccObject = (JSONObject)root.get("ccinfo");
		JSONObject ouObject = (JSONObject)ccObject.get("ou");
		return ouObject.get(attrName).toString();
	}
	
	/**
	 * 후결 목록
	 * @param appvLine
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getInactiveReviewStep(JSONObject appvLine, int initDivisionIndex) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}

		List<String> reviewers = new ArrayList<String>();
		
		for(int d = initDivisionIndex; d < divisions.size(); d++)
		{
			JSONObject division = (JSONObject) divisions.get(d);
		
			Object stepO = division.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
			
			for(int i = 0; i < steps.size(); i++)
			{
				JSONObject s = (JSONObject)steps.get(i);
				
				if(s.containsKey("unittype")){
					if(s.get("unittype").equals("person")){
						//jsonarray와 jsonobject 분기 처리
						JSONObject ouObject = new JSONObject();
						Object ouObj = s.get("ou");
						if(ouObj instanceof JSONArray){
							JSONArray ouArray = (JSONArray)ouObj;
							ouObject = (JSONObject)ouArray.get(0);
						} else {
							ouObject = (JSONObject)ouObj;
						}
							
						JSONObject taskObject = new JSONObject();
						Object personObj = ouObject.get("person");
						JSONArray persons = new JSONArray();
						if(personObj instanceof JSONObject){
							JSONObject jsonObj = (JSONObject)personObj;
							persons.add(jsonObj);
						} else {
							persons = (JSONArray)personObj;
						}
						
						JSONObject personObject = (JSONObject)persons.get(0);
						taskObject = (JSONObject)personObject.get("taskinfo");
					      
						if(taskObject.get("status").toString().equalsIgnoreCase("inactive") && taskObject.get("kind").toString().equalsIgnoreCase("review")){
							reviewers.add(personObject.get("code").toString());
						}	
					}
				}
			}
		}
			
		return reviewers;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject setOuForSubProcess(JSONObject appvLine, int divisionIndex, int stepIndex, JSONObject pendingOu, String execid) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		
		Object ouObj = stepObject.get("ou");
		JSONArray ouArray = new JSONArray();
		if(ouObj instanceof JSONObject){
			ouArray.add(ouObj);
		} else {
			ouArray = (JSONArray)ouObj;
		}
		
		for(int i = 0; i < ouArray.size(); i++)
		{
			JSONObject o = (JSONObject)ouArray.get(i);
			JSONObject ouTaskObject = (JSONObject)o.get("taskinfo");
			
			if(ouTaskObject.get("execid").toString().equalsIgnoreCase(execid)){
				
				if(ouObj instanceof JSONObject){
					stepObject.remove("ou");
					stepObject.put("ou", pendingOu);
				} else {
					ouArray.remove(i);
					ouArray.add(i, pendingOu);
				}
				break;
			}
		}
		
		return appvLine;
	}
	
	@SuppressWarnings("unchecked")
	public static String getInitiatorID(JSONObject apvLineObj) throws Exception {
		String initiatorID = "";
		
		JSONObject division = (JSONObject)getDivisions(apvLineObj).get(0);
		
		Object stepObj = division.get("step");
		JSONArray stepArr = new JSONArray();
		if(stepObj instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepObj;
			stepArr.add(stepJsonObj);
		} else {
			stepArr = (JSONArray)stepObj;
		}
		
		if(stepArr != null) {
			JSONObject step = (JSONObject)stepArr.get(0);
			
			String unitType = "";
			if(step.containsKey("unittype")){
				unitType = (String)step.get("unittype");	
			}
			
			if(step != null) {
				Object ouObj = step.get("ou");
				JSONArray ouArr = new JSONArray();
				if(ouObj instanceof JSONArray){
					ouArr = (JSONArray)ouObj;
				} else {
					ouArr.add((JSONObject)ouObj);
				}
				
				JSONObject ou = (JSONObject)ouArr.get(0);
				
				if(ou.containsKey("person") && unitType.equalsIgnoreCase("person")){
					Object personObj = ou.get("person");
					JSONArray personArr = new JSONArray();
					if(personObj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)personObj;
						personArr.add(jsonObj);
					} else {
						personArr = (JSONArray)personObj;
					}
					
					JSONObject person = (JSONObject)personArr.get(0);
					
					initiatorID = (String)person.get("code");
				}
			}
		}
		
		return initiatorID;
	}
	/**
	 * 공람결재자 목록
	 * @param appvLine
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getInactiveStepRouteReview(JSONObject appvLine, int initDivisionIndex) throws Exception {
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}

		List<String> reviewers = new ArrayList<String>();
		
		for(int d = initDivisionIndex; d < divisions.size(); d++)
		{
			JSONObject division = (JSONObject) divisions.get(d);
		
			Object stepO = division.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
			
			for(int i = 0; i < steps.size(); i++)
			{
				JSONObject s = (JSONObject)steps.get(i);
				
				if(s.containsKey("unittype") && s.containsKey("routetype")){
					if(s.get("unittype").equals("person") && s.get("routetype").equals("review") ){
						//jsonarray와 jsonobject 분기 처리
						JSONObject ouObject = new JSONObject();
						
						Object o = s.get("ou");
						JSONArray ouObj = new JSONArray();
						if(o instanceof JSONObject){
							ouObj.add((JSONObject)o);
						} else {
							ouObj = (JSONArray)o;
						}
						
						for(int j=0; j < ouObj.size();j++) {
							/*if(ouObj instanceof JSONArray){
								JSONArray ouArray = (JSONArray)ouObj;
								ouObject = (JSONObject)ouArray.get(0);
							} else {
								ouObject = (JSONObject)ouObj;
							}*/
							ouObject =  (JSONObject)ouObj.get(j);
								
							JSONObject taskObject = new JSONObject();
							Object personObj = ouObject.get("person");
							JSONArray persons = new JSONArray();
							if(personObj instanceof JSONObject){
								JSONObject jsonObj = (JSONObject)personObj;
								persons.add(jsonObj);
							} else {
								persons = (JSONArray)personObj;
							}
							
							JSONObject personObject = (JSONObject)persons.get(0);
							taskObject = (JSONObject)personObject.get("taskinfo");
						      
							if(taskObject.get("status").toString().equalsIgnoreCase("inactive") && taskObject.get("kind").toString().equalsIgnoreCase("confirm")){
								reviewers.add(personObject.get("code").toString());
							}
						}
					}
				}
			}
		}
			
		return reviewers;
	}
	//공람결재자 목록-inactive
	@SuppressWarnings("unchecked")
	public static JSONArray getStepRouteReviewer(JSONObject apvLineObj) throws Exception {
		JSONArray ret = new JSONArray();
		
		JSONObject root = (JSONObject)apvLineObj.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		for(int z = 0; z < divisions.size(); z++)
		{
			JSONObject division = (JSONObject)divisions.get(z);
			
			Object stepO = division.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
			
			String unitType = "";
			
			for(int i = 0; i < steps.size(); i++)
			{
				unitType = "";
				
				JSONObject s = (JSONObject)steps.get(i);
				if(s.containsKey("unittype") && s.containsKey("routetype")){
					if(s.get("unittype").equals("person") && s.get("routetype").equals("review") ){
						// index
						s.put("stepIndex", i);
						s.put("divisionIndex", z);
						
						unitType = (String)s.get("unittype");
						
						//jsonarray와 jsonobject 분기 처리
						Object ouObj = s.get("ou");
						JSONArray ouArray = new JSONArray();
						if(ouObj instanceof JSONArray){
							ouArray = (JSONArray)ouObj;
						} else {
							ouArray.add((JSONObject)ouObj);
						}
						
						boolean badd = false;
						for(int j = 0; j < ouArray.size(); j++)
						{
							
							JSONObject ouObject = (JSONObject)ouArray.get(j);
							JSONObject taskObject = new JSONObject();
							if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
							//if(ouObject.containsKey("person")){	
								Object personObj = ouObject.get("person");
								JSONArray persons = new JSONArray();
								if(personObj instanceof JSONObject){
									JSONObject jsonObj = (JSONObject)personObj;
									persons.add(jsonObj);
								} else {
									persons = (JSONArray)personObj;
								}
								
								JSONObject personObject = (JSONObject)persons.get(0);
								taskObject = (JSONObject)personObject.get("taskinfo");
								
								//전달 처리
								if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
									JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
									taskObject = (JSONObject)forwardedPerson.get("taskinfo");
								}
								
							} else if(ouObject.containsKey("role")) {
								JSONObject role = new JSONObject();
								role = (JSONObject)ouObject.get("role");
								taskObject = (JSONObject)role.get("taskinfo");
							} else {
								taskObject = (JSONObject)ouObject.get("taskinfo");
							}
							
							if(taskObject.get("status").toString().equalsIgnoreCase("inactive") && taskObject.get("kind").toString().equalsIgnoreCase("confirm")){
								//ret.add(s);
								badd = true;
							}
						}
						if(badd) ret.add(s);
					}
				}
			    		      
			}
		}
		
		return ret;
	}	
	//공람결재자 목록-pending
	@SuppressWarnings("unchecked")
	public static JSONArray getStepRouteReviewerPending(JSONObject apvLineObj) throws Exception {
		JSONArray ret = new JSONArray();
		
		JSONObject root = (JSONObject)apvLineObj.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		
		for(int z = 0; z < divisions.size(); z++)
		{
			JSONObject division = (JSONObject)divisions.get(z);
			
			Object stepO = division.get("step");
			JSONArray steps = new JSONArray();
			if(stepO instanceof JSONObject){
				JSONObject stepJsonObj = (JSONObject)stepO;
				steps.add(stepJsonObj);
			} else {
				steps = (JSONArray)stepO;
			}
			
			String unitType = "";
			
			for(int i = 0; i < steps.size(); i++)
			{
				unitType = "";
				
				JSONObject s = (JSONObject)steps.get(i);
				if(s.containsKey("unittype") && s.containsKey("routetype")){
					if(s.get("unittype").equals("person") && s.get("routetype").equals("review") ){
						// index
						s.put("stepIndex", i);
						s.put("divisionIndex", z);
						
						unitType = (String)s.get("unittype");
						
						//jsonarray와 jsonobject 분기 처리
						Object ouObj = s.get("ou");
						JSONArray ouArray = new JSONArray();
						if(ouObj instanceof JSONArray){
							ouArray = (JSONArray)ouObj;
						} else {
							ouArray.add((JSONObject)ouObj);
						}
						
						boolean badd = false;
						for(int j = 0; j < ouArray.size(); j++)
						{
							
							JSONObject ouObject = (JSONObject)ouArray.get(j);
							JSONObject taskObject = new JSONObject();
							if(ouObject.containsKey("person") && unitType.equalsIgnoreCase("person")){
							//if(ouObject.containsKey("person")){	
								Object personObj = ouObject.get("person");
								JSONArray persons = new JSONArray();
								if(personObj instanceof JSONObject){
									JSONObject jsonObj = (JSONObject)personObj;
									persons.add(jsonObj);
								} else {
									persons = (JSONArray)personObj;
								}
								
								JSONObject personObject = (JSONObject)persons.get(0);
								taskObject = (JSONObject)personObject.get("taskinfo");
								
								//전달 처리
								if(taskObject.get("kind").toString().equalsIgnoreCase("conveyance")){
									JSONObject forwardedPerson = (JSONObject)persons.get(persons.size()-1);
									taskObject = (JSONObject)forwardedPerson.get("taskinfo");
								}
								
							} else if(ouObject.containsKey("role")) {
								JSONObject role = new JSONObject();
								role = (JSONObject)ouObject.get("role");
								taskObject = (JSONObject)role.get("taskinfo");
							} else {
								taskObject = (JSONObject)ouObject.get("taskinfo");
							}
							
							if(taskObject.get("status").toString().equalsIgnoreCase("pending") && taskObject.get("kind").toString().equalsIgnoreCase("confirm")){
								//ret.add(s);
								badd = true;
							}
						}
						if(badd) ret.add(s);
					}
				}
			    		      
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static String getOuAttr(JSONObject appvLine, int divisionIndex, int stepIndex, String attrName) throws Exception {
		String ret = "";
		JSONObject root = (JSONObject)appvLine.get("steps");
		Object divisionObj = root.get("division");
		JSONArray divisions = new JSONArray();
		if(divisionObj instanceof JSONObject){
			JSONObject divisionJsonObj = (JSONObject)divisionObj;
			divisions.add(divisionJsonObj);
		} else {
			divisions = (JSONArray)divisionObj;
		}
		JSONObject division = (JSONObject)divisions.get(divisionIndex);// send unit.
		Object stepO = division.get("step");
		JSONArray stepArray = new JSONArray();
		if(stepO instanceof JSONObject){
			JSONObject stepJsonObj = (JSONObject)stepO;
			stepArray.add(stepJsonObj);
		} else {
			stepArray = (JSONArray)stepO;
		}
		JSONObject stepObject = (JSONObject)stepArray.get(stepIndex);
		Object ouObj = stepObject.get("ou");
		if(ouObj instanceof JSONObject){
			JSONObject ouObject = (JSONObject)stepObject.get("ou");
			
			ret = String.valueOf(ouObject.get(attrName));
		}
		return ret;
	}
	
	// 결재선 의견유무 및 actionComment 유무로 의견 업데이트여부 체크
	// 리턴은 "true","false","" 로 빈값이면 업데이트 안함
	@SuppressWarnings("unchecked")
	public static String chkIsCommentUpdate(JSONObject apvLineObj, String pActionComment, String pDescIsComment) throws Exception {
		String rtnIsComment = "";
		
		if(pActionComment.equalsIgnoreCase("true")) { // actionComment가 있으면 무조건 업데이트 , 부서내반송시 엔진(pDescIsComment)값 변경 없이 processdescription 테이블만 변경한채 의견이 들어오므로 변경여부 체크없이 무조건 업데이트 
			rtnIsComment = "true";
		}else {
			String isComment = CoviFlowApprovalLineHelper.chkIsComment(apvLineObj, "false");
			if(StringUtils.isBlank(isComment)) {
				// 빈값인경우 스킵
			}
			else if(isComment.equalsIgnoreCase("false") && pDescIsComment.equalsIgnoreCase("Y")) { // 기존 의견유무가 Y이고 결재선에 의견이 없어진경우 N으로변경
				rtnIsComment = "false";
			}else if(isComment.equalsIgnoreCase("true") && (StringUtils.isBlank(pDescIsComment) || pDescIsComment.equalsIgnoreCase("N"))) { // 기존 의견유무가 N이고 결재선에 의견이 생긴경우 Y로변경
				rtnIsComment = "true";
			}
		}
		
		return rtnIsComment;
	}
	
	// 결재선에 의견이 있는지 체크, 재귀함수로 기본호출은 (결재선object , "false")
	// return : 의견있으면 "true" , 없으면 "false" , 오류발생시 "" (사용하는쪽에서 리턴이 ""이면 이후처리 스킵 필요)
	@SuppressWarnings("unchecked")
	public static String chkIsComment(JSONObject appvLineObj, String pIsComment) throws Exception {
		
//		if(appvLineObj == null || appvLineObj.isEmpty()) {
//			return "";
//		}else if(appvLineObj.toString().indexOf("comment") > -1){
//			return true;
//		}else {
//			return false;
//		}
		
		String isComment = pIsComment;	
		
		try {
			if(StringUtils.isBlank(isComment) || isComment.equalsIgnoreCase("true")) return isComment;
			else {
				if(appvLineObj != null && appvLineObj.size() > 0) {
					for(Object key : appvLineObj.keySet()){
						String sKey = key.toString();
						Object val = appvLineObj.get(sKey);
						if(val == null || sKey.equalsIgnoreCase("ccinfo")) { // 참조는 의견체크에서 제외
							
						}else if(val instanceof JSONObject) {
							JSONObject json = (JSONObject)val;
							if((json.containsKey("comment") && !((JSONObject)json.get("comment")).get("#text").toString().equals(""))
									&& (!json.containsKey("visible") || !(json.get("visible").toString()).equalsIgnoreCase("n"))) { // 의견이 있고 visible이 n이 아닌경우만
								isComment = "true";
								break;
							}else {
								isComment = chkIsComment(json, isComment);
							}
							if(StringUtils.isBlank(isComment) || isComment.equalsIgnoreCase("true")) break;
						}else if(val instanceof JSONArray) {
							JSONArray jArr = (JSONArray)val;
							for(int i = 0; i < jArr.size(); i++) {
								JSONObject json = (JSONObject)jArr.get(i);
								if(json.containsKey("comment") && !((JSONObject)json.get("comment")).get("#text").toString().equals("")) {
									isComment = "true";
									break;
								}else {
									isComment = chkIsComment(json, isComment);
								}
								if(StringUtils.isBlank(isComment) || isComment.equalsIgnoreCase("true")) break;
							}
							if(StringUtils.isBlank(isComment) || isComment.equalsIgnoreCase("true")) break;
						}
					}
					
				}
			}
		}catch(Exception e){
			isComment = "";
		}
		
		return isComment;
	}
	
}
