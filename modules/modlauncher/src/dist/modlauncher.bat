
if exist logging.properties set LOGGING=-Djava.util.logging.config.file=logging.properties

set JAVA=java
if exist runtime\bin\java.exe set JAVA=runtime\bin\java.exe
if exist ..\runtime\bin\java.exe set JAVA=..\runtime\bin\java.exe

%JAVA% "-Dworkdir=%CD%" "-Djava.library.path=%CD%\nativelibs" %LOGGING% -Xmn256M -Xms512m -Xmx2048m -XX:+OptimizeStringConcat -XX:+AggressiveOpts -jar modlauncher.jar  %*
