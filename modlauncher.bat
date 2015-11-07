
if exist logging.properties set LOGGING=-Djava.util.logging.config.file=logging.properties

runtime\bin\java "-Dworkdir=%CD%" "-Djava.library.path=%CD%\nativelibs" %LOGGING% -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar modlauncher.jar  %*
