#!/bin/sh

java() {
	runtime/jre1.8.0_60/bin/java $*
}
java -classpath patcher.jar:javassist.jar org.gotti.wurmunlimited.patcher.PatchServerJar
chmod a+x WurmServerLauncher-patched
