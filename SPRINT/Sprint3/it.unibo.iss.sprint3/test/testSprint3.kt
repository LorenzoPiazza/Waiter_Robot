package test
 	//"tcp://mqtt.eclipse.org:1883"
	//mqtt.eclipse.org
	//tcp://test.mosquitto.org
	//mqtt.fluux.io
	//"tcp://broker.hivemq.com" 

import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.MqttUtils
import itunibo.planner.*
 
class testSprint3 {
	
var waiterLogic      : ActorBasic? = null
var	smartbell		 : ActorBasic? = null
var	waiterWalker	 : ActorBasic? = null
val mqttTest   	      = MqttUtils("test") 
val initDelayTime     = 4000L   // 
val useMqttInTest 	  = false
val mqttbrokerAddr    = "tcp://localhost" 
		
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	@Before
	fun systemSetUp() { 
   		kotlin.concurrent.thread(start = true) {
   			it.unibo.ctxwaiter.main()
			println("testSprint3 systemSetUp done")
   			if( useMqttInTest ){
				 while( ! mqttTest.connectDone() ){
					  println( "	attempting MQTT-conn to ${mqttbrokerAddr}  for the test unit ... " )
					  Thread.sleep(1000)
					  mqttTest.connect("test_nat", mqttbrokerAddr )					 
				 }
 			}	
 	}

}	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	@After
	fun terminate() {
		println("testSprint3 terminated ")
	}

/*UTILITIES*/		 
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forwardToSmartbell(msgId: String, payload:String){
		println(" --- forwardToSmartbell --- $msgId:$payload")
		if( smartbell != null )  MsgUtil.sendMsg( "test",msgId, payload, smartbell!!  )
	}
			
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forwardToWaiterLogic(msgId: String, payload:String){
		println(" --- forwardTowaiterLogic --- $msgId:$payload")
		if( waiterLogic != null )  MsgUtil.sendMsg( "test",msgId, payload, waiterLogic!!  )
	}
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun requestToWaiterLogic(msgId: String, payload:String){
		if( waiterLogic!= null ){
			val msg = MsgUtil.buildRequest("test",msgId, payload,waiterLogic!!.name)
			MsgUtil.sendMsg( msg, waiterLogic!!  )		
		}  
	}
	
	fun checkResourceWaiterLogic(value: String){		
		if( waiterLogic != null ){
			println(" --- checkResource --- ${waiterLogic!!.geResourceRep()} value=$value")
			assertTrue( waiterLogic!!.geResourceRep() == value)
		}  
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testAccept(){
		println("=========== testAccept =========== ")
		var N = 0
		var S = ""
		delay(5000)		//Time to set up
		waiterLogic!!.solve("numfreetables(N)","")
		N = waiterLogic!!.getCurSol("N").toString().toInt()
		assertTrue(N==2)
		delay(2000)
		//MANDO UNA RICHIESTA DI INGRESSO AL WAITERLOGIC
		requestToWaiterLogic("enterRequest", "enterRequest(1)")	
		delay(3000)
	
		//CONTROLLO LO STATO DEL WAITERLOGIC
		checkResourceWaiterLogic("serving_client(1)")
		while(!S.equals("busy(1)")){
			waiterLogic!!.solve("teatable(1, S)", "")
			S = waiterLogic!!.getCurSol("S").toString()
			delay(1000)
		}
	
		//CONTROLLO CHE UN TAVOLO SIA OCCUPATO CON L'ID DEL CLIENTE
		assertTrue( S.equals("busy(1)") )
		//CONTROLLO CHE I TAVOLI DISPONIBILI ORA SIANO 1
		waiterLogic!!.solve("numfreetables(N)","")								
		N = waiterLogic!!.getCurSol("N").toString().toInt()
		assertTrue(N==1)										
	}


@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
suspend fun testReachEntranceDoor(){
		println("=========== testReachEntranceDoor =========== ")
		while(!itunibo.planner.plannerUtil.atPos(1,4) ){
			delay(1000)
		}
		checkResourceWaiterLogic("serving_client(1)")
}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
suspend fun testConvoyToTable(){
		println("=========== testConvoyToTable =========== ")
		while(!itunibo.planner.plannerUtil.atPos(2,2) ){
			delay(1000)
		}
		delay(500)
		checkResourceWaiterLogic("rest(2,2)")
}	
	
	
suspend fun simulateRegularClient(){
	//"HIT THE SMARTBELL" to simulate a client enter-request
	forwardToSmartbell("ring", "ring(36.8)" )
}

suspend fun simulateSickClient(){
	forwardToSmartbell("ring", "ring(38)" )	
}		

	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testScenario4(){
		println("=========== testScenario4 (2 table busy)=========== ")
		delay(4000)		//Time to set up
		//TODO: set teatable(1, busy(1)) and teatable(2,busy(2)) on tearoomkb.pl
	
		// MANDO UNA RICHIESTA DI INGRESSO AL WAITERLOGIC
		requestToWaiterLogic("enterRequest", "enterRequest(3)")	
		delay(3000)
		
		/*---Verrà eseguito il task inform---*/
		// CONTROLLO LO STATO DEL WAITERLOGIC
		checkResourceWaiterLogic("serving_client(3)")
		//... E CHE LA SUA POSIZIONE NON CAMBI, supposto si trovi in pos(0,0)
		for(i in 0..5){
			assertTrue(itunibo.planner.plannerUtil.atPos(0,0))
			delay(1000)
		}									
	}		
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
@Test
	fun testSprint3(){
	 	runBlocking{
			while( waiterLogic == null || waiterWalker == null || smartbell == null){
				println("Trying to start the actors...")	
				delay(initDelayTime)  //time for system to start
				waiterWalker = it.unibo.kactor.sysUtil.getActor("waiterwalker")				
				waiterLogic  = it.unibo.kactor.sysUtil.getActor("waiterlogic")
				smartbell    = it.unibo.kactor.sysUtil.getActor("smartbell")						
			}
			
			
			 
			testAccept()
			testReachEntranceDoor()
			testConvoyToTable()
//			testTake()
//			testServe()
//			testCollect()
//			testConvoyToExit()
//			testClean()
//			testRest()	
		}
	 	
	 	println("testSprint3 BYE  ")
	}
}