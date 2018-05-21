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
import java.util.function.*;
/**
 * A factory that build classifier which first preprocess data and then apply another classifier 
 * @author Chan Chung Kwong
 * @param <M> the type of the model
 * @param <T> the type of the objects to be classified
 * @param <S> the type of the objects after preprocessing
 */
public class PreprocessClassifierFactory<M extends Trainable<S>,T,S> implements ClassifierFactory<Classifier<T>,PreprocessModel<M,T,S>,T>{
	private final Function<T,S> preprocessor;
	private final ClassifierFactory<? extends Classifier<S>,M,S> baseFactory;
	/**
	 * Create a factory
	 * @param preprocessor the preprocessors
	 * @param baseFactory the based factory
	 */
	public PreprocessClassifierFactory(Function<T,S> preprocessor,ClassifierFactory<? extends Classifier<S>,M,S> baseFactory){
		this.preprocessor=preprocessor;
		this.baseFactory=baseFactory;
	}
	@Override
	public Classifier<T> getClassifier(PreprocessModel<M,T,S> model){
		return new PreprocessClassifier(preprocessor,baseFactory.getClassifier(model.getUnderlying()));
	}
	@Override
	public PreprocessModel<M,T,S> createModel(){
		return new PreprocessModel<>(baseFactory.createModel(),preprocessor);
	}
	@Override
	public String toString(){
		return "preprocessed "+baseFactory.toString();
	}
	private static class PreprocessClassifier<T,S> implements Classifier<T>{
		private final Function<T,S> preprocessor;
		private final Classifier<S> baseClassifier;
		public PreprocessClassifier(Function<T,S> preprocessor,Classifier<S> baseClassifier){
			this.preprocessor=preprocessor;
			this.baseClassifier=baseClassifier;
		}
		@Override
		public List<ClassificationResult> getCandidates(T object,int max){
			return baseClassifier.getCandidates(preprocessor.apply(object),max);
		}
	}
}
