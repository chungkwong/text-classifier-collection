/*
 * Copyright (C) 2018 Chan Chung Kwong
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.chungkwong.classifier.util;
import java.util.*;
/**
 * Pair of object
 * @author Chan Chung Kwong
 * @param <K> the type of the first object
 * @param <V> the type of the last object
 */
public class Pair<K,V>{
	private final K key;
	private final V value;
	/**
	 * Create a pair
	 * @param key the first object
	 * @param value the last object
	 */
	public Pair(K key,V value){
		this.key=key;
		this.value=value;
	}
	/**
	 * @return the first object
	 */
	public K getKey(){
		return key;
	}
	/**
	 * @return the last object
	 */
	public V getValue(){
		return value;
	}
	@Override
	public String toString(){
		return key+"="+value;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Pair&&Objects.equals(((Pair)obj).key,key)
				&&Objects.equals(((Pair)obj).value,value);
	}
	@Override
	public int hashCode(){
		int hash=5;
		hash=47*hash+Objects.hashCode(this.key);
		hash=47*hash+Objects.hashCode(this.value);
		return hash;
	}
}
