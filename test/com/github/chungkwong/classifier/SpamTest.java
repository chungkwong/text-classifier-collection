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
import java.util.logging.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author kwong
 */
public class SpamTest{
	private final ClassifierTest<String> fullTester;
	private final ClassifierTest<String> partialTester;
	public SpamTest(){
		fullTester=new ClassifierTest<>(()->fullDataStream(),()->fullDataStream());
		partialTester=new ClassifierTest<>(()->trainDataStream(),()->testDataStream());
	}
	@Test
	public void testTfIdfFull() throws IOException{
		Logger.getGlobal().log(Level.INFO,"SPAM TF-IDF: {0}",ClassifierTest.toString(fullTester.test(ClassifierTest.getEnglishTfIdfClassifierFactory())));
	}
	@Test
	public void testBayesian() throws IOException{
		Logger.getGlobal().log(Level.INFO,"SPAM Bayesian predict: {0}",ClassifierTest.toString(partialTester.test(ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>()))));
	}
	@Test
	public void testTfIdf() throws IOException{
		Logger.getGlobal().log(Level.INFO,"SPAM TF-IDF predict: {0}",ClassifierTest.toString(partialTester.test(ClassifierTest.getEnglishTfIdfClassifierFactory())));
	}
	@Test
	public void testBayesianFull() throws IOException{
		Logger.getGlobal().log(Level.INFO,"SPAM Bayesian: {0}",ClassifierTest.toString(fullTester.test(ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>()))));
	}
	public Stream<Sample<String>> trainDataStream(){
		try{
			return Files.lines(new File("data/smsspamcollection/SMSSpamCollection.train").toPath(),StandardCharsets.UTF_8)
					.map((line)->parseLine(line));
		}catch(IOException ex){
			Logger.getLogger(SpamTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	public Stream<Sample<String>> testDataStream(){
		try{
			return Files.lines(new File("data/smsspamcollection/SMSSpamCollection.test").toPath(),StandardCharsets.UTF_8)
					.map((line)->parseLine(line));
		}catch(IOException ex){
			Logger.getLogger(SpamTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	public Stream<Sample<String>> fullDataStream(){
		try{
			return Files.lines(new File("data/smsspamcollection/SMSSpamCollection").toPath(),StandardCharsets.UTF_8)
					.map((line)->parseLine(line));
		}catch(IOException ex){
			Logger.getLogger(SpamTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
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
