package day22;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.FileUtil;

public class Main {
	
	static class XYZ {
		
		int x;
		int y;
		int z;
		
		public XYZ(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public static XYZ parse(String line) {
			String [] parts = line.split(",");
			return new XYZ(
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2])
				);
		}
		
		public String toString() {
			return "(" + x + "," + y + "," + z + ")";
		}
		
		public boolean equals(XYZ o) {
			if (o == null || !(o instanceof XYZ))
				return false;
			
			XYZ that = (XYZ)o;
			return this.x == that.x && this.y == that.y && this.z == that.z;
		}
		
		public int hashCode() {
			return x ^ 7 + y ^ 8 + z;
		}
		
	}
	
	static class Cube {
		
		XYZ from;
		XYZ to;
		
		public Cube(XYZ from, XYZ to) {
			this.from = from;
			this.to = to;
		}
		
		public boolean contains(int x, int y, int z) {
			return x >= from.x && x <= to.x &&
				   y >= from.y && y <= to.y &&
				   z >= from.z && z <= to.z;
		}
		public boolean containsEntirely(Cube other) {
			return other.from.x >= this.from.x &&
				   other.to.x <= this.to.x &&
				   other.from.y >= this.from.y &&
				   other.to.y <= this.to.y &&
				   other.from.z >= this.from.z &&
				   other.to.z <= this.to.z;
		}
		
		public boolean overlapsWith(Cube other) {
			
			if (!(other.from.x <= this.to.x && other.to.x >= this.from.x))
				return false;
			if (!(other.from.y <= this.to.y && other.to.y >= this.from.y))
				return false;
			if (!(other.from.z <= this.to.z && other.to.z >= this.from.z))
				return false;
			
			return true;
		}
		
		public long getVolume() {
			return ((long)(to.x - from.x + 1)) * 
				   ((long)(to.y - from.y + 1)) * 
				   ((long)(to.z - from.z + 1));  
		}
		
		public Cube intersection(Cube other) {
			return new Cube(
					new XYZ(
						Math.max(this.from.x, other.from.x),
						Math.max(this.from.y, other.from.y),
						Math.max(this.from.z, other.from.z)
					),
					new XYZ(
						Math.min(this.to.x, other.to.x),
						Math.min(this.to.y, other.to.y),
						Math.min(this.to.z, other.to.z)
					)
				);
		}
		
		public String toString() {
			return from.toString() + " - " + to.toString();
		}
		
	}
	
	/**
	 * Represents a line of input (and contains a useful 'cubeContains()' method
	 * that helps solve part 1 more efficiently).
	 */
	static class InputLine {
		
		Cube cube;
		boolean value;
		
		public InputLine(XYZ from, XYZ to, boolean value) {
			this.cube = new Cube(from, to);
			this.value = value;
		}
		
		public boolean cubeContains(int x, int y, int z) {
			return cube.contains(x, y, z);
		}
		
		public boolean overlapsWith(InputLine other) {
			return this.cube.overlapsWith(other.cube);
		}

		public static InputLine parse(String line) {
			//on x=-22..26,y=-27..20,z=-29..19
			int index = line.indexOf(' ');
			String action = line.substring(0, index);
			String [] parts = line.substring(index+1).split(",");

			int x1 = 0, x2 = 0;
			int y1 = 0, y2 = 0;
			int z1 = 0, z2 = 0;

			for (String part : parts) {
				char firstChar = part.charAt(0);
				String [] leftRight = part.substring(2).split("\\.\\.");
				int v1 = Integer.parseInt(leftRight[0]);
				int v2 = Integer.parseInt(leftRight[1]);
				if (firstChar == 'x') {
					x1 = Math.min(v1, v2);
					x2 = Math.max(v1, v2);
				} else if (firstChar == 'y') {
					y1 = Math.min(v1, v2);
					y2 = Math.max(v1, v2);
				} else if (firstChar == 'z') {
					z1 = Math.min(v1, v2);
					z2 = Math.max(v1, v2);
				}
			}

			boolean value = action.equals("on");
			return new InputLine(new XYZ(x1, y1, z1), new XYZ(x2, y2, z2), value);
		}
		
		public String toString() {
			return (value ? "on " : "off ") + 
					"x=" + cube.from.x + ".." + cube.to.x +
					",y=" + cube.from.y + ".." + cube.to.y +
					",z=" + cube.from.z + ".." + cube.to.z;
		}
		
	}
	
	static class Grid {
		
		Set<XYZ> trueLocs = new HashSet<>(1000);
		
		public boolean set(XYZ loc, boolean value) {
			if (value)
				return trueLocs.add(loc);
			else
				return trueLocs.remove(loc);
		}
		
		public void set(XYZ from, XYZ to, boolean value) {
			System.out.println(from + " - " + to + " : " + value);
			for (int x=from.x; x<=to.x; x++) {
				for (int y=from.y; y<=to.y; y++) {
					for (int z=from.z; z<=to.z; z++) {
						set(new XYZ(x, y, z), value);
					}
				}
			}
		}
		public void set(Cube cube, boolean value) {
			set(cube.from, cube.to, value);
		}
		
		public int getTrueCount(XYZ from, XYZ to) {
			int count = 0;
			for (int x=from.x; x<=to.x; x++) {
				for (int y=from.y; y<=to.y; y++) {
					for (int z=from.z; z<=to.z; z++) {
						if (get(new XYZ(x, y, z)))
							count++;
					}
				}
			}
			return count;
		}
		
		public boolean get(XYZ loc) {
			return trueLocs.contains(loc);
		}
		
	}
	
	/**
	 * Represents a cube with a value of either +1 to count all the volume
	 * as "on" or a -1 if we want to subtract the value of this region from
	 * the total.
	 */
	static class CubeValue {
		
		Cube cube;
		int sign;
		
		public CubeValue(Cube cube, int sign) {
			this.cube = cube;
			this.sign = sign;
		}
		
		public long getTotalValue() {
			return sign * cube.getVolume();
		}
		
	}
	
	/**
	 * Does not work.  Takes too long.  Instead of applying all steps and then
	 * seeing which locations are on, we have to look at each location and
	 * see if it is on after applying all steps.
	 */
	public static void solvePart1BruteForce() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day22/test.txt"));
		Grid grid = new Grid();
		for (String line : lines) {
			InputLine inputLine = InputLine.parse(line);
			grid.set(inputLine.cube, inputLine.value);
		}
		System.out.println(grid.getTrueCount(new XYZ(-50, -50, -50), new XYZ(50, 50, 50)));
	}
	
	/**
	 * Solves part 1 by examining each location from (-50, -50, -50) to (50, 50, 50)
	 * and seeing if it is "on" after applying all the rules.  This is able to run
	 * very quickly since we only examine 100^3=1,000,000 points.
	 */
	public static void solvePart1() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day22/input.txt"));
		
		List<InputLine> inputLines = new ArrayList<>(lines.size());
		for (String line : lines)
			inputLines.add(InputLine.parse(line));
		
		XYZ from = new XYZ(-50, -50, -50);
		XYZ to = new XYZ(50, 50, 50);
		
		int countTrue = 0;
		
		boolean value;
		
		for (int x=from.x; x<=to.x; x++) {
			for (int y=from.y; y<=to.y; y++) {
				for (int z=from.z; z<=to.z; z++) {
					
					value = false;
					for (InputLine line : inputLines) {
						if (line.value != value && line.cubeContains(x, y, z))
							value = line.value;
					}
					if (value)
						countTrue++;
				}
			}
		}
		
		System.out.println("Part 1: " + countTrue);
	}
	
	/**
	 * Inspired by: https://pastebin.com/DbAVS7gz
	 */
	public static void solvePart2() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day22/input.txt"));
		
		List<CubeValue> cubeValues = new ArrayList<>();
		
		for (String line : lines) {
			InputLine inputLine = InputLine.parse(line);
			CubeValue newCube = new CubeValue(inputLine.cube, inputLine.value ? 1 : -1);
			
			List<CubeValue> toAdd = new ArrayList<>();
			
			for (CubeValue existing : cubeValues) {
				if (newCube.cube.overlapsWith(existing.cube)) {
					Cube intersect = newCube.cube.intersection(existing.cube);
					
					int newSign = -existing.sign;
					
					//existing    newCube    result   comment
					// 1           1         -1       both cubes are positive, need to subtract common section to avoid double-counting
					// 1          -1         -1       existing cube is "on", turn part of it "off"
					//-1           1          1       existing cube is "off", turn part of it "on"
					//-1          -1          1       both cubes are negative, need to add common section to avoid double-counting
					
					CubeValue negative = new CubeValue(intersect, newSign);
					toAdd.add(negative);
				}
			}
			
			cubeValues.addAll(toAdd);
			
			//only add "on" regions:
			if (newCube.sign == 1)
				cubeValues.add(newCube);
		}
		
		long total = 0;
		for (CubeValue cube : cubeValues)
			total += cube.getTotalValue();
		
		System.out.println("Part 2: " + total);
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
