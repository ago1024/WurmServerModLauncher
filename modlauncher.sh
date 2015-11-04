#!/usr/bin/env bash 
./runtime/jre1.8.0_60/bin/java "-Dworkdir=$PWD" "-Djava.library.path=$PWD/nativelibs" -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar ./modlauncher.jar "$*"
