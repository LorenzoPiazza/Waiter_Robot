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

/*-------------------------------------------UTILITIES-------------------------------------------*/
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
		suspend fun requestToWaiterWalker(msgId: String, payload:String){
		if( waiterWalker!= null ){
			val msg = MsgUtil.buildRequest("test",msgId, payload,waiterWalker!!.name)
			MsgUtil.sendMsg( msg, waiterWalker!!  )
		}
	}

	fun checkResourceWaiterLogic(value: String){
		if( waiterLogic != null ){
			println(" --- checkResource --- ${waiterLogic!!.geResourceRep()} value=$value")
			assertTrue( waiterLogic!!.geResourceRep() == value)
		}
	}


/*-------------------------------------------TEST-------------------------------------------*/
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testScenario3(){
		var S=""
		println("=========== testScenario3 (1 tableclean - 1 table dirty(N))=========== ")
		delay(3000)		//Time to set up
		//set teatable(1, tableclean) and teatable(2,dirty(1)) on tearoomkb.pl
		waiterLogic!!.solve("replaceRule(teatable(1, tableclean), teatable(1,tableclean))", "")
		waiterLogic!!.solve("replaceRule(teatable(2, tableclean), teatable(2,dirty(1)))", "")
		// MANDO UNA RICHIESTA DI INGRESSO AL WAITERLOGIC
		requestToWaiterLogic("enterRequest", "enterRequest(3)")

		/*---Verr� eseguito il task accept---*/
		//CONTROLLO CHE IL TAVOLO SIA ORA OCCUPATO DAL NUOVO CLIENTE
		delay(3000)
		waiterLogic!!.solve("teatable(1, S)","")
		S = waiterLogic!!.getCurSol("S").toString()
		assertTrue(S.equals("busy(3)"))
		//... CHE SI RECHI ALLA ENTRANCE DOOR
		while(!itunibo.planner.plannerUtil.atPos(1,4) ){
			delay(500)
		}
		assertTrue(itunibo.planner.plannerUtil.atPos(1,4) )

		//... E CHE RAGGIUNGA il TAVOLO 1
		while(!itunibo.planner.plannerUtil.atPos(2,2) ){
			delay(500)
		}
		assertTrue(itunibo.planner.plannerUtil.atPos(2,2) )
	}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testScenario4(){
		println("=========== testScenario4 (2 table busy)=========== ")
		delay(4000)		//Time to set up
		//TODO: set teatable(1, busy(1)) and teatable(2,busy(2)) on tearoomkb.pl
		waiterLogic!!.solve("replaceRule(teatable(1, tableclean), teatable(1,busy(1)))", "")
		waiterLogic!!.solve("replaceRule(teatable(2, tableclean), teatable(2,busy(2)))", "")
		// MANDO UNA RICHIESTA DI INGRESSO AL WAITERLOGIC
		requestToWaiterLogic("enterRequest", "enterRequest(3)")
		delay(1000)

		/*---Verr� eseguito il task inform---*/
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
	suspend fun testScenario5_6(){
		var S  =""
		println("=========== testScenario 5 (1 table dirty-1 busy)=========== ")
		delay(4000)		//Time to set up
		//set teatable(1, busy(1)) and teatable(2,dirty(1)) on tearoomkb.pl
		waiterLogic!!.solve("replaceRule(teatable(1, tableclean), teatable(1,dirty(1)))", "")
		waiterLogic!!.solve("replaceRule(teatable(2, tableclean), teatable(2,busy(1)))", "")


	// MANDO UNA RICHIESTA DI INGRESSO AL WAITERLOGIC
		requestToWaiterLogic("enterRequest", "enterRequest(3)")
		delay(1000)

		/*---Verr� eseguito il task acceptButCleanFirst---*/
		// CONTROLLO LO STATO DEL WAITERLOGIC
		checkResourceWaiterLogic("serving_client(3)")
		//... CHE SI RECHI AL TAVOLO 1 PER PULIRLO
		while(!itunibo.planner.plannerUtil.atPos(2,2) ){
			delay(500)
		}
		assertTrue(itunibo.planner.plannerUtil.atPos(2,2) )
		//... CHE IL TAVOLO SIA ORA OCCUPATO DAL NUOVO CLIENTE
		delay(12500)	//Ci vogliono 12000ms per pulire un tavolo in stato dirty(1)
		waiterLogic!!.solve("teatable(1, S)","")
		S = waiterLogic!!.getCurSol("S").toString()
		assertTrue(S.equals("busy(3)"))
		//... E CHE RAGGIUNGA L'ENTRANCE DOOR
		while(!itunibo.planner.plannerUtil.atPos(1,4) ){
			delay(1000)
		}
		assertTrue(itunibo.planner.plannerUtil.atPos(1,4) )
	}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testMoveInTheMap(){
		delay(3000)
		var S  =""
		var	ST =""
		println("=========== test MoveInTheMap=========== ")
		requestToWaiterWalker("movetoCell", "movetoCell(5,4)")
		requestToWaiterWalker("movetoCell", "movetoCell(2,2)")
		requestToWaiterWalker("movetoCell", "movetoCell(5,0)")
		requestToWaiterWalker("movetoCell", "movetoCell(2,4)")
		delay(30000)
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

			/*TEST DEI VARI SCENARI DESCRITTI NEL DOCUMENTO DELLO SPRINT3*/
//			testScenario3()
//			testScenario4()
			testScenario5_6()
//			testMoveInTheMap()
		}
	 	println("testSprint3 BYE  ")
	}
}
