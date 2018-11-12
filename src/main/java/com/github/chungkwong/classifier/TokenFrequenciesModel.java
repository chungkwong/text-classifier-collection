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
import com.github.chungkwong.classifier.util.*;
import java.util.*;
/**
 * The common interface for token frequencies based model
 * @author Chan Chung Kwong
 * @param <T> type of the object to be classified
 */
public interface TokenFrequenciesModel<T> extends Trainable<Frequencies<T>>{
	/**
	 * @return the number of samples trained
	 */
	long getSampleCount();
	/**
	 * @return the frequencies table for each category
	 */
	Map<Category,Frequencies<T>> getTokenFrequencies();
	/**
	 * @return the number of samples that contains each token 
	 */
	Frequencies<T> getTotalDocumentFrequencies();
	/**
	 * @return the frequency of each token in all samples 
	 */
	Frequencies<T> getTotalTokenFrequencies();
	/**
	 * @return the number of samples in each category
	 */
	Frequencies<Category> getSampleCounts();
	/**
	 * @return the number of unique tokens in each category
	 */
	Frequencies<Category> getTokenCounts();
	/**
	 * Retain only the tokens that are contained in a given set
	 * @param toKeep the tokens to be kept
	 */
	void retainAll(Set<T> toKeep);
	/**
	 * @return the histogram of tokens
	 */
	default Frequencies<Long> getTokenHistogram(){
		Frequencies<Long> histogram=new Frequencies<>();
		getTotalTokenFrequencies().toMap().forEach((k,v)->histogram.advanceFrequency(v.getCount()));
		return histogram;
	}
	/**
	 * Get the quantile of token histogram
	 * @param q the level
	 * @return the quantile
	 */
	default long[] getQuantile(double... q){
		long[] acc=new long[q.length];
		long[] quantile=new long[q.length];
		Frequencies<Long> histogram=getTokenHistogram();
		long total=histogram.toMap().values().stream().mapToLong((c)->c.getCount()).sum();
		histogram.toMap().forEach((k,v)->{
			for(int i=0;i<q.length;i++){
				if(acc[i]<total*q[i]){
					acc[i]+=v.getCount();
					quantile[i]=k;
				}
			}
		});
		return quantile;
	}
}
