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
import java.util.function.*;
/**
 *
 * @author kwong
 */
public class ClassifierFactoryBuilder<T>{
	private final Supplier<TrainableClassifierFactory<Classifier<T>,T>> factorySupplier;
	private final String name;
	public ClassifierFactoryBuilder(Supplier<TrainableClassifierFactory<Classifier<T>,T>> factorySupplier,String name){
		this.factorySupplier=factorySupplier;
		this.name=name;
	}
	public TrainableClassifierFactory<Classifier<T>,T> getFactory(){
		return factorySupplier.get();
	}
	public String getName(){
		return name;
	}
	@Override
	public String toString(){
		return getName();
	}
}
