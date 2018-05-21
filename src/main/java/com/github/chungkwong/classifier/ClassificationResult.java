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
import java.util.*;
/**
 * Candidate of classification
 * @author Chan Chung Kwong
 */
public class ClassificationResult implements Comparable<ClassificationResult>{
	private final double score;
	private final Category category;
	/**
	 * Create a candidate of classification
	 * @param score the score, higher mean more confidence on the result
	 * @param category the assigned category
	 */
	public ClassificationResult(double score,Category category){
		this.score=score;
		this.category=category;
	}
	/**
	 * @return assigned category
	 */
	public Category getCategory(){
		return category;
	}
	/**
	 * @return the score
	 */
	public double getScore(){
		return score;
	}
	@Override
	public int compareTo(ClassificationResult o){
		return Double.compare(o.score,score);
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof ClassificationResult&&
				Objects.equals(((ClassificationResult)obj).category,category)&&
				((ClassificationResult)obj).score==score;
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=37*hash+(int)(Double.doubleToLongBits(this.score)^(Double.doubleToLongBits(this.score)>>>32));
		hash=37*hash+Objects.hashCode(this.category);
		return hash;
	}
	
	@Override
	public String toString(){
		return category+":"+score;
	}
}
