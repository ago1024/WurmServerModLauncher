runtime\bin\java "-Dworkdir=%CD%" "-Djava.library.path=%CD%\nativelibs" -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar modlauncher.jar
