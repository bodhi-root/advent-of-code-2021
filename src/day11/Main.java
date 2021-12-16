package day11;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class Grid {
	
		int [][] values;
		int flashes = 0;
		
		boolean [][] flashBuffer;
		
		public Grid(int [][] values) {
			this.values = values;
			this.flashBuffer = new boolean[values.length][values[0].length];
		}
		
		public int step() {
			//First, the energy level of each octopus increases by 1.
			for (int i=0; i<values.length; i++) {
				for (int j=0; j<values[i].length; j++) {
					values[i][j]++;
					flashBuffer[i][j] = false;	//also reset this
				}
			}
			
			//Then, any octopus with an energy level greater than 9 flashes. 
			//This increases the energy level of all adjacent octopuses by 1, 
			//including octopuses that are diagonally adjacent. If this causes 
			//an octopus to have an energy level greater than 9, it also flashes.
			//This process continues as long as new octopuses keep having their 
			//energy level increased beyond 9. (An octopus can only flash at most once per step.)
			
			int newFlashes = 0;
			do {
				newFlashes = 0;
				
				for (int i=0; i<values.length; i++) {
					for (int j=0; j<values[i].length; j++) {
						
						if (!flashBuffer[i][j] && values[i][j] > 9) {
							flashBuffer[i][j] = true;
							newFlashes++;
							
							for (int di=-1; di<=1; di++) {
								for (int dj=-1; dj<=1; dj++) {
									if (i+di >= 0 && i+di < values[i].length &&
										j+dj >= 0 && j+dj < values[i].length && 
										!(di == 0 && dj == 0)) {
										
										values[i+di][j+dj]++;
									}
								}
							}
						}
						
					}
				}
				
				this.flashes += newFlashes;
				
			} while (newFlashes > 0);
			
			//Finally, any octopus that flashed during this step has its energy level 
			//set to 0, as it used all of its energy to flash.
			int stepFlashes = 0;
			for (int i=0; i<values.length; i++) {
				for (int j=0; j<values[i].length; j++) {
					if (flashBuffer[i][j]) {
						values[i][j] = 0;
						stepFlashes++;
					}
				}
			}
			
			return stepFlashes;
		}
	
		public void print(PrintStream out) {
			StringBuilder s = new StringBuilder();
			for (int i=0; i<values.length; i++) {
				for (int j=0; j<values[i].length; j++) {
					s.append(values[i][j]);
				}
				out.println(s.toString());
				s.setLength(0);
			}
		}
		
		public static Grid loadFromFile(File file) throws IOException {
			List<String> lines = FileUtil.readLinesFromFile(file);
			int [][] values = new int[lines.size()][lines.size()];
			for (int i=0; i<values.length; i++) {
				String line = lines.get(i);
				for (int j=0; j<line.length(); j++)
					values[i][j] = Integer.parseInt(line.substring(j,j+1));
			}
			
			return new Grid(values);
		}
		
	}

	
	public static void solvePart1() throws Exception {
		Grid grid = Grid.loadFromFile(new File("files/day11/input.txt"));
		//grid.print(System.out);
		
		for (int i=0; i<100; i++) {
			grid.step();
			
			//System.out.println();
			//System.out.println("After " + (i+1) + " steps");
			//grid.print(System.out);
		}
		
		System.out.println(grid.flashes);
	}
	
	public static void solvePart2() throws Exception {
		Grid grid = Grid.loadFromFile(new File("files/day11/input.txt"));
		
		int size = grid.values.length * grid.values[0].length;
		int steps = 0;
		do {
			steps++;
		} while (grid.step() != size);
		
		System.out.println("Steps: " + steps);
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
