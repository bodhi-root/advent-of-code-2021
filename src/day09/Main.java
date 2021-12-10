package day09;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class Grid {
		
		int [][] values;
		
		public Grid(int [][] values) {
			this.values = values;
		}
		
		public static Grid loadFromFile(File file) throws IOException {
			List<String> lines = FileUtil.readLinesFromFile(file);
			
			int [][] values = new int[lines.size()][];
			for (int i=0; i<values.length; i++) {
				String line = lines.get(i);
				values[i] = new int[line.length()];
				for (int j=0; j<values[i].length; j++) {
					values[i][j] = Integer.parseInt(line.substring(j,j+1));
				}
			}
			
			return new Grid(values);
		}
		
		public int getRiskLevelSum() {
			int sum = 0;
			for (int i=0; i<values.length; i++) {
				for (int j=0; j<values[i].length; j++) {
					if (isLowPoint(i, j))
						sum += values[i][j] + 1;
				}
			}
			return sum;
		}
		
		public boolean isLowPoint(int i, int j) {
			int value = values[i][j];
			
			for (int di=-1; di<=1; di++) {
				if (di == 0)
					continue;
				
				int iTmp = i + di;
				if (iTmp >= 0 && iTmp < values.length) {
					if (values[iTmp][j] <= value)
						return false;
				}
			}
			
			for (int dj=-1; dj<=1; dj++) {
				if (dj == 0)
					continue;
				
				int jTmp = j + dj;
				if (jTmp >= 0 && jTmp < values[i].length) {
					if (values[i][jTmp] <= value)
						return false;
				}
			}
			
			return true;
		}
		
		public List<Integer> getBasinSizes() {
			List<Integer> sizes = new ArrayList<>();
			
			boolean [][] basin = new boolean[values.length][values[0].length];
			
			for (int i=0; i<values.length; i++) {
				for (int j=0; j<values[i].length; j++) {
					if (isLowPoint(i, j)) {
						fill(basin, false);
						expandBasin(basin, i, j);
						sizes.add(count(basin, true));
					}
				}
			}
			
			return sizes;
		}
		
		protected void expandBasin(boolean [][] basin, int i, int j) {
			basin[i][j] = true;
			
			for (int di=-1; di<=1; di++) {
				if (di == 0)
					continue;
				
				int iTmp = i + di;
				if (iTmp >= 0 && iTmp < values.length) {
					if (!basin[iTmp][j] && values[iTmp][j] < 9)
						expandBasin(basin, iTmp, j);
				}
			}
			
			for (int dj=-1; dj<=1; dj++) {
				if (dj == 0)
					continue;
				
				int jTmp = j + dj;
				if (jTmp >= 0 && jTmp < values[i].length) {
					if (!basin[i][jTmp] && values[i][jTmp] < 9)
						expandBasin(basin, i, jTmp);
				}
			}
		}
		
		public static void fill(boolean [][] array, boolean value) {
			for (int i=0; i<array.length; i++)
				Arrays.fill(array[i], value);
		}
		
		public static int count(boolean [][] array, boolean value) {
			int count = 0;
			for (int i=0; i<array.length; i++) {
				for (int j=0; j<array[i].length; j++) {
					if (array[i][j] == value)
						count++;
				}
			}
			return count;
		}

		
	}
	
	public static void solvePart1() throws Exception {
		Grid grid = Grid.loadFromFile(new File("files/day09/input.txt"));
		System.out.println(grid.getRiskLevelSum());
	}
	
	public static void solvePart2() throws Exception {
		Grid grid = Grid.loadFromFile(new File("files/day09/input.txt"));
		List<Integer> sizes = grid.getBasinSizes();
		Collections.sort(sizes);
		
		System.out.println("Sizes:");
		for (int size : sizes)
			System.out.println(size);
		
		int product = 1;
		for (int i=0; i<3; i++)
			product *= sizes.get(sizes.size()-i-1);
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
