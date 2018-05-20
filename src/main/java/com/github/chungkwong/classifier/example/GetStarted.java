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
import java.util.*;
/**
 *
 * @author kwong
 */
public class GetStarted{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		//创建用于英文的默认分类器工厂
		ClassifierFactory classifierFactory=Starter.getDefaultClassifierFactory(Locale.ENGLISH);
		//创建模型
		Trainable<String> model=classifierFactory.createModel();
		//从一个CSV文件读取样本，其中类别在第0列，文本在第1列
		model.train(TextDatasetHelper.csvRecords(new File(DATA_FILE).toPath(),1,0));
		//创建分类器
		Classifier<String> classifier=classifierFactory.getClassifier(model);
		//对未知类别的文本分类并输出分类名
		System.out.println(classifier.classify("To be classified").getCategory().getName());
	}
}
