package coviflow.converter;

import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.converter.child.BaseChildElementParser;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.apache.commons.lang3.StringUtils;

public class CallActivityXMLConverter extends org.activiti.bpmn.converter.CallActivityXMLConverter {

	public CallActivityXMLConverter() {
		// Override previous ParameterParser because parser setting when Construct SuperClass.
		InParameterParser inParameterParser = new InParameterParser();
		childParserMap.put(inParameterParser.getElementName(), inParameterParser);
		OutParameterParser outParameterParser = new OutParameterParser();
		childParserMap.put(outParameterParser.getElementName(), outParameterParser);
	}

	public class InParameterParser extends BaseChildElementParser {

		public String getElementName() {
			return ELEMENT_CALL_ACTIVITY_IN_PARAMETERS;
		}

		public void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception {
			String source = xtr.getAttributeValue(null, ATTRIBUTE_IOPARAMETER_SOURCE);
			String sourceExpression = xtr.getAttributeValue(null, ATTRIBUTE_IOPARAMETER_SOURCE_EXPRESSION);
			String target = xtr.getAttributeValue(null, ATTRIBUTE_IOPARAMETER_TARGET);
			if((StringUtils.isNotEmpty(source) || StringUtils.isNotEmpty(sourceExpression)) && StringUtils.isNotEmpty(target)) {

				IOParameter parameter = new IOParameter();
				if(StringUtils.isNotEmpty(sourceExpression)) {
					parameter.setSourceExpression(sourceExpression);
				}
				if(StringUtils.isNotEmpty(source)) {
					parameter.setSource(source);
				}

				parameter.setTarget(target);

				((CallActivity) parentElement).getInParameters().add(parameter);
			}
		}
	}

	public class OutParameterParser extends BaseChildElementParser {

		public String getElementName() {
			return ELEMENT_CALL_ACTIVITY_OUT_PARAMETERS;
		}

		public void parseChildElement(XMLStreamReader xtr, BaseElement parentElement, BpmnModel model) throws Exception {
			String source = xtr.getAttributeValue(null, ATTRIBUTE_IOPARAMETER_SOURCE);
			String sourceExpression = xtr.getAttributeValue(null, ATTRIBUTE_IOPARAMETER_SOURCE_EXPRESSION);
			String target = xtr.getAttributeValue(null, ATTRIBUTE_IOPARAMETER_TARGET);
			if((StringUtils.isNotEmpty(source) || StringUtils.isNotEmpty(sourceExpression)) && StringUtils.isNotEmpty(target)) {

				IOParameter parameter = new IOParameter();
				if(StringUtils.isNotEmpty(sourceExpression)) {
					parameter.setSourceExpression(sourceExpression);
				}
				if(StringUtils.isNotEmpty(source)) {
					parameter.setSource(source);
				}

				parameter.setTarget(target);

				((CallActivity) parentElement).getOutParameters().add(parameter);
			}
		}
	}
}
