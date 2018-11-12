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
/**
 * Feature selector
 * @author Chan Chung Kwong
 * @param <M> type of model
 * @param <T> underlying type of objects to be classified
 */
public interface FeatureSelector<M extends TokenFrequenciesModel<T>,T>{
	/**
	 * Select features
	 * @param model the model
	 * @param classifierSupplier function that create classifier from model
	 * @return selected features
	 */
	Set<T> select(M model,Function<M,? extends Classifier<Frequencies<T>>> classifierSupplier);
}
