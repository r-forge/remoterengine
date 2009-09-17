#!/bin/env Rscript

require( ant ) 

dir_filter <- function(.) file.info(.)$isdir
rscript <- file.path( R.home(), "bin", "Rscript" )

root <- getwd()
dir.create( "inst/java" )

setwd( "inst/examples" )
dirs <- Filter( dir_filter, dir( "." ) )
examples_dir <- getwd()
client.jar <- system.file( "java", "RemoteREngine-client.jar", package = "RemoteREngine" )
for( ex in dirs ){
	setwd( ex )
	file.copy( "../build.xml", "build.xml" )
	cat( "project=", ex, "\n",
		"client.jar=", client.jar, "\n",
		 file = "build.properties", sep = "", 
		 append = file.exists( "build.properties" ) )
	ant() 
	setwd( examples_dir )
}
setwd(root)


