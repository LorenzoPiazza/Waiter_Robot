package connQak

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.coap.MediaTypeRegistry
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ApplMessage
import org.eclipse.californium.core.CoapResponse
 

class connQakCoap( )  {

var waiterlogic	  : CoapClient = CoapClient()
var smartbell  	  : CoapClient = CoapClient()
var client_sim_1  : CoapClient = CoapClient()
var client_sim_2  : CoapClient = CoapClient()
	
	 fun createConnection(  ){
		 //Create connection for waiterlogic
		  	val urlwaiterlogic = "coap://${configurator.hostAddr}:${configurator.port}/${configurator.ctxqadest}/${configurator.qakwaiterlogic}"
 			System.out.println("connQakCoap | url=${urlwaiterlogic.toString()}")
 			//uriStr: coap://192.168.1.22:8060/ctxdomains/waiter
			//client = CoapClient(  )
		    waiterlogic.uri = urlwaiterlogic.toString()
			waiterlogic.setTimeout( 1000L )
 		    val respGetwaiterlogic  = waiterlogic.get( ) //CoapResponse
			if( respGetwaiterlogic != null )
				System.out.println("connQakCoap | createConnection doing  get | CODE=  ${respGetwaiterlogic.code} content=${respGetwaiterlogic.getResponseText()}")
			else
				System.out.println("connQakCoap | url=  ${urlwaiterlogic} FAILURE")
		 
		 //Create connection for smartbell
		  	val urlsmartbell = "coap://${configurator.hostAddr}:${configurator.port}/${configurator.ctxqadest}/${configurator.qaksmartbell}"
 			System.out.println("connQakCoap | url=${urlsmartbell.toString()}")
			//client = CoapClient(  )
		    smartbell.uri = urlsmartbell.toString()
			smartbell.setTimeout( 1000L )
 		    val respGetSmartbell  = smartbell.get( ) //CoapResponse
			if( respGetSmartbell != null )
				System.out.println("connQakCoap | createConnection doing  get | CODE=  ${respGetSmartbell.code} content=${respGetSmartbell.getResponseText()}")
			else
				System.out.println("connQakCoap | url=  ${urlsmartbell} FAILURE")
		 
		 	//Create connection for client_sim_1
		  	val urlclient_sim_1 = "coap://${configurator.hostAddr}:${configurator.port}/${configurator.ctxqadest}/${configurator.qakclient_sim_1}"
 			System.out.println("connQakCoap | url=${urlclient_sim_1.toString()}")
			//client = CoapClient(  )
		    client_sim_1.uri = urlclient_sim_1.toString()
			client_sim_1.setTimeout( 1000L )
 		    val respGetclient_sim_1  = client_sim_1.get( ) //CoapResponse
			if( respGetclient_sim_1 != null )
				System.out.println("connQakCoap | createConnection doing  get | CODE=  ${respGetclient_sim_1.code} content=${respGetclient_sim_1.getResponseText()}")
			else
				System.out.println("connQakCoap | url=  ${urlclient_sim_1} FAILURE")
		 	
			//Create connection for client_sim_2
		  	val urlclient_sim_2 = "coap://${configurator.hostAddr}:${configurator.port}/${configurator.ctxqadest}/${configurator.qakclient_sim_2}"
 			System.out.println("connQakCoap | url=${urlclient_sim_2.toString()}")
			//client = CoapClient(  )
		    client_sim_2.uri = urlclient_sim_2.toString()
			client_sim_2.setTimeout( 1000L )
 		    val respGetclient_sim_2  = client_sim_2.get( ) //CoapResponse
			if( respGetclient_sim_2 != null )
				System.out.println("connQakCoap | createConnection doing  get | CODE=  ${respGetclient_sim_2.code} content=${respGetclient_sim_2.getResponseText()}")
			else
				System.out.println("connQakCoap | url=  ${urlclient_sim_2} FAILURE")
	}
	
	 fun forward( msg: ApplMessage, client: CoapClient){		
        System.out.println("connQakCoap | PUT forward ${msg}  ")		
        val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
        System.out.println("connQakCoap | RESPONSE CODE=  ${respPut.code}")		
	}
	
	 fun request( msg: ApplMessage, client: CoapClient){
 		val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
  		if( respPut != null ) System.out.println("connQakCoap | answer= ${respPut.getResponseText()}")		
		
	}
	
	 fun emit( msg: ApplMessage, client: CoapClient){
//		val url = "coap://$hostIP:$port/ctx$destName"		//TODO
//		client = CoapClient( url )
        //println("PUT emit url=${url} ")		
         val respPut = client.put(msg.toString(), MediaTypeRegistry.TEXT_PLAIN)
         System.out.println("connQakCoap | PUT emit ${msg} RESPONSE CODE=  ${respPut.code}")		
		
	}
	
	 fun readRep( client: CoapClient  ) : String{
		val respGet : CoapResponse = client.get( )
		return respGet.getResponseText()
	}
}