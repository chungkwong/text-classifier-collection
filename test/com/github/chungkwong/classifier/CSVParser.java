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
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author kwong
 */
public class CSVParser implements Iterator<List<String>>{
	private final Iterator<String> lines;
	public CSVParser(Stream<String> lines){
		this.lines=lines.iterator();
		this.lines.next();
	}	
	public static Stream<List<String>> parse(Stream<String> lines){
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new CSVParser(lines),0),true);
	}
	@Override
	public boolean hasNext(){
		return lines.hasNext();
	}
	@Override
	public List<String> next(){
		List<String> row=new ArrayList<>();
		String line=lines.next();
		int last=0;
		for(int i=0;i<line.length();i++){
			char c=line.charAt(i);
			if(c==','){
				row.add(line.substring(last,i));
				last=i+1;
			}else if(c=='"'){
				StringBuilder field=new StringBuilder();
				quote:while(true){
					for(i=i+1;i<line.length();i++){
						c=line.charAt(i);
						if(c=='"'){
							if(i+1==line.length()){
								row.add(field.toString());
								return row;
							}else if(line.charAt(i+1)=='"'){
								field.append('"');
								++i;
							}else{
								row.add(field.toString());
								last=i+2;
								++i;
								break quote;
							}
						}else{
							field.append(c);
						}
					}
					line=lines.next();
					field.append("\n");
				}
			}
		}
		row.add(line.substring(last));
		return row;
	}
	public static void main(String[] args) throws IOException{
		parse(Files.lines(new File("data/YouTube-Spam-Collection-v1/Youtube01-Psy.csv").toPath(),StandardCharsets.UTF_8)).forEach((l)->System.out.println(l));
	}
}
