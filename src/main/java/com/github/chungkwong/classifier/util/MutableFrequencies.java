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
public class MutableFrequencies<T> implements Frequencies<T>{
	private final Map<T,Counter> frequency;
	/**
	 * Create a frequencies table backed by TreeMap
	 */
	public MutableFrequencies(){
		frequency=new TreeMap<>();
	}
	/**
	 * Create a frequencies table
	 * @param useHashMap if true, the table is backed by HashMap
	 */
	public MutableFrequencies(boolean useHashMap){
		frequency=useHashMap?new HashMap<>():new TreeMap<>();
	}
	/**
	 * Increase the frequency of a given object by one
	 * @param token the given object
	 */
	public void advanceFrequency(T token){
		Counter counter=frequency.get(token);
		if(counter==null){
			counter=new Counter(1);
			frequency.put(token,counter);
		}else{
			counter.advance();
		}
	}
	/**
	 * Increase the frequency of a given object by a given value
	 * @param token the given object
	 * @param times the given value
	 */
	public void advanceFrequency(T token,long times){
		Counter counter=frequency.get(token);
		if(counter==null){
			counter=new Counter(times);
			frequency.put(token,counter);
		}else{
			counter.advance(times);
		}
	}
	/**
	 * Merge frequencies into this table
	 * @param toMerge the source
	 */
	public void merge(MutableFrequencies<T> toMerge){
		toMerge.frequency.forEach((k,v)->advanceFrequency(k,v.getCount()));
	}
	/**
	 * Merge frequencies into this table
	 * @param toMerge the source
	 */
	public void merge(ImmutableFrequencies<T> toMerge){
		toMerge.toMap().forEach((k,v)->advanceFrequency(k,v));
	}
	/**
	 * Set the frequency of a object to zero
	 * @param token the object
	 */
	public void reset(T token){
		frequency.remove(token);
	}
	@Override
	public long getFrequency(T token){
		Counter counter=frequency.get(token);
		return counter==null?0:counter.getCount();
	}
	@Override
	public int getTokenCount(){
		return frequency.size();
	}
	/**
	 * Map representation of the table
	 * @return the map
	 */
	public Map<T,Counter> toMap(){
		return frequency;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof MutableFrequencies&&Objects.equals(frequency,((MutableFrequencies)obj).frequency);
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
