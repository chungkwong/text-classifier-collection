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
public class SentenceTest{
	@Test
	public void testTfIdf() throws IOException{
		ClassifierTest<String> tester=new ClassifierTest<>(()->fullDataStream(),()->fullDataStream());
		Logger.getGlobal().log(Level.INFO,"SENTENCE TF-IDF: {0}",ClassifierTest.toString(tester.test(ClassifierTest.getEnglishTfIdfClassifierFactory())));
	}
	@Test
	public void testBayesian() throws IOException{
		ClassifierTest<String> tester=new ClassifierTest<>(()->fullDataStream(),()->fullDataStream());
		Logger.getGlobal().log(Level.INFO,"SENTENCE Bayesian: {0}",ClassifierTest.toString(tester.test(ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>()))));
	}
	@Test
	public void testTfIdfOnWordList() throws IOException{
		ClassifierTest<String> tester=new ClassifierTest<>(()->trainDataStream(),()->fullDataStream());
		Logger.getGlobal().log(Level.INFO,"SENTENCE Word: {0}",ClassifierTest.toString(tester.test(ClassifierTest.getEnglishClassifierFactory(new TfIdfClassifierFactory<String>().setTfIdfFormula(TfIdfClassifierFactory.THREHOLD)))));
	}
	@Test
	public void testBayesianOnWordList() throws IOException{
		ClassifierTest<String> tester=new ClassifierTest<>(()->trainDataStream(),()->fullDataStream());
		Logger.getGlobal().log(Level.INFO,"SENTENCE Bayesian Word: {0}",ClassifierTest.toString(tester.test(ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>()))));
	}
	public Stream<Sample<String>> fullDataStream(){
		try{
			return Files.list(new File("data/SentenceCorpus/SentenceCorpus/labeled_articles").toPath())
					.flatMap((path)->{
						try{
							return Files.lines(path,StandardCharsets.UTF_8).filter((line)->!line.startsWith("#"));
						}catch(IOException ex){
							Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
							return Stream.empty();
						}
					})
					.map((line)->parseLine(line));
		}catch(IOException ex){
			Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	public Stream<Sample<String>> trainDataStream(){
		try{
			return Stream.of(new Sample<>(Files.lines(new File("data/SentenceCorpus/SentenceCorpus/word_lists/aim.txt").toPath(),StandardCharsets.UTF_8).collect(Collectors.joining(" ")),new Category("AIMX")),
					new Sample<>(Files.lines(new File("data/SentenceCorpus/SentenceCorpus/word_lists/base.txt").toPath(),StandardCharsets.UTF_8).collect(Collectors.joining(" ")),new Category("BASE")),
					new Sample<>(Files.lines(new File("data/SentenceCorpus/SentenceCorpus/word_lists/contrast.txt").toPath(),StandardCharsets.UTF_8).collect(Collectors.joining(" ")),new Category("CONT")),
					new Sample<>(Files.lines(new File("data/SentenceCorpus/SentenceCorpus/word_lists/own.txt").toPath(),StandardCharsets.UTF_8).collect(Collectors.joining(" ")),new Category("OWNX")));
		}catch(IOException ex){
			Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	public Sample<String> parseLine(String line){
		return new Sample<>(line.substring(5),new Category(line.substring(0,4)));
	}
}
