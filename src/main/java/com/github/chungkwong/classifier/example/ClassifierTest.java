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
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.util.*;
import java.util.logging.*;
/**
 * Some common method useful for the demo
 * @author kwong
 */
public class ClassifierTest{
	public static void printTestResult(DataSet<String> dataset,Locale locale){
		Validator<String> validator=new Validator<>();
		ClassifierFactory[] classifierFactories=ClassifierTest.getStandardClassifierFactories(locale);
		SplitDataSet[] splitDataSets=ClassifierTest.getSplitDataSets(dataset);
		validator.validate(splitDataSets,classifierFactories);
		Logger.getGlobal().log(Level.INFO,validator.toString());
		Logger.getGlobal().log(Level.INFO,"Best: {0}",validator.selectMostAccurate().toString());
	}
	private static ClassifierFactory[] getStandardClassifierFactories(Locale locale){
		ClassifierFactory factory1=Starter.getDefaultClassifierFactory(locale,false,new TfIdfClassifierFactory());
		ClassifierFactory factory2=Starter.getDefaultClassifierFactory(locale,false,new BayesianClassifierFactory());
		ClassifierFactory factory3=Starter.getDefaultClassifierFactory(locale,false,new KNearestClassifierFactory().setK(3));
		ClassifierFactory factory4=Starter.getDefaultClassifierFactory(locale,false,new SvmClassifierFactory());
		ClassifierFactory factory5=Starter.getDefaultClassifierFactory(locale,true,new TfIdfClassifierFactory());
		ClassifierFactory factory6=Starter.getDefaultClassifierFactory(locale,true,new BayesianClassifierFactory());
		ClassifierFactory factory7=Starter.getDefaultClassifierFactory(locale,true,new KNearestClassifierFactory().setK(3));
		ClassifierFactory factory8=Starter.getDefaultClassifierFactory(locale,true,new SvmClassifierFactory());
		return new ClassifierFactory[]{factory1,factory2,factory4,factory5,factory6,factory8};
	}
	private static SplitDataSet[] getSplitDataSets(DataSet<String> dataSet){
		return new SplitDataSet[]{
			DataDivider.randomSplit(dataSet,0.7),
			DataDivider.sequentialSplit(dataSet,0.7),
			DataDivider.noSplit(dataSet)};
	}
}
