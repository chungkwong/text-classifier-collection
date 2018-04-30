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
import java.util.function.*;
/**
 *
 * @author kwong
 */
public class PreprocessClassifierFactory<C extends Classifier<T>,T,S> implements TrainableClassifierFactory<Classifier<T>,T>{
	private final Function<T,S> preprocessor;
	private final TrainableClassifierFactory<Classifier<S>,S> baseFactory;
	public PreprocessClassifierFactory(Function<T,S> preprocessor,TrainableClassifierFactory<Classifier<S>,S> baseFactory){
		this.preprocessor=preprocessor;
		this.baseFactory=baseFactory;
	}
	@Override
	public void train(T data,Category category){
		baseFactory.train(preprocessor.apply(data),category);
	}
	@Override
	public Classifier<T> getClassifier(){
		return new PreprocessClassifier(preprocessor,baseFactory.getClassifier());
	}
}
class PreprocessClassifier<T,S> implements Classifier<T>{
	private final Function<T,S> preprocessor;
	private final Classifier<S> baseClassifier;
	public PreprocessClassifier(Function<T,S> preprocessor,Classifier<S> baseClassifier){
		this.preprocessor=preprocessor;
		this.baseClassifier=baseClassifier;
	}
	@Override
	public Category classify(T object){
		return baseClassifier.classify(preprocessor.apply(object));
	}

}
