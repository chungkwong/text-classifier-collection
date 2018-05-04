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
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author kwong
 */
public class TextPreprocessorTests{
	public TextPreprocessorTests(){
	}
	@Test
	public void testTokenizer(){
		assertTokenizeTo("I am a  bad girl.",new String[]{"I"," ","am"," ","a","  ","bad"," ","girl","."},TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.ENGLISH)));
		assertTokenizeTo("我是一个大苹果",new String[]{"我是一个大苹果"},TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.CHINESE)));
		assertTokenizeTo("我是一个大苹果",new String[]{"我","是","一","个","大","苹","果"},TextPreprocessors.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE)));
	}
	private void assertTokenizeTo(String text,String[] tokens,Function<String,Stream<String>> tokenizer){
		Assert.assertArrayEquals(tokens,tokenizer.apply(text).toArray());
	}
	@Test
	public void testWhitespaceFilter(){
		assertFilterTo(Stream.of("I"," ","hated","\t"," you"),new String[]{"I","hated"," you"},TextPreprocessors.getWhitespaceFilter());
	}
	@Test
	public void testCaseFilter(){
		assertFilterTo(Stream.of("I","haTed","you"),new String[]{"I","HATED","YOU"},TextPreprocessors.getUpcaser());
		assertFilterTo(Stream.of("I","haTed","you"),new String[]{"i","hated","you"},TextPreprocessors.getDowncaser());
	}
	@Test
	public void testStemmer(){
		assertFilterTo(Stream.of("I","was","eating","balls","happily"),new String[]{"I","wa","eat","ball","happili"},TextPreprocessors.getPorterStemmer());
		assertFilterTo(Stream.of("I","was","eating","balls","happily"),new String[]{"I","wa","eat","bal","hap"},TextPreprocessors.getLovinsStemmer());
		assertFilterTo(Stream.of("I","was","eating","balls","happily"),new String[]{"I","was","eat","ball","happili"},TextPreprocessors.getStemmer(Locale.ENGLISH));
		assertFilterTo(Stream.of("I","was","eating","balls","happily"),new String[]{"I","was","eating","balls","happily"},TextPreprocessors.getStemmer(Locale.CHINESE));
	}
	@Test
	public void testNgramGenerator(){
		assertFilterTo(Stream.of("万","里","长","城","永","不","倒"),new String[]{"万 ","里 ","长 ","城 ","永 ","不 ","倒 "},TextPreprocessors.getNgramGenerator(1));
		assertFilterTo(Stream.of("万","里","长","城","永","不","倒"),new String[]{"万 里 ","里 长 ","长 城 ","城 永 ","永 不 ","不 倒 "},TextPreprocessors.getNgramGenerator(2));
		assertFilterTo(Stream.of("万","里","长","城","永","不","倒"),new String[]{"万 里 ","里 长 ","长 城 ","万 里 长 城 ","城 永 ","里 长 城 永 ","永 不 ","长 城 永 不 ","不 倒 ","城 永 不 倒 "},TextPreprocessors.getNgramGenerator(2,4));
	}
	private void assertFilterTo(Stream<String> text,String[] tokens,Function<Stream<String>,Stream<String>> tokenizer){
		Assert.assertArrayEquals(tokens,tokenizer.apply(text).toArray());
	}
}
