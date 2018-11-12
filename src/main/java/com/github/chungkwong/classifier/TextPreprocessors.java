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
import com.github.chungkwong.classifier.util.*;
import com.ibm.icu.text.*;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.*;
/**
 * Provides various preprocessors for text in natural language.
 *
 * @author Chan Chung Kwong
 */
public class TextPreprocessors{
	/**
	 * Combining a tokenizer and some filters
	 *
	 * @param preTokenize the filter being applied before tokenization
	 * @param tokenizer being used to break text into tokens
	 * @param postTokenize the filter being applied after tokenization
	 * @return the combined preprocessor
	 */
	public static Function<String,Frequencies<String>> of(Function<String,String> preTokenize,Function<String,Stream<String>> tokenizer,Function<Stream<String>,Stream<String>> postTokenize){
		return preTokenize.andThen(tokenizer).andThen(postTokenize).andThen((s)->new Frequencies<>(s));
	}
	/**
	 * Combining a tokenizer and some filters
	 *
	 * @param tokenizer being used to break text into tokens
	 * @param postTokenize the filter being applied after tokenization
	 * @return the combined preprocessor
	 */
	public static Function<String,Frequencies<String>> of(Function<String,Stream<String>> tokenizer,Function<Stream<String>,Stream<String>> postTokenize){
		return tokenizer.andThen(postTokenize).andThen((s)->new Frequencies<>(s));
	}
	/**
	 * A preprocessor that break text into tokens
	 *
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
	 * A preprocessor that break text into tokens
	 *
	 * @param breakIterator being used to determine the boundary of the tokens
	 * @return the tokenizer
	 */
	public static Function<String,Stream<String>> getIcuTokenizer(com.ibm.icu.text.BreakIterator breakIterator){
		class TokenIterator implements Iterator<String>{
			private final String text;
			private final com.ibm.icu.text.BreakIterator iterator;
			private int lower, upper;
			public TokenIterator(String text){
				this.text=text;
				iterator=(com.ibm.icu.text.BreakIterator)breakIterator.clone();
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
	 * A preprocessor that break text into tokens by split text at separator
	 *
	 * @param pattern the pattern of separators
	 * @param keepSeparator if the separators become tokens
	 * @return the tokenizer
	 */
	public static Function<String,Stream<String>> getSeparatorTokenizer(Pattern pattern,boolean keepSeparator){
		class TokenIterator implements Iterator<String>{
			private final String text;
			private final Matcher matcher;
			private int lower, upper;
			public TokenIterator(String text){
				this.text=text;
				this.matcher=pattern.matcher(text);
				if(matcher.find()){
					if(matcher.start()==0){
						upper=matcher.end();
						if(!keepSeparator){
							findSeparator();
						}
					}else{
						upper=matcher.start();
					}
				}else{
					upper=text.length();
				}
			}
			@Override
			public boolean hasNext(){
				return lower<upper;
			}
			@Override
			public String next(){
				String token=text.substring(lower,upper);
				find();
				return token;
			}
			private void find(){
				if(keepSeparator&&upper!=text.length()&&upper==matcher.start()){
					lower=matcher.start();
					upper=matcher.end();
				}else{
					findSeparator();
				}
			}
			private void findSeparator(){
				if(upper==text.length()){
					lower=upper;
				}else{
					lower=matcher.end();
					if(matcher.find()){
						upper=matcher.start();
					}else{
						upper=text.length();
					}
				}
			}
		}
		return (text)->StreamSupport.stream(Spliterators.spliteratorUnknownSize(new TokenIterator(text),0),false);
	}
	/**
	 * A preprocessor that break text into tokens matching a pattern
	 *
	 * @param pattern the pattern of word
	 * @param keepOther if the character sequence not forming a word become
	 * token
	 * @return the tokenizer
	 */
	public static Function<String,Stream<String>> getWordTokenizer(Pattern pattern,boolean keepOther){
		class TokenIterator implements Iterator<String>{
			private final String text;
			private final Matcher matcher;
			private int lower, upper;
			public TokenIterator(String text){
				this.text=text;
				this.matcher=pattern.matcher(text);
				if(keepOther){
					if(matcher.find()){
						upper=matcher.start()==0?matcher.end():matcher.start();
					}else{
						upper=text.length();
					}
				}else{
					if(matcher.find()){
						lower=matcher.start();
						upper=matcher.end();
					}
				}
			}
			@Override
			public boolean hasNext(){
				return lower<upper;
			}
			@Override
			public String next(){
				String token=text.substring(lower,upper);
				if(upper==text.length()){
					lower=upper;
				}else if(keepOther){
					if(lower==matcher.start()){
						if(matcher.find()){
							lower=upper;
							upper=matcher.start();
						}else{
							lower=upper;
							upper=text.length();
						}
					}else{
						lower=matcher.start();
						upper=matcher.end();
					}
				}else{
					if(matcher.find()){
						lower=matcher.start();
						upper=matcher.end();
					}else{
						lower=upper;
					}
				}
				return token;
			}
		}
		return (text)->StreamSupport.stream(Spliterators.spliteratorUnknownSize(new TokenIterator(text),0),false);
	}
	/**
	 * A pre-tokenize preprocessor that apply Unicode normalization to the text
	 *
	 * @param form Unicode normalization form
	 * @return the normalizier
	 */
	public static Function<String,String> getJavaNormalizier(Normalizer.Form form){
		return (text)->Normalizer.normalize(text,form);
	}
	/**
	 * A pre-tokenize preprocessor that apply text transformation icu4j is
	 * required
	 *
	 * @param transformer a value from Transliterator.getAvailableIDs()
	 * @return the normalizier
	 */
	public static Function<String,String> getIcuTransformer(String transformer){
		return (text)->Transliterator.getInstance(transformer).transform(text);
	}
	/**
	 * A pre-tokenize preprocessor that apply text transformation icu4j is
	 * required
	 *
	 * @param transformer a value from Transliterator.getAvailableIDs()
	 * @param reverse reverse the direction
	 * @return the normalizier
	 */
	public static Function<String,String> getIcuTransformer(String transformer,boolean reverse){
		return (text)->Transliterator.getInstance(transformer,reverse?Transliterator.REVERSE:Transliterator.FORWARD).transform(text);
	}
	/**
	 * A post-tokenize preprocessor that drop tokens that are whitespace only
	 *
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getWhitespaceFilter(){
		return (tokens)->tokens.filter((token)->!token.codePoints().allMatch(Character::isWhitespace));
	}
	/**
	 * A post-tokenize preprocessor that keep only tokens matching a pattern
	 *
	 * @param pattern the pattern
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getKeepPatternFilter(Pattern pattern){
		return (tokens)->tokens.filter((token)->pattern.matcher(token).matches());
	}
	/**
	 * A post-tokenize preprocessor that drop tokens matching a pattern
	 *
	 * @param pattern the pattern
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getDropPatternFilter(Pattern pattern){
		return (tokens)->tokens.filter((token)->!pattern.matcher(token).matches());
	}
	/**
	 * A post-tokenize preprocessor that drop tokens that are stop words
	 *
	 * @param stopwords the words to be dropped
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getStopWordsFilter(Collection<String> stopwords){
		return (tokens)->tokens.filter((token)->!stopwords.contains(token));
	}
	/**
	 * A post-tokenize preprocessor that keep only tokens that are specified
	 * words
	 *
	 * @param protectedWords the words to be kept
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getProtectedWordsFilter(Collection<String> protectedWords){
		return (tokens)->tokens.filter((token)->protectedWords.contains(token));
	}
	/**
	 * A post-tokenize preprocessor that transform tokens into upper case
	 *
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getUpcaser(){
		return getUpcaser(Locale.getDefault());
	}
	/**
	 * A post-tokenize preprocessor that transform tokens into upper case
	 *
	 * @param locale the Locale
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getUpcaser(Locale locale){
		return (tokens)->tokens.map((token)->token.toUpperCase(locale));
	}
	/**
	 * A post-tokenize preprocessor that transform tokens into lower case
	 *
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getDowncaser(){
		return getDowncaser(Locale.getDefault());
	}
	/**
	 * A post-tokenize preprocessor that transform tokens into lower case
	 *
	 * @param locale the Locale
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getDowncaser(Locale locale){
		return (tokens)->tokens.map((token)->token.toLowerCase(locale));
	}
	/**
	 * A post-tokenize preprocessor that fold case icu4j is required
	 *
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getFoldcaser(){
		CaseMap.Fold fold=CaseMap.fold();
		return (tokens)->tokens.map((token)->fold.apply(token));
	}
	/**
	 * A post-tokenize preprocessor that replace pattern occurred in tokens
	 *
	 * @param pattern to be replaced
	 * @param replacement replacement, $ and \ have special meaning as specified
	 * in java.util.Matcher
	 * @param firstOnly only replace the first occurence in each token
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getReplacer(Pattern pattern,String replacement,boolean firstOnly){
		if(firstOnly){
			return (tokens)->tokens.map((token)->pattern.matcher(token).replaceFirst(replacement));
		}else{
			return (tokens)->tokens.map((token)->pattern.matcher(token).replaceAll(replacement));
		}
	}
	/**
	 * A post-tokenize preprocessor that map tokens
	 *
	 * @param mapping the mapping
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getMapper(Map<String,String> mapping){
		return (tokens)->tokens.map((token)->mapping.getOrDefault(token,token));
	}
	/**
	 * A post-tokenize preprocessor that apply stemming to the tokens
	 *
	 * @param locale identify the language
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getStemmer(Locale locale){
		SnowballStemmer stemmer;
		switch(locale.getISO3Language()){
			case "ara":
				stemmer=new ArabicStemmer();
				break;
			case "dan":
				stemmer=new DanishStemmer();
				break;
			case "nld":
				stemmer=new DutchStemmer();
				break;
			case "eng":
				stemmer=new EnglishStemmer();
				break;
			case "fin":
				stemmer=new FinnishStemmer();
				break;
			case "fra":
				stemmer=new FrenchStemmer();
				break;
			case "deu":
				stemmer=new GermanStemmer();
				break;
			case "hun":
				stemmer=new HungarianStemmer();
				break;
			case "ind":
				stemmer=new IndonesianStemmer();
				break;
			case "gle":
				stemmer=new IrishStemmer();
				break;
			case "ita":
				stemmer=new ItalianStemmer();
				break;
			case "nep":
				stemmer=new NepaliStemmer();
				break;
			case "nor":
				stemmer=new NorwegianStemmer();
				break;
			case "por":
				stemmer=new PortugueseStemmer();
				break;
			case "ron":
				stemmer=new RomanianStemmer();
				break;
			case "spa":
				stemmer=new SpanishStemmer();
				break;
			case "rus":
				stemmer=new RussianStemmer();
				break;
			case "swe":
				stemmer=new SwedishStemmer();
				break;
			case "tam":
				stemmer=new TamilStemmer();
				break;
			case "tur":
				stemmer=new TurkishStemmer();
				break;
			default:
				stemmer=new NaiveStemmer();
				break;
		}
		return getSnowballStemmer(stemmer);
	}
	/**
	 * A post-tokenize preprocessor that apply stemming to the tokens based on
	 * Porter's algorithm
	 *
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getPorterStemmer(){
		return getSnowballStemmer(new PorterStemmer());
	}
	/**
	 * A post-tokenize preprocessor that apply stemming to the tokens based on
	 * Lovins' algorithm
	 *
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getLovinsStemmer(){
		return getSnowballStemmer(new LovinsStemmer());
	}
	/**
	 * A post-tokenize preprocessor that apply Dutch stemming to the tokens
	 *
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getKraaijPohlmannStemmer(){
		return getSnowballStemmer(new KraaijPohlmannStemmer());
	}
	/**
	 * A post-tokenize preprocessor that apply German stemming to the tokens
	 * taking representation of umlaut by following e into account
	 *
	 * @return the stemmer
	 */
	public static Function<Stream<String>,Stream<String>> getAlternativeGermanStemmer(){
		return getSnowballStemmer(new German2Stemmer());
	}
	/**
	 * A post-tokenize preprocessor that apply stemmer to the tokens
	 *
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
	 * A post-tokenize preprocessor that generate n-gram tokens
	 *
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
	/**
	 * A post-tokenize preprocessor that convert each token into its synonyms
	 *
	 * @param synonyms the synonyms for each token
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getSynonymGenerator(Map<String,Collection<String>> synonyms){
		return getFlatFilter((token)->synonyms.getOrDefault(token,Collections.singletonList(token)).stream());
	}
	/**
	 * A post-tokenize preprocessor that convert each token into zero or more
	 * tokens
	 *
	 * @param transformer
	 * @return the filter
	 */
	public static Function<Stream<String>,Stream<String>> getFlatFilter(Function<String,Stream<String>> transformer){
		return (tokens)->tokens.flatMap((token)->transformer.apply(token));
	}
}
