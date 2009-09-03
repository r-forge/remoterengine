
start.server <- function( name = "test", port = 1099, registry.host = "localhost", run = TRUE, arguments = commandArgs(TRUE) ){
	rscript    <- file.path( R.home(), "bin", "Rscript" )
	script <- system.file( 'exec', 'remoterengine', package = 'RemoteREngine' )
    
	arguments <- if( length( arguments ) == 0 ) "" else paste( arguments, collapse = " " )
	cmd <- sprintf( '%s %s --name %s --port %d %s', rscript, script, name, port, arguments)
    if( run ){
        system( cmd )
    } else{
    	cmd
    }
}

