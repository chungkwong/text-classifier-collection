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
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author kwong
 */
public class SpamTest{
	public SpamTest(){
	}
	@Test
	public void testTfIdf() throws IOException{
		TfIdfClassifierFactory<String> tfIdfClassifierFactory=new TfIdfClassifierFactory<>();
		PreprocessClassifierFactory<Classifier<String>,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.ENGLISH)),TextPreprocessors.getWhitespaceFilter(),TextPreprocessors.getDowncaser()),
				tfIdfClassifierFactory);
		train(classifierFactory);
		Classifier<String> classifier=classifierFactory.getClassifier();
		classifierFactory=null;
		classify(classifier);
	}
	public void train(TrainableClassifierFactory<Classifier<String>,String> classifierFactory) throws IOException{
		trainDataStream().forEach((sample)->classifierFactory.train(sample.getData(),sample.getCategory()));
	}
	public void classify(Classifier<String> classifier) throws IOException{
		AtomicInteger truePositive=new AtomicInteger(0);
		AtomicInteger trueNegative=new AtomicInteger(0);
		AtomicInteger falsePositive=new AtomicInteger(0);
		AtomicInteger falseNegative=new AtomicInteger(0);
		testDataStream().forEach((sample)->{
			Category geussed=classifier.classify(sample.getData());
			if(geussed==spam&&sample.getCategory()==spam){
				truePositive.incrementAndGet();
			}else if(geussed==ham&&sample.getCategory()==ham){
				trueNegative.incrementAndGet();
			}else if(geussed==spam&&sample.getCategory()==ham){
				falsePositive.incrementAndGet();
			}else if(geussed==ham&&sample.getCategory()==spam){
				falseNegative.incrementAndGet();
			}else{
				System.err.println("Impossible");
			}
		});
		Logger.getGlobal().log(Level.INFO,"True positive:{0}",truePositive.get());
		Logger.getGlobal().log(Level.INFO,"True negative:{0}",trueNegative.get());
		Logger.getGlobal().log(Level.INFO,"False positive:{0}",falsePositive.get());
		Logger.getGlobal().log(Level.INFO,"False negative:{0}",falseNegative.get());
	}
	public Stream<Sample<String>> trainDataStream() throws IOException{
		return Files.lines(new File("data/smsspamcollection/SMSSpamCollection.train").toPath(),StandardCharsets.UTF_8)
				.map((line)->parseLine(line));
	}
	public Stream<Sample<String>> testDataStream() throws IOException{
		return Files.lines(new File("data/smsspamcollection/SMSSpamCollection.test").toPath(),StandardCharsets.UTF_8)
				.map((line)->parseLine(line));
	}
	public Stream<Sample<String>> fullDataStream() throws IOException{
		return Files.lines(new File("data/smsspamcollection/SMSSpamCollection").toPath(),StandardCharsets.UTF_8)
				.map((line)->parseLine(line));
	}
	public Sample<String> parseLine(String line){
		if(line.startsWith("spam\t")){
			return new Sample<>(line.substring(5),spam);
		}else if(line.startsWith("ham\t")){
			return new Sample<>(line.substring(4),ham);
		}else{
			throw new RuntimeException("Bad format:"+line);
		}
	}
	private final Category ham=new Category("ham");
	private final Category spam=new Category("spam");
}
