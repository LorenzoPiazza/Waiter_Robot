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
		
		 		var CurrentBodyTemperature = 36.0
		 		var CurrentClientId = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("&&&&& smartbell | starts")
						println("PRESS ENTER TO SIMULATE THE ARRIVE OF A CLIENT")
						 readLine()  
						forward("ring", "ring($CurrentBodyTemperature)" ,"smartbell" ) 
					}
					 transition( edgeName="goto",targetState="listening", cond=doswitch() )
				}	 
				state("listening") { //this:State
					action { //it:State
						println("&&&&& smartbell | listening to ringing...")
						delay(2000) 
					}
					 transition(edgeName="t037",targetState="checkClient",cond=whenDispatch("ring"))
				}	 
				state("checkClient") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("ring(TEMPERATURE)"), Term.createTerm("ring(TEMPERATURE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("&&&&& smartbell | DRIIN !! Let's control the body temperature...")
								delay(5000) 
								 CurrentBodyTemperature = payloadArg(0).toString().toDouble()  
						}
					}
					 transition( edgeName="goto",targetState="doEnterReq", cond=doswitchGuarded({ CurrentBodyTemperature < 37.5  
					}) )
					transition( edgeName="goto",targetState="refuseClient", cond=doswitchGuarded({! ( CurrentBodyTemperature < 37.5  
					) }) )
				}	 
				state("doEnterReq") { //this:State
					action { //it:State
						 CurrentClientId ++  
						println("&&&&& smartbell | Forward the entranceRequest for client $CurrentClientId.")
						request("enterRequest", "enterRequest($CurrentClientId)" ,"waiterlogic" )  
						delay(5000) 
					}
					 transition(edgeName="t038",targetState="checkAnswer",cond=whenReply("answer"))
				}	 
				state("checkAnswer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("answer(TIME)"), Term.createTerm("answer(TIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0).toString().toInt() == 0  
								 ){println("WELCOME! You're the Client $CurrentClientId. The waiter is coming to you. ")
								}
								else
								 {println("The Room is full. Retry in ${payloadArg(0)} millisec")
								 }
						}
						if( checkMsgContent( Term.createTerm("answer(TIME)"), Term.createTerm("cleanFirst(TIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("WELCOME! You're the Client $CurrentClientId. The waiter has to clean first a table and then will come to you. ")
						}
					}
					 transition( edgeName="goto",targetState="listening", cond=doswitch() )
				}	 
				state("refuseClient") { //this:State
					action { //it:State
						println("&&&&& smartbell | I'm sorry! You're not allowed to enter cause your body temperature is too high.")
					}
					 transition( edgeName="goto",targetState="listening", cond=doswitch() )
				}	 
			}
		}
}