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
import java.util.function.*;
import java.util.stream.*;
/**
 * Dataset split into train set and test set
 * @author Chan Chung Kwong
 * @param <T> type of sample data
 */
public class SplitDataSet<T>{
	private final Supplier<Stream<Sample<T>>> trainSampleSupplier;
	private final Supplier<Stream<Sample<T>>> testSampleSupplier;
	private final String name;
	/**
	 * Create a dataset
	 * @param trainSampleSupplier to be used to generate train samples
	 * @param testSampleSupplier to be used to generate test samples
	 * @param name the name
	 */
	public SplitDataSet(Supplier<Stream<Sample<T>>> trainSampleSupplier,Supplier<Stream<Sample<T>>> testSampleSupplier,String name){
		this.trainSampleSupplier=trainSampleSupplier;
		this.testSampleSupplier=testSampleSupplier;
		this.name=name;
	}
	/**
	 * @return train samples
	 */
	public Stream<Sample<T>> getTrainSamples(){
		return trainSampleSupplier.get();
	}
	/**
	 * @return test samples
	 */
	public Stream<Sample<T>> getTestSamples(){
		return testSampleSupplier.get();
	}
	/**
	 * @return the name of the dataset
	 */
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return getName();
	}
}
