/* Generated by AN DISI Unibo */ 
package it.unibo.ctxbasicrobot
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "127.0.0.1", this, "tearoom_covid19_sprint2.pl", "sysRules.pl"
	)
}

