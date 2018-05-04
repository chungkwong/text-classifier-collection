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
import com.github.chungkwong.classifier.util.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class ClassifierTest<T>{
	private final Supplier<Stream<Sample<T>>> trainDataSource;
	private final Supplier<Stream<Sample<T>>> testDataSource;
	public ClassifierTest(Supplier<Stream<Sample<T>>> trainDataSource,Supplier<Stream<Sample<T>>> testDataSource){
		this.trainDataSource=trainDataSource;
		this.testDataSource=testDataSource;
	}
	public Frequencies<Pair<Category,Category>> test(TrainableClassifierFactory<Classifier<T>,T> classifierFactory){
		train(classifierFactory);
		Classifier<T> classifier=classifierFactory.getClassifier();
		classifierFactory=null;
		return classify(classifier);
	}
	private void train(TrainableClassifierFactory<Classifier<T>,T> classifierFactory){
		trainDataSource.get().forEach((sample)->classifierFactory.train(sample.getData(),sample.getCategory()));
	}
	private Frequencies<Pair<Category,Category>> classify(Classifier<T> classifier){
		Frequencies<Pair<Category,Category>> table=new Frequencies<>(true);
		testDataSource.get().forEach((sample)->{
			table.advanceFrequency(new Pair<>(sample.getCategory(),classifier.classify(sample.getData())));
		});
		return table;
	}
	public static TrainableClassifierFactory<Classifier<String>,String> getEnglishTfIdfClassifierFactory(){
		return getEnglishClassifierFactory(new TfIdfClassifierFactory<>());
	}
	public static TrainableClassifierFactory<Classifier<String>,String> getEnglishClassifierFactory(TrainableClassifierFactory<Classifier<Stream<String>>,Stream<String>> base){
		PreprocessClassifierFactory<Classifier<String>,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.ENGLISH)),TextPreprocessors.getWhitespaceFilter(),TextPreprocessors.getDowncaser()),
				base);
		return classifierFactory;
	}
	public static TrainableClassifierFactory<Classifier<String>,String> getChineseTfIdfClassifierFactory(){
		return getChineseClassifierFactory(new TfIdfClassifierFactory<>());
	}
	public static TrainableClassifierFactory<Classifier<String>,String> getChineseClassifierFactory(TrainableClassifierFactory<Classifier<Stream<String>>,Stream<String>> base){
		PreprocessClassifierFactory<Classifier<String>,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(TextPreprocessors.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE)),TextPreprocessors.getWhitespaceFilter(),TextPreprocessors.getNgramGenerator(2)),
				base);
		return classifierFactory;
	}
	public static String toString(Frequencies<Pair<Category,Category>> frequencies){
		HashSet<Category> categories=new HashSet<>();
		frequencies.toMap().forEach((k,v)->{
			categories.add(k.getKey());
			categories.add(k.getValue());
		});
		Category[] cats=categories.toArray(new Category[0]);
		StringBuilder buf=new StringBuilder("\n");
		for(Category category:cats){
			buf.append('\t').append(category);
		}
		for(Category first:cats){
			buf.append('\n').append(first);
			for(Category second:cats){
				buf.append('\t').append(frequencies.getFrequency(new Pair<>(first,second)));
			}
		}
		return buf.toString();
	}
}
