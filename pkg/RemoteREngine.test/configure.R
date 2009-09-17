#!/bin/env Rscript

require( ant ) 

rscript <- file.path( R.home(), "bin", "Rscript" )
root <- getwd()
dir.create( "inst/java" )
setwd( "inst/java_src" )

java_src.dir <- getwd()
ant()
setwd(root)

