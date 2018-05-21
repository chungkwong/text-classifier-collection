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
package com.github.chungkwong.classifier;
import com.github.chungkwong.classifier.util.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 * Feature selector based on Tf-Idf
 * @author kwong
 * @param <M> type of model
 * @param <T> underlying type to be classified
 */
public class TfIdfFeatureSelector<M extends TokenFrequenciesModel<T>,T> implements FeatureSelector<M,T>{
	private final int count;
	private final TfIdfFormula formula;
	/**
	 * Create a feature selector
	 * @param count the number of features to be kept
	 */
	public TfIdfFeatureSelector(int count){
		this.count=count;
		this.formula=TfIdfFormula.STANDARD;
	}
	/**
	 * Create a feature selector
	 * @param count the number of features to be kept
	 * @param formula the Tf-Idf formula
	 */
	public TfIdfFeatureSelector(int count,TfIdfFormula formula){
		this.count=count;
		this.formula=formula;
	}
	@Override
	public Set<T> select(M model,Function<M,? extends Classifier<Stream<T>>> classifierSupplier){
		LimitedSortedList<Pair<T,Double>> list=new LimitedSortedList<>(count,(p1,p2)->Double.compare(p2.getValue(),p1.getValue()));
		ImmutableFrequencies<T> documentFrequencies=model.getTotalDocumentFrequencies();
		ImmutableFrequencies<T> tokenFrequencies=model.getTotalTokenFrequencies();
		long sampleCount=model.getSampleCount();
		documentFrequencies.toMap().forEach((token,docFreq)->list.add(new Pair<>(token,
				formula.calculate(tokenFrequencies.getFrequency(token),docFreq,sampleCount))));
		return list.getElements().stream().map((p)->p.getKey()).collect(Collectors.toSet());
	}
	@Override
	public String toString(){
		return "[TfIdfFeatureSelector"+count+"]";
	}
}
