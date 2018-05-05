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
/**
 *
 * @author kwong
 */
public class ClassificationResult implements Comparable<ClassificationResult>{
	private final double score;
	private final Category category;
	public ClassificationResult(double score,Category category){
		this.score=score;
		this.category=category;
	}
	public Category getCategory(){
		return category;
	}
	public double getScore(){
		return score;
	}
	@Override
	public String toString(){
		return category+":"+score;
	}
	@Override
	public int compareTo(ClassificationResult o){
		return Double.compare(o.score,score);
	}
}
