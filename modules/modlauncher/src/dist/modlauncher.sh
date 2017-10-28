#!/usr/bin/env bash
if test -e "logging.properties"; then
	LOGGING=-Djava.util.logging.config.file=logging.properties
fi


if test -x "runtime/jre1.8.0_121/bin/java"; then
	JAVA="runtime/jre1.8.0_121/bin/java"
elif test -x "../runtime/jre1.8.0_121/bin/java"; then
	JAVA="../runtime/jre1.8.0_121/bin/java"
else
	JAVA="java"
fi

LD_LIBRARY_PATH="$PWD/nativelibs"
if test $(uname -m) = "x86_64"; then
	LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$PWD/linux64"
else
	LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$PWD"
fi
export LD_LIBRARY_PATH

"$JAVA" "-Dworkdir=$PWD" "-Djava.library.path=$PWD/nativelibs" $LOGGING -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar ./modlauncher.jar "$*"
