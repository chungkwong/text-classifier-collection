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
import com.github.chungkwong.classifier.util.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 * Trainable model being used to classify streams based on frequencies of token in the stream
 * @author Chan Chung Kwong
 * @param <T> the type of tokens in the streams
 */
public class FrequenciesModel<T> extends SimpleTrainableModel<Stream<T>,FrequenciesModel.FrequencyProfile<T>> 
		implements TokenFrequenciesModel<T>,Persistable<T>{
	/**
	 * Create a model
	 */
	public FrequenciesModel(){
		super(()->new FrequenciesModel.FrequencyProfile<>(),(data,profile)->profile.update(data));
	}
	@Override
	public long getSampleCount(){
		return getProfiles().values().stream().mapToLong((profile)->profile.getDocumentCount()).sum();
	}
	@Override
	public Map<Category,ImmutableFrequencies<T>> getTokenFrequencies(){
		return getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->new ImmutableFrequencies<>(e.getValue().getTokenFrequencies())));
	}
	@Override
	public ImmutableFrequencies<T> getTotalDocumentFrequencies(){
		Frequencies<T> documentFrequenciesRaw=new Frequencies<>();
		getProfiles().forEach((k,v)->{
			documentFrequenciesRaw.merge(v.getDocumentFrequencies());
		});
		return new ImmutableFrequencies<>(documentFrequenciesRaw);
	}
	@Override
	public ImmutableFrequencies<T> getTotalTokenFrequencies(){
		Frequencies<T> tokenFrequenciesRaw=new Frequencies<>();
		getProfiles().forEach((k,v)->{
			tokenFrequenciesRaw.merge(v.getTokenFrequencies());
		});
		return new ImmutableFrequencies<>(tokenFrequenciesRaw);
	}
	@Override
	public ImmutableFrequencies<Category> getSampleCounts(){
		return new ImmutableFrequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->e.getValue().getDocumentCount())));
	}
	@Override
	public ImmutableFrequencies<Category> getTokenCounts(){
		return new ImmutableFrequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->(long)e.getValue().getTokenFrequencies().getTokenCount())));
	}
	@Override
	public void retainAll(Set<T> toKeep){
		getProfiles().forEach((k,v)->{
			v.getDocumentFrequencies().toMap().keySet().retainAll(toKeep);
			v.getTokenFrequencies().toMap().keySet().retainAll(toKeep);
		});
	}
	@Override
	public void save(File directory,Function<T,String> encoder){
		directory.mkdirs();
		getProfiles().forEach((category,profile)->{
			try{
				Files.write(new File(directory,category+DOC_COUNT).toPath(),Long.toString(profile.getDocumentCount()).getBytes(StandardCharsets.UTF_8));
				Files.write(new File(directory,category+DOC_FREQ).toPath(),profile.getDocumentFrequencies().toMap().entrySet().stream().map((e)->(CharSequence)(encoder.apply(e.getKey())+"\t"+e.getValue()))::iterator);
				Files.write(new File(directory,category+TOKEN_FREQ).toPath(),profile.getTokenFrequencies().toMap().entrySet().stream().map((e)->(CharSequence)(encoder.apply(e.getKey())+"\t"+e.getValue()))::iterator);
			}catch(IOException ex){
				Logger.getLogger(FrequenciesModel.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
	}
	@Override
	public void load(File directory,Function<String,T> decoder){
		try{
			Files.list(directory.toPath()).filter((path)->path.getFileName().toString().endsWith(DOC_COUNT)).forEach((path)->{
				String categoryName=path.getFileName().toString();
				categoryName=categoryName.substring(0,categoryName.length()-DOC_COUNT.length());
				Category category=new Category(categoryName);
				if(!getProfiles().containsKey(category)){
					getProfiles().put(category,new FrequencyProfile<>());
				}
				FrequencyProfile<T> profile=getProfiles().get(category);
				try{
					profile.setDocumentCount(Long.valueOf(new String(Files.readAllBytes(path),StandardCharsets.UTF_8).trim()));
					Files.lines(new File(directory,categoryName+DOC_FREQ).toPath(),StandardCharsets.UTF_8).
							forEach((line)->loadLine(line,profile.getDocumentFrequencies(),decoder));
					Files.lines(new File(directory,categoryName+TOKEN_FREQ).toPath(),StandardCharsets.UTF_8).
							forEach((line)->loadLine(line,profile.getTokenFrequencies(),decoder));
				}catch(IOException ex){
					Logger.getLogger(FrequenciesModel.class.getName()).log(Level.SEVERE,null,ex);
				}
			});
		}catch(IOException ex){
			Logger.getLogger(FrequenciesModel.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private void loadLine(String line,Frequencies<T> frequencies,Function<String,T> decoder){
		int cut=line.indexOf('\t');
		if(cut!=-1){
			frequencies.advanceFrequency(decoder.apply(line.substring(0,cut)),Long.valueOf(line.substring(cut+1)));
		}
	}
	private static final String DOC_FREQ="_docFreq";
	private static final String TOKEN_FREQ="_tokenFreq";
	private static final String DOC_COUNT="_docCount";
	/**
	 * Profile that records frequencies of each token
	 * @param <T> the type of tokens
	 */
	public static class FrequencyProfile<T>{
		private long documentCount=0;
		private final Frequencies<T> tokenFrequencies=new Frequencies<>();
		private final Frequencies<T> documentFrequencies=new Frequencies<>();
		/**
		 * Create a empty profile
		 */
		public FrequencyProfile(){
		}
		/**
		 * Update the profile based on sample data
		 * @param object sample data
		 */
		public void update(Stream<T> object){
			Set<T> found=new TreeSet<>();
			object.forEach((token)->{
				tokenFrequencies.advanceFrequency(token);
				if(!found.contains(token)){
					documentFrequencies.advanceFrequency(token);
					found.add(token);
				}
			});
			++documentCount;
		}
		/**
		 * @return the number of samples that contains each token in the category
		 */
		public Frequencies<T> getDocumentFrequencies(){
			return documentFrequencies;
		}
		/**
		 * @return the frequency of each token in the category
		 */
		public Frequencies<T> getTokenFrequencies(){
			return tokenFrequencies;
		}
		/**
		 * @return the number of sample in the category
		 */
		public long getDocumentCount(){
			return documentCount;
		}
		private void setDocumentCount(long documentCount){
			this.documentCount=documentCount;
		}
	}
}
