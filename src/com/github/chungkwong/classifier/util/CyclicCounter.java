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
 *
 * @author kwong
 */
public class CyclicCounter{
	private int count;
	private final int cycle;
	public CyclicCounter(int cycle){
		this.cycle=cycle;
	}
	public CyclicCounter(int count,int cycle){
		this.count=count;
		this.cycle=cycle;
	}
	public void advance(){
		++count;
		if(count==cycle){
			count=0;
		}
	}
	public int getCount(){
		return count;
	}
	public int getCycle(){
		return cycle;
	}
}