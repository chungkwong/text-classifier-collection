/*
 * Copyright (C) 2018 kwong
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
/**
 * A modulus counter 
 * @author kwong
 */
public class CyclicCounter{
	private int count;
	private final int cycle;
	/**
	 * Create a counter with initial value 0
	 * @param cycle the modulus
	 */
	public CyclicCounter(int cycle){
		this.cycle=cycle;
	}
	/**
	 * Create a counter
	 * @param cycle the modulus
	 * @param count the initial value
	 */
	public CyclicCounter(int cycle,int count){
		this.count=(count+cycle)%cycle;
		this.cycle=cycle;
	}
	/**
	 * Advance the current value by one
	 */
	public void advance(){
		++count;
		if(count==cycle){
			count=0;
		}
	}
	/**
	 * @return current value of the counter
	 */
	public int getCount(){
		return count;
	}
	/**
	 * @return the modulus
	 */
	public int getCycle(){
		return cycle;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof CyclicCounter&&((CyclicCounter)obj).count==count&&((CyclicCounter)obj).cycle==cycle;
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=23*hash+this.count;
		hash=23*hash+this.cycle;
		return hash;
	}
	@Override
	public String toString(){
		return Long.toString(count);
	}
}
