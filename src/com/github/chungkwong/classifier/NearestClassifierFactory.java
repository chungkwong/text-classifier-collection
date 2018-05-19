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
 * Factory for nearest classifier. Since such classifiers need to iterate 
 * all samples in order to classify a object, they are very slow on large dataset.
 * @author kwong
 * @param <T> the type of the objects to be classified
 */
public class NearestClassifierFactory<T> implements ClassifierFactory<Classifier<Stream<T>>,DocumentVectorsModel<T>,Stream<T>>{
	private TfIdfFormula tfIdfFormula=TfIdfFormula.STANDARD;
	public NearestClassifierFactory(){
	}
	/**
	 * Set TF-IDF formula
	 * @param tfIdfFormula TF-IDF formula
	 * @return
	 */
	public NearestClassifierFactory<T> setTfIdfFormula(TfIdfFormula tfIdfFormula){
		this.tfIdfFormula=tfIdfFormula;
		return this;
	}
	/**
	 * @return TF-IDF formula
	 */
	public TfIdfFormula getTfIdfFormula(){
		return tfIdfFormula;
	}
	@Override
	public Classifier<Stream<T>> getClassifier(DocumentVectorsModel<T> model){
		return new NearestClassifier<>(model.getProfiles(),model.getTotalDocumentFrequencies(),
				model.getSampleCount(),tfIdfFormula);
	}
	@Override
	public DocumentVectorsModel<T> createModel(){
		return new DocumentVectorsModel<>();
	}
	private static class NearestClassifier<T> implements Classifier<Stream<T>>{
		private final TfIdfFormula tfIdfFormula;
		private final ImmutableFrequencies<T> documentFrequencies;
		private final long documentCount;
		private final Map<Category,DocumentVectorsModel.VectorsProfile<T>> profiles;
		public NearestClassifier(Map<Category,DocumentVectorsModel.VectorsProfile<T>> profiles,
				ImmutableFrequencies<T> documentFrequencies,
				long documentCount,TfIdfFormula tfIdfFormula){
			this.profiles=profiles;
			this.documentFrequencies=documentFrequencies;
			this.documentCount=documentCount;
			this.tfIdfFormula=tfIdfFormula;
		}
		private int i=0;
		@Override
		public List<ClassificationResult> getCandidates(Stream<T> object,int max){
			System.err.println(i++);
			ImmutableFrequencies<T> unknown=new ImmutableFrequencies<>(object);
			return profiles.entrySet().stream().map((e)->probe(unknown,e.getValue(),e.getKey())).sorted().collect(Collectors.toList());
		}
		private ClassificationResult probe(ImmutableFrequencies<T> unknown,DocumentVectorsModel.VectorsProfile<T> profile,Category category){
			double dist=Double.POSITIVE_INFINITY;
			for(ImmutableFrequencies<T> sample:profile.getDocumentVectors()){
				dist=Math.min(dist,calculateDistance(sample,unknown));
			}
			return new ClassificationResult(1.0/dist,category);
		}
		private double calculateDistance(ImmutableFrequencies<T> v1,ImmutableFrequencies<T> v2){
			double dist=0;
			for(Map.Entry<T,Long> e:v1.toMap().entrySet()){
				T token=e.getKey();
				double diff=getTfIdf(token,e.getValue())-getTfIdf(token,v2.getFrequency(token));
				dist+=diff*diff;
			}
			for(Map.Entry<T,Long> e:v2.toMap().entrySet()){
				T token=e.getKey();
				if(!v1.toMap().containsKey(token)){
					double diff=getTfIdf(token,0)-getTfIdf(token,e.getValue());
					dist+=diff*diff;
				}
			}
			return dist;
		}
		private double getTfIdf(T token,long freq){
			return tfIdfFormula.calculate(freq,documentFrequencies.getFrequency(token),documentCount);
		}
	}
	@Override
	public String toString(){
		return "Nearest";
	}
}