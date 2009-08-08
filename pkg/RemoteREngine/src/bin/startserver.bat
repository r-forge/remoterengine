set DIR=..\..\inst
rem Set jvm heap initial and maximum sizes (in megabytes).
set JAVA_HEAP_MAX_SIZE=1024
set LD_LIBRARY_PATH=d:\JavaLibraries\jri\JRI_0.5.0

java -cp %DIR%\java\RemoteREngine-server.jar -Xmx%JAVA_HEAP_MAX_SIZE%M -Drjava.path=%R_HOME%\library\rJava -Drjava.class.path=%R_HOME%\library\rJava\java\boot;%R_HOME%\library\rJava\java -Djava.rmi.server.codebase=file:D:\eclipse\RemoteRServer\pkg\RemoteREngine\inst\java\RemoteREngine-server.jar -Djava.security.policy=server.policy -Djava.library.path=%LD_LIBRARY_PATH% org.rosuda.REngine.remote.server.REngineServer %1 %2 %3 %4 %5 %6