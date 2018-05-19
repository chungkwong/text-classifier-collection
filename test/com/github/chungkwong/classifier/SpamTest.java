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
import java.util.logging.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author kwong
 */
public class SpamTest{
	@Test
	public void test() throws IOException{
		Validator<String> validator=new Validator<>();
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> factory1=ClassifierTest.getEnglishTfIdfClassifierFactory();
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> factory2=ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>());
		PreprocessClassifierFactory<DocumentVectorsModel<String>,String,Stream<String>> factory3=ClassifierTest.getEnglishClassifierFactory(new NearestClassifierFactory<>());
		PreprocessClassifierFactory<DocumentVectorsModel<String>,String,Stream<String>> factory4=ClassifierTest.getEnglishClassifierFactory(new SvmClassifierFactory<>());
		DataSet<String> dataset=new DataSet<>(()->TextDatasetHelper.labeledLines(new File("data/smsspamcollection/SMSSpamCollection").toPath()),"Spam");
		ClassifierFactory[] classifierFactories=new ClassifierFactory[]{factory1,factory2,factory4};
		validator.validate(new SplitDataSet[]{DataDivider.randomSplit(dataset,0.7)},classifierFactories);
		validator.validate(new SplitDataSet[]{DataDivider.sequentialSplit(dataset,0.7)},classifierFactories);
		validator.validate(new SplitDataSet[]{DataDivider.noSplit(dataset)},classifierFactories);
		Logger.getGlobal().log(Level.INFO,validator.toString());
		Logger.getGlobal().log(Level.INFO,validator.selectMostAccurate().toString());
	}
}
