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
import java.util.stream.*;
/**
 *
 * @author kwong
 * @param <T> token type
 */
public class ImmutableFrequencies<T>{
	private final Map<T,Long> frequency;
	public ImmutableFrequencies(Map<T,Long> frequency){
		this.frequency=frequency;
	}
	public ImmutableFrequencies(Frequencies<T> frequency){
		this.frequency=frequency.toMap().entrySet().stream().collect(
				Collectors.toMap((e)->e.getKey(),(e)->e.getValue().getCount()));
	}
	public ImmutableFrequencies(Stream<T> tokens){
		this.frequency=tokens.collect(
				Collectors.groupingBy((e)->e,Collectors.counting()));
	}
	public long getFrequency(T token){
		return frequency.getOrDefault(token,0L);
	}
	public int getTokenCount(){
		return frequency.size();
	}
	public Map<T,Long> toMap(){
		return frequency;
	}
}
