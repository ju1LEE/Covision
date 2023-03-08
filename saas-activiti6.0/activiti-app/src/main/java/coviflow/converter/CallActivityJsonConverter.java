package coviflow.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverterUtil;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class CallActivityJsonConverter extends org.activiti.editor.language.json.converter.CallActivityJsonConverter {
	
	public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

		fillJsonTypes(convertersToBpmnMap);
		fillBpmnTypes(convertersToJsonMap);
	}

	public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
		convertersToBpmnMap.put(STENCIL_CALL_ACTIVITY, CallActivityJsonConverter.class);
	}

	public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
		convertersToJsonMap.put(CallActivity.class, CallActivityJsonConverter.class);
	}

	protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
		CallActivity callActivity = new CallActivity();
		if (StringUtils.isNotEmpty(getPropertyValueAsString(PROPERTY_CALLACTIVITY_CALLEDELEMENT, elementNode))) {
			callActivity.setCalledElement(getPropertyValueAsString(PROPERTY_CALLACTIVITY_CALLEDELEMENT, elementNode));
		}

		callActivity.getInParameters().addAll(convertToIOParameters(PROPERTY_CALLACTIVITY_IN, "inParameters", elementNode));
		callActivity.getOutParameters().addAll(convertToIOParameters(PROPERTY_CALLACTIVITY_OUT, "outParameters", elementNode));

		return callActivity;
	}

	private List<IOParameter> convertToIOParameters(String propertyName, String valueName, JsonNode elementNode) {
		List<IOParameter> ioParameters = new ArrayList<IOParameter>();
		JsonNode parametersNode = getProperty(propertyName, elementNode);
		if (parametersNode != null) {
			parametersNode = BpmnJsonConverterUtil.validateIfNodeIsTextual(parametersNode);
			JsonNode itemsArrayNode = parametersNode.get(valueName);
			if (itemsArrayNode != null) {
				for (JsonNode itemNode : itemsArrayNode) {
					JsonNode sourceNode = itemNode.get(PROPERTY_IOPARAMETER_SOURCE);
					JsonNode sourceExpressionNode = itemNode.get(PROPERTY_IOPARAMETER_SOURCE_EXPRESSION);
					if ((sourceNode != null && StringUtils.isNotEmpty(sourceNode.asText())) || (sourceExpressionNode != null && StringUtils.isNotEmpty(sourceExpressionNode.asText()))) {

						IOParameter parameter = new IOParameter();
						if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_IOPARAMETER_SOURCE, itemNode))) {
							parameter.setSource(getValueAsString(PROPERTY_IOPARAMETER_SOURCE, itemNode));
						}
						// distinct settings.
						if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_IOPARAMETER_SOURCE_EXPRESSION, itemNode))) {
							parameter.setSourceExpression(getValueAsString(PROPERTY_IOPARAMETER_SOURCE_EXPRESSION, itemNode));
						}
						if (StringUtils.isNotEmpty(getValueAsString(PROPERTY_IOPARAMETER_TARGET, itemNode))) {
							parameter.setTarget(getValueAsString(PROPERTY_IOPARAMETER_TARGET, itemNode));
						}
						ioParameters.add(parameter);
					}
				}
			}
		}
		return ioParameters;
	}
	
	static class CallActivityBpmnJsonConverter extends BpmnJsonConverter {
		public static void fillTypes() {
			CallActivityJsonConverter.fillTypes(convertersToBpmnMap, convertersToJsonMap);
		}
	}
}
