package day14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	static class Rule {
		
		String left;
		String right;
		
		public Rule(String left, String right) {
			this.left = left;
			this.right = right;
		}
		
		public static Rule parse(String line) {
			String left = line.substring(0, 2);
			String right = line.substring(6, 7);
			return new Rule(left, right);
		}
		
		public String toString() {
			return left + "=>" + right;
		}
		
	}
	
	static class Input {
		
		String polymer;
		List<Rule> rules = new ArrayList<>();
		
		public static Input readFromFile(File file) throws IOException {
			Input input = new Input();
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			try {
				input.polymer = in.readLine();
				in.readLine();
				
				String line;
				while ((line = in.readLine()) != null)
					input.rules.add(Rule.parse(line));
			}
			finally {
				in.close();
			}
			
			return input;
		}
		
		public void print(PrintStream out) {
			out.println("Polymer: " + polymer);
			out.println("Rules:");
			for (Rule rule : rules)
				out.println(rule.left + "=>" + rule.right); 
		}
		
		public Rule getRuleFor(String match) {
			for (Rule rule : rules) {
				if (rule.left.equals(match))
					return rule;
			}
			return null;
		}
		
		public String step(String polymer) {
			StringBuilder out = new StringBuilder();
			
			out.append(polymer.charAt(0));
			
			for (int i=0; i<polymer.length()-1; i++) {
				String match = polymer.substring(i, i+2);
				Rule rule = getRuleFor(match);
				if (rule != null)
					out.append(rule.right);
				else
					System.err.println("Warning: no match found for '" + match + "'");
				out.append(match.charAt(1));
			}
			
			return out.toString();
		}
		
		public long solvePart1() {
			String polymer = this.polymer;
			for (int i=0; i<10; i++)
				polymer = step(polymer);
			
			return CharCounts.create(polymer).getPart1Answer();
		}
		
		public long solvePart2() {
			//shortcuts for 20-step expansion of each rule
			Map<String, String> pairExpansions = new HashMap<>();
			Map<String, CharCounts> pairCounts = new HashMap<>();
			
			for (Rule rule : rules) {
				String expand20 = expandPair(rule.left, 20);
				pairExpansions.put(rule.left, expand20);
				pairCounts.put(rule.left, CharCounts.create(expand20));
			}
			
			//solve:
			CharCounts counts = new CharCounts();
			counts.increment(polymer.charAt(0));
			
			for (int i=0; i<polymer.length()-1; i++) {
				String iPair = polymer.substring(i, i+2);
				String expansion = pairExpansions.get(iPair);
				
				for (int j=0; j<expansion.length()-1; j++) {
					String jPair = expansion.substring(j, j+2);
					counts.addAll(pairCounts.get(jPair));
					counts.add(jPair.charAt(0), -1);
				}
				counts.add(iPair.charAt(0), -1);
			}
			
			counts.print(System.out);
			return counts.getPart1Answer();
		}
		
		public String expandPair(String pair, int steps) {
			String polymer = pair;
			for (int i=0; i<steps; i++)
				polymer = step(polymer);
			
			return polymer;
		}
		
	}
	
	static class Counter {
		long value = 0;
	}
	
	static class CharCounts {
		
		Map<Character, Counter> counts = new HashMap<>(30);
		
		public void increment(char ch) {
			add(ch, 1);
		}
		public void add(char ch, long value) {
			Counter counter = counts.get(ch);
			if (counter == null) {
				counter = new Counter();
				counts.put(ch, counter);
			}
			counter.value += value;
		}
		public void addAll(CharCounts that) {
			for (Map.Entry<Character, Counter> entry : that.counts.entrySet())
				add(entry.getKey(), entry.getValue().value);
		}
		
		public static CharCounts create(String text) {
			CharCounts counts = new CharCounts();
			char [] chars = text.toCharArray();
			for (char ch : chars)
				counts.increment(ch);
			
			return counts;	
		}
		
		public void print(PrintStream out) {
			for (Map.Entry<Character, Counter> entry : counts.entrySet())
				out.println(entry.getKey() + ": " + entry.getValue().value);
		}
		
		public long getPart1Answer() {
			long minCount = Long.MAX_VALUE;
			long maxCount = Long.MIN_VALUE;
			for (Counter counter : counts.values()) {
				minCount = Math.min(minCount, counter.value);
				maxCount = Math.max(maxCount, counter.value);
			}
			return maxCount - minCount;
		}
		
	}
	
	public static void solvePart1() throws Exception {
		Input input = Input.readFromFile(new File("files/day14/input.txt"));
		input.print(System.out);
		
		String polymer = input.polymer;
		for (int i=0; i<10; i++) {
			//System.out.println();
			System.out.println("Step " + (i+1));
			polymer = input.step(polymer);
			//System.out.println("Result: " + polymer);
			System.out.println("Length: " + polymer.length());
		}
		
		System.out.println("Part 1: " + input.solvePart1());
	}
	
	public static void solvePart2() throws Exception {
		Input input = Input.readFromFile(new File("files/day14/input.txt"));
		input.print(System.out);
		
		System.out.println("Part 2: " + input.solvePart2());
	}
	
	public static void main(String [] args) {
		try {
			//solvePart1();
			solvePart2();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
