callback <- function( signature = "", object = NULL ){
	
	if( !is.character( signature ) ){
		stop( "'signature' should be a character vector" )
	}
	signature <- signature[1]
	
	cb <- if( is.null( object ) ){
		.jnew( "org/rosuda/REngine/remote/common/callbacks", signature )
	} else {
		# TODO: bring "object" into java
		warning( "ObjectCallback not yet supported" )
	}
	# TODO: send the callback
	
}
