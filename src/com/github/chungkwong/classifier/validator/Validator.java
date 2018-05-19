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
 * Validator being used to evaluate classifier
 * @author kwong
 * @param <T> the type of object to be classified
 */
public class Validator<T>{
	private final Map<Pair<ClassifierFactory,SplitDataSet<T>>,ConfusionMatrix> matrices=new HashMap<>();
	/**
	 * Create a validator
	 */
	public Validator(){
	}
	/**
	 * 
	 * @param datasets
	 * @param factories
	 */
	public void validate(SplitDataSet<T>[] datasets,ClassifierFactory<? extends Classifier<T>,? extends Trainable<T>,T>[] factories){
		for(SplitDataSet<T> dataset:datasets){
			for(ClassifierFactory<? extends Classifier<T>,? extends Trainable<T>,T> factory:factories){
				validate(dataset,factory);
			}
		}
	}
	/**
	 * @return Set of ClassifierFactory appeared in the matrix
	 */
	public Set<ClassifierFactory> getClassifierFactories(){
		return matrices.keySet().stream().map((e)->e.getKey()).collect(Collectors.toSet());
	}
	/**
	 * @return Set of datasets appeared in the matrix
	 */
	public Set<SplitDataSet<T>> getDatasets(){
		return matrices.keySet().stream().map((e)->e.getValue()).collect(Collectors.toSet());
	}
	/**
	 * @param factory the classifier factory
	 * @param dataset the dataset
	 * @return the confusion matrix
	 */
	public ConfusionMatrix getFrequency(ClassifierFactory factory,SplitDataSet<T> dataset){
		return matrices.get(new Pair<>(factory,dataset));
	}
	/**
	 * Generate and record confusion matrix
	 * @param dataset 
	 * @param classifierFactory the factory for classifier being tested
	 * @return confusion matrix
	 */
	public ConfusionMatrix validate(SplitDataSet<T> dataset,ClassifierFactory classifierFactory){
		ConfusionMatrix matrix=validate(dataset.getTrainSamples(),dataset.getTestSamples(),classifierFactory);
		matrices.put(new Pair<>(classifierFactory,dataset),matrix);
		return matrix;
	}
	public ClassifierFactory selectMostAccurate(){
		Map<ClassifierFactory,Double> worst=new HashMap<>();
		matrices.forEach((k,v)->{
			Double o=worst.get(k.getKey());
			double accuracy=v.getAccuracy();
			if(o==null||accuracy<o){
				worst.put(k.getKey(),accuracy);
			}
		});
		return worst.entrySet().stream().max((e1,e2)->Double.compare(e1.getValue(),e2.getValue())).map((e)->e.getKey()).orElse(null);
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Validator&&Objects.equals(matrices,((Validator)obj).matrices);
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=79*hash+Objects.hashCode(this.matrices);
		return hash;
	}
	@Override
	public String toString(){
		ClassifierFactory[] factories=getClassifierFactories().toArray(new ClassifierFactory[0]);
		SplitDataSet[] datasets=getDatasets().toArray(new SplitDataSet[0]);
		StringBuilder buf=new StringBuilder("\n");
		matrices.forEach((k,v)->{
			buf.append("\n\n").append(k.getKey()).append('-').append(k.getValue()).append("\n");
			buf.append(Objects.toString(v));
		});
		buf.append("\n\nsummary:\n");
		for(SplitDataSet dataSet:datasets){
			buf.append('\t').append(dataSet.getName());
		}
		for(ClassifierFactory first:factories){
			buf.append('\n').append(first);
			for(SplitDataSet second:datasets){
				ConfusionMatrix matrix=matrices.get(new Pair<>(first,second));
				buf.append('\t');
				if(matrix!=null)
					buf.append(matrix.getAccuracy());
			}
		}
		return buf.toString();
	}
	/**
	 * Generate confusion matrix
	 * @param <M> the type of the model
	 * @param <T> the type of object to be classified
	 * @param trainSampleStream the source of train data
	 * @param testSampleStream the source of test data
	 * @param classifierFactory the factory for classifier being tested
	 * @return confusion matrix
	 */
	public static <M extends Trainable<T>,T> ConfusionMatrix validate(Stream<Sample<T>> trainSampleStream,Stream<Sample<T>> testSampleStream,ClassifierFactory<Classifier<T>,M,T> classifierFactory){
		return validate(testSampleStream,classifierFactory.getClassifier(train(trainSampleStream,classifierFactory)));
	}
	private static <M extends Trainable<T>,T> M train(Stream<Sample<T>> trainSampleStream,ClassifierFactory<Classifier<T>,M,T> classifierFactory){
		M model=classifierFactory.createModel();
		trainSampleStream.forEach((sample)->model.train(sample.getData(),sample.getCategory()));
		return model;
	}
	/**
	 * Generate confusion matrix
	 * @param <T> the type of object to be classified
	 * @param testSampleStream the test data source
	 * @param classifier the classifier being tested
	 * @return confusion matrix
	 */
	public static <T> ConfusionMatrix validate(Stream<Sample<T>> testSampleStream,Classifier<T> classifier){
		ConfusionMatrix table=new ConfusionMatrix();
		Long time=System.currentTimeMillis();
		testSampleStream.forEach((sample)->{
			table.advanceFrequency(sample.getCategory(),classifier.classify(sample.getData()).getCategory());
		});
		table.advanceTestTime(System.currentTimeMillis()-time);
		return table;
	}
}
