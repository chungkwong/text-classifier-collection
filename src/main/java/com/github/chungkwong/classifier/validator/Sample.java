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
package com.github.chungkwong.classifier.validator;
import com.github.chungkwong.classifier.Category;
import java.util.*;
/**
 * Sample data and its category
 * @author Chan Chung Kwong
 * @param <T> the type of the data
 */
public class Sample<T>{
	private final T data;
	private final Category category;
	/**
	 * Create a sample
	 * @param data the data
	 * @param category the category
	 */
	public Sample(T data,Category category){
		this.category=category;
		this.data=data;
	}
	/**
	 * @return the data
	 */
	public T getData(){
		return data;
	}
	/**
	 * @return the category
	 */
	public Category getCategory(){
		return category;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Sample&&
				Objects.equals(((Sample)obj).category,category)&&
				Objects.equals(((Sample)obj).data,data);
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=73*hash+Objects.hashCode(this.data);
		hash=73*hash+Objects.hashCode(this.category);
		return hash;
	}
	@Override
	public String toString(){
		return category+":"+data;
	}
}
