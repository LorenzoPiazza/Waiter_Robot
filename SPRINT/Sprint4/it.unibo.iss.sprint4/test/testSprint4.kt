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

class testSprint4 {

var waiterLogic      : ActorBasic? = null
var	smartbell		 : ActorBasic? = null
var	waiterWalker	 : ActorBasic? = null
var	clientSimulator1 : ActorBasic? = null
var	clientSimulator2 : ActorBasic? = null
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
			println("testSprint4 systemSetUp done")
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
		println("testSprint4 terminated ")
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
	suspend fun forwardToClientSimulator1(msgId: String, payload:String){
		println(" --- forwardToclientSimulator1 --- $msgId:$payload")
		if( clientSimulator1 != null )  MsgUtil.sendMsg( "test", msgId, payload, clientSimulator1!!  )
	}
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun forwardToClientSimulator2(msgId: String, payload:String){
		println(" --- forwardToclientSimulator2 --- $msgId:$payload")
		if( clientSimulator2 != null )  MsgUtil.sendMsg( "test", msgId, payload, clientSimulator2!!  )
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
			println(" --- checkResource --- \n ${waiterLogic!!.geResourceRep()} \n --- expected --- \n $value")
			assertTrue( waiterLogic!!.geResourceRep() == value)
		}
	}
	fun checkResourceSmartbell(value: String){
		if( smartbell != null ){
			println(" --- checkResource --- ${smartbell!!.geResourceRep()} expected=$value")
			assertTrue( smartbell!!.geResourceRep() == value)
		}
	}
	fun checkResourceClientSimulator1(value: String){
		if( clientSimulator1 != null ){
			println(" --- checkResource --- ${clientSimulator1!!.geResourceRep()} expected=$value")
			assertTrue( clientSimulator1!!.geResourceRep() == value)
		}
	}
	fun checkResourceClientSimulator2(value: String){
		if( clientSimulator2 != null ){
			println(" --- checkResource --- ${clientSimulator2!!.geResourceRep()} expected=$value")
			assertTrue( clientSimulator2!!.geResourceRep() == value)
		}
	}


/*-------------------------------------------TEST-------------------------------------------*/
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testSickRing(){
		forwardToSmartbell("ring", "ring(38)")
		delay(4500) //time to check the temperature
		//CONTROLLIAMO CHE LA SMARTBELL RIFIUTI L'INGRESSO DEL CLIENTE
		checkResourceSmartbell("smartbell | Not allowed! Your body temperature is too high!")
		//E LO STATO DELLA TEAROOM
		checkResourceWaiterLogic("waiter(rest(0,0)) \n [teatable1(tableclean),teatable2(tableclean)]")
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun test2ClientsEnterRequest(){
		//ARRIVA IL CLIENTE 1
		forwardToSmartbell("ring", "ring(36)")
		delay(8000) //time to check the temperature, forward the request to the waiter, accept the request
		//CONTROLLIAMO LO STATO DELLA TEAROOM
		checkResourceWaiterLogic("waiter(serving_client(1)) \n [teatable1(busy(1)),teatable2(tableclean)]")
		while(!itunibo.planner.plannerUtil.atPos(2,2) ){ //wait until the waiter reach entrance door and convoy to table 1
			delay(1000)
		}
		delay(1000) 
		checkResourceWaiterLogic("waiter(rest(2,2)) \n [teatable1(busy(1)),teatable2(tableclean)]")
	
		//ARRIVA IL CLIENTE 2
		forwardToSmartbell("ring", "ring(36)")
		delay(8000) //time to check the temperature, forward the request to the waiter, accept the request
		//CONTROLLIAMO LO STATO DELLA TEAROOM
		checkResourceWaiterLogic("waiter(serving_client(2)) \n [teatable1(busy(1)),teatable2(busy(2))]")
		while(!itunibo.planner.plannerUtil.atPos(4,2) ){ //wait until the waiter reach entrance door and convoy client to table 2
			delay(1000)
		}
		delay(1000)
		checkResourceWaiterLogic("waiter(rest(4,2)) \n [teatable1(busy(1)),teatable2(busy(2))]")
	}
	

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testClientSimulation(){
		forwardToClientSimulator1("proceed", "proceed")
		delay(200)
		checkResourceWaiterLogic("waiter(serving_client(1)) \n [teatable1(busy(1)),teatable2(busy(2))]")
		delay(5000) //time to reach table 1 and take the order
		forwardToClientSimulator2("proceed", "proceed")
		delay(200)
		checkResourceWaiterLogic("waiter(serving_client(2)) \n [teatable1(busy(1)),teatable2(busy(2))]")
		delay(5000)		//time to reach table 2 and take the order
		delay(20000) 	//time to serve order 1 and order 2
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
suspend fun testBusyRoom(){
	forwardToSmartbell("ring", "ring(36)")
	delay(4000) //time to check the temperature
	checkResourceWaiterLogic("waiter(serving_client(3)) \n [teatable1(busy(1)),teatable2(busy(2))]")
}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testPaymentAndMaxStayTime(){
		forwardToClientSimulator1("proceed", "proceed")
		delay(7000)	//time to reach table 1 and collect payment
		checkResourceWaiterLogic("waiter(serving_client(1)) \n [teatable1(dirty(1)),teatable2(busy(2))]")
		delay(25000)	// wait until the waiter convoy client 1 to exit and clean table 1.

		while(!itunibo.planner.plannerUtil.atPos(4,2) ){ // maxStayTime! wait until the waiter reach table 2
			delay(1000)
		}
		delay(2000)
		checkResourceWaiterLogic("waiter(serving_client(2)) \n [teatable1(tableclean),teatable2(dirty(1))]")
		while(!itunibo.planner.plannerUtil.atPos(5,4) ){ //wait until the waiter convoy client 2 to exit
			delay(1000)
		} 
	}
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testCleanTable(){
		while(!itunibo.planner.plannerUtil.atPos(4,2) ){ //wait until the waiter reach table 2 to clean it
			delay(1000)
		} 
		delay(18000)	//time to totally clean table 2
		checkResourceWaiterLogic("waiter(rest(4,2)) \n [teatable1(tableclean),teatable2(tableclean)]")
	}	

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	suspend fun testRest(){
		//TEST CHE IL WAITER RITORNI ALLA HOME
		while(!itunibo.planner.plannerUtil.atPos(0,0) ){
			delay(1000)
		}
		assertTrue(itunibo.planner.plannerUtil.atPos(0,0) )
	}				

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
@Test
	fun testSprint4(){
	 	runBlocking{
			while( waiterLogic == null || waiterWalker == null || smartbell == null || clientSimulator1 == null || clientSimulator2 == null ){
				println("Trying to start the actors...")
				delay(initDelayTime)  //time for system to start
				waiterWalker 		= it.unibo.kactor.sysUtil.getActor("waiterwalker")
				waiterLogic  		= it.unibo.kactor.sysUtil.getActor("waiterlogic")
				smartbell    		= it.unibo.kactor.sysUtil.getActor("smartbell")
				clientSimulator1	= it.unibo.kactor.sysUtil.getActor("client_simulator1")
				clientSimulator2	= it.unibo.kactor.sysUtil.getActor("client_simulator2")
			}
		delay(3000)	
		testSickRing() 				// test di una richiesta di cliente con febbre.
		test2ClientsEnterRequest()	// test sull'arrivo di due clienti in sequenza, uno dopo l'altro.
		testClientSimulation()		// i due clienti ordinano
		testBusyRoom()				// test di una richiesta fatta con la stanza piena.
		testPaymentAndMaxStayTime()	// il cliente 1 paga ed esce, il cliente 2 fa scadere maxStayTime ed esce.
		testCleanTable()			// il waiter pulisce i tavoli.
		testRest()					// il waiter torna alla home a riposare.
		}
	 	println("testSprint4 BYE  ")
	}
}
