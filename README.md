# Text classifier collection

__A full fledged text classification toolkit for Java__

## Features

- Full fledged
    - Bulit-in support for common text preprocessors found in information retrieval system 
    - Bulit-in SVM, kNN, and naive Bayesian classifiers
    - Bulit-in support for CVS format
- Highly customizable
    - You can plugin your own tokenizer, transformer, stopwords, synonyms,
      and TF-IDF formula etc.
    - You can implements you own classifier and use with the toolkit
- User friendly
    - Can choose the best classifier according to a dataset
    - Make use of streaming and functional API introduced in Java8

## Framework

### Overview

Since most classifiers originally work on euclidean space, texts are transformed into
vectors before applying them. The usual way to do that is that split text into tokens
and consider the frequency of each token.

### Tokenizer

Text need to be tokenizied before being used to train or classify. The toolkit includes:
- Java class `java.text.BreakIterator` based locale awared method, recommanded if 
  the locale of the text is supported by Java.
- Split using a regular expression that matches separator, the other parts can be kept or not 
  according to your opinion.
- Split using a regular expression that matches token, the other parts can be kept or not 
  according to your opinion.

Surely, you can pass your own tokenizers into the toolkit.

### Filters

Filters transform a token stream into another. The toolkit includes:
- Those who map a token into a token, usually used to normalize text，e.g.
    - Normalize Unicode
    - Apply transformations provided by icu4j
    - Upcase
    - Downcase
    - Fold case. Since there are no one to one corresponding between lower case letters 
      and upper case letters in many languages, case folding should be used to ignore case.
    - Stemming，i.e. convert words into their root from. Stemming algorithm from Snowball
      are included:  Arabic,  Danish,  Dutch,  English,  Finnish,  French,  German, 
      Hungarian,  Indonesian,  Irish,  Italian,  Nepali,  Norwegian,  Portuguese,  
      Romanian,  Spanish,  Russian,  Swedish,  Tamil,  Turkish
    - Text replacement based on regular expression(Backward reference is allowed)
    - User-defined mapping
- Those who remove some tokens from the stream, e.g.
    - Remove token that are whitespace
    - Remove stop words 
    - Keep only protected words
    - Remove tokens that match a regular expression
    - Remove tokens that do not match a regular expression
- Those who map a token into zero or more tokens, e.g.
    - Insert synonyms
    - User-defined mapping
- Convert the stream into a stream form by n-gram from the original stream.

Surely, you can pass your own filters into the toolkit.

As a rule of thumb：
- The larger the training set is, the less aggressive filters can be applied in 
  order to improve accuracy by using more details.
- The smaller the training set is, the more aggressive filters should be applied 
  in order to prevent over-fitting.

### Classifier

Classifiers are used to assign class labels to token streams. The toolkit includes:

- kNN classifier. Such classifier search for k samples nearest to a token stream,
  and then label the stream using the labels of the samples. Since iterate over all
  samples is needed, it may be very slow for large datasets.
- Naive Bayesian classifier. Such classifier estimate the probability that the stream
  belong to a class, assuming the appearance of tokens is independent.
- TF-IDF classifier. Such classifier calculate the angle between the token TF-IDF 
  vector of the stream and the token TF-IDF vector of the class.
- SVM classifier. Such classifier use support vector machine which solve a kind 
  of conditional optimization problem.

## Evalation

Dataset|Samples|Classes|Accuracy
---|---|---|---
[YouTube Spam Collection](http://archive.ics.uci.edu/ml/datasets/YouTube+Spam+Collection)|1956|2|92.1%
[SMS Spam Collection](http://archive.ics.uci.edu/ml/datasets/SMS+Spam+Collection)|5574|2|98.2%
[Sentence Classification](http://archive.ics.uci.edu/ml/datasets/Sentence+Classification)|1510|5|80.4%
[Reuters-21578 Text Categorization Collection](http://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection)|21578|135|59.8%
[Reuters-21578 Text Categorization Collection](http://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection)|21578|175|67.8%

## Usage

### Get the toolkit

First add the toolkit to the dependency of you project, for Maven projects,
add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.chungkwong</groupId>
    <artifactId>text-classifier-collection</artifactId>
    <version>0.1</version>
</dependency>
```

### Get started

```java
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.util.*;
public class GetStarted{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		//Get a default ClassifierFactory for english
		ClassifierFactory classifierFactory=Starter.getDefaultClassifierFactory(Locale.ENGLISH);
		//Create a model
		Trainable<String> model=classifierFactory.createModel();
		//Train the model using samples from a CSV file，where class labels come from column 0 and texts come from column 1
		model.train(TextDatasetHelper.csvRecords(new File(DATA_FILE).toPath(),1,0));
		//Generate a classifier
		Classifier<String> classifier=classifierFactory.getClassifier(model);
		//Classify a piece of text and output the geussed class label
		System.out.println(classifier.classify("To be classified").getCategory().getName());
	}
}
```

### Customize 

```java
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
public class Demo{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		//Create a naive Bayesian ClassifierFactory. For other classifier, use
        //new SvmClassifierFactory(),new KNearestClassifierFactory().setK(k) or new TfIdfClassifierFactory()
		BayesianClassifierFactory<String> baseClassifierFactory=new BayesianClassifierFactory<>();
		//Select at most 500 features using Tf-Idf
		baseClassifierFactory.setFeatureSelector(new TfIdfFeatureSelector<>(500));
		//Transform input text, the following one turn traditional Chinese into simplified Chinese
		Function<String,String> preTokenize=TextPreprocessors.getIcuTransformer("Traditional-Simplified");
		//Create a tokenizer, the following one consider all characters as token
		Function<String,Stream<String>> tokenizer=TextPreprocessors.getJavaTokenizer(BreakIterator.getCharacterInstance(Locale.CHINESE));
		//Create token stream filter, the following one remove white space and then generate 2-gram
		Function<Stream<String>,Stream<String>> postTokenize=TextPreprocessors.getWhitespaceFilter().andThen(TextPreprocessors.getNgramGenerator(2));
		//Create a text ClassifierFactory with given preprocessors applied
		PreprocessClassifierFactory<FrequenciesModel<String>,String,Stream<String>> classifierFactory=new PreprocessClassifierFactory<>(
				TextPreprocessors.of(preTokenize,tokenizer,postTokenize),baseClassifierFactory);
		PreprocessModel<FrequenciesModel<String>,String,Stream<String>> model=classifierFactory.createModel();
		model.train(TextDatasetHelper.csvRecords(new File(DATA_FILE).toPath(),1,0));
		Classifier<String> classifier=classifierFactory.getClassifier(model);
		//Output the candidates and their scores
		System.out.println(classifier.getCandidates("To be classified"));
	}
}
```

### Calculate confusion matrix and accuracy 

```java
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
public class Evaluator{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		Validator<String> validator=new Validator<>();
		ClassifierFactory[] classifierFactories=getStandardClassifierFactories(Locale.ENGLISH);
		DataSet<String> dataset=new DataSet<>(()->TextDatasetHelper.csvRecords(new File(DATA_FILE).toPath(),1,0),"foobar");
		SplitDataSet[] splitDataSets=getSplitDataSets(dataset);
		validator.validate(splitDataSets,classifierFactories);
		Logger.getGlobal().log(Level.INFO,validator.toString());
		Logger.getGlobal().log(Level.INFO,"Best: {0}",validator.selectMostAccurate().toString());
	}
	private static ClassifierFactory[] getStandardClassifierFactories(Locale locale){
		ClassifierFactory factory1=Starter.getDefaultClassifierFactory(locale,false,new TfIdfClassifierFactory());
		ClassifierFactory factory2=Starter.getDefaultClassifierFactory(locale,false,new BayesianClassifierFactory());
		ClassifierFactory factory3=Starter.getDefaultClassifierFactory(locale,false,new KNearestClassifierFactory().setK(3));
		ClassifierFactory factory4=Starter.getDefaultClassifierFactory(locale,false,new SvmClassifierFactory());
		return new ClassifierFactory[]{factory1,factory2,factory3,factory4};
	}
	private static SplitDataSet[] getSplitDataSets(DataSet<String> dataSet){
		return new SplitDataSet[]{
			DataDivider.randomSplit(dataSet,0.7),
			DataDivider.sequentialSplit(dataSet,0.7),
			DataDivider.noSplit(dataSet)};
	}
}
```

# 文本分类器

__一个强大易用的Java文本分类工具包__

## 特色

- 功能全面
    - 内置信息检索中各种常用的文本预处理方法
    - 内置SVM、kNN、朴素贝叶斯等多种分类器
    - 内置支持CSV等格式数据的读取
- 高度可定制
    - 你可以插入你编写的分词方法、单词规范化方法、停用词列表、同义词列表、TF-IDF公式等等
    - 可以轻易实现你自己的分类器而与工具包中其它工具一起使用
- 容易使用
    - 可自动按给定数据集选取最优分类器
    - 与Java8引入的流和函数式API无缝结合

## 原理

### 概述

大部分文本分类器并不是直接对文本分类，而是把文本切分为“单词”，然后计算各单词的词频，
从而把文本化为基于单词频数的向量，进而使用对欧氏空间中向量分类的分类器。

### 分词

由于大部分分类器都基于单词工作，在训练或分类前需要把文本切分为单词序列。本工具包提供了以下分词方法：
- 基于Java类`java.text.BreakIterator`的语言感知分词方法，在需要处理的语言获Java平台支持时这一般是首选
- 基于分隔符正则表达式的分词方法，可以按你的意愿把分隔符视为单词或丢弃
- 基于单词正则表达式的分词方法，可以按你的意愿把不满足正则表达式的部分视为单词或丢弃

当然你也可以轻易实现其它分词算法并传给工具包调用。

### 过滤器

过滤器用于把单词序列转换为另一单词序列。本工具包提供了以下过滤器：
- 把一个单词映射到一个单词的过滤器，用于等同分类时应该视为一样的单词（规范化），如
    - 对单词进行Unicode规范化
    - 对单词进行icu4j提供的转换，如全半角转换和繁简转换
    - 对单词进行小写化
    - 对单词进行大写化
    - 对单词进行大小写折叠。由于在一些语言中大小写间并不是一对应的，
      在希望统一大小写时应该用这过滤器而不是大写化或小写化
    - 词干提取，如统一单复数形式和时态。目前支持来自Snowball例子的阿拉伯文、丹麦文、
      荷兰文、英文、芬兰的、法文、德国的、匈牙利文、印度尼西亚文、爱尔兰文、意大利文、
      尼泊尔文、挪威文、葡萄牙文、罗马尼亚文、西班牙文、俄文、瑞典文、泰米尔文、土耳其文
    - 对单词进行正则表达式文本替换（容许向后引用）
    - 自定义映射
- 去除部分单词的过滤器，用于压缩模型或消除干扰，如
    - 去除只由空白组成的单词
    - 去除出现在停用词名单中的单词
    - 仅保留出现在保留词名单中的单词
    - 去除满足某正则表达式的单词
    - 仅保留满足某正则表达式的单词
- 把一个单词转换为零个或多个单词的过滤器
    - 插入同义词
    - 自定义映射
- 把单词序列转换为n-gram，如“万里长城永不倒”分成2-Gram“万里”、“里长”、“长城”、“城永”
  、“永不”、“不倒”。对于中文文本分类，由于难以准确分词，而中文词语多为两字词，采用
  2-gram是合适的起点。

当然你也可以轻易实现其它过滤器并传给工具包调用。

一般原则是：
- 在训练数据较多时使用较宽松的预处理，以便利用细致特征进行更精确的分类。
- 在训练数据较少时使用较积极的预处理，以防过度拟合。

### 特征选取

当把所有单词都当作特征时，模型可能会太大，而且可能有过度拟合倾向。因此，
有时可能需要只保留部分单词作为特征。本工具包提供了以下特征选取器：
- 选择Tf-Idf最高的哪些单词
- 选择词频在一定范围内的单词

### 分类器

分类器用于把猜测单词序列属于哪个分类。本工具包提供了以下分类器：

- kNN分类器。找出k个与待分类文本最接近的样本，按这些样本的类别按多数表决方法决定待分类文本的类别。
  由于分类时必须遍历样本，在样本较多时可能会很慢。
- 朴素贝叶斯分类器。在各单词的出现相互独立的假设下，用贝叶斯公式计算待分类文本在各类别中概率。
- TF-IDF分类器。按待分类文本的单词TF-IDF向量与各类别的单词TF-IDF向量间的夹角决定与类别的相关性。
- SVM分类器。使用支持向量机进行分类。

## 用法

### 获取本工具包

首先为你的项目增加对本工具包的依赖。对于Maven项目把往你的`pom.xml`中加（使用Gradle或Ivy的自己改）：

```xml
<dependency>
    <groupId>com.github.chungkwong</groupId>
    <artifactId>text-classifier-collection</artifactId>
    <version>0.1</version>
</dependency>
```

### 基本使用

```java
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.util.*;
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
```

### 定制

```java
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
public class Demo{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		//创建朴素贝叶斯单词流分类器，对于其它分类器改为new SvmClassifierFactory()、
		//new KNearestClassifierFactory().setK(k)或new TfIdfClassifierFactory()
		BayesianClassifierFactory<String> baseClassifierFactory=new BayesianClassifierFactory<>();
		//只用Tf-Idf最高的500个单词做分类
		baseClassifierFactory.setFeatureSelector(new TfIdfFeatureSelector<>(500));
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
```

### 计算混淆矩阵和准确率

```java
package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
public class Evaluator{
	private static final String DATA_FILE="data/foobar.csv";
	public static void main(String[] args){
		Validator<String> validator=new Validator<>();
		ClassifierFactory[] classifierFactories=getStandardClassifierFactories(Locale.ENGLISH);
		DataSet<String> dataset=new DataSet<>(()->TextDatasetHelper.csvRecords(new File(DATA_FILE).toPath(),1,0),"foobar");
		SplitDataSet[] splitDataSets=getSplitDataSets(dataset);
		validator.validate(splitDataSets,classifierFactories);
		Logger.getGlobal().log(Level.INFO,validator.toString());
		Logger.getGlobal().log(Level.INFO,"Best: {0}",validator.selectMostAccurate().toString());
	}
	private static ClassifierFactory[] getStandardClassifierFactories(Locale locale){
		ClassifierFactory factory1=Starter.getDefaultClassifierFactory(locale,false,new TfIdfClassifierFactory());
		ClassifierFactory factory2=Starter.getDefaultClassifierFactory(locale,false,new BayesianClassifierFactory());
		ClassifierFactory factory3=Starter.getDefaultClassifierFactory(locale,false,new KNearestClassifierFactory().setK(3));
		ClassifierFactory factory4=Starter.getDefaultClassifierFactory(locale,false,new SvmClassifierFactory());
		return new ClassifierFactory[]{factory1,factory2,factory3,factory4};
	}
	private static SplitDataSet[] getSplitDataSets(DataSet<String> dataSet){
		return new SplitDataSet[]{
			DataDivider.randomSplit(dataSet,0.7),
			DataDivider.sequentialSplit(dataSet,0.7),
			DataDivider.noSplit(dataSet)};
	}
}
```

## 效果

数据集|样本数|分类数|准确率
---|---|---|---
[YouTube Spam Collection](http://archive.ics.uci.edu/ml/datasets/YouTube+Spam+Collection)|1956|2|92.1%
[SMS Spam Collection](http://archive.ics.uci.edu/ml/datasets/SMS+Spam+Collection)|5574|2|98.2%
[Sentence Classification](http://archive.ics.uci.edu/ml/datasets/Sentence+Classification)|1510|5|80.4%
[Reuters-21578 Text Categorization Collection](http://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection)|21578|135|59.8%
[Reuters-21578 Text Categorization Collection](http://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection)|21578|175|67.8%
