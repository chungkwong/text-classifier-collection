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
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class FrequencyClassifierFactory<C extends Classifier<Stream<T>>,T> extends SimpleClassifierFactory<C,Stream<T>,FrequencyClassifierFactory.FrequencyProfile<T>>{
	public FrequencyClassifierFactory(Function<Map<Category,FrequencyClassifierFactory.FrequencyProfile<T>>,C> classifierBuilder){
		super(()->new FrequencyClassifierFactory.FrequencyProfile<>(),(data,profile)->profile.update(data),classifierBuilder);
	}
	public int getSampleCount(){
		return getProfiles().values().stream().mapToInt((profile)->profile.getDocumentCount()).sum();
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
	public static class FrequencyProfile<T>{
		private int documentCount=0;
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
		public int getDocumentCount(){
			return documentCount;
		}
	}
}
