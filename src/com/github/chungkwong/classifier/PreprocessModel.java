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
 * Model based on another model
 * @author kwong
 * @param <M> type of the backed model
 * @param <T> type of the objects to be classified
 * @param <S> type of the objects that the backed model can classify
 */
public class PreprocessModel<M extends Trainable<S>,T,S> implements Trainable<T>{
	private final M underlying;
	private final Function<T,S> preprocessor;
	/**
	 * Create a model
	 * @param underlying backed model
	 * @param preprocessor to be used to preprocess data
	 */
	public PreprocessModel(M underlying,Function<T,S> preprocessor){
		this.underlying=underlying;
		this.preprocessor=preprocessor;
	}
	/**
	 * @return the backed model
	 */
	public M getUnderlying(){
		return underlying;
	}
	/**
	 * @return the preprocessor
	 */
	public Function<T,S> getPreprocessor(){
		return preprocessor;
	}
	@Override
	public void train(T data,Category category){
		underlying.train(preprocessor.apply(data),category);
	}
}
