package day06;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.FileUtil;

public class Main {
	
	static class Counter {
		
		int count;
		
		public Counter(int value) {
			this.count = value;
		}
		
	}
	
	public static void solvePart1() throws Exception {
		//String input = "3,4,3,1,2";
		String input = FileUtil.readLineFromFile(new File("files/day06/input.txt"));
		
		String [] parts = input.split(",");
		List<Counter> fish = new ArrayList<>();
		for (String part : parts)
			fish.add(new Counter(Integer.parseInt(part.trim())));
		
		for (int day=0; day<80; day++) {
			int newFish = 0;
			for (Counter counter : fish) {
				if (counter.count == 0) {
					newFish++;
					counter.count = 6;
				} else {
					counter.count--;
				}
			}
			
			for (int i=0; i<newFish; i++)
				fish.add(new Counter(8));
			
			System.out.println("After " + (day+1) + " days: " + fish.size());
		}
	}
	
	public static void solvePart2() throws Exception {
		
		//String input = "3,4,3,1,2";
		String input = FileUtil.readLineFromFile(new File("files/day06/input.txt"));
		for (int i=1; i<=18; i++) 
			System.out.println("After " + i + " days: " + countFishAfter(i, input));
		
		System.out.println("After 80 days: " + countFishAfter(80, input));
		System.out.println("After 256 days: " + countFishAfter(256, input));
		
		
	}
	
	public static long countFishAfter(int days, String input) {
		Map<Integer, Long> lookup = new HashMap<>();
		
		long count = 0;
		
		String [] parts = input.split(",");
		for (String part : parts) {
			int value = Integer.parseInt(part.trim());
			Long answer = lookup.get(value);
			if (answer == null) {
				answer = countFish(days, value);
				lookup.put(value, answer);
			}
			count += answer;
		}
		
		return count;
	}
	
	public static long countFish(int daysLeft, int value) {
		//System.out.println("Starting: daysLeft=" + daysLeft + ", value " + value);
		long count = 1;
		
		while (value < daysLeft) {
			daysLeft -= (value+1);
			//System.out.println("New Fish @ daysLeft=" + daysLeft);
			count += countFish(daysLeft, 8);
			
			value = 6;
		}
		
		return count;
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
