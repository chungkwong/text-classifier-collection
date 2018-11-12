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
import java.util.*;
import java.util.stream.*;
/**
 * 
 * Factory for TF-IDF classifier
 * @param <T> the type of data to be classified
 * @author Chan Chung Kwong
 */
public class TfIdfClassifierFactory<T> extends BagClassifierFactory<Classifier<Frequencies<T>>,FrequenciesModel<T>,T>{
	private TfIdfFormula tfIdfFormula;
	/**
	 * Create a factory with standard TF-IDF formula
	 */
	public TfIdfClassifierFactory(){
		tfIdfFormula=TfIdfFormula.STANDARD;
	}
	/**
	 * Set TF-IDF formula
	 * @param tfIdfFormula TF-IDF formula
	 * @return this
	 */
	public TfIdfClassifierFactory<T> setTfIdfFormula(TfIdfFormula tfIdfFormula){
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
	public Classifier<Frequencies<T>> createClassifier(FrequenciesModel<T> model){
		return new TfIdfClassifier<>(model.getTokenFrequencies(),
				model.getTotalDocumentFrequencies(),model.getSampleCount(),tfIdfFormula);
	}
	@Override
	public FrequenciesModel<T> createModel(){
		return new FrequenciesModel<>();
	}
	
	private static class TfIdfClassifier<T> implements Classifier<Frequencies<T>>{
		private final Map<Category,Frequencies<T>> profiles;
		private final DoubleCounters<Category> norms;
		private final Frequencies<T> documentFrequencies;
		private final long documentCount;
		private final TfIdfFormula tfIdfFormula;
		public TfIdfClassifier(Map<Category,Frequencies<T>> profiles,
				Frequencies<T> documentFrequencies,long documentCount,
				TfIdfFormula tfIdfFormula){
			this.profiles=profiles;
			this.documentFrequencies=documentFrequencies;
			this.documentCount=documentCount;
			this.tfIdfFormula=tfIdfFormula;
			norms=new DoubleCounters<>(true);
			profiles.forEach((c,f)->f.toMap().forEach((t,v)->{
				double tfidf=tfIdfFormula.calculate(v.getCount(),documentFrequencies.getFrequency(t),documentCount);
				norms.advanceCounter(c,tfidf*tfidf);
			}));
		}
		@Override
		public List<ClassificationResult> getCandidates(Frequencies<T> document,int max){
			return profiles.entrySet().stream().map((e)->new ClassificationResult(cosSquare(document,e.getValue(),norms.getFrequency(e.getKey())),e.getKey())).
					collect(Collectors.toList());
		}
		private double cosSquare(Frequencies<T> document,Frequencies<T> category,double categoryNorm){
			double documentNorm=norm(document);
			Frequencies<T> shorter,longer;
			if(document.getTokenCount()>category.getTokenCount()){
				shorter=category;
				longer=document;
			}else{
				shorter=document;
				longer=category;
			}
			double product=0;
			Iterator<Map.Entry<T,Counter>> iterator=shorter.toMap().entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<T,Counter> next=iterator.next();
				T token=next.getKey();
				double shortTfIdf=tfIdfFormula.calculate(next.getValue().getCount(),documentFrequencies.getFrequency(token),documentCount);
				double longTfIdf=tfIdfFormula.calculate(longer.getFrequency(token),documentFrequencies.getFrequency(token),documentCount);
				product+=shortTfIdf*longTfIdf;
			}
			return product*product/(documentNorm*categoryNorm);
		}
		private double norm(Frequencies<T> document){
			double documentNorm=0;
			for(Map.Entry<T,Counter> entry:document.toMap().entrySet()){
				double tfidf=tfIdfFormula.calculate(entry.getValue().getCount(),documentFrequencies.getFrequency(entry.getKey()),documentCount);
				documentNorm+=tfidf*tfidf;
			}
			return documentNorm;
		}
	}
	@Override
	protected String getName(){
		return "TF-IDF";
	}
}
