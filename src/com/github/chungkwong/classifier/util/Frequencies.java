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
import java.util.*;
/**
 *
 * @author kwong
 */
public class Frequencies<T>{
	private final Map<T,Counter> frequency;
	public Frequencies(){
		frequency=new TreeMap<>();
	}
	public Frequencies(boolean useHashMap){
		frequency=useHashMap?new HashMap<>():new TreeMap<>();
	}
	public void advanceFrequency(T token){
		Counter counter=frequency.get(token);
		if(counter==null){
			counter=new Counter(1);
			frequency.put(token,counter);
		}else{
			counter.advance();
		}
	}
	public void advanceFrequency(T token,long times){
		Counter counter=frequency.get(token);
		if(counter==null){
			counter=new Counter(times);
			frequency.put(token,counter);
		}else{
			counter.advance(times);
		}
	}
	public void merge(Frequencies<T> toMerge){
		toMerge.frequency.forEach((k,v)->advanceFrequency(k,v.getCount()));
	}
	public long getFrequency(T token){
		Counter counter=frequency.get(token);
		return counter==null?0:counter.getCount();
	}
	public int getTokenCount(){
		return frequency.size();
	}
	public Map<T,Counter> toMap(){
		return frequency;
	}
}
