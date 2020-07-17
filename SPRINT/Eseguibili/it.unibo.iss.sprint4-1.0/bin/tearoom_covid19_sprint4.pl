%====================================================================================
% tearoom_covid19_sprint4 description   
%====================================================================================
mqttBroker("localhost", "1883", "unibo/polar").
context(ctxwaiter, "localhost",  "TCP", "8050").
 qactor( waiterwalker, ctxwaiter, "it.unibo.waiterwalker.Waiterwalker").
  qactor( maxstaytimeobserver, ctxwaiter, "it.unibo.maxstaytimeobserver.Maxstaytimeobserver").
  qactor( waiterlogic, ctxwaiter, "it.unibo.waiterlogic.Waiterlogic").
  qactor( smartbell, ctxwaiter, "it.unibo.smartbell.Smartbell").
  qactor( barman, ctxwaiter, "it.unibo.barman.Barman").
  qactor( client_simulator1, ctxwaiter, "it.unibo.client_simulator1.Client_simulator1").
  qactor( client_simulator2, ctxwaiter, "it.unibo.client_simulator2.Client_simulator2").
