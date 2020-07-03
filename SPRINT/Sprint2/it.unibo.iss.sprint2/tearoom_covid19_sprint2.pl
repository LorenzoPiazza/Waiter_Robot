%====================================================================================
% tearoom_covid19_sprint2 description   
%====================================================================================
mqttBroker("localhost", "1883", "unibo/polar").
context(ctxwaiter, "localhost",  "TCP", "8050").
context(ctxwaiterwalker, "127.0.0.1",  "TCP", "8060").
 qactor( waiterwalker, ctxwaiterwalker, "it.unibo.waiterwalker.Waiterwalker").
  qactor( maxstaytimeobserver, ctxwaiter, "it.unibo.maxstaytimeobserver.Maxstaytimeobserver").
  qactor( waiterlogic, ctxwaiter, "it.unibo.waiterlogic.Waiterlogic").
  qactor( smartbell, ctxwaiter, "it.unibo.smartbell.Smartbell").
  qactor( barman, ctxwaiter, "it.unibo.barman.Barman").
  qactor( client_simulator, ctxwaiter, "it.unibo.client_simulator.Client_simulator").
