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
 * Frequencies table
 * @author kwong
 * @param <T> type of object to be counted
 */
public interface Frequencies<T>{
	
	/**
	 * Get the frequency of a object
	 * @param token the object
	 * @return the frequency
	 */
	public long getFrequency(T token);
	/**
	 * @return the number of unique objects found
	 */
	public int getTokenCount();
}
