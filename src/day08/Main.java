package day08;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import common.FileUtil;

public class Main {
	
	static Map<Integer, int []> LINE_COUNT_MAP = new HashMap<>();
	
	static {
		LINE_COUNT_MAP.put(6, new int [] {0, 6, 9});
		LINE_COUNT_MAP.put(2, new int [] {1});
		LINE_COUNT_MAP.put(5, new int [] {2, 3, 5});
		LINE_COUNT_MAP.put(4, new int [] {4});
		LINE_COUNT_MAP.put(3, new int [] {7});
		LINE_COUNT_MAP.put(7, new int [] {8});
	}
	
	public static void solvePart1() throws Exception {
		int count = 0;
		
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day08/input.txt"));
		for (String line : lines) {
			int index = line.indexOf('|');
			//String left = line.substring(0, index).trim();
			String right = line.substring(index+1).trim();
			
			String [] parts = right.split("\\s+");
			for (String part : parts) {
				int len = part.length();
				if (LINE_COUNT_MAP.get(len).length == 1)
					count++;
			}
		}
		
		System.out.println("Count: " + count);
	}
	
	public static Set<Character> toSet(String text) {
		Set<Character> set = new HashSet<>();
		
		char [] chars = text.toCharArray();
		for (char ch : chars)
			set.add(ch);
		
		return set;
	}
	
	/*
	  0:      1:      2:      3:      4:
	 aaaa    ....    aaaa    aaaa    ....
	b    c  .    c  .    c  .    c  b    c
	b    c  .    c  .    c  .    c  b    c
	 ....    ....    dddd    dddd    dddd
	e    f  .    f  e    .  .    f  .    f
	e    f  .    f  e    .  .    f  .    f
	 gggg    ....    gggg    gggg    ....

	  5:      6:      7:      8:      9:
	 aaaa    aaaa    aaaa    aaaa    aaaa
	b    .  b    .  .    c  b    c  b    c
	b    .  b    .  .    c  b    c  b    c
	 dddd    dddd    ....    dddd    dddd
	.    f  e    f  .    f  e    f  .    f
	.    f  e    f  .    f  e    f  .    f
	 gggg    gggg    ....    gggg    gggg
		 */
		
	@SuppressWarnings("unchecked")
	static Set<Character> [] VALUES = (Set<Character> []) new Set<?> [] {
		toSet("abcefg"),
		toSet("cf"),
		toSet("acdeg"),
		toSet("acdfg"),
		toSet("bcdf"),
		toSet("abdfg"),
		toSet("abdefg"),
		toSet("acf"),
		toSet("abcdefg"),
		toSet("abcdfg")	
	};

	static String toString(Set<?> values) {
		StringBuilder s = new StringBuilder();
		s.append("{");
		Iterator<?> iValues = values.iterator();
		if (iValues.hasNext())
			s.append(String.valueOf(iValues.next()));
		while (iValues.hasNext())
			s.append(",").append(String.valueOf(iValues.next()));
		s.append("}");
		return s.toString();
	}
	
	public static int solveLine(String line) {
		
		String [] values = new String[10];
		
		int index = line.indexOf('|');
		String left = line.substring(0, index).trim();
		String right = line.substring(index+1).trim();
		
		String [] parts = left.split("\\s+");
		List<String> unknowns = new ArrayList<>();
		
		//set unique values: 1, 4, 7, 8
		for (String part : parts) {
			int len = part.length();
			int [] options = LINE_COUNT_MAP.get(len);
			
			if (options.length == 1) {
				int value = options[0];
				values[value] = part;
			} else {
				unknowns.add(part);
			}
		}
		
		//6 => {0, 6, 9}
		
		//find 9   (contains both 4 and 7)
		Set<Character> target = toSet(values[4]);
		target.addAll(toSet(values[7]));
		for (String text : unknowns) {
			if (text.length() == 6) {
				Set<Character> chars = toSet(text);
				if (chars.containsAll(target)) {
					values[9] = text;
					unknowns.remove(text);
					break;
				}
			}
		}
		
		//find 0 and 6 (0 is the only one containing 1)
		Set<Character> oneChars = toSet(values[1]);
		for (int i=unknowns.size()-1; i>=0; i--) {
			String text = unknowns.get(i);
			if (text.length() == 6) {
				if (toSet(text).containsAll(oneChars))
					values[0] = text;
				else
					values[6] = text;
				
				unknowns.remove(i);
				continue;
			}
		}
		
		// 5 => {2, 3, 5}
		//find 3 (contains 1)
		for (String text : unknowns) {
			if (toSet(text).containsAll(oneChars)) {
				values[3] = text;
				unknowns.remove(text);
				break;
			}
		}
		
		//find 2 and 5 (5 is the only one contained in 9)
		Set<Character> nineChars = toSet(values[9]);
		for (int i=unknowns.size()-1; i>=0; i--) {
			String text = unknowns.get(i);
			if (nineChars.containsAll(toSet(text)))
				values[5] = text;
			else
				values[2] = text;

			unknowns.remove(i);
		}
		
		
		//System.out.println("Solution: ");
		//for (int value=0; value<10; value++)
		//	System.out.println(value + ": " + values[value]);
		
		for (int value=0; value<10; value++) {
			if (values[value] == null)
				throw new IllegalStateException("No solution found for line: " + line);
		}
		
		//solve right side:
		List<Set<Character>> valuesCharSets = new ArrayList<>();
		for (String value : values)
			valuesCharSets.add(toSet(value));
		
		StringBuilder s = new StringBuilder();
		for (String part : right.split("\\s+")) {
			Set<Character> chars = toSet(part);
			for (int value=0; value<10; value++) {
				if (equalCharSets(valuesCharSets.get(value), chars)) {
					s.append(value);
					break;
				}
			}
		}
		
		return Integer.parseInt(s.toString());
	}
	
	public static boolean equalCharSets(Set<Character> a, Set<Character> b) {
		return a.size() == b.size() && a.containsAll(b);
	}
	
	public static void solvePart2() throws Exception {
		
		//TODO: see if solution is 'good' enough
		
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day08/input.txt"));
		int sum = 0;
		for (String line : lines) {
			int value = solveLine(line);
			sum += value;
		}
		System.out.println("Sum: " + sum);
	}
	
	public static void main(String [] args) {
		try {
			//solvePart1();
			
			//System.out.println(solveLine("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf"));
			solvePart2();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
