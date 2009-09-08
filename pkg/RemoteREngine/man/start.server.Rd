\name{start.server}
\Rdversion{1.1}
\alias{start.server}
\title{
Utility to start the R server
}
\description{
	Utility to start the R server. This calls the script \code{remoterengine}
	contained in the \code{exec} directory of the package. See examples below
}
\usage{
start.server(run = TRUE, arguments = commandArgs(TRUE))
}
\arguments{
  \item{run}{logical, if TRUE the command is run. Otherwise it is just returned}
  \item{arguments}{Additional arguments, typically used by the command line}
}
\author{
Romain Francois <francoisromain@free.fr>
}
\examples{
# the script that gets called
script <- system.file( "exec", "remoterengine", package = "RemoteREngine" )

# make sure the file exists
stopifnot( file.exists( script ) )
}
\keyword{manip}

