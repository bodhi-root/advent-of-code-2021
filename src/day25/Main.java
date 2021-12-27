package day25;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class World {
		
		char [][] map;
		
		char [][] buffer;
		
		public World(char [][] map) {
			this.map = map;
			this.buffer = new char[map.length][map[0].length];
		}
		
		public static World loadFromFile(File file) throws IOException {
			List<String> lines = FileUtil.readLinesFromFile(file);
			char [][] map = new char[lines.size()][];
			for (int i=0; i<lines.size(); i++)
				map[i] = lines.get(i).toCharArray();
			
			return new World(map);
		}
		
		public void print(PrintStream out) {
			for (int i=0; i<map.length; i++)
				out.println(map[i]);
		}
		
		public int step() {
			int count = 0;
			
			//clear buffer:
			for (int i=0; i<buffer.length; i++)
				Arrays.fill(buffer[i], '?');
			
			//move '>' if right space is empty
			int toJ;
			for (int i=0; i<buffer.length; i++) {
				for (int j=0; j<buffer[i].length; j++) {
					if (map[i][j] == '>') {
						toJ = j + 1;
						if (toJ == map[i].length) 
							toJ = 0;
						
						if (map[i][toJ] == '.') {
							buffer[i][j] = '.';
							buffer[i][toJ] = '>';
							count++;
							continue;
						} 
					}
					
					if (buffer[i][j] == '?') {	//make sure we don't overwrite a newly-moved '>'
						buffer[i][j] = map[i][j];
					}
				}
			}
			
			//swap buffers:
			char [][] tmp = this.map;
			this.map = buffer;
			this.buffer = tmp;
			
			//clear buffer:
			for (int i=0; i<buffer.length; i++)
				Arrays.fill(buffer[i], '?');
			
			//move 'v' if bottom space is empty
			int toI;
			for (int i=0; i<buffer.length; i++) {
				for (int j=0; j<buffer[i].length; j++) {
					if (map[i][j] == 'v') {
						toI = i + 1;
						if (toI == buffer.length) 
							toI = 0;
						
						if (map[toI][j] == '.') {
							buffer[i][j] = '.';
							buffer[toI][j] = 'v';
							count++;
							continue;
						} 
					}
					
					if (buffer[i][j] == '?') {	//make sure we don't overwrite a newly-moved 'v'
						buffer[i][j] = map[i][j];
					}
				}
			}
			
			//swap buffers:
			tmp = this.map;
			this.map = buffer;
			this.buffer = tmp;
			
			return count;
		}
		
	}
	
	public static void solvePart1() throws Exception {
		//World world = World.loadFromFile(new File("files/day25/test.txt"));
		World world = World.loadFromFile(new File("files/day25/input.txt"));
		world.print(System.out);
		System.out.println();
		
		int steps = 0;
		while (world.step() > 0) {
			steps++;
			
			//System.out.println("After " + steps + " steps:");
			//world.print(System.out);
			//System.out.println();
			
			//if (steps > 10)
			//	break;
		}
		
		System.out.println("Part 1: " + (steps+1));
	}
	
	public static void main(String [] args) {
		try {
			solvePart1();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
