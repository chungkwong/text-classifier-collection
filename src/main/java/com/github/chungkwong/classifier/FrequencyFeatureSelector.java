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
import java.util.function.*;
import java.util.stream.*;
/**
 * Feature selector based on token frequency
 * @author Chan Chung Kwong
 * @param <M> type of model
 * @param <T> underlying type to be classified
 */
public class FrequencyFeatureSelector<M extends TokenFrequenciesModel<T>,T> implements FeatureSelector<M,T>{
	private final int start,end;
	private final boolean document;
	/**
	 * Create a feature selector
	 * @param start the lower bound of the frequency range to be kept(inclusive)
	 * @param end the upper bound of the frequency range to be kept(exclusive)
	 * @param document use document frequency instead of token frequency
	 */
	public FrequencyFeatureSelector(int start,int end,boolean document){
		this.start=start;
		this.end=end;
		this.document=document;
	}
	@Override
	public Set<T> select(M model,Function<M,? extends Classifier<Stream<T>>> classifierSupplier){
		ImmutableFrequencies<T> frequencies=document?model.getTotalDocumentFrequencies():model.getTotalTokenFrequencies();
		return frequencies.toMap().entrySet().stream().
				filter((e)->e.getValue()>=start&&e.getValue()<end).
				map((e)->e.getKey()).collect(Collectors.toSet());
	}
	@Override
	public String toString(){
		return "[Frequency"+start+","+end+","+document+"]";
	}
}
