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
 * Factory for C4.5 classifier.
 * @author Chan Chung Kwong
 * @param <T> Underlying type to be classified
 */
public class C45ClassifierFactory<T> extends StreamClassifierFactory<Classifier<Stream<T>>,DocumentVectorsModel<T>,T>{
	/**
	 * Create a C4.5 classifier factory
	 */
	public C45ClassifierFactory(){
	}
	@Override
	public Classifier<Stream<T>> createClassifier(DocumentVectorsModel<T> model){
		return new C45Classifier<>(buildTree(model));
	}
	private DecisionTree<T> buildTree(DocumentVectorsModel<T> model){
		Map<Category,MutableFrequencies<T>> documentFrequencies=model.getDocumentFrequencies();
		ImmutableFrequencies<Category> sampleCounts=model.getSampleCounts();
		T feature=selectFeature(documentFrequencies,model.getTotalDocumentFrequencies(),
				sampleCounts,model.getSampleCount());
		if(feature==null){
			return new DecisionTree<>(model.getProfiles().entrySet().stream().max((e1,e2)->
					Long.compare(e1.getValue().getDocumentVectors().size(),e2.getValue().getDocumentVectors().size())).
					map((e)->e.getKey()).orElse(null));
		}else{
			DocumentVectorsModel<T> subModel=new DocumentVectorsModel<>();
			model.getProfiles().entrySet().forEach((e)->subModel.getProfiles().put(e.getKey(),new DocumentVectorsModel.VectorsProfile<>(
					e.getValue().getDocumentVectors().stream().filter((v)->v.getFrequency(feature)==0).collect(Collectors.toList()))));
			model.getProfiles().keySet().stream().filter((cat)->subModel.getProfiles().get(cat).getDocumentVectors().isEmpty()).
					forEach((cat)->subModel.getProfiles().remove(cat));
			DecisionTree<T> lower=buildTree(subModel);
			subModel.getProfiles().clear();
			model.getProfiles().entrySet().forEach((e)->subModel.getProfiles().put(e.getKey(),new DocumentVectorsModel.VectorsProfile<>(
					e.getValue().getDocumentVectors().stream().filter((v)->v.getFrequency(feature)>0).collect(Collectors.toList()))));
			model.getProfiles().keySet().stream().filter((cat)->subModel.getProfiles().get(cat).getDocumentVectors().isEmpty()).
					forEach((cat)->subModel.getProfiles().remove(cat));
			DecisionTree<T> higher=buildTree(subModel);
			return new DecisionTree<>(lower,higher,0,feature);
		}
	}
	private static final double threhold=10e-6;
	private T selectFeature(Map<Category,MutableFrequencies<T>> model,ImmutableFrequencies<T> docFreq,Frequencies<Category> sampleCounts,long sampleCount){
		T bestFeature=null;
		double maxGain=Double.NEGATIVE_INFINITY;
		for(T feature:docFreq.toMap().keySet()){
			double gain=getInformationGain(feature,model,docFreq,sampleCounts,sampleCount);
			if(gain>maxGain){
				maxGain=gain;
				bestFeature=feature;
			}
		}
		return maxGain>threhold?bestFeature:null;
	}
	private double getInformationGain(T feature,Map<Category,MutableFrequencies<T>> model,Frequencies<T> docFreq,Frequencies<Category> sampleCounts,long sampleCount){
		return getEntropy(feature,docFreq,sampleCount)-getSplitEntropy(feature,model,sampleCounts,sampleCount);
	}
	private double getEntropy(T feature,Frequencies<T> docFreq,long sampleCount){
		double freq=((double)docFreq.getFrequency(feature))/sampleCount;
		double nfreq=1-freq;
		return -freq*Math.log(freq)-nfreq*Math.log(nfreq);
	}
	private double getSplitEntropy(T feature,Map<Category,MutableFrequencies<T>> model,Frequencies<Category> sampleCounts,long sampleCount){
		double entropy=0;
		for(Map.Entry<Category,MutableFrequencies<T>> entry:model.entrySet()){
			long documentCount=sampleCounts.getFrequency(entry.getKey());
			entropy+=getEntropy(feature,entry.getValue(),documentCount)*documentCount;
		}
		return entropy/sampleCount;
	}
	@Override
	public DocumentVectorsModel<T> createModel(){
		return new DocumentVectorsModel<>();
	}
	private static class C45Classifier<T> implements Classifier<Stream<T>>{
		private final DecisionTree<T> tree;
		public C45Classifier(DecisionTree<T> tree){
			this.tree=tree;
		}
		@Override
		public List<ClassificationResult> getCandidates(Stream<T> object,int max){
			ImmutableFrequencies<T> data=new ImmutableFrequencies<>(object);
			DecisionTree<T> node=tree;
			while(!node.isLeaf()){
				node=node.getChild(data);
			}
			return Collections.singletonList(new ClassificationResult(1.0,node.getCategory()));
		}
	}
	private static class DecisionTree<T>{
		private final DecisionTree<T> lower;
		private final DecisionTree<T> higher;
		private final T feature;
		private final long cut;
		private final Category category;
		public DecisionTree(DecisionTree<T> lower,DecisionTree<T> higher,long cut,T feature){
			this.lower=lower;
			this.higher=higher;
			this.feature=feature;
			this.cut=cut;
			this.category=null;
		}
		public DecisionTree(Category category){
			this.lower=null;
			this.higher=null;
			this.feature=null;
			this.cut=0;
			this.category=category;
		}
		public boolean isLeaf(){
			return category!=null;
		}
		public Category getCategory(){
			return category;
		}
		public DecisionTree<T> getChild(ImmutableFrequencies<T> object){
			return object.getFrequency(feature)<=cut?lower:higher;
		}
		@Override
		public String toString(){
			return toString(0,new StringBuilder()).toString();
		}
		public StringBuilder toString(int lv,StringBuilder builder){
			System.err.println(lv);
			for(int i=0;i<lv;i++)
				builder.append('-');
			if(isLeaf()){
				builder.append(category.getName()).append('\n');
			}else{
				builder.append(feature).append(':').append(cut).append('\n');
				lower.toString(lv+1,builder);
				higher.toString(lv+1,builder);
			}
			return builder;
		}
	}
	@Override
	protected String getName(){
		return "C4.5";
	}
}