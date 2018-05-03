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
 *
 * @author kwong
 */
public class BayesianClassifierFactory<T> implements TrainableClassifierFactory<Classifier<Stream<T>>,Stream<T>>{
	private final FrequencyClassifierFactory<Classifier<Stream<T>>,T> base;
	public BayesianClassifierFactory(){
		base=new FrequencyClassifierFactory<>((profiles)->new BayesianClassifierFactory.BayesianClassifier<>(
				getBase().getImmutableProfiles(),getBase().getSampleCounts(),getBase().getTokenCounts(),getBase().getTotalTokenFrequencies()));
	}
	@Override
	public void train(Stream<T> data,Category category){
		base.train(data,category);
	}
	@Override
	public Classifier<Stream<T>> getClassifier(){
		return base.getClassifier();
	}
	public FrequencyClassifierFactory<Classifier<Stream<T>>,T> getBase(){
		return base;
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
		public Category classify(Stream<T> object){
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
			double best=0.0;
			int bestIndex=-1;
			for(int i=0;i<categories.length;i++){
				if(score[i]>best){
					best=score[i];
					bestIndex=i;
				}
			}
			return bestIndex==-1?null:categories[bestIndex];
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
}
