/* Generated by AN DISI Unibo */ 
package it.unibo.waiter

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Waiter ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
		 		var ntableAvailable = 2
		 	 	val maxStayTime = 10000
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = false
						println("&&&&& waiter | starts &&&&&")
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("waitForRequest") { //this:State
					action { //it:State
						println("&&&&& waiter | waiting for any kind of requests... &&&&&")
						delay(3000) 
						stateTimer = TimerActor("timer_waitForRequest", 
							scope, context!!, "local_tout_waiter_waitForRequest", 12000.toLong() )
					}
					 transition(edgeName="t00",targetState="rest",cond=whenTimeout("local_tout_waiter_waitForRequest"))   
					transition(edgeName="t01",targetState="evaluateEntrance",cond=whenRequest("enterRequest"))
					transition(edgeName="t02",targetState="takeOrder",cond=whenDispatch("readyToOrder"))
					transition(edgeName="t03",targetState="serveClient",cond=whenDispatch("orderReady"))
					transition(edgeName="t04",targetState="collectPayment",cond=whenDispatch("readyToPay"))
				}	 
				state("evaluateEntrance") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("enterRequest(CID)"), Term.createTerm("enterRequest(CID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("&&&&& waiter | evaluating the enterRequest of the client ID = ${payloadArg(0)} ")
						}
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="accept", cond=doswitchGuarded({ NtableAvailable >= 1  
					}) )
					transition( edgeName="goto",targetState="inform", cond=doswitchGuarded({! ( NtableAvailable >= 1  
					) }) )
				}	 
				state("accept") { //this:State
					action { //it:State
						println("&&&&& waiter | EnterRequest accepted. &&&&&")
						answer("enterRequest", "answer", "answer(0)"   )  
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="reachEntranceDoor", cond=doswitch() )
				}	 
				state("reachEntranceDoor") { //this:State
					action { //it:State
						println("&&&&& waiter | I'm going to the entrance door... &&&&&")
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="convoyToTable", cond=doswitch() )
				}	 
				state("convoyToTable") { //this:State
					action { //it:State
						println("&&&&& waiter | Follow me to the table... &&&&&")
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("inform") { //this:State
					action { //it:State
						println("&&&&& waiter | Sorry, at the moment the TeaRoom is full. Retry in ${maxStayTime/1000} seconds &&&&&")
						answer("enterRequest", "answer", "answer(maxStayTime)"   )  
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("takeOrder") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("readyToOrder(CID)"), Term.createTerm("readyToOrder(CID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("&&&&& waiter | I'm taking the order of client ${payloadArg(0)} and transmitting it to barman .&&&&&")
								forward("order", "order(${payloadArg(0)},LimonTea)" ,"barman" ) 
								delay(3000) 
						}
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("serveClient") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("orderReady(CID,TEA)"), Term.createTerm("orderReady(CID,TEA)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("&&&&& waiter | I'm going to barman, taking the ${payloadArg(1)} for the client ${payloadArg(0)} and serving it at the table.&&&&&")
								delay(3000) 
						}
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("collectPayment") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("&&&&& waiter | I'm going to the table of client ${payloadArg(0)} and collect the payment.&&&&&")
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="convoyToExit", cond=doswitch() )
				}	 
				state("convoyToExit") { //this:State
					action { //it:State
						println("&&&&& waiter | Follow me to the exit door...Bye,Bye! &&&&&")
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="clean", cond=doswitch() )
				}	 
				state("clean") { //this:State
					action { //it:State
						println("&&&&& waiter | I'm cleaning the table! &&&&&")
						delay(3000) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("rest") { //this:State
					action { //it:State
						println("&&&&& waiter | I'm going to return to home and relax. &&&&&")
						delay(3000) 
					}
				}	 
			}
		}
}
