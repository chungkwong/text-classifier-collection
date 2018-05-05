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
public class YoutubeTest{
	@Test
	public void testTfIdf() throws IOException{
		Logger.getGlobal().log(Level.INFO,"YOUTUBE TF-IDF: {0}",Validator.validate(fullDataStream(),fullDataStream(),ClassifierTest.getEnglishTfIdfClassifierFactory()));
	}
	@Test
	public void testBayesian() throws IOException{
		Logger.getGlobal().log(Level.INFO,"YOUTUBE Bayesian: {0}",Validator.validate(fullDataStream(),fullDataStream(),ClassifierTest.getEnglishClassifierFactory(new BayesianClassifierFactory<>())));
	}
	public Stream<Sample<String>> fullDataStream(){
		try{
			return Files.list(new File("data/YouTube-Spam-Collection-v1").toPath())
					.flatMap((path)->{
						try{
							return CSVParser.parse(Files.lines(path,StandardCharsets.UTF_8));
						}catch(IOException ex){
							Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
							return Stream.empty();
						}
					})
					.map((line)->new Sample<>(line.get(3),new Category(line.get(4))));
		}catch(IOException ex){
			Logger.getLogger(YoutubeTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
}
