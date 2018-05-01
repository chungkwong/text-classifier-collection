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
public class TfIdfClassifierFactory<T> implements TrainableClassifierFactory<Classifier<Stream<T>>,Stream<T>>{
	public static final TfIdfFormula STANDARD=((freq,docFreq,docCount)->{
		return freq==0?0:(1+Math.log(freq))*Math.log(1+((double)docCount)/docFreq);
	});
	public static final TfIdfFormula FREQUENCY=((freq,docFreq,docCount)->{
		return freq;
	});
	private final SimpleClassifierFactory<Classifier<Stream<T>>,Stream<T>,FrequencyProfile> base;
	private TfIdfFormula tfIdfFormula;
	public TfIdfClassifierFactory(){
		tfIdfFormula=STANDARD;
		base=new SimpleClassifierFactory<>(()->new FrequencyProfile<>(),(data,profile)->profile.update(data),(profiles)->new TfIdfClassifier<>(profiles,getTfIdfFormula()));
	}
	public TfIdfClassifierFactory<T> setTfIdfFormula(TfIdfFormula tfIdfFormula){
		this.tfIdfFormula=tfIdfFormula;
		return this;
	}
	public TfIdfFormula getTfIdfFormula(){
		return tfIdfFormula;
	}
	@Override
	public void train(Stream<T> data,Category category){
		base.train(data,category);
	}
	@Override
	public Classifier<Stream<T>> getClassifier(){
		return base.getClassifier();
	}
	@FunctionalInterface
	public interface TfIdfFormula{
		double calculate(int freq,int docFreq,int docCount);
	}
}
class FrequencyProfile<T>{
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
class TfIdfClassifier<T> implements Classifier<Stream<T>>{
	private Map<Category,ImmutableFrequencies<T>> profiles=new HashMap<>();
	private ImmutableFrequencies<T> documentFrequencies;
	private int documentCount;
	private final TfIdfClassifierFactory.TfIdfFormula tfIdfFormula;
	public TfIdfClassifier(Map<Category,FrequencyProfile> profiles,TfIdfClassifierFactory.TfIdfFormula tfIdfFormula){
		Frequencies<T> documentFrequenciesRaw=new Frequencies<>();
		profiles.forEach((k,v)->{
			this.profiles.put(k,new ImmutableFrequencies<>(v.getTokenFrequency()));
			documentFrequenciesRaw.merge(v.getDocumentFrequency());
			documentCount+=v.getDocumentCount();
		});
		documentFrequencies=new ImmutableFrequencies<>(documentFrequenciesRaw);
		System.out.println("TOKEN:"+documentFrequencies.getTokenCount());
		this.tfIdfFormula=tfIdfFormula;
	}
	@Override
	public Category classify(Stream<T> object){
		ImmutableFrequencies<T> document=new ImmutableFrequencies<>(object);
		Optional<ScoredCategory> category=profiles.entrySet().stream().map((e)->new ScoredCategory(cosSquare(document,e.getValue()),e.getKey())).
				max((a,b)->Double.compare(a.getScore(),b.getScore()));
		return category.map((e)->e.getCategory()).orElse(null);
	}
	private double cosSquare(ImmutableFrequencies<T> document,ImmutableFrequencies<T> category){
		ImmutableFrequencies<T> shorter,longer;
		if(document.getTokenCount()>category.getTokenCount()){
			shorter=category;
			longer=document;
		}else{
			shorter=document;
			longer=category;
		}
		double product=0,shortModSq=0,longModSq=0;
		Iterator<Map.Entry<T,Integer>> iterator=shorter.toMap().entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<T,Integer> next=iterator.next();
			T token=next.getKey();
			double shortTfIdf=tfIdfFormula.calculate(next.getValue(),documentFrequencies.getFrequency(token),documentCount);
			double longTfIdf=tfIdfFormula.calculate(longer.getFrequency(token),documentFrequencies.getFrequency(token),documentCount);
			shortModSq+=shortTfIdf*shortTfIdf;
			longModSq+=longTfIdf*longTfIdf;
			product+=shortModSq*longTfIdf;
		}
		return product*product/(shortModSq*longModSq);
	}
}