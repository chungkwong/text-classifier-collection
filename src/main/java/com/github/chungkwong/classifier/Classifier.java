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
package com.github.chungkwong.classifier;
import java.util.*;
/**
 * A classifier
 * @author Chan Chung Kwong
 * @param <T> the type of the objects to be classified
 */
public interface Classifier<T>{
	/**
	 * Classify a object
	 * @param object to be classified
	 * @return the best result of classification
	 */
	default ClassificationResult classify(T object){
		return getCandidates(object,1).stream().min((r1,r2)->r1.compareTo(r2)).orElse(null);
	}
	/**
	 * Classify a object
	 * @param object to be classified
	 * @return the results of classification
	 */
	default List<ClassificationResult> getCandidates(T object){
		return getCandidates(object,Integer.MAX_VALUE);
	}
	/**
	 * Classify a object
	 * @param object to be classified
	 * @param max the number of top scored results required
	 * @return the results of classification
	 */
	List<ClassificationResult> getCandidates(T object,int max);
}
