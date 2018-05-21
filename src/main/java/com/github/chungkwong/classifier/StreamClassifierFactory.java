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
import java.util.stream.*;
/**
 * Classifier factory for stream
 * @author Chan Chung Kwong
 * @param <C> the type of classifier that the factory build
 * @param <M> the type of model
 * @param <T> the type of underlying data to be classified
 */
public abstract class StreamClassifierFactory<C extends Classifier<Stream<T>>,M extends TokenFrequenciesModel<T>,T> implements ClassifierFactory<C,M,Stream<T>>{
	private FeatureSelector<M,T> featureSelector;
	/**
	 * @return the feature selector
	 */
	public FeatureSelector<M,T> getFeatureSelector(){
		return featureSelector;
	}
	/**
	 * Set feature selector
	 * @param featureSelector to be set
	 * @return this
	 */
	public StreamClassifierFactory<C,M,T> setFeatureSelector(FeatureSelector<M,T> featureSelector){
		this.featureSelector=featureSelector;
		return this;
	}
	@Override
	public C getClassifier(M model){
		if(featureSelector!=null)
			model.retainAll(featureSelector.select(model,this::createClassifier));
		return createClassifier(model);
	}
	/**
	 * Create a classifier from a model
	 * @param model the model
	 * @return the classifier
	 */
	protected abstract C createClassifier(M model);
	/**
	 * @return the name of the factory
	 */
	protected abstract String getName();
	@Override
	public String toString(){
		if(getFeatureSelector()!=null)
			return getName()+getFeatureSelector().toString();
		else
			return getName();
	}
	
}
