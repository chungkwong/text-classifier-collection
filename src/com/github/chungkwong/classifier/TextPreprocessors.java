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
import com.github.chungkwong.classifier.util.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.*;
/**
 * Provides various preprocessors for text in natural language.
 * @author kwong
 */
public class TextPreprocessors{
	/**
	 * Combining a tokenizer and a chain of filters
	 * @param tokenizer being used to break text into tokens
	 * @param filters being used to transform the stream of token
	 * @return the combined preprocessor
	 */
	public static Function<String,Stream<String>> of(Function<String,Stream<String>> tokenizer,Function<Stream<String>,Stream<String>>... filters){
		Function<String,Stream<String>> preprocessor=tokenizer;
		for(Function<Stream<String>,Stream<String>> filter:filters){
			preprocessor=preprocessor.andThen(filter);
		}
		return preprocessor;
	}
	/**
	 * A preprocessor that break text into tokens 
	 * @param breakIterator being used to determine the boundary of the tokens
	 * @return the tokenizer
	 */
	public static Function<String,Stream<String>> getJavaTokenizer(BreakIterator breakIterator){
		class TokenIterator implements Iterator<String>{
			private final String text;
			private final BreakIterator iterator;
			private int lower, upper;
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
	/**
	 * A preprocessor that apply Unicode normalization to the tokens 
	 * @param form Unicode normalization form
	 * @return the normalizier
	 */
	public static Function<Stream<String>,Stream<String>> getJavaNormalizier(Normalizer.Form form){
		return (tokens)->tokens.map((token)->Normalizer.normalize(token,form));
	}
	/**
	 * A preprocessor that drop tokens that are whitespace only
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getWhitespaceFilter(){
		return (tokens)->tokens.filter((token)->!token.codePoints().allMatch(Character::isWhitespace));
	}
	/**
	 * A preprocessor that transform tokens into upper case
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getUpcaser(){
		return getUpcaser(Locale.getDefault());
	}
	/**
	 * A preprocessor that transform tokens into upper case
	 * @param locale the Locale
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getUpcaser(Locale locale){
		return (tokens)->tokens.map((token)->token.toUpperCase(locale));
	}
	/**
	 * A preprocessor that transform tokens into lower case
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getDowncaser(){
		return getDowncaser(Locale.getDefault());
	}
	/**
	 * A preprocessor that transform tokens into lower case
	 * @param locale the Locale
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getDowncaser(Locale locale){
		return (tokens)->tokens.map((token)->token.toLowerCase(locale));
	}
	/**
	 * A preprocessor that apply stemming to the tokens 
	 * @param locale identify the language
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getStemmer(Locale locale){
		SnowballStemmer stemmer;
		switch(locale.getISO3Language()){
			case "ara":stemmer=new ArabicStemmer();break;
			case "dan":stemmer=new DanishStemmer();break;
			case "nld":stemmer=new DutchStemmer();break;
			case "eng":stemmer=new EnglishStemmer();break;
			case "fin":stemmer=new FinnishStemmer();break;
			case "fra":stemmer=new FrenchStemmer();break;
			case "deu":stemmer=new GermanStemmer();break;
			case "hun":stemmer=new HungarianStemmer();break;
			case "ind":stemmer=new IndonesianStemmer();break;
			case "gle":stemmer=new IrishStemmer();break;
			case "ita":stemmer=new ItalianStemmer();break;
			case "nep":stemmer=new NepaliStemmer();break;
			case "nor":stemmer=new NorwegianStemmer();break;
			case "por":stemmer=new PortugueseStemmer();break;
			case "ron":stemmer=new RomanianStemmer();break;
			case "spa":stemmer=new SpanishStemmer();break;
			case "rus":stemmer=new RussianStemmer();break;
			case "swe":stemmer=new SwedishStemmer();break;
			case "tam":stemmer=new TamilStemmer();break;
			case "tur":stemmer=new TurkishStemmer();break;
			default:stemmer=new NaiveStemmer();break;
		}
		return getSnowballStemmer(stemmer);
	}
	/**
	 * A preprocessor that apply stemming to the tokens based on Porter's algorithm 
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getPorterStemmer(){
		return getSnowballStemmer(new PorterStemmer());
	}
	/**
	 * A preprocessor that apply stemming to the tokens based on Lovins' algorithm
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getLovinsStemmer(){
		return getSnowballStemmer(new LovinsStemmer());
	}
	/**
	 * A preprocessor that apply Dutch stemming to the tokens 
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getKraaijPohlmannStemmer(){
		return getSnowballStemmer(new KraaijPohlmannStemmer());
	}
	/**
	 * A preprocessor that apply German stemming to the tokens taking representation of umlaut by following e into account
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getAlternativeGermanStemmer(){
		return getSnowballStemmer(new German2Stemmer());
	}
	/**
	 * A preprocessor that apply stemmer to the tokens 
	 * @param stemmer Snowball stemmer
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getSnowballStemmer(SnowballStemmer stemmer){
		return (tokens)->tokens.map((token)->{
			stemmer.setCurrent(token);
			stemmer.stem();
			return stemmer.getCurrent();
		});
	}
	/**
	 * A preprocessor that generate n-gram tokens
	 * @param n the number of tokens being combined into a token 
	 * @return the n-gram generator
	 */
	public static Function<Stream<String>,Stream<String>> getNgramGenerator(int... n){
		class NgramIterator implements Iterator<String>{
			private final Iterator<String> underlying;
			private final int bufferSize;
			private final String[] buffer;
			private final int[] n;
			private final CyclicCounter bufferPointer;
			private final CyclicCounter curr;
			private final int[][] offset;
			private final StringBuilder str=new StringBuilder();
			private static final String SEPARATOR=" ";
			public NgramIterator(Iterator<String> underlying,int... n){
				this.underlying=underlying;
				this.n=n;
				bufferSize=Arrays.stream(n).max().getAsInt();
				buffer=new String[bufferSize];
				bufferPointer=new CyclicCounter(bufferSize);
				curr=new CyclicCounter(n.length);
				offset=new int[bufferSize][bufferSize];
				for(int i=0;i<bufferSize;i++){
					for(int j=0;j<bufferSize;j++){
						offset[i][j]=(i-j+bufferSize-1)%bufferSize;
					}
				}
			}
			@Override
			public boolean hasNext(){
				fetchNext();
				return str.length()>0;
			}
			@Override
			public String next(){
				fetchNext();
				String ret=str.toString();
				str.setLength(0);
				return ret;
			}
			private void fetchNext(){
				if(str.length()>0){
					return;
				}
				while(true){
					if(curr.getCount()==0){
						if(!underlying.hasNext()){
							return;
						}
						buffer[bufferPointer.getCount()]=underlying.next();
						bufferPointer.advance();
					}
					int k=n[curr.getCount()]-1;
					int p=bufferPointer.getCount();
					curr.advance();
					if(buffer[offset[p][k]]!=null){
						for(int j=k;j>=0;j--){
							str.append(buffer[offset[p][j]]).append(SEPARATOR);
						}
						return;
					}
				}
			}
		}
		return (tokens)->StreamSupport.stream(Spliterators.spliteratorUnknownSize(new NgramIterator(tokens.iterator(),n),0),false);
	}
}
