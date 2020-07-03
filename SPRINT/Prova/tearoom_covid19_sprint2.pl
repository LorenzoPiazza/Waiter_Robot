%====================================================================================
% tearoom_covid19_sprint2 description   
%====================================================================================
mqttBroker("localhost", "1883", "unibo/polar").
context(ctxtest, "localhost",  "TCP", "8060").
 qactor( maxstaytimeobserver, ctxtest, "it.unibo.maxstaytimeobserver.Maxstaytimeobserver").
  qactor( tester, ctxtest, "it.unibo.tester.Tester").
