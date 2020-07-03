/* Generated by AN DISI Unibo */ 
package it.unibo.waiterlogic

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Waiterlogic ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	@kotlinx.coroutines.ExperimentalCoroutinesApi			
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
		 		var NTableAvailable 	 = 2
		 		var CurrentClientId		 = 0
				var CurrentSelectedTable = 0
				var CurX				 = 0
				var CurY				 = 0
				var ST	: String 		 = ""		//state of Tables
				var SW	: String   		 = ""		//state of Waiter
				
				var X_barman		= "0"
				var Y_barman		= "0"
			
				var X_entrancedoor  = "0"
				var Y_entrancedoor  = "0"
			
				var X_exitdoor      = "0"
				var Y_exitdoor      = "0"
			 
				var X_teatable1     = "0"
				var Y_teatable1     = "0"
		
				var X_teatable2     = "0"
				var Y_teatable2     = "0"
				
		 	 	val MaxStayTime = 20000L
		 	 	val TimeToRest  = 20000L
		 	 	var CurrentTime = 0L
		 	 	var CSituationAtTimeout = ""
		 	 	var WaiterState = "rest(0,0)"
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = false
						println("&&&&& waiter | starts")
						itunibo.planner.plannerUtil.initAI(  )
						solve("consult('tearoomkb.pl')","") //set resVar	
						solve("consult('sysRules.pl')","") //set resVar	
						solve("pos(entrancedoor,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) { X_entrancedoor=getCurSol("X").toString() ;  Y_entrancedoor=getCurSol("Y").toString()  
						}
						else
						{}
						println("entrancedoor($X_entrancedoor, $Y_entrancedoor )")
						solve("pos(exitdoor,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) { X_exitdoor = getCurSol("X").toString();  Y_exitdoor = getCurSol("Y").toString()  
						}
						else
						{}
						println("exitdoor($X_exitdoor, $Y_exitdoor )")
						solve("pos(barman,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) { X_barman = getCurSol("X").toString();  Y_barman = getCurSol("Y").toString()  
						}
						else
						{}
						println("barman($X_barman, $Y_barman)")
						solve("pos(teatable1,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) { X_teatable1 = getCurSol("X").toString();  Y_teatable1 = getCurSol("Y").toString()  
						}
						else
						{}
						println("teatable1($X_teatable1, $Y_teatable1)")
						solve("pos(teatable2,X,Y)","") //set resVar	
						if( currentSolution.isSuccess() ) { X_teatable2 = getCurSol("X").toString();  Y_teatable2 = getCurSol("Y").toString()  
						}
						else
						{}
						println("teatable2($X_teatable2, $Y_teatable2)")
						emit("local_startMaxstaytimeObserver", "local_startMaxstaytimeObserver($MaxStayTime)" ) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("waitForRequest") { //this:State
					action { //it:State
							
						 			CurrentClientId 	 = 0
						 			CurrentSelectedTable = 0
									CurX             	 = itunibo.planner.plannerUtil.getPosX()
									CurY             	 = itunibo.planner.plannerUtil.getPosY()
									WaiterState 	 	 = "rest($CurX, $CurY)"
						println("&&&&& waiter | waiting for any kind of requests...")
						solve("replaceRule(waiter(S),waiter($WaiterState))","") //set resVar	
						solve("waiter(SW)","") //set resVar	
						 SW = getCurSol("SW").toString();  
						solve("stateOfTeatables(ST)","") //set resVar	
						 ST = getCurSol("ST").toString()  
						println("ROOM STATE: waiter: $SW ---------- $ST")
						updateResourceRep( WaiterState  
						)
						stateTimer = TimerActor("timer_waitForRequest", 
							scope, context!!, "local_tout_waiterlogic_waitForRequest", TimeToRest )
					}
					 transition(edgeName="t00",targetState="rest",cond=whenTimeout("local_tout_waiterlogic_waitForRequest"))   
					transition(edgeName="t01",targetState="evaluateEntrance",cond=whenRequest("enterRequest"))
					transition(edgeName="t02",targetState="reachTableToOrder",cond=whenDispatch("readyToOrder"))
					transition(edgeName="t03",targetState="reachServiceDesk",cond=whenDispatch("orderReady"))
					transition(edgeName="t04",targetState="collectPayment",cond=whenDispatch("readyToPay"))
					transition(edgeName="t05",targetState="collectPayment",cond=whenDispatch("maxStayTime"))
				}	 
				state("evaluateEntrance") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("enterRequest(CID)"), Term.createTerm("enterRequest(CID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												CurrentClientId = payloadArg(0).toString().toInt()
								 				WaiterState 	= "serving_client($CurrentClientId)"
								println("&&&&& waiter | evaluating the enterRequest of the client ID = ${payloadArg(0)} ")
								updateResourceRep( WaiterState  
								)
								solve("numfreetables(N)","") //set resVar	
								if( currentSolution.isSuccess() ) { NTableAvailable = getCurSol("N").toString().toInt()  
								}
								else
								{}
								println("&&&&& waiter | There are $NTableAvailable table available.")
						}
						delay(1000) 
					}
					 transition( edgeName="goto",targetState="accept", cond=doswitchGuarded({ NTableAvailable >= 1  
					}) )
					transition( edgeName="goto",targetState="inform", cond=doswitchGuarded({! ( NTableAvailable >= 1  
					) }) )
				}	 
				state("accept") { //this:State
					action { //it:State
						println("&&&&& waiter | EnterRequest accepted.")
						solve("teatable(N,tableclean)","") //set resVar	
						if( currentSolution.isSuccess() ) { CurrentSelectedTable = getCurSol("N").toString().toInt()  
						}
						else
						{}
						solve("replaceRule(teatable($CurrentSelectedTable,tableclean),teatable($CurrentSelectedTable,busy($CurrentClientId)))","") //set resVar	
						solve("stateOfTeatables(ST)","") //set resVar	
						if( currentSolution.isSuccess() ) { ST = getCurSol("ST").toString()  
						}
						else
						{}
						println("State of Tables: $ST ")
						answer("enterRequest", "answer", "answer(0)"   )  
					}
					 transition( edgeName="goto",targetState="reachEntranceDoor", cond=doswitch() )
				}	 
				state("reachEntranceDoor") { //this:State
					action { //it:State
						delay(1000) 
						println("&&&&& waiter | I'm going to the entrance door...")
						request("movetoCell", "movetoCell($X_entrancedoor,$Y_entrancedoor)" ,"waiterwalker" )  
					}
					 transition(edgeName="t06",targetState="convoyToTable",cond=whenReply("atcell"))
					transition(edgeName="t07",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("convoyToTable") { //this:State
					action { //it:State
						delay(1000) 
						println("&&&&& waiter | Follow me to the table $CurrentSelectedTable...")
						if(  CurrentSelectedTable == 1  
						 ){request("movetoCell", "movetoCell($X_teatable1,$Y_teatable1)" ,"waiterwalker" )  
						}
						else
						 {request("movetoCell", "movetoCell($X_teatable2,$Y_teatable2)" ,"waiterwalker" )  
						 }
					}
					 transition(edgeName="t08",targetState="startCountdown",cond=whenReply("atcell"))
					transition(edgeName="t09",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("startCountdown") { //this:State
					action { //it:State
						forward("table_reached", "table_reached($CurrentClientId)" ,"client_simulator" ) 
						CurrentTime = getCurrentTime()
						emit("local_consulting", "local_consulting($CurrentClientId,$CurrentTime)" ) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("inform") { //this:State
					action { //it:State
						println("&&&&& waiter | Sorry, at the moment the TeaRoom is full. Retry in ${MaxStayTime/1000} seconds.")
						answer("enterRequest", "answer", "answer($MaxStayTime)"   )  
						delay(4000) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("reachTableToOrder") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("readyToOrder(CID)"), Term.createTerm("readyToOrder(CID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												CurrentClientId = payloadArg(0).toString().toInt()
								 				WaiterState 	= "serving_client($CurrentClientId)"
								updateResourceRep( WaiterState  
								)
								solve("teatable(N,busy($CurrentClientId))","") //set resVar	
								if( currentSolution.isSuccess() ) { CurrentSelectedTable = getCurSol("N").toString().toInt()  
								}
								else
								{}
								if(  CurrentSelectedTable == 1  
								 ){request("movetoCell", "movetoCell($X_teatable1,$Y_teatable1)" ,"waiterwalker" )  
								}
								else
								 {request("movetoCell", "movetoCell($X_teatable2,$Y_teatable2)" ,"waiterwalker" )  
								 }
								println("&&&&& waiter | I'm reaching the table $CurrentSelectedTable to take the order of the client ${payloadArg(0)}.")
						}
					}
					 transition(edgeName="t010",targetState="takeOrder",cond=whenReply("atcell"))
					transition(edgeName="t011",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("takeOrder") { //this:State
					action { //it:State
						delay(1000) 
						println("&&&&& waiter | Taking the order of client $CurrentClientId and transmitting it to barman.")
						delay(2000) 
						forward("order", "order(${payloadArg(0)},LemonTea)" ,"barman" ) 
						CurrentTime = getCurrentTime()
						emit("local_preparation", "local_preparation($CurrentClientId,$CurrentTime)" ) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("reachServiceDesk") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("orderReady(CID,TEA)"), Term.createTerm("orderReady(CID,TEA)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
									CurrentClientId = payloadArg(0).toString().toInt()
								 				WaiterState 	= "serving_client($CurrentClientId)"
								delay(1000) 
								println("&&&&& waiter | The order for Client $CurrentClientId is ready! I'm going to service desk")
								updateResourceRep( WaiterState  
								)
								request("movetoCell", "movetoCell($X_barman,$Y_barman)" ,"waiterwalker" )  
						}
					}
					 transition(edgeName="t012",targetState="reachTableToServe",cond=whenReply("atcell"))
					transition(edgeName="t013",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("reachTableToServe") { //this:State
					action { //it:State
						solve("teatable(N,busy($CurrentClientId))","") //set resVar	
						if( currentSolution.isSuccess() ) { CurrentSelectedTable = getCurSol("N").toString().toInt()  
						}
						else
						{}
						if(  CurrentSelectedTable == 1  
						 ){request("movetoCell", "movetoCell($X_teatable1,$Y_teatable1)" ,"waiterwalker" )  
						}
						else
						 {request("movetoCell", "movetoCell($X_teatable2,$Y_teatable2)" ,"waiterwalker" )  
						 }
						println("&&&&& waiter | I'm serving the order of the client $CurrentClientId to the table $CurrentSelectedTable.")
					}
					 transition(edgeName="t014",targetState="serveClient",cond=whenReply("atcell"))
					transition(edgeName="t015",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("serveClient") { //this:State
					action { //it:State
						forward("tea_served", "tea_served($CurrentClientId)" ,"client_simulator" ) 
						CurrentTime = getCurrentTime()
						emit("local_consuming", "local_consuming($CurrentClientId,$CurrentTime)" ) 
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("collectPayment") { //this:State
					action { //it:State
							
						 			CurrentClientId = payloadArg(0).toString().toInt()
						 		 	WaiterState 	= "serving_client($CurrentClientId)"
						if( checkMsgContent( Term.createTerm("readyToPay(CID)"), Term.createTerm("readyToPay(CID)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("&&&&& waiter | I'm going to the table of client $CurrentClientId and collect the payment.")
								updateResourceRep( WaiterState  
								)
								emit("local_leaving", "local_leaving($CurrentClientId)" ) 
						}
						if( checkMsgContent( Term.createTerm("maxStayTime(CID,CSTATE)"), Term.createTerm("maxStayTime(CID,STATE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 CSituationAtTimeout = payloadArg(1).toString()  
								if(  CSituationAtTimeout == "consulting" 
								 ){println("&&&&& waiter | MAX STAY TIME FOR CLIENT $CurrentClientId is OVER while he's $CSituationAtTimeout the men�! No need to collect the payment.")
								}
								else
								 {println("&&&&& waiter | MAX STAY TIME FOR CLIENT $CurrentClientId is OVER while he's $CSituationAtTimeout! I'm going to collect the payment.")
								 }
								updateResourceRep( WaiterState  
								)
						}
						solve("teatable(N,busy($CurrentClientId))","") //set resVar	
						if( currentSolution.isSuccess() ) { CurrentSelectedTable = getCurSol("N").toString().toInt()  
						}
						else
						{}
						if(  CurrentSelectedTable == 1  
						 ){request("movetoCell", "movetoCell($X_teatable1,$Y_teatable1)" ,"waiterwalker" )  
						}
						else
						 {request("movetoCell", "movetoCell($X_teatable2,$Y_teatable2)" ,"waiterwalker" )  
						 }
					}
					 transition(edgeName="t016",targetState="convoyToExit",cond=whenReply("atcell"))
					transition(edgeName="t017",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("convoyToExit") { //this:State
					action { //it:State
						delay(2000) 
						println("&&&&& waiter | Thank you! Follow me to the exit door...Bye,Bye!")
						solve("replaceRule(teatable($CurrentSelectedTable,busy($CurrentClientId)),teatable($CurrentSelectedTable,dirty))","") //set resVar	
						solve("stateOfTeatables(ST)","") //set resVar	
						if( currentSolution.isSuccess() ) { ST = getCurSol("ST").toString()  
						}
						else
						{}
						println("State of Tables: $ST ")
						request("movetoCell", "movetoCell($X_exitdoor,$Y_exitdoor)" ,"waiterwalker" )  
					}
					 transition(edgeName="t018",targetState="reachTableToClean",cond=whenReply("atcell"))
					transition(edgeName="t019",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("reachTableToClean") { //this:State
					action { //it:State
							WaiterState = "cleaning(table($CurrentSelectedTable))"	 
						updateResourceRep( WaiterState  
						)
						if(  CurrentSelectedTable == 1  
						 ){request("movetoCell", "movetoCell($X_teatable1,$Y_teatable1)" ,"waiterwalker" )  
						}
						else
						 {request("movetoCell", "movetoCell($X_teatable2,$Y_teatable2)" ,"waiterwalker" )  
						 }
					}
					 transition(edgeName="t020",targetState="clean",cond=whenReply("atcell"))
					transition(edgeName="t021",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("clean") { //this:State
					action { //it:State
						println("&&&&& waiter | I'm cleaning the table!")
						delay(3000) 
						solve("replaceRule(teatable($CurrentSelectedTable,dirty),teatable($CurrentSelectedTable,tableclean))","") //set resVar	
						solve("stateOfTeatables(ST)","") //set resVar	
						if( currentSolution.isSuccess() ) { ST = getCurSol("ST").toString()  
						}
						else
						{}
						println("State of Tables: $ST ")
					}
					 transition( edgeName="goto",targetState="waitForRequest", cond=doswitch() )
				}	 
				state("rest") { //this:State
					action { //it:State
						println("&&&&& waiter | I'm going to return to home and relax.")
						request("movetoCell", "movetoCell(0,0)" ,"waiterwalker" )  
					}
					 transition(edgeName="t022",targetState="waitForRequest",cond=whenReply("atcell"))
					transition(edgeName="t023",targetState="unexpected",cond=whenReply("walkbreak"))
				}	 
				state("unexpected") { //this:State
					action { //it:State
						println("Sorry, there is something wrong ...")
						println("$name in ${currentState.stateName} | $currentMsg")
					}
				}	 
			}
		}
}
