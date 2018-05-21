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
package com.github.chungkwong.classifier.validator;
import java.util.*;
import java.util.stream.*;
/**
 * Utility being used to divide dataset
 * @author Chan Chung Kwong
 */
public class DataDivider{
	/**
	 * Partition dataset into train set and test set randomly.
	 * All sample are cached in RAM, so randomSplit should be used instead for
	 * large dataset.
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @param trainRatio the ratio of data used for train
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> randomSplitInRam(DataSet<T> dataset,double trainRatio){
		List<Sample<T>> list=dataset.getSamples().collect(Collectors.toList());
		Collections.shuffle(list);//FIXME: all data are loaded to RAM
		int cut=(int)(list.size()*trainRatio);
		String name=dataset.getName()+"(train=random"+trainRatio+')';
		return new SplitDataSet<>(()->list.subList(0,cut).stream(),()->list.subList(cut,list.size()).stream(),name);
	}
	/**
	 * Partition dataset into train set and test set sequentially.
	 * All sample are cached in RAM, so randomSplit should be used instead for
	 * large dataset.
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @param trainRatio the ratio of data used for train
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> sequentialSplitInRam(DataSet<T> dataset,double trainRatio){
		List<Sample<T>> list=dataset.getSamples().collect(Collectors.toList());
		int cut=(int)(list.size()*trainRatio);
		String name=dataset.getName()+"(train=first"+trainRatio+')';
		return new SplitDataSet<>(()->list.subList(0,cut).stream(),()->list.subList(cut,list.size()).stream(),name);
	}
	/**
	 * Partition dataset into train set and test set randomly
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @param trainRatio the ratio of data used for train
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> randomSplit(DataSet<T> dataset,double trainRatio){
		int count=(int)dataset.getSamples().count();
		BitSet bitSet=new BitSet(count);
		for(int i=0;i<count;i++)
			bitSet.set(i,Math.random()<trainRatio);
		String name=dataset.getName()+"(train=random"+trainRatio+')';
		return new SplitDataSet<>(()->getTrainStream(bitSet,dataset),()->getTestStream(bitSet,dataset),name);
	}
	private static <T> Stream<Sample<T>> getTrainStream(BitSet bitSet,DataSet<T> dataset){
		int[] index=new int[]{0};
		return dataset.getSamples().filter((sample)->bitSet.get(index[0]++));
	}
	private static <T> Stream<Sample<T>> getTestStream(BitSet bitSet,DataSet<T> dataset){
		int[] index=new int[]{0};
		return dataset.getSamples().filter((sample)->!bitSet.get(index[0]++));
	}
	/**
	 * Partition dataset into train set and test set sequentially
	 * @param <T> the type of the data
	 * @param dataset to be divided
	 * @param trainRatio the ratio of data used for train
	 * @return split data set
	 */
	public static <T> SplitDataSet<T> sequentialSplit(DataSet<T> dataset,double trainRatio){
		long cut=(int)(dataset.getSamples().count()*trainRatio);
		String name=dataset.getName()+"(train=first"+trainRatio+')';
		return new SplitDataSet<>(()->dataset.getSamples().limit(cut),()->dataset.getSamples().skip(cut),name);
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
