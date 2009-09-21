#!/bin/env Rscript

# {{{ :tabSize=2:indentSize=2:noTabs=false:folding=explicit:collapseFolds=1:
# This file is part of the RemoteREngine project
# 
# The RemoteREngine project is free software: 
# you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 2 of the License, or
# (at your option) any later version.
# 
# The RemoteREngine project is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with the RemoteREngine project. If not, see <http://www.gnu.org/licenses/>. 
# }}}

# {{{ generate.build.properties
generate.build.properties <- function(  ){
	R_PACKAGE_DIR <- Sys.getenv( "R_PACKAGE_DIR" ) 
	testng.jar    <- file.path( R_PACKAGE_DIR, "java_src", "lib", "testng-5.10-jdk15.jar" )
	client.jar    <- system.file( "java", "RemoteREngine-client.jar", package = "RemoteREngine" )
	testcases.jar <- file.path( R_PACKAGE_DIR, "java", "RemoteREngine-test.jar" )
	rmicodebase   <- paste( "file:/", system.file( "java", "RemoteREngine-client.jar", package = "RemoteREngine" ), sep = "" )
	client.policy <- system.file( "policy", "client.policy", package = "RemoteREngine" ) 
	server.policy <- system.file( "policy", "server.policy", package = "RemoteREngine" ) 
	
	generated.properties <- paste( "\n\n\n# generated content below\n\n",
		"testng.jar=", testng.jar, "\n", 
		"client.jar=", client.jar, "\n", 
		"testcases.jar=", testcases.jar, "\n", 
		"rmicodebase=", rmicodebase, "\n", 
		"client.policy=", client.policy, "\n" ,
		"server.policy=", server.policy, "\n", sep = "" )
	cat( generated.properties, file = "build.properties" ,	sep = "", 
		append = TRUE )
	cat( generated.properties, file = "../testng/build.properties", sep = "" )
}			
# }}}

require( ant ) 

root <- getwd()
setwd( "inst/java_src" )

java_src.dir <- getwd()
generate.build.properties()

ant()
setwd(root)

