package egovframework.covision.coviflow.javadelegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class Logging implements JavaDelegate {

	  public void execute(DelegateExecution execution) throws Exception {
		  //String var = (String) execution.getVariable("input");
		  //var = var.toUpperCase();
		  //execution.setVariable("input", var);
		  System.out.println("test logging");
		  
	  }
}
