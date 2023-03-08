package coviflow.converter;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.bpmn.converter.export.FailedJobRetryCountExport;
import org.activiti.bpmn.converter.export.MultiInstanceExport;
import org.activiti.bpmn.converter.util.BpmnXMLUtil;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.apache.commons.lang3.StringUtils;

public class ServiceTaskXMLConverter extends org.activiti.bpmn.converter.ServiceTaskXMLConverter {

	@Override
	public void convertToXML(XMLStreamWriter xtw, BaseElement baseElement, BpmnModel model) throws Exception {
		xtw.writeStartElement(getXMLElementName());
		boolean didWriteExtensionStartElement = false;
		writeDefaultAttribute(ATTRIBUTE_ID, baseElement.getId(), xtw);
		if (baseElement instanceof FlowElement) {
			writeDefaultAttribute(ATTRIBUTE_NAME, ((FlowElement) baseElement).getName(), xtw);
		}

		if (baseElement instanceof FlowNode) {
			final FlowNode flowNode = (FlowNode) baseElement;

			// Modified. hgsong 2020/12/01
			if (flowNode.isAsynchronous()) {
				writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_ASYNCHRONOUS, ATTRIBUTE_VALUE_TRUE, xtw);
			}
			if (flowNode.isNotExclusive()) {
				writeQualifiedAttribute(ATTRIBUTE_ACTIVITY_EXCLUSIVE, ATTRIBUTE_VALUE_FALSE, xtw);
			}

			if (baseElement instanceof Activity) {
				final Activity activity = (Activity) baseElement;
				if (activity.isForCompensation()) {
					writeDefaultAttribute(ATTRIBUTE_ACTIVITY_ISFORCOMPENSATION, ATTRIBUTE_VALUE_TRUE, xtw);
				}
				if (StringUtils.isNotEmpty(activity.getDefaultFlow())) {
					FlowElement defaultFlowElement = model.getFlowElement(activity.getDefaultFlow());
					if (defaultFlowElement instanceof SequenceFlow) {
						writeDefaultAttribute(ATTRIBUTE_DEFAULT, activity.getDefaultFlow(), xtw);
					}
				}
			}

			if (baseElement instanceof Gateway) {
				final Gateway gateway = (Gateway) baseElement;
				if (StringUtils.isNotEmpty(gateway.getDefaultFlow())) {
					FlowElement defaultFlowElement = model.getFlowElement(gateway.getDefaultFlow());
					if (defaultFlowElement instanceof SequenceFlow) {
						writeDefaultAttribute(ATTRIBUTE_DEFAULT, gateway.getDefaultFlow(), xtw);
					}
				}
			}
		}

		writeAdditionalAttributes(baseElement, model, xtw);

		if (baseElement instanceof FlowElement) {
			final FlowElement flowElement = (FlowElement) baseElement;
			if (StringUtils.isNotEmpty(flowElement.getDocumentation())) {

				xtw.writeStartElement(ELEMENT_DOCUMENTATION);
				xtw.writeCharacters(flowElement.getDocumentation());
				xtw.writeEndElement();
			}
		}

		didWriteExtensionStartElement = writeExtensionChildElements(baseElement, didWriteExtensionStartElement, xtw);
		didWriteExtensionStartElement = writeListeners(baseElement, didWriteExtensionStartElement, xtw);
		didWriteExtensionStartElement = BpmnXMLUtil.writeExtensionElements(baseElement, didWriteExtensionStartElement, model.getNamespaces(), xtw);
		if (baseElement instanceof Activity) {
			final Activity activity = (Activity) baseElement;
			FailedJobRetryCountExport.writeFailedJobRetryCount(activity, xtw);

		}

		if (didWriteExtensionStartElement) {
			xtw.writeEndElement();
		}

		if (baseElement instanceof Activity) {
			final Activity activity = (Activity) baseElement;
			MultiInstanceExport.writeMultiInstance(activity, xtw);

		}

		writeAdditionalChildElements(baseElement, model, xtw);

		xtw.writeEndElement();
	}
}
