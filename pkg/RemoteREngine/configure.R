#!/bin/env Rscript
version <- read.dcf( "DESCRIPTION" )[, "Version" ]
setwd( "inst/java_src" )

build.properties <- readLines( "build.properties" )
build.properties <- gsub( "@VERSION@" , version, build.properties )

cat( build.properties, file = "build.properties", sep = "\n" )
ant()

