package day15;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.FileUtil;

public class Main {
	
	static class Grid {
		
		int [][] values;
		
		public Grid(int [][] values) {
			this.values = values;
		}
		
		public static Grid loadFromFile(File file) throws IOException {
			List<String> lines = FileUtil.readLinesFromFile(file);
			int [][] values = new int [lines.size()][lines.get(0).length()];
			for (int i=0; i<values.length; i++) {
				String line = lines.get(i);
				for (int j=0; j<values[i].length; j++) {
					values[i][j] = Integer.parseInt(line.substring(j,j+1));
				}
			}
			
			return new Grid(values);
		}
		
		public int getValue(Location loc) {
			return values[loc.i][loc.j];
		}
		
		public List<Location> getAdjacentLocations(Location loc) {
			List<Location> nextLocs = new ArrayList<>(4);
			
			if (loc.j+1 < values[loc.i].length)
				nextLocs.add(new Location(loc.i, loc.j+1));
			if (loc.i+1 < values.length)
				nextLocs.add(new Location(loc.i+1, loc.j));
			if (loc.j-1 >= 0)
				nextLocs.add(new Location(loc.i, loc.j-1));
			if (loc.i-1 >= 0)
				nextLocs.add(new Location(loc.i-1, loc.j));
			
			return nextLocs;
		}
	
		public boolean isBottomRight(Location loc) {
			return (loc.i == values.length - 1 && 
				    loc.j == values[0].length - 1);
		}
		public Location getBottomRightLocation() {
			return new Location(values.length-1, values[0].length-1);
		}
		
		public void expandGridForPart2() {
			int height = values.length;
			int width = values[0].length;
			
			int [][] newValues = new int[height * 5][width * 5];
			
			//copy to top left:
			for (int i=0; i<height; i++) {
				for (int j=0; j<width; j++)
					newValues[i][j] = values[i][j];
			}
			
			//build left set of tiles:
			for (int iTile=1; iTile<5; iTile++) {
				int iTop = iTile * height;
				for (int di=0; di<height; di++) {
					for (int dj=0; dj<width; dj++) {
						newValues[iTop+di][dj] = newValues[iTop+di-height][dj]+1;
						if (newValues[iTop+di][dj] == 10)
							newValues[iTop+di][dj] = 1;
					}
				}
			}
			
			//build others incrementally from set on left
			for (int iTile=0; iTile<5; iTile++) {
				int iTop = iTile * height;
				for (int jTile=1; jTile<5; jTile++) {
					int jLeft = jTile * width;
					for (int di=0; di<height; di++) {
						for (int dj=0; dj<width; dj++) {
							newValues[iTop+di][jLeft+dj] = newValues[iTop+di][jLeft+dj-width]+1;
							if (newValues[iTop+di][jLeft+dj] == 10)
								newValues[iTop+di][jLeft+dj] = 1;
						}
					}
				}
			}
			
			this.values = newValues;
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
			
	}
	
	static class Location {
		
		int i;
		int j;
		
		public Location(int i, int j) {
			this.i = i;
			this.j = j;
		}
		
		public int hashCode() {
			return i ^ 7 + j;
		}
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Location))
				return false;
			
			Location that = (Location)o;
			return this.i == that.i && this.j == that.j;
		}
		
	}
	
	static class Path {
		
		Grid grid;
		List<Location> path = new ArrayList<>();
		long cost = 0;
		
		public Path(Grid grid, Location start) {
			this.grid = grid;
			path.add(start);	//does not count toward cost
		}
		
		private Path(Grid grid) {	//used for copying
			this.grid = grid;
		}
		public Path copy() {
			Path copy = new Path(grid);
			copy.path.addAll(this.path);
			copy.cost = this.cost;
			return copy;
		}
		
		public int size() {
			return path.size();
		}
		public void add(Location loc) {
			path.add(loc);
			cost += grid.values[loc.i][loc.j];
		}
		public Location removeLast() {
			Location loc = path.remove(path.size()-1);
			cost -= grid.values[loc.i][loc.j];
			return loc;
		}
		public Location getFirst() {
			return path.get(0);
		}
		public Location getLast() {
			return path.get(path.size()-1);
		}
		
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (Location loc : path) {
				if (s.length() > 0)
					s.append("-");
				s.append('(').append(loc.i).append(',').append(loc.j).append(')');
			}
			return s.toString();
		}
	}
	
	static class Context {
		
		Grid grid;
		Path [][] bestPaths;
		
		public Context(Grid grid) {
			this.grid = grid;
			this.bestPaths = new Path[grid.values.length][grid.values[0].length];
		}
			
		public void setBestPathTo(Location loc, Path path) {
			bestPaths[loc.i][loc.j] = path; 
		}
		public Path getBestPathTo(Location loc) {
			return bestPaths[loc.i][loc.j];
		}
		
		/**
		 * Builds out all possible paths from (0, 0) to the bottom right corner.
		 * This is done doing a breadth-first search and keeping track of the
		 * best path to each location on the grid.  An optimal solution will
		 * never cross itself (if so you could cut out the loop and make it more 
		 * cost-efficient).  We keep a list of all paths that need to be grown.
		 * This is pruned with each step so that we drop any paths that arrive
		 * at a location in a worse way than already found.  This guarantees
		 * we will never have more than 'width x height' paths in our list of
		 * growing lists at any time.  In fact, our list starts out with just
		 * 1 path in it, grows to a large size (near 'width x height') and
		 * then starts shrinking back down to 1.  It pauses a few times along
		 * the way.  I think this is just the garbage collector running.
		 * Otherwise, this runs extremely fast, even for the 500x500 grid, solving
		 * this in about 20 seconds.
		 * 
		 * NOTE: I tried several depth-first solutions and these all failed.
		 * Depth-first searches explore long, sub-optimal paths that are pruned
		 * much earlier in the breadth-first search.  The breadth-first search
		 * works well because each path we are growing is the same number of steps
		 * long (and thus about the same cost).  Since shorter paths in general
		 * have lower costs than longer ones, we should be finding the optimal
		 * path to each point in our grid very quickly and efficiently.
		 */
		public void scoreAllPaths() {
			Collection<Path> paths = new ArrayList<>();
			paths.add(new Path(grid, new Location(0, 0)));
			
			int counter = 0;
			while (!paths.isEmpty()) {
				paths = grow(paths, this);
				counter++;
				if (counter % 10 == 0)
					System.out.println("Count: " + counter + " (paths=" + paths.size() + ")");
			}
		}
		
		/**
		 * Helper function for 'scoreAllPaths()'.  This grows the current list of
		 * new paths, storing new bests along the way.  It returns a new set of
		 * paths that need to be grown, dropping any duplicates that arrive at a
		 * known location in a worse way than our previous best.
		 */
		public Collection<Path> grow(Collection<Path> paths, Context ctx) {
			
			Map<Location, Path> nextPaths = new HashMap<>();
			
			for (Path path : paths) {
				List<Location> nextLocs = path.grid.getAdjacentLocations(path.getLast());
				for (Location nextLoc : nextLocs) {
					Path bestPath = ctx.getBestPathTo(nextLoc);
					if (bestPath == null || (path.cost + path.grid.getValue(nextLoc) < bestPath.cost)) {
						Path newPath = path.copy();
						newPath.add(nextLoc);
						ctx.setBestPathTo(nextLoc, newPath);
						
						if (!path.grid.isBottomRight(nextLoc))
							nextPaths.put(nextLoc, newPath);
					}
					
				}
			}
			
			return nextPaths.values();
		}
		
	}
	
	public static void solvePart1() throws Exception {
		Grid grid = Grid.loadFromFile(new File("files/day15/input.txt"));
		Context ctx = new Context(grid);
		ctx.scoreAllPaths();
		Path best = ctx.getBestPathTo(grid.getBottomRightLocation());
		System.out.println(best.toString());
		System.out.println("Best: " + best.cost);
	}
	
	public static void solvePart2() throws Exception {
		Grid grid = Grid.loadFromFile(new File("files/day15/input.txt"));
		grid.expandGridForPart2();
		grid.print(System.out);
		Context ctx = new Context(grid);
		ctx.scoreAllPaths();
		Path best = ctx.getBestPathTo(grid.getBottomRightLocation());
		//System.out.println(best.toString());
		System.out.println("Best: " + best.cost);
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
