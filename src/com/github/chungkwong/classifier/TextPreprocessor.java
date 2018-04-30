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
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class TextPreprocessor{
	public static Function<String,Stream<String>> of(Function<String,Stream<String>> tokenizer,Function<Stream<String>,Stream<String>>... filters){
		Function<String,Stream<String>> preprocessor=tokenizer;
		for(Function<Stream<String>,Stream<String>> filter:filters){
			preprocessor=preprocessor.andThen(filter);
		}
		return preprocessor;
	}
	public static Function<String,Stream<String>> getJavaTokenizer(BreakIterator breakIterator){
		class TokenIterator implements Iterator<String>{
			private final String text;
			private final BreakIterator iterator;
			private int lower,upper;
			public TokenIterator(String text){
				this.text=text;
				iterator=(BreakIterator)breakIterator.clone();
				iterator.setText(text);
				lower=iterator.first();
				upper=iterator.next();
			}
			@Override
			public boolean hasNext(){
				return upper!=BreakIterator.DONE;
			}
			@Override
			public String next(){
				String token=text.substring(lower,upper);
				lower=upper;
				upper=iterator.next();
				return token;
			}
		}
		return (text)->StreamSupport.stream(Spliterators.spliteratorUnknownSize(new TokenIterator(text),0),false);
	}
	public static Function<Stream<String>,Stream<String>> getJavaNormalizier(Normalizer.Form form){
		return (tokens)->tokens.map((token)->Normalizer.normalize(token,form));
	}
	public static Function<Stream<String>,Stream<String>> getWhitespaceFilter(){
		return (tokens)->tokens.filter((token)->!token.codePoints().allMatch(Character::isWhitespace));
	}
	public static Function<Stream<String>,Stream<String>> getUpcaser(){
		return getUpcaser(Locale.getDefault());
	}
	public static Function<Stream<String>,Stream<String>> getUpcaser(Locale locale){
		return (tokens)->tokens.map((token)->token.toUpperCase(locale));
	}
	public static Function<Stream<String>,Stream<String>> getDowncaser(){
		return getDowncaser(Locale.getDefault());
	}
	public static Function<Stream<String>,Stream<String>> getDowncaser(Locale locale){
		return (tokens)->tokens.map((token)->token.toLowerCase(locale));
	}
}
