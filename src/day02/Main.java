package day02;

import java.io.File;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class Submarine {
		
		int depth = 0;
		int x = 0;  //horizontal
		
		public void run(String cmd) {
			String [] parts = cmd.split("\\s+");
			String action = parts[0];
			int amount = Integer.parseInt(parts[1]);
			
			if (action.equalsIgnoreCase("forward"))
				x += amount;
			else if (action.equalsIgnoreCase("up"))
				depth -= amount;
			else if (action.equalsIgnoreCase("down"))
				depth += amount;
			else
				throw new IllegalArgumentException("Unrecognized action: " + action);
		}
		
	}
	
	static class Submarine2 {
		
		int depth = 0;
		int x = 0;  //horizontal
		
		int aim = 0;
		
		public void run(String cmd) {
			String [] parts = cmd.split("\\s+");
			String action = parts[0];
			int amount = Integer.parseInt(parts[1]);
			
			if (action.equalsIgnoreCase("forward")) {
				x += amount;
				depth += amount * aim;
			}
			else if (action.equalsIgnoreCase("up"))
				aim -= amount;
			else if (action.equalsIgnoreCase("down"))
				aim += amount;
			else
				throw new IllegalArgumentException("Unrecognized action: " + action);
		}
		
	}
	
	public static void solvePart1() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day02/input.txt"));
		Submarine sub = new Submarine();
		for (String line : lines)
			sub.run(line);
		
		System.out.println("depth = " + sub.depth + ", x = " + sub.x);
		System.out.println("product = " + (sub.depth * sub.x));
	}
	
	public static void solvePart2() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day02/input.txt"));
		Submarine2 sub = new Submarine2();
		for (String line : lines)
			sub.run(line);
		
		System.out.println("depth = " + sub.depth + ", x = " + sub.x);
		System.out.println("product = " + (sub.depth * sub.x));
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
