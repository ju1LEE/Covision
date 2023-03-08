package egovframework.covision.coviflow.util;

public class ExternalAssigneeManager {
	
	/*
	 * coviflow쪽 performer관리와는 별개로
	 * activiti쪽 assignee에 대한 관리가 필요함
	 * 
	 * 1. activiti에 externalGroup을 연결하는 방안 - The activiti-ldap source code 참고
	 * (https://forums.activiti.org/content/custom-user-management-activiti-external-database)
	 * 2. 연결하지 않고, 이중으로 관리하되 Rest API를 통해 activiti내의 assignee관리를 할 수 있게 해 주는 방안
	 * 
	 * 1번 방안의 경우 관리하는 시나리오는 다음과 같음(실제 구현은 개발단계에서..)
	 * 1.1. 결재선 parsing
	 * 1.2. 단일 사용자는 UserEntityManager를 이용
	 * 1.3. 그룹(Virtual role / Job function)은 GroupEntityManager를 이용
	 * 
	 * 2번 방안의 경우
	 * 2.1. User를 이중으로 관리 할 수는 없음.
	 * 2.2. 이중으로 관리한다면 Group 정도 가능 할 것으로 예상됨.
	 * 
	 * 
	 * */

}
