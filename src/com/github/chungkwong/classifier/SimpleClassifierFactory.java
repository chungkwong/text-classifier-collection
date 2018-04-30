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
import java.util.*;
import java.util.function.*;
/**
 *
 * @author kwong
 */
public class SimpleClassifierFactory<C extends Classifier<T>,T,P> implements TrainableClassifierFactory<C,T>{
	private final Map<Category,P> profiles=new HashMap<>();
	private final Supplier<P> profileBuilder;
	private final BiConsumer<T,P> profileUpdater;
	private final Function<Map<Category,P>,C> classifierBuilder;
	public SimpleClassifierFactory(Supplier<P> profileBuilder,BiConsumer<T,P> profileUpdater,Function<Map<Category,P>,C> classifierBuilder){
		this.profileBuilder=profileBuilder;
		this.profileUpdater=profileUpdater;
		this.classifierBuilder=classifierBuilder;
	}
	@Override
	public void train(T data,Category category){
		P profile=profiles.get(category);
		if(profile==null){
			profile=profileBuilder.get();
			profiles.put(category,profile);
		}
		profileUpdater.accept(data,profile);
	}
	@Override
	public C getClassifier(){
		return classifierBuilder.apply(profiles);
	}	
}
