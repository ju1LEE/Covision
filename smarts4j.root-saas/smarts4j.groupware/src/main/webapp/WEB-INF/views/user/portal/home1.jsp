<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
	#jb-container {
		width: 1180px;
		margin: 0px auto;
		padding: 20px;
		border: 1px solid #bcbcbc;
	}
	
	#jb-header {
		padding: 20px;
		margin-bottom: 20px;
		border: 1px solid #bcbcbc;
	}
	
	#jb-sidebar-left {
		width: 200px;
		padding: 20px;
		margin-right: 20px;
		margin-bottom: 20px;
		float: left;
		border: 1px solid #bcbcbc;
	}
	
	#jb-content {
		width: 695px;
		padding: 20px;
		margin-bottom: 20px;
		float: left;
		border: 1px solid #bcbcbc;
	}
	
	#jb-sidebar-right {
		width: 200px;
		padding: 20px;
		margin-bottom: 20px;
		float: right;
		border: 1px solid #bcbcbc;
	}
	
	#jb-footer {
		clear: both;
		padding: 20px;
		border: 1px solid #bcbcbc;
	}
</style>

<script type="text/javascript">
	var webSocket;
	var message, message1, message2;
	var $output;

	$(document).ready(function (){
		$('#dvTime').html(new Date().toLocaleString());
		
		webSocket = new WebSocket("ws://localhost:8081/groupware/ws");
		message = document.getElementById("textID");
		message1 = document.getElementById("textID1");
		message2 = document.getElementById("textID2");
		$output = $('#dvOutput');
		
		webSocket.onopen = function(message){ wsOpen(message);};
		webSocket.onmessage = function(message){ wsGetMessage(message);};
		webSocket.onclose = function(message){ wsClose(message);};
		webSocket.onerror = function(message){ wsError(message);};
		
	});
	
	function wsOpen(message){
		$output.append("<p>Connected ... \n</p>");
	}
	
	function send_message(){
		webSocket.send(message.value);
		$output.append("<p>Message sended to the server : " + message.value + "\n</p>");
		message.value = "";
	}
	
	function send_message1(){
		$.ajax({
	        type : "POST",
	        url : "/groupware/sendmessage.do",
	        data : { message : message1.value},
	        success : function(res) {
	        	
	        },
	        error : function(e) {
	            alert('Error : ' + e);
	        }
	    });
	}
	
	function wsCloseConnection(){
		webSocket.close();
	}
	
	function wsGetMessage(message){
		$output.append("<p>Message received from to the server : " + message.data + "\n</p>");
		//$('#txtUnreadCnt').html(message.data);
		document.getElementById("txtUnreadCnt").innerHTML = message.data;
	}
	
	function wsClose(message){
		$output.append("<p>Disconnect ... \n</p>");
	}

	function wserror(message){
		$output.append("<p>Error ... \n</p>");
	}
	
	/* ajax long polling */
	function send_message2(){
		poll(); 
	}
	
	function poll(){
	    setTimeout(function(){
	        $.ajax({ 
	        	type : "POST",
				url: "longpolling.do",
				data : { message : message2.value},
				success: function(data){ 
				//Update your dashboard gauge salesGauge.setValue(data.value);
					$output.append("<p>Message received from to the server : " + data.message + "\n</p>");
				}, 
				dataType: "json", 
				complete: poll, 
				timeout: 10000 //10 sec 
	        });
	    }, 30000);
	}
	
</script>

${layout}
