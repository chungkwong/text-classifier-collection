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
import com.github.chungkwong.classifier.validator.*;
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
	public void testTfIdf() throws IOException{		Validator<String> validator=new Validator<>();
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> factory1=ClassifierTest.getEnglishTfIdfClassifierFactory();
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> factory2=ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>());
		DataSet<String> dataset=new DataSet<>(()->fullDataStream(),"Sentence");
		validator.validate(new SplitDataSet[]{DataDivider.randomSplit(dataset,0.7)},new ClassifierFactory[]{factory1,factory2});
		validator.validate(new SplitDataSet[]{DataDivider.sequentialSplit(dataset,0.7)},new ClassifierFactory[]{factory1,factory2});
		validator.validate(new SplitDataSet[]{DataDivider.noSplit(dataset)},new ClassifierFactory[]{factory1,factory2});
		Logger.getGlobal().log(Level.INFO,validator.toString());
		Logger.getGlobal().log(Level.INFO,validator.selectMostAccurate().toString());
	}
	@Test
	public void testTfIdfOnWordList() throws IOException{
		Logger.getGlobal().log(Level.INFO,"SENTENCE Word: {0}",Validator.validate(trainDataStream(),fullDataStream(),ClassifierTest.getEnglishClassifierFactory(new TfIdfClassifierFactory<String>().setTfIdfFormula(TfIdfClassifierFactory.THREHOLD))));
	}
	@Test
	public void testBayesianOnWordList() throws IOException{
		Logger.getGlobal().log(Level.INFO,"SENTENCE Bayesian Word: {0}",Validator.validate(trainDataStream(),fullDataStream(),ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>())));
	}
	public Stream<Sample<String>> fullDataStream(){
		try{
			return Files.list(new File("data/SentenceCorpus/SentenceCorpus/labeled_articles").toPath())
					.flatMap((path)->TextDatasetHelper.labeledLines(path));
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
}
