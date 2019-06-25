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
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 * Trainable model being used to classify streams based on frequencies of token
 * in the stream
 *
 * @author Chan Chung Kwong
 * @param <T> the type of tokens in the streams
 */
public class DocumentVectorsModel<T> extends SimpleTrainableModel<Frequencies<T>,DocumentVectorsModel.VectorsProfile<T>> implements TokenFrequenciesModel<T>,Persistable<T>{
	/**
	 * Create a model
	 */
	public DocumentVectorsModel(){
		super(()->new DocumentVectorsModel.VectorsProfile<>(),(data,profile)->profile.update(data));
	}
	@Override
	public long getSampleCount(){
		return getProfiles().values().stream().mapToLong((profile)->profile.getDocumentVectors().size()).sum();
	}
	@Override
	public Map<Category,Frequencies<T>> getTokenFrequencies(){
		return getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),
				(e)->{
					Frequencies<T> tokenFrequenciesRaw=new Frequencies<>(true);
					e.getValue().getDocumentVectors().forEach((vector)->tokenFrequenciesRaw.merge(vector));
					return tokenFrequenciesRaw;
				}));
	}
	@Override
	public Frequencies<T> getTotalDocumentFrequencies(){
		Frequencies<T> documentFrequenciesRaw=new Frequencies<>(true);
		getProfiles().values().stream().flatMap((vectors)->vectors.getDocumentVectors().stream()).
				flatMap((v)->v.toMap().keySet().stream()).forEach((t)->documentFrequenciesRaw.advanceFrequency(t));
		return documentFrequenciesRaw;
	}
	@Override
	public Frequencies<T> getTotalTokenFrequencies(){
		Frequencies<T> tokenFrequenciesRaw=new Frequencies<>(true);
		getProfiles().values().stream().flatMap((vectors)->vectors.getDocumentVectors().stream()).
				forEach((v)->tokenFrequenciesRaw.merge(v));
		return tokenFrequenciesRaw;
	}
	@Override
	public Frequencies<Category> getSampleCounts(){
		return new Frequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->new Counter(e.getValue().getDocumentVectors().size()))));
	}
	@Override
	public Frequencies<Category> getTokenCounts(){
		return new Frequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),
				(e)->new Counter(e.getValue().getDocumentVectors().stream().flatMap((v)->v.toMap().keySet().stream()).distinct().count()))));
	}
	public Map<Category,Frequencies<T>> getDocumentFrequencies(){
		return getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)
				->e.getValue().getDocumentVectors().stream().collect(()->new Frequencies<>(true),(f,v)->f.merge(v),(f1,f2)->f1.merge(f2))));
	}
	@Override
	public void retainAll(Set<T> toKeep){
		getProfiles().forEach((k,v)->{
			v.getDocumentVectors().forEach((vector)->vector.toMap().keySet().retainAll(toKeep));
		});
	}
	@Override
	public void save(File directory,Function<T,String> encoder){
		try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(new File(directory,"MODEL")))){
			out.writeObject(getProfiles());
		}catch(IOException ex){
			Logger.getLogger(SimpleTrainableModel.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public void load(File directory,Function<String,T> decoder){
		try(ObjectInputStream out=new ObjectInputStream(new FileInputStream(new File(directory,"MODEL")))){
			getProfiles().putAll((Map<? extends Category,? extends VectorsProfile<T>>)out.readObject());
		}catch(IOException|ClassCastException|ClassNotFoundException ex){
			Logger.getLogger(DocumentVectorsModel.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	/**
	 * Profile that records document vector
	 *
	 * @param <T> the type of tokens
	 */
	public static class VectorsProfile<T> implements Serializable{
		private final List<Frequencies<T>> vectors;
		/**
		 * Create a empty profile
		 */
		public VectorsProfile(){
			vectors=new LinkedList<>();
		}
		/**
		 * Create a profile
		 *
		 * @param vectors initial vector
		 */
		public VectorsProfile(List<Frequencies<T>> vectors){
			this.vectors=vectors;
		}
		/**
		 * Update the profile based on sample data
		 *
		 * @param object sample data
		 */
		public void update(Frequencies<T> object){
			vectors.add((Frequencies<T>)object);
		}
		/**
		 * @return the number of sample in the category
		 */
		public List<Frequencies<T>> getDocumentVectors(){
			return vectors;
		}
	}
}
