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
/**
 * Factory for Classifier
 * @author kwong
 * @param <C> the type of classifier that the factory build
 * @param <M> the type of model
 * @param <T> the type of data to be classified
 */
public interface ClassifierFactory<C extends Classifier<T>,M extends Trainable<T>,T>{
	/**
	 * Get a classifier
	 * @param model the model
	 * @return the classifier
	 */
	C getClassifier(M model);
	/**
	 * Create a new model for use with this factory
	 * @return the new model
	 */
	M createModel();
}
