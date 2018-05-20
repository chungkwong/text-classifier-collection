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
import com.github.chungkwong.classifier.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.logging.*;
import java.util.stream.*;
/**
 * Utility being used to load textual data
 * @author kwong
 */
public class TextDatasetHelper{
	/**
	 * Load samples from a text file where each line provide one record,
	 * Each line contains a category label ,a tab and a piece of text data
	 * @param path the path to the file
	 * @return the samples stream
	 */
	public static Stream<Sample<String>> labeledLines(Path path){
		try{
			return Files.lines(path).filter((line)->line.indexOf('\t')!=-1).map((line)->{
				int i=line.indexOf('\t');
				return new Sample<>(line.substring(i+1,line.length()),new Category(line.substring(0,i)));
			});
		}catch(IOException ex){
			Logger.getLogger(TextDatasetHelper.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	/**
	 * Load samples from a CSV file where each row provide one record
	 * @param path the path to the file
	 * @param categoryField the index of category field
	 * @param dataField the index of data field
	 * @param skipHeader if the first line should be skiped
	 * @param separator the separator between fields
	 * @param quotationMark the quotationMark
	 * @return the samples stream
	 */
	public static Stream<Sample<String>> csvRecords(Path path,int dataField,int categoryField,boolean skipHeader,char separator,char quotationMark){
		try{
			return CsvParser.parse(Files.lines(path),skipHeader,separator,quotationMark).
					map((record)->new Sample<>(record.get(dataField),new Category(record.get(categoryField))));
		}catch(IOException ex){
			Logger.getLogger(TextDatasetHelper.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
	/**
	 * Load samples from a CSV file where each row provide one record
	 * @param path the path to the file
	 * @param categoryField the index of category field
	 * @param dataField the index of data field
	 * @return the samples stream
	 */
	public static Stream<Sample<String>> csvRecords(Path path,int dataField,int categoryField){
		try{
			return CsvParser.parse(Files.lines(path)).
					map((record)->new Sample<>(record.get(dataField),new Category(record.get(categoryField))));
		}catch(IOException ex){
			Logger.getLogger(TextDatasetHelper.class.getName()).log(Level.SEVERE,null,ex);
			return Stream.empty();
		}
	}
}
