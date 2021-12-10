package day01;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import common.FileUtil;

public class Main {

	public static void solvePart1() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day01/input.txt"));
		List<Integer> values = new ArrayList<>();
		for (String line : lines)
			values.add(Integer.parseInt(line));
		
		int lastValue = values.get(0);
		int increases = 0;
		for (int i=1; i<values.size(); i++) {
			int value = values.get(i);
			if (value > lastValue)
				increases++;
			
			lastValue = value;
		}
		
		System.out.println(increases + " out of " + lines.size() + " increased");
	}
	
	public static void solvePart2() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day01/input.txt"));
		List<Integer> values = new ArrayList<>();
		for (String line : lines)
			values.add(Integer.parseInt(line));
		
		List<Integer> sums = new ArrayList<>();
		for (int i=2; i<values.size(); i++) {
			int sum = values.get(i) + values.get(i-1) + values.get(i-2);
			sums.add(sum);
		}
		
		int lastValue = sums.get(0);
		int increases = 0;
		for (int i=1; i<sums.size(); i++) {
			int value = sums.get(i);
			if (value > lastValue)
				increases++;
			
			lastValue = value;
		}
		
		System.out.println(increases + " out of " + sums.size() + " increased");
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
