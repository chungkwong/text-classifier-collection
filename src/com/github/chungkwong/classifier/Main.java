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
import java.text.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class Main{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException{
		System.out.println(TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.CHINESE)).apply("我是一个大苹果").collect(Collectors.toList()));
		System.out.println(TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.ENGLISH)).apply("I am Tom's I.D.E.").collect(Collectors.toList()));
		System.out.println(TextPreprocessors.getNgramGenerator(2,4,10).apply(Stream.of("万","里","长","城","永","不","倒")).collect(Collectors.toList()));
		System.out.println(TextPreprocessors.getPorterStemmer().apply(Stream.of("I","was","eating","balls","happily")).collect(Collectors.toList()));
		TfIdfClassifierFactory<String> tfIdfClassifierFactory=new TfIdfClassifierFactory<>();
		tfIdfClassifierFactory.getBase().loadModel(new File("data/THUCNews/stat"),(x)->x);
		Map<Category,FrequencyClassifierFactory.FrequencyProfile<String>> profiles=tfIdfClassifierFactory.getBase().getProfiles();
		profiles.forEach((k,v)->{
			System.out.println(k);
			v.getTokenFrequency().toMap().entrySet().stream().limit(20).
					forEach((e)->System.out.println(e.getKey()+":"+e.getValue()));
		});
	}
}
