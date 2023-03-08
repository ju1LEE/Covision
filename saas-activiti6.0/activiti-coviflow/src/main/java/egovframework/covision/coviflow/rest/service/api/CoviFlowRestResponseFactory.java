package egovframework.covision.coviflow.rest.service.api;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.task.Comment;
import org.activiti.rest.service.api.RestUrlBuilder;
import org.activiti.rest.service.api.RestUrls;
import org.activiti.rest.service.api.engine.CommentResponse;
import org.activiti.rest.service.api.engine.variable.BooleanRestVariableConverter;
import org.activiti.rest.service.api.engine.variable.DateRestVariableConverter;
import org.activiti.rest.service.api.engine.variable.DoubleRestVariableConverter;
import org.activiti.rest.service.api.engine.variable.IntegerRestVariableConverter;
import org.activiti.rest.service.api.engine.variable.LongRestVariableConverter;
import org.activiti.rest.service.api.engine.variable.RestVariableConverter;
import org.activiti.rest.service.api.engine.variable.ShortRestVariableConverter;
import org.activiti.rest.service.api.engine.variable.StringRestVariableConverter;

public class CoviFlowRestResponseFactory {

	protected List<RestVariableConverter> variableConverters = new ArrayList<RestVariableConverter>();

	public CoviFlowRestResponseFactory(){
		initializeVariableConverters();		
	}
	
	public static final String[] TEST = {"test0", "test1"};
	
	public CommentResponse createTest(Comment comment, RestUrlBuilder urlBuilder) {
	    CommentResponse result = new CommentResponse();
	    result.setAuthor(comment.getUserId());
	    result.setMessage(comment.getFullMessage());
	    result.setId(comment.getId());
	    result.setTime(comment.getTime());
	    result.setTaskId(comment.getTaskId());
	    result.setProcessInstanceId(comment.getProcessInstanceId());
	    
	    if (comment.getTaskId() != null) {
	      result.setTaskUrl(urlBuilder.buildUrl(TEST, comment.getTaskId(), comment.getId()));
	    }
	    
	    if (comment.getProcessInstanceId() != null) {
	      result.setProcessInstanceUrl(urlBuilder.buildUrl(TEST, comment.getProcessInstanceId(), comment.getId()));
	    }
	    
	    return result;
	  }
	
	
	/**
	 * @return list of {@link RestVariableConverter} which are used by this
	 *         factory. Additional converters can be added and existing ones
	 *         replaced ore removed.
	 */
	public List<RestVariableConverter> getVariableConverters() {
		return variableConverters;
	}

	/**
	 * Called once when the converters need to be initialized. Override of
	 * custom conversion needs to be done between java and rest.
	 */
	protected void initializeVariableConverters() {
		variableConverters.add(new StringRestVariableConverter());
		variableConverters.add(new IntegerRestVariableConverter());
		variableConverters.add(new LongRestVariableConverter());
		variableConverters.add(new ShortRestVariableConverter());
		variableConverters.add(new DoubleRestVariableConverter());
		variableConverters.add(new BooleanRestVariableConverter());
		variableConverters.add(new DateRestVariableConverter());
	}

	protected String formatUrl(String serverRootUrl, String[] fragments,
			Object... arguments) {
		StringBuilder urlBuilder = new StringBuilder(serverRootUrl);
		for (String urlFragment : fragments) {
			urlBuilder.append("/");
			urlBuilder.append(MessageFormat.format(urlFragment, arguments));
		}
		return urlBuilder.toString();
	}

	protected RestUrlBuilder createUrlBuilder() {
		return RestUrlBuilder.fromCurrentRequest();
	}
}
