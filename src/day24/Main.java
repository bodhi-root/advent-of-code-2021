package day24;

import java.io.File;
import java.util.List;

import common.FileUtil;

public class Main {
	
	/**
	 * Verify my interpretation of the code.  Namely, that the
	 * first 3 "eql x w" commands are always false and that the
	 * fourth test is where we can start to branch.
	 */
	public static void experiment() throws Exception {
		File file = new File("files/day24/input.txt");
		List<String> lines = FileUtil.readLinesFromFile(file);
		
		//get indices of "inp w" commands:
		System.out.println("'inp w' commands:");
		int [] inputIndices = new int[14];
		int nextIndex = 0;
		for (int i=0; i<lines.size(); i++) {
			if (lines.get(i).equals("inp w")) {
				inputIndices[nextIndex++] = i;
				System.out.println(i);
			}
		}
		System.out.println();
		
		//get indices of "eql x w" commands:
		System.out.println("'eql x w' commands:");
		int [] testIndices = new int[14];
		nextIndex = 0;
		for (int i=0; i<lines.size(); i++) {
			if (lines.get(i).equals("eql x w")) {
				testIndices[nextIndex++] = i;
				System.out.println(i);
			}
		}
		System.out.println();
		
		ALU alu = new ALU();
		alu.loadProgramFromFile(file);
		
		//command index 6: first "eql x w" (always false)
		for (int d1=1; d1<=9; d1++) {
			//System.out.println("d1 = " + d1);
			testRun(alu, String.valueOf(d1), 7);
			//System.out.println("x = " + alu.getValue("x"));
			//System.out.println();
			if (alu.getValue("x") != 0)
				System.out.println("Found example of x != 0");
		}
		
		
		//command index 24: second "eql x w" (always false)
		for (int d1=1; d1<=9; d1++) {
			for (int d2=1; d2<=9; d2++) {
				String input = String.valueOf(d1) + String.valueOf(d2);
				//System.out.println("input = " + input);
				testRun(alu, input, 25);
				//System.out.println("x = " + alu.getValue("x"));
				//System.out.println();
				if (alu.getValue("x") != 0)
					System.out.println("Found example of x != 0");
			}
		}

		//command index 42: third "eql x w" (always false)
		for (int d1=1; d1<=9; d1++) {
			for (int d2=1; d2<=9; d2++) {
				for (int d3=1; d3<=9; d3++) {
					String input = "" + d1 + d2 + d3;
					//System.out.println("input = " + input);
					testRun(alu, input, 43);
					//System.out.println("x = " + alu.getValue("x"));
					//System.out.println();
					if (alu.getValue("x") != 0)
						System.out.println("Found example of x != 0");
				}
			}
		}

		//command index 60: fourth "eql x w" (first real branch)
		for (int d1=1; d1<=9; d1++) {
			for (int d2=1; d2<=9; d2++) {
				for (int d3=1; d3<=9; d3++) {
					for (int d4=1; d4<=9; d4++) {
						String input = "" + d1 + d2 + d3 + d4;
						System.out.println("input = " + input);
						testRun(alu, input, 60);
						System.out.println("x = " + alu.getValue("x"));
						//System.out.println();
						//if (alu.getValue("x") != 0)
						//	System.out.println("Found example of x != 0");
					}
				}
			}
		}
	}
	
	public static void testRun(ALU alu, String input, int lines) {
		alu.reset();
		alu.setInput(input);
		
		for (int i=0; i<lines; i++)
			alu.program.get(i).apply(alu);
		
		/*
		System.out.println("w = " + alu.getValue("w"));
		System.out.println("x = " + alu.getValue("x"));
		System.out.println("y = " + alu.getValue("y"));
		System.out.println("z = " + alu.getValue("z"));
		*/
	}
	
	/**
	 * Brute force approach fails.  There are just too many number combinations
	 * and we don't hit a good one fast enough.
	 */
	public static void solvePart1BruteForce() throws Exception {
		ALU alu = new ALU();
		alu.loadProgramFromFile(new File("files/day24/input.txt"));
		
		//long serialNumber = 99999999999999L;
		//long serialNumber = 11121199999999L;
		//long serialNumber = 99898399433929L;
		//long serialNumber = 59899999999999L;
		//long serialNumber = 11111111111111L;
		//long serialNumber = 99999399999999L;
		//long serialNumber = 91897399498995L;
		long serialNumber = 51121176121391L;
		String serialNumberText = String.valueOf(serialNumber);
		
		int steps = 0;
		
		while (!alu.checkModelNumber(serialNumberText)) {
			
			steps++;
			if (steps % 100000 == 0)
				System.out.println("Checking: " + serialNumber);
			
			serialNumber--;
			serialNumberText = String.valueOf(serialNumber);
			
			//ensure we don't have zeroes:
			while (serialNumberText.indexOf('0') >= 0) {
				serialNumber--;
				serialNumberText = String.valueOf(serialNumber);
			}
			
			if (serialNumberText.length() < 14)
				throw new IllegalStateException("No 14-digit valid number found!");
		}
		
		System.out.println("Part 1: " + serialNumberText);
	}
	
	public static void solvePart1And2() throws Exception {
		ALU alu = new ALU();
		alu.loadProgramFromFile(new File("files/day24/input.txt"));
		
		//solutions found manually by following code logic and assuming all
		//'eql x w' that can evaluate true, do so.  This results in the following
		//rules:
		//
		// d4 = d3 + 1
		// d7 = d6 + 6
		// d9 = d8 - 5
		// d11 = d10 - 1
		// d12 = d5 + 2
		// d13 = d2 + 8
		// d14 = d1 - 4
		String part1 = "91897399498995";
		if (alu.checkModelNumber(part1))
			System.out.println("Part 1: " + part1);
		
		String part2 = "51121176121391";
		if (alu.checkModelNumber(part2))
			System.out.println("Part 2: " + part2);
	}
	
	public static void main(String [] args) {
		try {
			//experiment();
			solvePart1And2();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
