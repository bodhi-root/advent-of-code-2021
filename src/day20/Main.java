package day20;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import common.FileUtil;

public class Main {
	
	static class Grid {
		
		Set<String> trueSet = new HashSet<>();
		boolean outOfBoundsValue = false;
		
		int iMin = 0;
		int iMax = 0;
		int jMin = 0;
		int jMax = 0;
		
		public void set(int i, int j, boolean value) {
			if (value)
				trueSet.add(toKey(i, j));
			else
				trueSet.remove(toKey(i, j));
			
			iMin = Math.min(iMin, i);
			jMin = Math.min(jMin, j);
			iMax = Math.max(iMax, i);
			jMax = Math.max(jMax, j);
		}
		
		public int getTrueCount() {
			return trueSet.size();
		}
		
		public boolean get(int i, int j) {
			if (i < iMin || j < jMin || i > iMax || j > jMax)
				return outOfBoundsValue;
			
			return trueSet.contains(toKey(i, j));
		}
		
		public String toKey(int i, int j) {
			return i + ":" + j;
		}
		
		public Grid step(String decoder) {
			StringBuilder s = new StringBuilder();
			
			Grid newGrid = new Grid();
			for (int i=iMin-2; i<=iMax+2; i++) {
				for (int j=jMin-2; j<=jMax+2; j++) {
					
					for (int di=-1; di<=1; di++) {
						for (int dj=-1; dj<=1; dj++) {
							s.append(get(i+di, j+dj) ? '1' : '0');
						}
					}
					
					int index = Integer.parseInt(s.toString(), 2);
					if (decoder.charAt(index) == '#')
						newGrid.set(i, j, true);
					
					s.setLength(0);
				}
			}
			
			//out of bounds logic (since we can't assume decoder[0] = '.':
			//if out of bounds is all false, we use value from index 0, otherwise last value (all 1's)
			int outOfBoundsIndex = this.outOfBoundsValue ? decoder.length() - 1 : 0;
			newGrid.outOfBoundsValue = decoder.charAt(outOfBoundsIndex) == '#';
			
			return newGrid;
		}
		
		public void print(PrintStream out) {
			StringBuilder s = new StringBuilder();
			for (int i=iMin; i<=iMax; i++) {
				for (int j=jMin; j<=jMax; j++) {
					s.append(get(i, j) ? '#' : '.');
				}
				
				out.println(s.toString());
				s.setLength(0);
			}
		}
		
	}
	
	static class Input {
		
		String decoder;
		Grid grid;
		
		public static Input readFromFile(File file) throws IOException {
			Input input = new Input();
			input.grid = new Grid();
			
			List<String> lines = FileUtil.readLinesFromFile(file);
			input.decoder = lines.get(0);
			
			List<String> gridLines = lines.subList(2, lines.size());
			for (int i=0; i<gridLines.size(); i++) {
				String line = gridLines.get(i);
				for (int j=0; j<line.length(); j++) {
					if (line.charAt(j) == '#')
						input.grid.set(i, j, true);
				}
			}
		
			return input;
		}
		
	}
	
	public static void solvePart1() throws Exception {
		Input input = Input.readFromFile(new File("files/day20/input.txt"));
		
		Grid grid = input.grid;
		grid.print(System.out);
		System.out.println();
		
		System.out.println("True Count: " + grid.getTrueCount());
		System.out.println();
		
		for (int i=0; i<2; i++) {
			System.out.println("After " + (i+1) + " steps");
			grid = grid.step(input.decoder);
			//grid.print(System.out);
			System.out.println("True Count: " + grid.getTrueCount());
			System.out.println();
		}
	}
	
	public static void solvePart2() throws Exception {
		Input input = Input.readFromFile(new File("files/day20/input.txt"));
		
		Grid grid = input.grid;
		grid.print(System.out);
		System.out.println();
		
		System.out.println("True Count: " + grid.getTrueCount());
		System.out.println();
		
		for (int i=0; i<50; i++) {
			System.out.println("After " + (i+1) + " steps");
			grid = grid.step(input.decoder);
			//grid.print(System.out);
			System.out.println("True Count: " + grid.getTrueCount());
			System.out.println();
		}
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
