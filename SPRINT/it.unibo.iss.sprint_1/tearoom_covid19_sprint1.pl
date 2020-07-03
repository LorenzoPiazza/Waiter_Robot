%====================================================================================
% tearoom_covid19_sprint1 description   
%====================================================================================
context(ctxwaiterwalker, "localhost",  "TCP", "8050").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( waiterwalker, ctxwaiterwalker, "it.unibo.waiterwalker.Waiterwalker").
