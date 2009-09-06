/*
 * Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
 *
 * This file is part of the RemoteREngine project
 *
 * The RemoteREngine project is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The RemoteREngine project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the RemoteREngine project. If not, see <http://www.gnu.org/licenses/>.
 */
package org.rosuda.REngine.remote.common.history;

import java.util.regex.Pattern;

/**
 * Matcher that accepts commands matching a given pattern
 * 
 * @author Romain Francois
 *
 */
public class PatternCommandMatcher implements CommandMatcher {

	/**
	 * Pattern to match
	 */
	protected Pattern pattern ; 
	
	/**
	 * Constructor
	 * @param pattern pattern to match
	 */
	public PatternCommandMatcher( Pattern pattern ){
		this.pattern = pattern ; 
	}
	
	/**
	 * accept if the command matches the pattern
	 */
	@Override
	public final boolean accept(String command) {
		return pattern.matcher( command ).find() ;
	}

}
