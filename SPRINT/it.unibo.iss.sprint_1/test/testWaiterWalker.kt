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
 
 

class testWaiterWalker {
	
var waiterWalker      : ActorBasic? = null
val mqttTest   	      = MqttUtils("test") 
val initDelayTime     = 4000L   // 
val useMqttInTest 	  = false
val mqttbrokerAddr    = "tcp://broker.hivemq.com" 
		
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	@Before
	fun systemSetUp() { 
		
   		kotlin.concurrent.thread(start = true) {
			it.unibo.ctxwaiter.main() // MainCtxWaiter()
			println("testWaiterWalker systemSetUp done")
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
		println("testWaiterWalker terminated ")
	}
	 	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forwardToWaiter(msgId: String, payload:String){
		println(" --- forwardToWaiterWalker --- $msgId:$payload")
		if( waiterWalker != null )  MsgUtil.sendMsg( "test",msgId, payload, waiterWalker!!  )
	}
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun requestToWaiter(msgId: String, payload:String){
		if( waiterWalker != null ){
			val msg = MsgUtil.buildRequest("test",msgId, payload,waiterWalker!!.name)
			MsgUtil.sendMsg( msg, waiterWalker!!  )		
		}  
	}
	
	fun checkResource(value: String){		
		if( waiterWalker != null ){
			println(" --- checkResource --- ${waiterWalker!!.geResourceRep()} value=$value")
			assertTrue( waiterWalker!!.geResourceRep() == value)
		}  
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testMoveToFreeCell(){
		println("=========== testMoveToFreeCell =========== ")
 		requestToWaiter( "movetoCell", "movetoCell(4,2)" )		//Assumption: cell (4,2) is free
		delay(1000)
		checkResource("movingTo( cell(4,2) )")
		delay(5000)		//let basic robot phisically move towards the goal
			while(! itunibo.planner.plannerUtil.atPos(4,2))
				delay(1500)			
		checkResource("at( cell(4,2) )")
	}


@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testMovetoDifferentCells(){
		println("=========== testMoveToDifferentCells =========== ")
 		requestToWaiter( "movetoCell", "movetoCell(1,4)" )		//REACH THE ENTRANCE DOOR
		delay(1000)
		checkResource("movingTo( cell(1,4) )")
		delay(5000)		//let basic robot phisically move towards the goal
			while(! itunibo.planner.plannerUtil.atPos(1,4))
				delay(1500)			
		checkResource("at( cell(1,4) )")
	 	
		requestToWaiter( "movetoCell", "movetoCell(2,2)" )		//REACH THE TEATABLE 1
		delay(1000)
		checkResource("movingTo( cell(2,2) )")
		delay(5000)		//let basic robot phisically move towards the goal
			while(! itunibo.planner.plannerUtil.atPos(2,2))
				delay(1500)			
		checkResource("at( cell(2,2) )")
	 	
		requestToWaiter( "movetoCell", "movetoCell(4,0)" )		//REACH SERVICEDESK
		delay(1000)
		checkResource("movingTo( cell(4,0) )")
		delay(5000)		//let basic robot phisically move towards the goal
			while(! itunibo.planner.plannerUtil.atPos(4,0))
				delay(1500)			
		checkResource("at( cell(4,0) )")
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
@Test
	fun testWaiterWalker(){
	 	runBlocking{
			while( waiterWalker == null ){
				delay(initDelayTime)  //time for waiterWalker to start
				waiterWalker = it.unibo.kactor.sysUtil.getActor("waiterwalker")				
			}
			testMovetoDifferentCells()
			testMoveToFreeCell()
		}
	 	println("testWaiterWalker BYE  ")  
	}
}