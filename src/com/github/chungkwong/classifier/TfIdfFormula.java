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
 * TF-IDF formula
 */
@FunctionalInterface
public interface TfIdfFormula{
	/**
	 * Calcuate TF-IDF
	 * @param freq token frequency
	 * @param docFreq document frequency
	 * @param docCount sample document count
	 * @return TF-IDF
	 */
	double calculate(long freq,long docFreq,long docCount);
	/**
	 * Standard TF-IDF formula
	 */
	public static final TfIdfFormula STANDARD=((freq,docFreq,docCount)->{
		return freq==0?0:(1+Math.log(freq))*Math.log(1+((double)docCount)/docFreq);
	});
	/**
	 * Use token frequency as TF-IDF
	 */
	public static final TfIdfFormula FREQUENCY=((freq,docFreq,docCount)->{
		return freq;
	});
	/**
	 * Use token occurence as TF-IDF
	 */
	public static final TfIdfFormula THREHOLD=((freq,docFreq,docCount)->{
		return freq==0?0:1;
	});
}
