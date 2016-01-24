#!/bin/sh

if test -x "runtime/jre1.8.0_60/bin/java"; then
	JAVA="runtime/jre1.8.0_60/bin/java"
elif test -x "../runtime/jre1.8.0_60/bin/java"; then
	JAVA="../runtime/jre1.8.0_60/bin/java"
else
	JAVA="java"
fi

"$JAVA" -classpath patcher.jar:javassist.jar org.gotti.wurmunlimited.patcher.PatchServerJar
chmod a+x WurmServerLauncher-patched
