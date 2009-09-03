\name{start.server}
\Rdversion{1.1}
\alias{start.server}
\title{
Utility to start the R server
}
\description{
Utility to start the R server
}
\usage{
start.server(name = "test", port = 1099, registry.host = "localhost", 
	run = TRUE, arguments = commandArgs(TRUE))
}
\arguments{
  \item{name}{name of the R engine in the rmi registry}
  \item{port}{port used by the rmi registry}
  \item{registry.host}{hostname where the rmi registry runs}
  \item{run}{logical, if TRUE the command is run. Otherwise it is just returned}
  \item{arguments}{Additional arguments, typically used by the command line}
}
\author{
Romain Francois <francoisromain@free.fr>
}
\keyword{manip}

