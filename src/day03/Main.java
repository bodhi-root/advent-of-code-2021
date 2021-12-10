package day03;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class Diagnostics {
		
		List<String> lines;
		
		public Diagnostics(List<String> lines) {
			this.lines = lines;
		}
		
		public int getPowerConsumption() {
			int [] counts = new int[lines.get(0).length()];
			for (String line : lines) {
				char [] chars = line.toCharArray();
				for (int i=0; i<counts.length; i++) {
					if (chars[i] == '1')
						counts[i]++;
				}
			}
			
			StringBuilder gamma = new StringBuilder();
			StringBuilder epsilon = new StringBuilder();
			for (int i=0; i<counts.length; i++) {
				if (counts[i] > (lines.size() - counts[i])) {
					gamma.append('1');
					epsilon.append('0');
				}
				else {
					gamma.append('0');
					epsilon.append('1');
				}
			}
			
			int iGamma = Integer.parseInt(gamma.toString(), 2);
			int iEpsilon = Integer.parseInt(epsilon.toString(), 2);
			int power = iGamma * iEpsilon;
			
			System.out.println("Gamma: " + gamma.toString() + " " + iGamma);
			System.out.println("Epsilon: " + epsilon.toString() + " " + iEpsilon);
			System.out.println("Power Consumption: " + power);
			
			return power;
		}
		
		protected int countChars(List<String> lines, int position, char target) {
			int count = 0;
			for (String line : lines) {
				if (line.charAt(position) == target)
					count++;
			}
			return count;
		}
		
		public String applyPart2Filter(boolean oxygen) {
			int len = lines.get(0).length();
			
			List<String> lines = new ArrayList<>(this.lines);	//make copy
			int bitPosition = 0;
			
			while (lines.size() > 1) {
				
				int onesCount = countChars(lines, bitPosition, '1');
				int zeroesCount = lines.size() - onesCount;
				
				char matchChar;
				if (onesCount == zeroesCount) {
					matchChar = oxygen ? '1' : '0';
				} else {
					if (oxygen) {
						matchChar = onesCount > zeroesCount ? '1' : '0';
					} else {
						matchChar = onesCount > zeroesCount ? '0' : '1';
					}
				}
									
				for (int i=lines.size()-1; i>=0; i--) {
					if (lines.get(i).charAt(bitPosition) != matchChar)
						lines.remove(i);
				}

				bitPosition++;
				if (bitPosition >= len)
					bitPosition = 0;
			}
			
			return lines.get(0);
		}
		
		public int getOxygenRating() {
			return Integer.parseInt(applyPart2Filter(true), 2);
		}
		public int getCo2Rating() {
			return Integer.parseInt(applyPart2Filter(false), 2);
		}
		
	}
	
	public static int binaryToInt(String line) {
		return Integer.parseInt(line, 2);
	}
	
	public static void solvePart1() throws Exception {
		//List<String> lines = FileUtil.readLinesFromFile(new File("files/day03/test.txt"));
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day03/input.txt"));
		for (String line : lines)
			System.out.println(line);
		
		Diagnostics diagnostics = new Diagnostics(lines);
		System.out.println(diagnostics.getPowerConsumption());
	}
	
	public static void solvePart2() throws Exception {
		//List<String> lines = FileUtil.readLinesFromFile(new File("files/day03/test.txt"));
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day03/input.txt"));
		for (String line : lines)
			System.out.println(line);

		Diagnostics diagnostics = new Diagnostics(lines);
		int oxygenRating = diagnostics.getOxygenRating();
		int co2Rating = diagnostics.getCo2Rating();
		int product = oxygenRating * co2Rating;
		
		System.out.println("Oxygen Rating: " + oxygenRating);
		System.out.println("CO2 Rating: " + co2Rating);
		System.out.println("Product: " + product);
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
