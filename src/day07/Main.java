package day07;

import java.io.File;

import common.FileUtil;

public class Main {
	
	static interface Cost {
		
		public int getCost(int start, int end);
		
	}
	
	static class Part1Cost implements Cost {
		public int getCost(int start, int end) {
			return Math.abs(start - end);
		}
	}
	
	static class Part2Cost implements Cost {
		public int getCost(int start, int end) {
			int len = Math.abs(start - end);
			int cost = len;
			while (len > 1) {
				len--;
				cost += len;
			}
			return cost;
		}
	}
	
	public static void solve(Cost cost) throws Exception {
	
		String line = FileUtil.readLineFromFile(new File("files/day07/input.txt"));
		String [] parts = line.split(",");
		int [] values = new int[parts.length];
		for (int i=0; i<parts.length; i++)
			values[i] = Integer.parseInt(parts[i]);
		
		int min = values[0];
		int max = values[1];
		for (int i=1; i<values.length; i++) {
			min = Math.min(min, values[i]);
			max = Math.max(max, values[i]);
		}
		
		int bestValue = -1;
		int bestScore = Integer.MAX_VALUE;
		
		for (int value=min; value<=max; value++) {
			int score = 0;
			for (int i=0; i<values.length; i++) {
				score += cost.getCost(values[i], value);
			}
			
			if (score < bestScore) {
				bestScore = score;
				bestValue = value;
			}
		}
		
		System.out.println("Best Position: " + bestValue);
		System.out.println("Score: " + bestScore);
	}
	
	public static void solvePart1() throws Exception {
		solve(new Part1Cost());
	}

	public static void solvePart2() throws Exception {
		solve(new Part2Cost());
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
