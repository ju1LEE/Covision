package coviflow.converter;

import org.activiti.bpmn.converter.BpmnXMLConverter;

import coviflow.converter.CallActivityJsonConverter.CallActivityBpmnJsonConverter;

public class CoviConverterManager {

    private static class InstanceHolder {
        private static final CoviConverterManager INSTANCE = new CoviConverterManager();
    }
    public static CoviConverterManager getInstance() {
        return InstanceHolder.INSTANCE;
    }
    
    
	public void init() {
		// XML Converter
		BpmnXMLConverter.addConverter(new CallActivityXMLConverter());
		BpmnXMLConverter.addConverter(new UserTaskXMLConverter());
		BpmnXMLConverter.addConverter(new ServiceTaskXMLConverter());

		// JSON Converter
		CallActivityBpmnJsonConverter.fillTypes();
	}

}
