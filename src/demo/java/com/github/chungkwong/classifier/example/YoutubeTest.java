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
 * Test the classifiers on the YouTube Spam Collection form
 * http://archive.ics.uci.edu/ml/datasets/YouTube+Spam+Collection
 * @author Chan Chung Kwong
 */
public class YoutubeTest{
	//Path to the data directory
	private static final String PATH="data/YouTube-Spam-Collection-v1";
	public static void main(String[] args)throws IOException{
		DataSet<String> dataset=new DataSet<>(()->fullDataStream(),"Youtube");
		ClassifierTest.printTestResult(dataset,Locale.ENGLISH);
	}
	private static Stream<Sample<String>> fullDataStream(){
		try{
			return Files.list(new File(PATH).toPath())
					.flatMap((file)->TextDatasetHelper.csvRecords(file,3,4));
		}catch(IOException ex){
			Logger.getLogger(YoutubeTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
}
