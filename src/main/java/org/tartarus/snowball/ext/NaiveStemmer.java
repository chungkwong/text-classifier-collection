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
package org.tartarus.snowball.ext;
import org.tartarus.snowball.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class NaiveStemmer extends SnowballStemmer{
	@Override
	public boolean stem(){
		return false;
	}
	@Override
	public boolean equals(Object o){
		return o instanceof TurkishStemmer;
	}
	@Override
	public int hashCode(){
		return NaiveStemmer.class.getName().hashCode();
	}
}
