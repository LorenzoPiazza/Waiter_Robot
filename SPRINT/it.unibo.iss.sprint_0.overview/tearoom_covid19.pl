%====================================================================================
% tearoom_covid19 description   
%====================================================================================
context(ctxtearoom, "localhost",  "TCP", "9000").
 qactor( waiter, ctxtearoom, "it.unibo.waiter.Waiter").
  qactor( smartbell, ctxtearoom, "it.unibo.smartbell.Smartbell").
  qactor( barman, ctxtearoom, "it.unibo.barman.Barman").
  qactor( client_simulator, ctxtearoom, "it.unibo.client_simulator.Client_simulator").
  qactor( situation_observer, ctxtearoom, "it.unibo.situation_observer.Situation_observer").
