/*
 * Copyright (C) 2018 Chan Chung Kwong
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
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.util.*;
/**
 * Test the classifiers on the SMS Spam Collection form
 * http://archive.ics.uci.edu/ml/datasets/SMS+Spam+Collection
 * @author Chan Chung Kwong
 */
public class SpamTest{
	//Path to the data file
	private static final String PATH="data/smsspamcollection/SMSSpamCollection";
	public static void main(String[] args)throws IOException{
		DataSet<String> dataset=new DataSet<>(()->TextDatasetHelper.labeledLines(new File(PATH).toPath()),"Spam");
		ClassifierTest.printTestResult(dataset,Locale.ENGLISH);
	}
}
