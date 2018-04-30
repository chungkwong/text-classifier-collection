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
import com.github.chungkwong.classifier.util.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author kwong
 */
public class NewsTest{
	@Test
	public void testTfIdf() throws IOException{
		TfIdfClassifierFactory<String> tfIdfClassifierFactory=new TfIdfClassifierFactory<>();
		PreprocessClassifierFactory<Classifier<String>,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessor.of(TextPreprocessor.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE)),TextPreprocessor.getWhitespaceFilter(),TextPreprocessor.getDowncaser()),
				tfIdfClassifierFactory);
		train(classifierFactory);
		Classifier<String> classifier=classifierFactory.getClassifier();
		classifierFactory=null;
		classify(classifier);
	}
	public void train(TrainableClassifierFactory<Classifier<String>,String> classifierFactory) throws IOException{
		fullDataStream().forEach((sample)->classifierFactory.train(sample.getData(),sample.getCategory()));
	}
	public void classify(Classifier<String> classifier) throws IOException{
		Frequencies<Pair<Category,Category>> table=new Frequencies<>(true);
		fullDataStream().forEach((sample)->{
			table.advanceFrequency(new Pair<>(sample.getCategory(),classifier.classify(sample.getData())));
		});
		Logger.getGlobal().log(Level.INFO,"result:{0}",table.toMap());
	}
	public Stream<Sample<String>> fullDataStream() throws IOException{
		return Files.list(new File("data/THUCNews/THUCNews").toPath())
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
						return parseFile(path);
					}catch(IOException ex){
						Logger.getLogger(NewsTest.class.getName()).log(Level.SEVERE,null,ex);
						throw new RuntimeException(ex);
					}
				});
	}
	public Sample<String> parseFile(Path path) throws IOException{
		String content=Files.lines(path,StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
		return new Sample<>(content,new Category(path.getParent().getFileName().toString()));
	}
}
