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
package com.github.chungkwong.classifier.validator;
import java.util.*;
import java.util.stream.*;
/**
 * Utility being used to divide dataset
 * @author kwong
 */
public class DataDivider{
	/**
	 * Partition dataset into train set and test set randomly
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @param trainRatio the ratio of data used for train
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> randomSplit(DataSet<T> dataset,double trainRatio){
		List<Sample<T>> list=dataset.getSamples().collect(Collectors.toList());
		Collections.shuffle(list);//FIXME: all data are loaded to RAM
		int cut=(int)(list.size()*trainRatio);
		String name=dataset.getName()+"(train=random"+trainRatio+')';
		return new SplitDataSet<>(()->list.subList(0,cut).stream(),()->list.subList(cut,list.size()).stream(),name);
	}
	/**
	 * Partition dataset into train set and test set sequentially
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @param trainRatio the ratio of data used for train
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> sequentialSplit(DataSet<T> dataset,double trainRatio){
		List<Sample<T>> list=dataset.getSamples().collect(Collectors.toList());
		int cut=(int)(list.size()*trainRatio);
		String name=dataset.getName()+"(train=first"+trainRatio+')';
		return new SplitDataSet<>(()->list.subList(0,cut).stream(),()->list.subList(cut,list.size()).stream(),name);
	}
	/**
	 * Use the full dataset for both train and test
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> noSplit(DataSet<T> dataset){
		return new SplitDataSet<>(()->dataset.getSamples(),()->dataset.getSamples(),dataset.getName());
	}
}
