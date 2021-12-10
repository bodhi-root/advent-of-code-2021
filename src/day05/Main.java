package day05;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.FileUtil;

public class Main {
	
	static class Counter {
		int x;
		int y;
		
		public Counter(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		int value = 0;
	}
	
	static class Grid {
		
		Map<String, Counter> values = new HashMap<>();
		
		public void increment(int x, int y) {
			String key = x + ":" + y;
			Counter counter = values.get(key);
			if (counter == null) {
				counter = new Counter(x, y);
				values.put(key, counter);
			}
			counter.value++;
		}
		
		public int countIntersections() {
			int count = 0;
			for (Counter counter : values.values()) {
				if (counter.value >= 2)
					count++;
			}
			return count;
		}
		
		public void mark(Line line) {
			//System.out.println(line);
			
			int dx, dy;
			if (line.x1 == line.x2)
				dx = 0;
			else
				dx = line.x2 > line.x1 ? 1 : -1;
				
			if (line.y1 == line.y2)
				dy = 0;
			else
				dy = line.y2 > line.y1 ? 1 : -1;
			
			int x = line.x1;
			int y = line.y1;
			
			int xSize = Math.abs(line.x2 - line.x1) + 1;
			int ySize = Math.abs(line.y2 - line.y1) + 1;
			int size = Math.max(xSize, ySize);
			
			if (((line.y1 + ((size-1) * dy)) != line.y2) ||
				((line.x1 + ((size-1) * dx)) != line.x2)) {
				throw new RuntimeException("Unsupported line: " + line.toString());
			}
			
			for (int i=0; i<size; i++) {
				increment(x, y);
				x += dx;
				y += dy;
			}
			
		}
		
		protected void print(PrintStream out) {
			int xMax = 0;
			int yMax = 0;
			
			for (Counter counter : values.values()) {
				xMax = Math.max(xMax, counter.x);
				yMax = Math.max(yMax, counter.y);
			}
			
			StringBuilder s = new StringBuilder();
			for (int y=0; y<=yMax; y++) {
				for (int x=0; x<=xMax; x++) {
					String key = x + ":" + y;
					Counter counter = values.get(key);
					s.append(counter == null ? "." : String.valueOf(counter.value));
				}
				out.println(s.toString());
				s.setLength(0);
			}
		}
		
	}
	
	static class Line {
		
		int x1;
		int y1;
		int x2;
		int y2;
		
		public static Line parseLine(String line) {
			Line obj = new Line();
			
			//911,808 -> 324,221
			int index = line.indexOf('-');
			String left = line.substring(0,index).trim();
			String right = line.substring(index + 2).trim();
			
			String [] parts = left.split(",");
			obj.x1 = Integer.parseInt(parts[0].trim());
			obj.y1 = Integer.parseInt(parts[1].trim());
			
			parts = right.split(",");
			obj.x2 = Integer.parseInt(parts[0].trim());
			obj.y2 = Integer.parseInt(parts[1].trim());
			
			return obj;
		}
		
		public boolean isHorizontal() {
			return y1 == y2;
		}
		public boolean isVertical() {
			return x1 == x2;
		}
		
		public String toString() {
			return "(" + x1 + ":" + y1 + "),(" + x2 + ":" + y2 + ")";
		}
		
	}
	
	public static void solvePart1() throws Exception {
		Grid grid = new Grid();
		
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day05/test.txt"));
		//List<String> lines = FileUtil.readLinesFromFile(new File("files/day05/input.txt"));
		for (String line : lines) {
			Line lineObj = Line.parseLine(line);
			if (lineObj.isHorizontal() || lineObj.isVertical()) {
				grid.mark(lineObj);
			}
		}
		
		grid.print(System.out);
		System.out.println();
		
		System.out.println(grid.countIntersections());
	}
	
	public static void solvePart2() throws Exception {
		Grid grid = new Grid();
		
		//List<String> lines = FileUtil.readLinesFromFile(new File("files/day05/test.txt"));
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day05/input.txt"));
		for (String line : lines) {
			Line lineObj = Line.parseLine(line);
			grid.mark(lineObj);
		}
		
		//grid.print(System.out);
		//System.out.println();
		
		System.out.println(grid.countIntersections());
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
