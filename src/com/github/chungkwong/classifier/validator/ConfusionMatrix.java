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
package com.github.chungkwong.classifier.validator;
import com.github.chungkwong.classifier.*;
import com.github.chungkwong.classifier.util.*;
import java.util.*;
/**
 * Confusion matrix
 * @author kwong
 */
public class ConfusionMatrix{
	private final Frequencies<Pair<Category,Category>> matrix;
	private long testTime;
	public ConfusionMatrix(){
		this.matrix=new Frequencies<>(true);
	}
	void advanceFrequency(Category real,Category classified){
		matrix.advanceFrequency(new Pair<>(real,classified));
	}
	void advanceFrequency(Category real,Category classified,long times){
		matrix.advanceFrequency(new Pair<>(real,classified),times);
	}
	public void getFrequency(Category real,Category classified){
		matrix.getFrequency(new Pair<>(real,classified));
	}
	void setTestTime(long testTime){
		this.testTime=testTime;
	}
	public long getTestTime(){
		return testTime;
	}
	public double getF1Measure(Category category){
		double recall=getRecall(category);
		double precision=getPrecision(category);
		return 2*recall*precision/(recall+precision);
	}
	public double getRecall(Category category){
		Counter total=new Counter();
		matrix.toMap().forEach((k,v)->{
			if(Objects.equals(category,k.getKey()))
				total.advance(v.getCount());
		});
		return (matrix.getFrequency(new Pair<>(category,category))+0.0)/total.getCount();
	}
	public double getPrecision(Category category){
		Counter total=new Counter();
		matrix.toMap().forEach((k,v)->{
			if(Objects.equals(category,k.getValue()))
				total.advance(v.getCount());
		});
		return (matrix.getFrequency(new Pair<>(category,category))+0.0)/total.getCount();
	}
	public double getAccuracy(){
		Counter accurate=new Counter();
		Counter total=new Counter();
		matrix.toMap().forEach((k,v)->{
			total.advance(v.getCount());
			if(Objects.equals(k.getKey(),k.getValue()))
				accurate.advance(v.getCount());
		});
		return (accurate.getCount()+0.0)/total.getCount();
	}
	public long getTestSampleCount(){
		Counter total=new Counter();
		matrix.toMap().forEach((k,v)->{
			total.advance(v.getCount());
		});
		return total.getCount();
	}
	public Set<Category> getCategories(){
		HashSet<Category> categories=new HashSet<>();
		matrix.toMap().forEach((k,v)->{
			categories.add(k.getKey());
			categories.add(k.getValue());
		});
		return categories;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof ConfusionMatrix&&Objects.equals(matrix,((ConfusionMatrix)obj).matrix);
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=79*hash+Objects.hashCode(this.matrix);
		return hash;
	}
	@Override
	public String toString(){
		Category[] categories=getCategories().toArray(new Category[0]);
		StringBuilder buf=new StringBuilder("\n");
		for(Category category:categories){
			buf.append('\t').append(category);
		}
		for(Category first:categories){
			buf.append('\n').append(first);
			for(Category second:categories){
				buf.append('\t').append(matrix.getFrequency(new Pair<>(first,second)));
			}
		}
		buf.append("\nSample:").append(getTestSampleCount());
		buf.append("\nAccuracy:").append(getAccuracy());
		buf.append("\nTime:").append(getTestTime());
		return buf.toString();
	}
}