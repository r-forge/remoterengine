# this really should be in rJava
javadoc <- function( package = "RemoteREngine", browse = TRUE ){
    link <- system.file( "javadoc", "index.html", package = package )
    if( file.exists( link ) ){
        if( browse ) {
            browseURL( link )
        } 
        invisible( link )
    } else{
        warning( gettextf( "package '%s' does not have javadoc documentation" ) )
    }
    
}

