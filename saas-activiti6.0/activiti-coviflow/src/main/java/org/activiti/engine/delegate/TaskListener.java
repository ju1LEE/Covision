package org.activiti.engine.delegate;

public interface TaskListener {
	void notify(DelegateTask delegateTask) throws Exception;
}
