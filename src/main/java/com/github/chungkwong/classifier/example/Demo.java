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
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 * Show how to use the toolkit
 * @author kwong
 */
public class Demo{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		//创建朴素贝叶斯单词流分类器，对于其它分类器改为new SvmClassifierFactory()、
		//new KNearestClassifierFactory().setK(k)或new TfIdfClassifierFactory()
		BayesianClassifierFactory<String> baseClassifierFactory=new BayesianClassifierFactory<>();
		//创建对输入文本进行的转换，如这里是把繁体中文转换为简体中文
		Function<String,String> preTokenize=TextPreprocessors.getIcuTransformer("Traditional-Simplified");
		//创建分词器，这里是把每个字符当作一个单词
		Function<String,Stream<String>> tokenizer=TextPreprocessors.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE));
		//创建单词流过滤器，这里是去除空白单词和转换为2-gram
		Function<Stream<String>,Stream<String>> postTokenize=TextPreprocessors.getWhitespaceFilter().andThen(TextPreprocessors.getNgramGenerator(2));
		//创建与预处理器相结合的文本分类器
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(preTokenize,tokenizer,postTokenize),baseClassifierFactory);
		PreprocessModel<FrequenciesModel<String>,String,Stream<String>> model=classifierFactory.createModel();
		model.train(TextDatasetHelper.csvRecords(new File(DATA_FILE).toPath(),1,0));
		Classifier<String> classifier=classifierFactory.getClassifier(model);
		//输出类别候选及其得分
		System.out.println(classifier.getCandidates("To be classified"));
	}
}
