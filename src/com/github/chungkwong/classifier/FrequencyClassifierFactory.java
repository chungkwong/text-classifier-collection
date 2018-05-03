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
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.nio.charset.*;
import java.util.logging.*;
/**
 *
 * @author kwong
 */
public class FrequencyClassifierFactory<C extends Classifier<Stream<T>>,T> extends SimpleClassifierFactory<C,Stream<T>,FrequencyClassifierFactory.FrequencyProfile<T>>{
	public FrequencyClassifierFactory(Function<Map<Category,FrequencyClassifierFactory.FrequencyProfile<T>>,C> classifierBuilder){
		super(()->new FrequencyClassifierFactory.FrequencyProfile<>(),(data,profile)->profile.update(data),classifierBuilder);
	}
	public long getSampleCount(){
		return getProfiles().values().stream().mapToLong((profile)->profile.getDocumentCount()).sum();
	}
	public Map<Category,ImmutableFrequencies<T>> getImmutableProfiles(){
		return getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->new ImmutableFrequencies<>(e.getValue().getTokenFrequency())));
	}
	public ImmutableFrequencies<T> getTotalDocumentFrequencies(){
		Frequencies<T> documentFrequenciesRaw=new Frequencies<>();
		getProfiles().forEach((k,v)->{
			documentFrequenciesRaw.merge(v.getDocumentFrequency());
		});
		return new ImmutableFrequencies<>(documentFrequenciesRaw);
	}
	public ImmutableFrequencies<T> getTotalTokenFrequencies(){
		Frequencies<T> tokenFrequenciesRaw=new Frequencies<>();
		getProfiles().forEach((k,v)->{
			tokenFrequenciesRaw.merge(v.getTokenFrequency());
		});
		return new ImmutableFrequencies<>(tokenFrequenciesRaw);
	}
	public ImmutableFrequencies<Category> getSampleCounts(){
		return new ImmutableFrequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->e.getValue().getDocumentCount())));
	}
	public ImmutableFrequencies<Category> getTokenCounts(){
		return new ImmutableFrequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->(long)e.getValue().getTokenFrequency().getTokenCount())));
	}
	public void saveModel(File directory,Function<T,String> encoder){
		directory.mkdirs();
		getProfiles().forEach((category,profile)->{
			try{
				Files.write(new File(directory,category+DOC_COUNT).toPath(),Long.toString(profile.getDocumentCount()).getBytes(StandardCharsets.UTF_8));
				Files.write(new File(directory,category+DOC_FREQ).toPath(),profile.getDocumentFrequency().toMap().entrySet().stream().map((e)->(CharSequence)(encoder.apply(e.getKey())+"\t"+e.getValue()))::iterator);
				Files.write(new File(directory,category+TOKEN_FREQ).toPath(),profile.getTokenFrequency().toMap().entrySet().stream().map((e)->(CharSequence)(encoder.apply(e.getKey())+"\t"+e.getValue()))::iterator);
			}catch(IOException ex){
				Logger.getLogger(FrequencyClassifierFactory.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
	}
	public void loadModel(File directory,Function<String,T> decoder) throws IOException{
		Files.list(directory.toPath()).filter((path)->path.getFileName().toString().endsWith(DOC_COUNT)).forEach((path)->{
			String categoryName=path.getFileName().toString();
			categoryName=categoryName.substring(0,categoryName.length()-DOC_COUNT.length());
			Category category=new Category(categoryName);
			if(!getProfiles().containsKey(category))
				getProfiles().put(category,new FrequencyProfile<>());
			FrequencyProfile<T> profile=getProfiles().get(category);
			try{
				profile.setDocumentCount(Long.valueOf(new String(Files.readAllBytes(path),StandardCharsets.UTF_8).trim()));
				Files.lines(new File(directory,categoryName+DOC_FREQ).toPath(),StandardCharsets.UTF_8).
						forEach((line)->loadLine(line,profile.getDocumentFrequency(),decoder));
				Files.lines(new File(directory,categoryName+TOKEN_FREQ).toPath(),StandardCharsets.UTF_8).
						forEach((line)->loadLine(line,profile.getTokenFrequency(),decoder));
			}catch(IOException ex){
				Logger.getLogger(FrequencyClassifierFactory.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
	}
	private void loadLine(String line,Frequencies<T> frequencies,Function<String,T> decoder){
		int cut=line.indexOf('\t');
		if(cut!=-1)
			frequencies.advanceFrequency(decoder.apply(line.substring(0,cut)),Long.valueOf(line.substring(cut+1)));
	}
	private static final String DOC_FREQ="_docFreq";
	private static final String TOKEN_FREQ="_tokenFreq";
	private static final String DOC_COUNT="_docCount";
	public static class FrequencyProfile<T>{
		private long documentCount=0;
		private final Frequencies<T> tokenFrequency=new Frequencies<>();
		private final Frequencies<T> documentFrequency=new Frequencies<>();
		public FrequencyProfile(){
		}
		public void update(Stream<T> object){
			Set<T> found=new TreeSet<>();
			object.forEach((token)->{
				tokenFrequency.advanceFrequency(token);
				if(!found.contains(token)){
					documentFrequency.advanceFrequency(token);
					found.add(token);
				}
			});
			++documentCount;
		}
		public Frequencies<T> getDocumentFrequency(){
			return documentFrequency;
		}
		public Frequencies<T> getTokenFrequency(){
			return tokenFrequency;
		}
		public long getDocumentCount(){
			return documentCount;
		}
		private void setDocumentCount(long documentCount){
			this.documentCount=documentCount;
		}
	}
}
