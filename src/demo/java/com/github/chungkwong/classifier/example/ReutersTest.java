package com.github.chungkwong.classifier.example;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.util.*;
import com.github.chungkwong.classifier.validator.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
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
/**
 * Test the classifiers on the Reuters-21578 Text Categorization Collection form
 * http://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection
 * @author Chan Chung Kwong
 */
public class ReutersTest{
	//Path to the data directory
	private static final String PATH="/home/kwong/projects/text-classifier/data/reuters21578";
	public static void main(String[] args)throws IOException{
		DataSet<String> dataset=new DataSet<>(()->fullDataStream(),"Reuters");
		ClassifierTest.printTestResult(dataset,Locale.ENGLISH);
	}
	private static Stream<Sample<String>> fullDataStream(){
		try{
			return Files.list(new File(PATH).toPath()).filter((p)->p.toString().endsWith(".sgm"))
					.flatMap((path)->getSamples(path));
		}catch(IOException ex){
			Logger.getLogger(SentenceTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	private static Stream<Sample<String>> getSamples(Path path){
		return parseFile(path).flatMap((doc)->getTopics(doc).
				map((topic)->new Sample<>(getText(doc),new Category(topic))));
	}
	private static Stream<String> getTopics(Map<String,Object> doc){
		Object parent=doc.get("PLACES");
		if(parent instanceof Map){
			Object topics=((Map<String,Map<String,Object>>)doc.get("PLACES")).get("D");
			if(topics instanceof List)
				return ((List<String>)topics).stream();
			else if(topics==null)
				return Stream.empty();
			else
				return Stream.of((String)topics);
		}else
			return Stream.empty();
	}
	private static String getText(Map<String,Object> doc){
		Object text=doc.get("TEXT");
		if(text instanceof Map)
			return ((Map<String,String>)text).getOrDefault("BODY","");
		else if(text instanceof String)
			return (String)text;
		else{
			throw new IllegalStateException();
		}
	}
	private static Stream<Map<String,Object>> parseFile(Path path){
		try{
			List<Map<String,Object>> articles=new ArrayList<>();
			String text=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
			LinkedList<Pair<String,Map<String,Object>>> stack=new LinkedList<>();
			int last=0;
			for(int i=0;i<text.length();i++){
				char c=text.charAt(i);
				if(c=='<'){
					if(text.charAt(i+1)=='/'){
						Pair<String,Map<String,Object>> pop=stack.pop();
						if(stack.isEmpty()){
							articles.add(pop.getValue());
						}else if(pop.getValue().isEmpty()){
							Object value=stack.peek().getValue().get(pop.getKey());
							if(value instanceof List){
								((List)value).add(text.substring(last,i));
							}else if(value!=null){
								ArrayList<Object> arrayList=new ArrayList<>();
								arrayList.add(value);
								arrayList.add(text.substring(last,i));
								stack.peek().getValue().put(pop.getKey(),arrayList);
							}else{
								stack.peek().getValue().put(pop.getKey(),text.substring(last,i));
							}
						}else{
							stack.peek().getValue().put(pop.getKey(),pop.getValue());
						}
					}else if(text.charAt(i+1)!='!'){
						int j=i+1;
						while(Character.isLetter(text.charAt(j)))
							++j;
						stack.push(new Pair<>(text.substring(i+1,j),new HashMap<>()));
					}
					i=text.indexOf('>',i);
					last=i+1;
				}
			}
			return articles.stream();
		}catch(IOException ex){
			Logger.getLogger(ReutersTest.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
}