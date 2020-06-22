/* Generated by AN DISI Unibo */ 
package it.unibo.smartbell

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Smartbell ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
		 		val currentBodyTemperature = 36
		 		val currentClientID = 1
		 		val timeToWait = 0
		 			
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("&&&&& smartbell | starts &&&&&")
					}
					 transition( edgeName="goto",targetState="listening", cond=doswitch() )
				}	 
				state("listening") { //this:State
					action { //it:State
						println("&&&&& smartbell | listening to ringing... &&&&&")
					}
					 transition(edgeName="t05",targetState="checkClient",cond=whenRequest("ring"))
				}	 
				state("checkClient") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("ring(N)"), Term.createTerm("ring(N)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("&&&&& smartbell | DRIIN !! Let's control the body temperature... &&&&&")
								 readLine()  
						}
					}
					 transition( edgeName="goto",targetState="doEnterReq", cond=doswitchGuarded({ currentBodyTemperature < 37.5  
					}) )
					transition( edgeName="goto",targetState="refuseClient", cond=doswitchGuarded({! ( currentBodyTemperature < 37.5  
					) }) )
				}	 
				state("doEnterReq") { //this:State
					action { //it:State
						println("&&&&& smartbell | Forward the entranceRequest for client $currentClientID. &&&&&")
						request("enterRequest", "enterRequest("$currentClientID")" ,"waiter" )  
						 currentClientID ++  
					}
					 transition(edgeName="t06",targetState="showAnswer",cond=whenReply("answer"))
				}	 
				state("showAnswer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("answer(TIME)"), Term.createTerm("answer(TIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 timeToWait = ${payloadArg(0)}.toInt()  
								if(  timeToWait == 0  
								 ){println("&&&&& smartbell | Welcome! The waiter is coming. &&&&&")
								answer("ring", "waiterResponse", "waiterResponse("$currentClientID","$timeToWait")"   )  
								}
								else
								 {println("&&&&& smartbell | The teaRoom is full! Retry in ${timeToWait/1000} seconds. &&&&&")
								 answer("ring", "waiterResponse", "waiterResponse("$currentClientID","$timeToWait")"   )  
								 }
						}
					}
					 transition( edgeName="goto",targetState="listening", cond=doswitch() )
				}	 
				state("refuseClient") { //this:State
					action { //it:State
						println("&&&&& smartbell | I'm sorry! You're not allowed to enter cause your body temperature is high. &&&&&")
						answer("ring", "refused", "refused("-High body temperature-")"   )  
					}
					 transition( edgeName="goto",targetState="listening", cond=doswitch() )
				}	 
			}
		}
}
