rem Set jvm heap initial and maximum sizes (in megabytes).
set JAVA_HEAP_MAX_SIZE=1024
set CODEBASE=file:%DEFAULTDIR%\java\RemoteREngine-server.jar


set CURRENTDIR=%cd%
cd ..\..\inst
set defaultdir=%cd%
cd %CURRENTDIR%

IF EXIST %R_HOME% GOTO CHECK_JRI
 ECHO Ensure R_HOME environment variable is set to the Home directory for your R installation


:CHECK_JRI
IF EXIST %LD_LIBRARY_PATH% GOTO RUN
 ECHO Ensure LD_LIBRARY_PATH environment variable points to the directory containing jri.dll
 GOTO END

:RUN 
java -cp %DEFAULTDIR%\java\RemoteREngine-server.jar -Xmx%JAVA_HEAP_MAX_SIZE%M -Drjava.path=%R_HOME%\library\rJava -Drjava.class.path=%R_HOME%\library\rJava\java\boot;%R_HOME%\library\rJava\java -Djava.security.policy=server.policy -Djava.library.path=%LD_LIBRARY_PATH% -Djava.rmi.server.codebase=%CODEBASE% org.rosuda.REngine.remote.server.REngineServer %1 %2 %3 %4 %5 %6
)
:END

