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
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public interface DataSet<T>{
	Stream<Sample<T>> getTestSamples();
	static <T> DataSet<T> of(Sample<T>... samples){
		return ()->Arrays.stream(samples);
	}
	static <T> DataSet<T> of(Collection<Sample<T>> samples){
		return ()->samples.stream();
	}
	static <T> DataSet<T> of(Map<Category,T> samples){
		return ()->samples.entrySet().stream().map((e)->new Sample<>(e.getValue(),e.getKey()));
	}
}
