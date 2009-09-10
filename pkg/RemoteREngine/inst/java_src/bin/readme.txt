The Java arguments required to start the server are as follows:
java -cp D:\eclipse\REngine\pkg\RemoteREngine\inst\java\RemoteREngine-server.jar 
-Xmx1024M 
-Drjava.path=C:\Progra~1\R\R-2.9.0\library\rJava\java 
-Drjava.class.path=C:\Progra~1\R\R-2.9.0\library\rJava\java\boot;C:\Progra~1\R\R-2.9.0\library\rJava\java 
-Djava.security.policy=D:\eclipse\REngine\pkg\RemoteREngine\inst\policy\server.policy 
-Djava.library.path=C:\Progra~1\R\R-2.8.1\library\rJava\jri 
-Djava.rmi.server.codebase=file:/D:/eclipse/REngine/pkg/RemoteREngine/inst/java/RemoteREngine-server.jar 
org.rosuda.REngine.remote.server.REngineServer
