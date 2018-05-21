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
import java.util.*;
import java.util.stream.*;
/**
 * Trainable model being used to classify streams based on frequencies of token in the stream
 * @author kwong
 * @param <T> the type of tokens in the streams
 */
public class DocumentVectorsModel<T> extends SimpleTrainableModel<Stream<T>,DocumentVectorsModel.VectorsProfile<T>> implements TokenFrequenciesModel<T>{
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
	public Map<Category,ImmutableFrequencies<T>> getTokenFrequencies(){
		return getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),
				(e)->{
					Frequencies<T> tokenFrequenciesRaw=new Frequencies<>();
					e.getValue().getDocumentVectors().forEach((vector)->tokenFrequenciesRaw.merge(vector));
					return new ImmutableFrequencies<>(tokenFrequenciesRaw);
				}));
	}
	@Override
	public ImmutableFrequencies<T> getTotalDocumentFrequencies(){
		Frequencies<T> documentFrequenciesRaw=new Frequencies<>();
		getProfiles().values().stream().flatMap((vectors)->vectors.getDocumentVectors().stream()).
				flatMap((v)->v.toMap().keySet().stream()).forEach((t)->documentFrequenciesRaw.advanceFrequency(t));
		return new ImmutableFrequencies<>(documentFrequenciesRaw);
	}
	@Override
	public ImmutableFrequencies<T> getTotalTokenFrequencies(){
		Frequencies<T> tokenFrequenciesRaw=new Frequencies<>();
		getProfiles().values().stream().flatMap((vectors)->vectors.getDocumentVectors().stream()).
				forEach((v)->tokenFrequenciesRaw.merge(v));
		return new ImmutableFrequencies<>(tokenFrequenciesRaw);
	}
	@Override
	public ImmutableFrequencies<Category> getSampleCounts(){
		return new ImmutableFrequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->(long)e.getValue().getDocumentVectors().size())));
	}
	@Override
	public ImmutableFrequencies<Category> getTokenCounts(){
		return new ImmutableFrequencies<>(getProfiles().entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),
				(e)->(long)e.getValue().getDocumentVectors().stream().flatMap((v)->v.toMap().keySet().stream()).distinct().count())));
	}
	@Override
	public void retainAll(Set<T> toKeep){
		getProfiles().forEach((k,v)->{
			v.getDocumentVectors().forEach((vector)->vector.toMap().keySet().retainAll(toKeep));
		});
	}	
	/**
	 * Profile that records document vector
	 * @param <T> the type of tokens
	 */
	public static class VectorsProfile<T>{
		private final List<ImmutableFrequencies<T>> vectors=new LinkedList<>();
		/**
		 * Create a empty profile
		 */
		public VectorsProfile(){
		}
		/**
		 * Update the profile based on sample data
		 * @param object sample data
		 */
		public void update(Stream<T> object){
			vectors.add(new ImmutableFrequencies<>(object));
		}
		/**
		 * @return the number of sample in the category
		 */
		public List<ImmutableFrequencies<T>> getDocumentVectors(){
			return vectors;
		}
	}
}