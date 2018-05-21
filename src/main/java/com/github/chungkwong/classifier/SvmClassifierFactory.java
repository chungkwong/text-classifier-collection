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
import de.bwaldvogel.liblinear.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * Factory for SVM classifier
 * @author kwong
 * @param <T> the type of the objects to be classified
 */
public class SvmClassifierFactory<T> extends StreamClassifierFactory<Classifier<Stream<T>>,DocumentVectorsModel<T>,T>{
	private TfIdfFormula tfIdfFormula=TfIdfFormula.STANDARD;
	private Parameter parameter=new Parameter(SolverType.L2R_L2LOSS_SVC_DUAL,1,0.1);
	/**
	 * Create a SVM classifier factory
	 */
	public SvmClassifierFactory(){
	}
	/**
	 * @return parameters of liblinear
	 */
	public Parameter getParameter(){
		return parameter;
	}
	/**
	 * Set parameters of liblinear
	 * @param parameter
	 * @return this
	 */
	public SvmClassifierFactory<T> setParameter(Parameter parameter){
		this.parameter=parameter;
		return this;
	}
	/**
	 * Set TF-IDF formula
	 * @param tfIdfFormula TF-IDF formula
	 * @return
	 */
	public SvmClassifierFactory<T> setTfIdfFormula(TfIdfFormula tfIdfFormula){
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
	public Classifier<Stream<T>> createClassifier(DocumentVectorsModel<T> model){
		ImmutableFrequencies<T> totalDocumentFrequencies=model.getTotalDocumentFrequencies();
		Problem problem=new Problem();
		problem.l=(int)model.getSampleCount();
		problem.n=totalDocumentFrequencies.getTokenCount();
		
		Map<T,Integer> tokenIndex=new HashMap<>();
		int sampleCount=(int)model.getSampleCount();
		problem.y=new double[sampleCount];
		problem.x=new Feature[sampleCount][];
		int i=0,j=0;
		Iterator<Map.Entry<Category,DocumentVectorsModel.VectorsProfile<T>>> iterator=model.getProfiles().entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<Category,DocumentVectorsModel.VectorsProfile<T>> next=iterator.next();
			for(ImmutableFrequencies<T> sample:next.getValue().getDocumentVectors()){
				problem.y[i]=j;
				problem.x[i]=toFeatureArray(sample,tokenIndex,totalDocumentFrequencies,sampleCount,tfIdfFormula);
				++i;
			}
			++j;
		}
		return new SvmClassifier<>(Linear.train(problem,parameter),
				tokenIndex,totalDocumentFrequencies,sampleCount,tfIdfFormula,
				model.getProfiles().keySet().toArray(new Category[0]));
	}
	@Override
	public DocumentVectorsModel<T> createModel(){
		return new DocumentVectorsModel<>();
	}
	private static <T> Feature[] toFeatureArray(ImmutableFrequencies<T> object,Map<T,Integer> tokenIndex,
			ImmutableFrequencies<T> documentFrequencies,long documentCount,TfIdfFormula formula){
		Feature[] features=new Feature[object.getTokenCount()];
		int i=0;
		for(Map.Entry<T,Long> e:object.toMap().entrySet()){
			T token=e.getKey();
			Integer index=tokenIndex.get(token);
			if(index==null){
				index=tokenIndex.size()+1;
				tokenIndex.put(token,index);
			}
			features[i++]=new FeatureNode(index,formula.calculate(e.getValue(),documentFrequencies.getFrequency(token),documentCount));
		}
		double factor=0;
		for(Feature feature:features)
			factor+=feature.getValue()*feature.getValue();
		factor=Math.sqrt(factor);
		for(Feature feature:features)
			feature.setValue(feature.getValue()/factor);
		Arrays.sort(features,(f,g)->Integer.compare(f.getIndex(),g.getIndex()));
		return features;	
	}
	private static class SvmClassifier<T> implements Classifier<Stream<T>>{
		private final Model model;
		private final ImmutableFrequencies<T> documentFrequencies;
		private final long documentCount;
		private final TfIdfFormula tfIdfFormula;
		private final Map<T,Integer> tokenIndex;
		private final Category[] categories;
		public SvmClassifier(Model model,Map<T,Integer> tokenIndex,
				ImmutableFrequencies<T> documentFrequencies,long documentCount,
				TfIdfFormula tfIdfFormula,Category[] categories){
			this.model=model;
			this.tokenIndex=tokenIndex;
			this.categories=categories;
			this.documentCount=documentCount;
			this.documentFrequencies=documentFrequencies;
			this.tfIdfFormula=tfIdfFormula;
		}
		@Override
		public List<ClassificationResult> getCandidates(Stream<T> object,int max){
			Feature[] features=toFeatureArray(object,tokenIndex,documentFrequencies,documentCount,tfIdfFormula);
			int categoryIndex=(int)(Linear.predict(model,features)+0.5);
			if(categoryIndex>=0&&categoryIndex<categories.length)
				return Collections.singletonList(new ClassificationResult(1.0,categories[categoryIndex]));
			else
				return Collections.emptyList();
		}
		private static <T> Feature[] toFeatureArray(Stream<T> tokens,Map<T,Integer> tokenIndex,
				ImmutableFrequencies<T> documentFrequencies,long documentCount,TfIdfFormula formula){
			ImmutableFrequencies<T> object=new ImmutableFrequencies<>(tokens);
			Feature[] features=object.toMap().entrySet().stream().filter((e)->tokenIndex.containsKey(e.getKey())).
					map((e)->new FeatureNode(tokenIndex.get(e.getKey()),formula.calculate(e.getValue(),documentFrequencies.getFrequency(e.getKey()),documentCount))).toArray(Feature[]::new);
			double factor=0;
			for(Feature feature:features)
				factor+=feature.getValue()*feature.getValue();
			factor=Math.sqrt(factor);
			for(Feature feature:features)
				feature.setValue(feature.getValue()/factor);
			Arrays.sort(features,(f,g)->Integer.compare(f.getIndex(),g.getIndex()));
			return features;	
		}
	}
	@Override
	protected String getName(){
		return "SVM";
	}
}