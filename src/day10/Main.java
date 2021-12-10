package day10;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import common.FileUtil;

public class Main {
	
	static class Parser {
		
		Set<Character> openingChars = new HashSet<>();
		Set<Character> closingChars = new HashSet<>();
		Map<Character, Character> openToCloseMap = new HashMap<>();
		
		public Parser() {
			openToCloseMap.put('(', ')');
			openToCloseMap.put('[', ']');
			openToCloseMap.put('{', '}');
			openToCloseMap.put('<', '>');
			
			openingChars.addAll(openToCloseMap.keySet());
			closingChars.addAll(openToCloseMap.values());
		}
	
		public boolean isOpeningChar(char ch) {
			return openingChars.contains(ch);
		}
		public boolean isClosingChar(char ch) {
			return closingChars.contains(ch);
		}
		public char getClosingChar(char ch) {
			return openToCloseMap.get(ch);
		}
		
		public int checkForCorruption(String text) {
			char [] chars = text.toCharArray();
			Stack<Character> stack = new Stack<>();
			for (int i=0; i<chars.length; i++) {
				char ch = chars[i];
				if (isOpeningChar(ch)) {
					stack.push(getClosingChar(ch));
				} else {
					if (stack.isEmpty())
						return i;
					if (ch != stack.pop())
						return i;
				}
			}
			
			return -1;
		}
		
		public String completeLine(String text) {
			char [] chars = text.toCharArray();
			Stack<Character> stack = new Stack<>();
			for (int i=0; i<chars.length; i++) {
				char ch = chars[i];
				if (isOpeningChar(ch)) {
					stack.push(getClosingChar(ch));
				} else {
					if (stack.isEmpty())
						return "";
					if (ch != stack.pop())
						return "";
				}
			}
			
			if (stack.isEmpty())
				return "";
			
			char [] end = new char[stack.size()];
			for (int i=0; i<end.length; i++)
				end[i] = stack.pop();
			return new String(end);
		}
		
	}
	
	
	
	public static void solvePart1() throws Exception {
		Map<Character, Integer> scores = new HashMap<>();
		scores.put(')', 3);
		scores.put(']', 57);
		scores.put('}', 1197);
		scores.put('>', 25137);
		
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day10/input.txt"));
		
		Parser parser = new Parser();
		int sum = 0;
		for (String line : lines) {
			int index = parser.checkForCorruption(line);
			if (index >= 0) {
				sum += scores.get(line.charAt(index));
			}
		}
		
		System.out.println("Score: " + sum);
	}
	
	public static void solvePart2() throws Exception {
		Map<Character, Integer> scores = new HashMap<>();
		scores.put(')', 1);
		scores.put(']', 2);
		scores.put('}', 3);
		scores.put('>', 4);
		
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day10/input.txt"));
		
		Parser parser = new Parser();
		List<Long> lineScores = new ArrayList<>();
		for (String line : lines) {
			String end = parser.completeLine(line);
			if (end.length() > 0) {
				char [] chars = end.toCharArray();
				
			    long score = 0;
				for (char ch : chars)
					score = score * 5 + scores.get(ch);
				
				lineScores.add(score);
			}
		}
		
		Collections.sort(lineScores);
		int midIndex = lineScores.size() / 2;
		System.out.println("Score: " + lineScores.get(midIndex));
	}
	
	public static void main(String [] args) {
		try {
			solvePart1();
			solvePart2();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
