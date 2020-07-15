package connQak

import org.json.JSONObject
import java.io.File
import java.net.InetAddress
//import java.nio.charset.Charset
//import org.apache.commons.io.Charsets
  

object configurator{
//Page
	@JvmStatic public var pageTemplate	= "tearoomGuiSocket"

//MQTT broker	
//	@JvmStatic var mqtthostAddr    	= "broker.hivemq.com"
	@JvmStatic var mqtthostAddr    	= "localhost"
	@JvmStatic var mqttport    		= "1883"

	
//Tearoom application
	@JvmStatic var hostAddr   	    = "localhost";  //"192.168.1.5";		
	@JvmStatic var port    			= "8050";
	@JvmStatic var qakwaiterlogic   = "waiterlogic";
	@JvmStatic var qaksmartbell   	= "smartbell";
	@JvmStatic var qakclient_sim_1 	= "client_simulator1";
	@JvmStatic var qakclient_sim_2 	= "client_simulator2";
	@JvmStatic var ctxqadest 		= "ctxwaiter";
	
	
	@JvmStatic	//to be used by Java
	fun configure(){
		try{
			val configfile =   File("mypageConfig.json")
			val config     =   configfile.readText()	//charset: Charset = Charsets.UTF_8
			//println( "		--- configurator | config=$config" )
			val jsonObject	=  JSONObject( config )			
			pageTemplate 	=  jsonObject.getString("page") 
			hostAddr    	=  jsonObject.getString("host") 
			port    		=  jsonObject.getString("port")
			qakwaiterlogic  =  jsonObject.getString("qakwaiterlogic")
			qaksmartbell    =  jsonObject.getString("qaksmartbell")
			qakclient_sim_1 =  jsonObject.getString("qakclient_sim_1")
			qakclient_sim_2 =  jsonObject.getString("qakclient_sim_2")
			ctxqadest		=  jsonObject.getString("ctxqadest")
			System.out.println("System IP Address : " + (InetAddress.getLocalHost().getHostAddress()).trim()); 
			System.out.println( "		--- configurator | configfile path=${configfile.getPath()} pageTemplate=$pageTemplate hostAddr=$hostAddr port=$port" )
		}catch(e:Exception){
			System.out.println( " &&& SORRY mypageConfig.json NOT FOUND ")
			pageTemplate 	= "tearoomGuiSocket"   
			hostAddr    	= "localhost"        
			port    		= "8050"
			qakwaiterlogic  = "waiterlogic"              
			qaksmartbell    = "smartbell"
			qakclient_sim_1 = "client_simulator1"
			qakclient_sim_2 = "client_simulator2";        
			ctxqadest		= "ctxwaiter"     			   
			System.out.println( "		--- configurator | pageTemplate=$pageTemplate hostAddr=$hostAddr port=$port"  )
	}
		
	}//configure
	
	
}

