package org.activiti.engine.delegate;

/**
 * Callback interface to be notified of execution events like starting a process instance, ending an activity instance or taking a transition.
 * 
 * @author Tom Baeyens
 * @author Joram Barrez
 * @author Yvo Swillens
 */
public interface ExecutionListener {

	void notify(DelegateExecution execution) throws Exception;
}
