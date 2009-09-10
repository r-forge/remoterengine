set CURRENTDIR=%cd%
cd ..\..\..\inst
set DEFAULTDIR=%cd%
set CODEBASE=%DEFAULTDIR%\java\RemoteREngine-client-junit.jar
set RMICODEBASE=file:/D:/eclipse/REngine/pkg/RemoteREngine/inst/java/RemoteREngine-client-junit.jar
cd %CURRENTDIR%

java -cp %CODEBASE% -Djava.rmi.server.codebase=%RMICODEBASE% -Djava.security.policy=%DEFAULTDIR%/policy/client.policy org.rosuda.REngine.remote.client.test.RemoteREngineTest

