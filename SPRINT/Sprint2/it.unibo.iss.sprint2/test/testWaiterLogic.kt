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
 
class testWaiterLogic {
	
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
			it.unibo.ctxwaiter.main() // MainCtxWaiter()
			println("testwaiterLogic systemSetUp done")
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
		println("testwaiterLogic terminated ")
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
	
	fun checkResourceWaiterWalker(value: String){		
		if( waiterLogic != null ){
			println(" --- checkResource --- ${waiterLogic!!.geResourceRep()} value=$value")
			assertTrue( waiterWalker!!.geResourceRep() == value)
		}  
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testAccept(){
		println("=========== testAccept =========== ")
	}


@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testReach(){
		println("=========== testReach =========== ")

	}
	
	
suspend fun simulateRegularClient(waiterLogic : ActorBasic){
	//"HIT THE SMARTBELL" to simulate a client enter-request
	forwardToSmartbell("ring", "ring(36.8)" )
}

suspend fun simulateSickClient(){
	forwardToSmartbell("ring", "ring(38)" )	
}		
		
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
@Test
	fun testwaiterLogic(){
	 	runBlocking{
			while( waiterLogic == null ){
				delay(initDelayTime)  //time for system to start
				waiterWalker = it.unibo.kactor.sysUtil.getActor("waiterwalker")				
				waiterLogic  = it.unibo.kactor.sysUtil.getActor("waiterlogic")
				smartbell    = it.unibo.kactor.sysUtil.getActor("smartbell")				
			}
			
			
 
			/*testAccept()
			testReach()
			testConvoyToTable()
			testTake()
			testServe()
			testCollect()
			testConvoyToExit()
			testClean()
			testRest()	*/		 	
		}
	 	println("testWaiterLogic BYE  ")  
	}
}