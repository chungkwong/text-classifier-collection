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
package com.github.chungkwong.classifier;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class TextPreprocessorTests{
	public TextPreprocessorTests(){
	}
	@Test
	public void testTokenizer(){
		assertTokenizeTo("I am a  bad girl.",new String[]{"I"," ","am"," ","a","  ","bad"," ","girl","."},TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.ENGLISH)));
		assertTokenizeTo("我是一个大苹果",new String[]{"我是一个大苹果"},TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.CHINESE)));
		assertTokenizeTo("我是一个大苹果",new String[]{"我","是","一","个","大","苹","果"},TextPreprocessors.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE)));
		Function<String,Stream<String>> separatorTokenizer1=TextPreprocessors.getSeparatorTokenizer(Pattern.compile("\\s+"),false);
		Function<String,Stream<String>> separatorTokenizer2=TextPreprocessors.getSeparatorTokenizer(Pattern.compile("\\s+"),true);
		Function<String,Stream<String>> wordTokenizer1=TextPreprocessors.getWordTokenizer(Pattern.compile("\\w+"),false);
		Function<String,Stream<String>> wordTokenizer2=TextPreprocessors.getWordTokenizer(Pattern.compile("\\w+"),true);
		assertTokenizeTo("",new String[]{},separatorTokenizer1);
		assertTokenizeTo("",new String[]{},separatorTokenizer2);
		assertTokenizeTo("",new String[]{},wordTokenizer1);
		assertTokenizeTo("",new String[]{},wordTokenizer2);
		assertTokenizeTo("   ",new String[]{},separatorTokenizer1);
		assertTokenizeTo("   ",new String[]{"   "},separatorTokenizer2);
		assertTokenizeTo("   ",new String[]{},wordTokenizer1);
		assertTokenizeTo("   ",new String[]{"   "},wordTokenizer2);
		assertTokenizeTo("text",new String[]{"text"},separatorTokenizer1);
		assertTokenizeTo("text",new String[]{"text"},separatorTokenizer2);
		assertTokenizeTo("text",new String[]{"text"},wordTokenizer1);
		assertTokenizeTo("text",new String[]{"text"},wordTokenizer2);
		assertTokenizeTo("I hated you",new String[]{"I","hated","you"},separatorTokenizer1);
		assertTokenizeTo("I hated you",new String[]{"I"," ","hated"," ","you"},separatorTokenizer2);
		assertTokenizeTo("I hated you",new String[]{"I","hated","you"},wordTokenizer1);
		assertTokenizeTo("I hated you",new String[]{"I"," ","hated"," ","you"},wordTokenizer2);
		assertTokenizeTo(" I am a  bad girl. ",new String[]{"I","am","a","bad","girl."},separatorTokenizer1);
		assertTokenizeTo(" I am a  bad girl. ",new String[]{" ","I"," ","am"," ","a","  ","bad"," ","girl."," "},separatorTokenizer2);
		assertTokenizeTo(" I am a  bad girl. ",new String[]{"I","am","a","bad","girl"},wordTokenizer1);
		assertTokenizeTo(" I am a  bad girl. ",new String[]{" ","I"," ","am"," ","a","  ","bad"," ","girl",". "},wordTokenizer2);
	}
	private void assertTokenizeTo(String text,String[] tokens,Function<String,Stream<String>> tokenizer){
		Assert.assertArrayEquals(tokens,tokenizer.apply(text).toArray());
	}
	@Test
	public void testTransformer(){
		assertTransformTo("万里长城永不倒","萬里長城永不倒",TextPreprocessors.getIcuTransformer("Simplified-Traditional"));
		assertTransformTo("萬里長城永不倒","万里长城永不倒",TextPreprocessors.getIcuTransformer("Simplified-Traditional",true));
	}
	private void assertTransformTo(String text,String result,Function<String,String> transformer){
		Assert.assertEquals(result,transformer.apply(text));
	}
	@Test
	public void testPatternFilter(){
		assertFilterTo(Stream.of("I","hated","you","All"),new String[]{"hated","you"},TextPreprocessors.getKeepPatternFilter(Pattern.compile("[a-z]+")));
		assertFilterTo(Stream.of("I","hated","you","All"),new String[]{"I","All"},TextPreprocessors.getDropPatternFilter(Pattern.compile("[a-z]+")));
		assertFilterTo(Stream.of("I","hated","you","All"),new String[]{"hated","All"},TextPreprocessors.getStopWordsFilter(Arrays.asList("I","you")));
		assertFilterTo(Stream.of("I","hated","you","All"),new String[]{"I","you"},TextPreprocessors.getProtectedWordsFilter(Arrays.asList("I","you")));
	}
	@Test
	public void testWhitespaceFilter(){
		assertFilterTo(Stream.of("I"," ","hated","\t"," you"),new String[]{"I","hated"," you"},TextPreprocessors.getWhitespaceFilter());
	}
	@Test
	public void testCaseFilter(){
		assertFilterTo(Stream.of("I","haTed","you"),new String[]{"I","HATED","YOU"},TextPreprocessors.getUpcaser());
		assertFilterTo(Stream.of("I","haTed","you"),new String[]{"i","hated","you"},TextPreprocessors.getDowncaser());
		assertFilterTo(Stream.of("I","haTed","you"),new String[]{"i","hated","you"},TextPreprocessors.getFoldcaser());
	}
	@Test
	public void testReplacer(){
		assertFilterTo(Stream.of("I","HATE","you"),new String[]{"II","HHATE","you"},TextPreprocessors.getReplacer(Pattern.compile("([A-Z])"),"$1$1",true));
		assertFilterTo(Stream.of("I","HATE","you"),new String[]{"II","HHAATTEE","you"},TextPreprocessors.getReplacer(Pattern.compile("([A-Z])"),"$1$1",false));
		Map<String,String> mapping=new HashMap<>();
		mapping.put("you","me");
		assertFilterTo(Stream.of("I","HATE","you"),new String[]{"I","HATE","me"},TextPreprocessors.getMapper(mapping));
		Map<String,Collection<String>> synonyms=new HashMap<>();
		synonyms.put("I",Arrays.asList("I","我"));
		synonyms.put("you",Arrays.asList("you","你"));
		assertFilterTo(Stream.of("I","HATE","you"),new String[]{"I","我","HATE","you","你"},TextPreprocessors.getSynonymGenerator(synonyms));
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
