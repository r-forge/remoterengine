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
package org.rosuda.REngine.remote.common.tools;

import java.util.HashMap;

/**
 * 
 * @author Romain Francois
 *
 * @param <K>
 * @param <V>
 */
public class Waiter<K,V> {

	protected HashMap<K,V> map ; 
	
	public Waiter( ){
		map = new HashMap<K, V>(); 
	}
	
	/**
	 * Waits until the map has a value for the key 
	 * 
	 * @param key the key
	 * @return the value of the given key
	 * 
	 * FIXME: this is not correct as it blocks on the first asked key
	 */
	public synchronized V get(K key) {
		while ( map.isEmpty() || !map.containsKey(key)){
			try {
				wait(100);
				afterWaiting(); 
			} catch (InterruptedException e) { }
		}
		return map.remove( key ) ; 
	}

	/**
	 * Adds an object to the map
	 * 
	 * @param key key 
	 * @param value value
	 */
	public synchronized void put(K key, V value) {
		map.put(key, value); 
		notifyAll();
	}
	
	/**
	 * Hook method executed after the call to wait
	 */
	public void afterWaiting(){} ;

}
