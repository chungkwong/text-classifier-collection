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
 * Records of frequency of different objects
 * @author Chan Chung Kwong
 * @param <T> the type of the objects to be recorded
 */
public class DoubleCounters<T>{
	private final Map<T,DoubleCounter> frequency;
	/**
	 * Create a frequencies table backed by TreeMap
	 */
	public DoubleCounters(){
		frequency=new TreeMap<>();
	}
	/**
	 * Create a frequencies table
	 * @param useHashMap if true, the table is backed by HashMap
	 */
	public DoubleCounters(boolean useHashMap){
		frequency=useHashMap?new HashMap<>():new TreeMap<>();
	}
	/**
	 * Increase the frequency of a given object by a given value
	 * @param token the given object
	 * @param amount the given value
	 */
	public void advanceCounter(T token,double amount){
		DoubleCounter counter=frequency.get(token);
		if(counter==null){
			counter=new DoubleCounter(amount);
			frequency.put(token,counter);
		}else{
			counter.advance(amount);
		}
	}
	/**
	 * Merge frequencies into this table
	 * @param toMerge the source
	 */
	public void merge(DoubleCounters<T> toMerge){
		toMerge.frequency.forEach((k,v)->advanceCounter(k,v.getValue()));
	}
	/**
	 * Set the frequency of a object to zero
	 * @param token the object
	 */
	public void reset(T token){
		frequency.remove(token);
	}
	public double getFrequency(T token){
		DoubleCounter counter=frequency.get(token);
		return counter==null?0:counter.getValue();
	}
	public int getTokenCount(){
		return frequency.size();
	}
	/**
	 * Map representation of the table
	 * @return the map
	 */
	public Map<T,DoubleCounter> toMap(){
		return frequency;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof DoubleCounters&&Objects.equals(frequency,((DoubleCounters)obj).frequency);
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=31*hash+Objects.hashCode(this.frequency);
		return hash;
	}
	@Override
	public String toString(){
		return frequency.toString();
	}
}
