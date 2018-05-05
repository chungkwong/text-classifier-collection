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
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.util.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class Validator<T>{
	private final Map<Pair<String,String>,ConfusionMatrix> matrices=new HashMap<>();
	public Validator(){
	}
	public void validate(SplitDataset<T>[] dataset,ClassifierFactoryBuilder<T>[] builder){
		for(int i=0;i<dataset.length;i++)
			for(int j=0;i<builder.length;j++)
				validate(dataset[i],builder[j]);
	}
	public ConfusionMatrix validate(SplitDataset<T> dataset,ClassifierFactoryBuilder<T> builder){
		ConfusionMatrix matrix=validate(dataset.getTrainSamples(),dataset.getTestSamples(),builder.getFactory());
		matrices.put(new Pair<>(dataset.getName(),builder.getName()),matrix);
		return matrix;
	}
	public static <T> ConfusionMatrix validate(Stream<Sample<T>> trainSampleStream,Stream<Sample<T>> testSampleStream,TrainableClassifierFactory<Classifier<T>,T> classifierFactory){
		train(trainSampleStream,classifierFactory);
		Classifier<T> classifier=classifierFactory.getClassifier();
		classifierFactory=null;
		return validate(testSampleStream,classifier);
	}
	private static <T> void train(Stream<Sample<T>> trainSampleStream,TrainableClassifierFactory<Classifier<T>,T> classifierFactory){
		trainSampleStream.forEach((sample)->classifierFactory.train(sample.getData(),sample.getCategory()));
	}
	public static <T> ConfusionMatrix validate(Stream<Sample<T>> testSampleStream,Classifier<T> classifier){
		ConfusionMatrix table=new ConfusionMatrix();
		Long time=System.currentTimeMillis();
		testSampleStream.forEach((sample)->{
			table.advanceFrequency(sample.getCategory(),classifier.classify(sample.getData()).getCategory());
		});
		table.setTestTime(System.currentTimeMillis()-time);
		return table;
	}
}
