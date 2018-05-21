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
import com.github.chungkwong.classifier.Category;
import com.github.chungkwong.classifier.validator.Sample;
import com.github.chungkwong.classifier.util.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 * Test the classifiers on the THUCNews dataset
 * http://thuctc.thunlp.org/
 * @author kwong
 */
public class NewsTest{
	//Path to the data directory
	private static final String PATH="data/THUCNews/small";
	public static void main(String[] args)throws IOException{
		DataSet<String> dataset=new DataSet<>(()->fullDataStream(),"THUCNews");
		ClassifierTest.printTestResult(dataset,Locale.CHINESE);
	}
	/*public void testTfIdf() throws IOException{
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> classifierFactory=ClassifierTest.getChineseClassifierFactory(new TfIdfClassifierFactory<>());
		PreprocessModel<FrequenciesModel<String>,String,Stream<String>> model=classifierFactory.createModel();
		model.getUnderlying().load(new File("data/THUCNews/stat"),(x)->x);
		Logger.getGlobal().log(Level.INFO,"SENTENCE TF-IDF: {0}",Validator.validate(fullDataStream(),classifierFactory.getClassifier(model)));
	}*/
	private static Stream<Sample<String>> fullDataStream(){
		try{
			long time=System.currentTimeMillis();
			Counter c=new Counter();
			return Files.list(new File(PATH).toPath())
					.flatMap((path)->{
						try{
							return Files.list(path);
						}catch(IOException ex){
							Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
							return Stream.empty();
						}
					})
					.map((path)->{
						try{
							c.advance();
							if(c.getCount()%1000==0){
								System.out.println(c.getCount()+":"+(System.currentTimeMillis()-time)/1000);
							}
							return parseFile(path);
						}catch(IOException ex){
							Logger.getLogger(NewsTest.class.getName()).log(Level.SEVERE,null,ex);
							throw new RuntimeException(ex);
						}
					});
		}catch(IOException ex){
			Logger.getLogger(NewsTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	private static Sample<String> parseFile(Path path) throws IOException{
		String content=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
		return new Sample<>(content,new Category(path.getParent().getFileName().toString()));
	}
}
