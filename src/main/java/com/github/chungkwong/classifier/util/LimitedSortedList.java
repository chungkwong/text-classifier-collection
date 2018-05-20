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
 * List that only keep limited number of elements in ascending order
 * @author kwong
 * @param <T> type of elements
 */
public class LimitedSortedList<T>{
	private final int k;
	private final List<T> base;
	private final Comparator<T> comparator;
	/**
	 * Create a LimitedSortedList
	 * @param limit number of elements to be kept
	 * @param comparator being used to compare elements
	 */
	public LimitedSortedList(int limit,Comparator<T> comparator){
		this.k=limit;
		this.base=new ArrayList<>(limit);
		this.comparator=comparator;
	}
	/**
	 * Add a element to the list
	 * @param e the element
	 */
	public void add(T e){
		for(int i=0;i<base.size();i++){
			if(comparator.compare(e,base.get(i))<0){
				if(base.size()>=k)
					base.remove(base.size()-1);
				base.add(i,e);
				return;
			}
		}
		if(base.size()<k)
			base.add(e);
	}
	/**
	 * @return the elements of the list
	 */
	public List<T> getElements(){
		return base;
	}
}
