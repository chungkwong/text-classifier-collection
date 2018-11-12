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
/**
 * Counter, i.e. mutable double precision floating point number
 * @author Chan Chung Kwong
 */
public class DoubleCounter{
	private double value;
	/**
	 * Create a counter with initial value 0
	 */
	public DoubleCounter(){
	}
	/**
	 * Create a counter
	 * @param value initial value
	 */
	public DoubleCounter(double value){
		this.value=value;
	}
	/**
	 * @return current value of the counter
	 */
	public double getValue(){
		return value;
	}
	/**
	 * Add given value to the current value of the counter
	 * @param amount to be added
	 */
	public void advance(double amount){
		value+=amount;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Counter&&((DoubleCounter)obj).value==value;
	}
	@Override
	public int hashCode(){
		int hash=5;
		hash=97*hash+Double.hashCode(value);
		return hash;
	}
	@Override
	public String toString(){
		return Double.toString(value);
	}
}
