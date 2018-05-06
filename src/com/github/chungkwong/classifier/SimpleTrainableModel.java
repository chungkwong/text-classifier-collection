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
 * Trainable model that only rely on profile about each category
 * @author kwong
 * @param <T> the type of the object that the model will be used to classify
 * @param <P> the type of the profile about each category
 */
public class SimpleTrainableModel<T,P> implements Trainable<T>{
	private final Map<Category,P> profiles=new HashMap<>();
	private final Supplier<P> profileBuilder;
	private final BiConsumer<T,P> profileUpdater;
	/**
	 * Create a simple trainable model
	 * @param profileBuilder being used to create a profile for a category
	 * @param profileUpdater being called to update profile according to training data
	 */
	public SimpleTrainableModel(Supplier<P> profileBuilder,BiConsumer<T,P> profileUpdater){
		this.profileBuilder=profileBuilder;
		this.profileUpdater=profileUpdater;
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
	/**
	 * @return the profiles about each category
	 */
	public Map<Category,P> getProfiles(){
		return profiles;
	}
}
