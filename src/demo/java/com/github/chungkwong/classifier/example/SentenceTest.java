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
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 * Test the classifiers on the Sentence Classification form
 * http://archive.ics.uci.edu/ml/datasets/Sentence+Classification
 * @author Chan Chung Kwong
 */
public class SentenceTest{
	//Path to the data directory
	private static final String PATH="data/SentenceCorpus/SentenceCorpus/labeled_articles";
	public static void main(String[] args)throws IOException{
		DataSet<String> dataset=new DataSet<>(()->fullDataStream(),"Sentence");
		ClassifierTest.printTestResult(dataset,Locale.ENGLISH);
	}
	private static Stream<Sample<String>> fullDataStream(){
		try{
			return Files.list(new File(PATH).toPath())
					.flatMap((path)->TextDatasetHelper.labeledLines(path));
		}catch(IOException ex){
			Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
}
