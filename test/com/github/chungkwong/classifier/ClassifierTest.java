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
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class ClassifierTest{
	public static PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> getEnglishTfIdfClassifierFactory(){
		return getEnglishClassifierFactory(new TfIdfClassifierFactory<String>());
	}
	public static <M extends Trainable<Stream<String>>> PreprocessClassifierFactory<M,String,Stream<String>> getEnglishClassifierFactory(ClassifierFactory<Classifier<Stream<String>>,M,Stream<String>> base){
		PreprocessClassifierFactory<M,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(TextPreprocessors.getJavaTokenizer(BreakIterator.getWordInstance(Locale.ENGLISH)),TextPreprocessors.getWhitespaceFilter().andThen(TextPreprocessors.getDowncaser())),
				base);
		return classifierFactory;
	}
	public static PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> getChineseTfIdfClassifierFactory(){
		return getChineseClassifierFactory(new TfIdfClassifierFactory<>());
	}
	public static <M extends Trainable<Stream<String>>> PreprocessClassifierFactory<M,String,Stream<String>> getChineseClassifierFactory(ClassifierFactory<Classifier<Stream<String>>,M,Stream<String>> base){
		PreprocessClassifierFactory<M,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(TextPreprocessors.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE)),TextPreprocessors.getWhitespaceFilter().andThen(TextPreprocessors.getNgramGenerator(2))),
				base);
		return classifierFactory;
	}
}
