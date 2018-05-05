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
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class SplitDataset<T>{
	private final Supplier<Stream<Sample<T>>> trainSampleSupplier;
	private final Supplier<Stream<Sample<T>>> testSampleSupplier;
	private final String name;
	public SplitDataset(Supplier<Stream<Sample<T>>> trainSampleSupplier,Supplier<Stream<Sample<T>>> testSampleSupplier,String name){
		this.trainSampleSupplier=trainSampleSupplier;
		this.testSampleSupplier=testSampleSupplier;
		this.name=name;
	}	
	public Stream<Sample<T>> getTrainSamples(){
		return trainSampleSupplier.get();
	}
	public Stream<Sample<T>> getTestSamples(){
		return testSampleSupplier.get();
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return getName();
	}
	
}
