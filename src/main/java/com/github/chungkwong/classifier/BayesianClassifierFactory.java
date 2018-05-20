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
 * Factory for Bayesian classifier
 * @author kwong
 * @param <T> the type of the objects to be classified
 */
public class BayesianClassifierFactory<T> implements ClassifierFactory<Classifier<Stream<T>>,FrequenciesModel<T>,Stream<T>>{
	/**
	 * Create a Bayesian classifier factory
	 */
	public BayesianClassifierFactory(){
	}
	@Override
	public Classifier<Stream<T>> getClassifier(FrequenciesModel<T> model){
		return new BayesianClassifier<>(model.getTokenFrequencies(),
				model.getSampleCounts(),model.getTokenCounts(),model.getTotalTokenFrequencies());
	}
	@Override
	public FrequenciesModel<T> createModel(){
		return new FrequenciesModel<>();
	}
	private static class BayesianClassifier<T> implements Classifier<Stream<T>>{
		private final Map<Category,ImmutableFrequencies<T>> profiles;
		private final ImmutableFrequencies<Category> documentCounts;
		private final ImmutableFrequencies<Category> tokenCounts;
		private final ImmutableFrequencies<T> tokenFrequencies;
		private final long documentCount;
		private final int tokenCount;
		public BayesianClassifier(Map<Category,ImmutableFrequencies<T>> profiles,
				ImmutableFrequencies<Category> documentCounts,ImmutableFrequencies<Category> tokenCounts,
				ImmutableFrequencies<T> tokenFrequencies){
			this.profiles=profiles;
			this.documentCounts=documentCounts;
			this.tokenCounts=tokenCounts;
			this.tokenFrequencies=tokenFrequencies;
			this.documentCount=documentCounts.toMap().values().stream().mapToLong((i)->i).sum();
			this.tokenCount=tokenFrequencies.getTokenCount();
		}
		@Override
		public List<ClassificationResult> getCandidates(Stream<T> object,int max){
			Category[] categories=profiles.keySet().toArray(new Category[0]);
			double[] score=new double[categories.length];
			Arrays.fill(score,1.0);
			int[] maxExp=new int[]{0,0};
			object.forEach((token)->{
				for(int i=0;i<categories.length;i++){
					score[i]=Math.scalb(score[i],maxExp[0]);
					score[i]*=getCategoryProbability(categories[i],token);
					maxExp[1]=Math.min(-Math.getExponent(score[i]),maxExp[1]);
				}
				maxExp[0]=maxExp[1];
				maxExp[1]=-Double.MIN_EXPONENT;
			});
			ArrayList<ClassificationResult> results=new ArrayList<>(categories.length);
			for(int i=0;i<categories.length;i++){
				results.add(new ClassificationResult(score[i],categories[i]));
			}
			return results;
		}
		private double getCategoryProbability(Category category,T token){
			return getCategoryProbability(category)*getTokenProbability(token,category)/getTokenProbability(token);
		}
		private double getCategoryProbability(Category category){
			return ((double)documentCounts.getFrequency(category))/documentCount;
		}
		private double getTokenProbability(T token,Category category){
			long frequency=profiles.get(category).getFrequency(token);
			return frequency!=0?((double)frequency)/tokenCounts.getFrequency(category):1.0/profiles.get(category).getTokenCount();
		}
		private double getTokenProbability(T token){
			long frequency=tokenFrequencies.getFrequency(token);
			return frequency!=0?((double)frequency)/tokenCount:1.0/tokenFrequencies.getTokenCount();
		}
	}
	@Override
	public String toString(){
		return "Bayesian";
	}
}
