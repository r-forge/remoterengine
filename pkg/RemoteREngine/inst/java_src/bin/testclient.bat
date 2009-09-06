set DIR=..\..\inst
java -cp %DIR%/java/RemoteREngine-client.jar -Djava.rmi.server.codebase=file:%DIR%\client.jar -Djava.security.policy=../../inst/policy/client.policy org.rosuda.REngine.remote.client.test.RemoteREngineTest

