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
import java.util.stream.*;
/**
 * Records of frequency of different objects that do not tends to change
 * @author Chan Chung Kwong
 * @param <T> the type of the objects to be recorded
 */
public class ImmutableFrequencies<T>{
	private final Map<T,Long> frequency;
	/**
	 * Create a frequencies table
	 * @param frequency the source
	 */
	public ImmutableFrequencies(Map<T,Long> frequency){
		this.frequency=frequency;
	}
	/**
	 * Create a frequencies table
	 * @param frequency the source
	 */
	public ImmutableFrequencies(Frequencies<T> frequency){
		this.frequency=frequency.toMap().entrySet().stream().collect(
				Collectors.toMap((e)->e.getKey(),(e)->e.getValue().getCount()));
	}
	/**
	 * Create a frequencies table
	 * @param tokens the objects to be recorded
	 */
	public ImmutableFrequencies(Stream<T> tokens){
		this.frequency=tokens.collect(
				Collectors.groupingBy((e)->e,Collectors.counting()));
	}
	/**
	 * Get the frequency of a object
	 * @param token the object
	 * @return the frequency
	 */
	public long getFrequency(T token){
		return frequency.getOrDefault(token,0L);
	}
	/**
	 * @return the number of unique objects found
	 */
	public int getTokenCount(){
		return frequency.size();
	}
	/**
	 * Map representation of the table
	 * @return the map
	 */
	public Map<T,Long> toMap(){
		return frequency;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof ImmutableFrequencies&&Objects.equals(frequency,((ImmutableFrequencies)obj).frequency);
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
