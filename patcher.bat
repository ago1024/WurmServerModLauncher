set JAVA=java
if exist runtime\bin\java.exe set JAVA=runtime\bin\java.exe
if exist ..\runtime\bin\java.exe set JAVA=..\runtime\bin\java.exe

%JAVA% -classpath patcher.jar;javassist.jar org.gotti.wurmunlimited.patcher.PatchServerJar
pause
