@echo off

ECHO ============================
ECHO LAUNCHING VIRTUAL ROBOT
ECHO ============================
cd .\SPRINT\Sprint4\it.unibo.virtualRobot2020\node\WEnv\server\src
START node main 8999
SLEEP 5
START http://localhost:8090

ECHO ============================
ECHO LAUNCHING THE BASIC ROBOT
ECHO ============================

cd ../../../../../../..
cd .\SPRINT\Eseguibili\it.unibo.qak20.basicrobot-1.0\bin
START .\it.unibo.qak20.basicrobot.bat
cd ../../../..
SLEEP 5

ECHO ============================
ECHO LAUNCHING THE WAITER CONTEXT
ECHO ============================
cd .\SPRINT\Eseguibili\it.unibo.iss.sprint4-1.0\bin
START .\it.unibo.iss.sprint4.bat
cd ../../../..
SLEEP 8

ECHO ============================
ECHO LAUNCHING THE WEB APPLICATION
ECHO ============================
cd .\SPRINT\Eseguibili\it.unibo.iss.sprint4.clientWeb-boot-1.0\bin
START .\it.unibo.iss.sprint4.clientWeb.bat
SLEEP 10
START http://localhost:7001

PAUSE