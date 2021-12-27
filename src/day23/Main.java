package day23;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class Location {
		
		final int i;
		final int j;
		
		public Location(int i, int j) {
			this.i = i;
			this.j = j;
		}
		
	}
	
	static class World {
		
		char [][] map;
		
		List<Location> hallwayLocs = new ArrayList<>();
		List<Location> roomLocs = new ArrayList<>();
		
		public World(char [][] map) {
			this.map = map;
		}
		
		public static World readFromFile(File file, boolean part2) throws IOException {
			List<String> lines = FileUtil.readLinesFromFile(file);
			
			if (part2) {
				lines.add(3, "  #D#C#B#A#  ");
				lines.add(4, "  #D#B#A#C#  ");
			}
			
			char [][] map = new char[lines.size()][];
			for (int i=0; i<lines.size(); i++)
				map[i] = lines.get(i).toCharArray();
			
			World world = new World(map);
			
			//I re-ordered these locations to put positions I think are good
			//moves toward the top.  I'm not sure this makes any difference
			//though since we sort moves by expected cost.
			
			//for (int j=1; j<=11; j++)
			//	world.hallwayLocs.add(new Location(1, j));
			world.hallwayLocs.add(new Location(1, 1));
			world.hallwayLocs.add(new Location(1, 11));
			world.hallwayLocs.add(new Location(1, 2));
			world.hallwayLocs.add(new Location(1, 10));
			world.hallwayLocs.add(new Location(1, 4));
			world.hallwayLocs.add(new Location(1, 8));
			world.hallwayLocs.add(new Location(1, 6));
			world.hallwayLocs.add(new Location(1, 3));
			world.hallwayLocs.add(new Location(1, 9));
			world.hallwayLocs.add(new Location(1, 5));
			world.hallwayLocs.add(new Location(1, 7));
			
			for (int i=2; i<=3; i++) {
				//world.roomLocs.add(new Location(i, 3));
				//world.roomLocs.add(new Location(i, 5));
				//world.roomLocs.add(new Location(i, 7));
				//world.roomLocs.add(new Location(i, 9));
				world.roomLocs.add(new Location(i, 9));
				world.roomLocs.add(new Location(i, 3));
				world.roomLocs.add(new Location(i, 7));
				world.roomLocs.add(new Location(i, 5));
			}
			
			if (part2) {
				for (int i=4; i<=5; i++) {
					//world.roomLocs.add(new Location(i, 3));
					//world.roomLocs.add(new Location(i, 5));
					//world.roomLocs.add(new Location(i, 7));
					//world.roomLocs.add(new Location(i, 9));
					world.roomLocs.add(new Location(i, 9));
					world.roomLocs.add(new Location(i, 3));
					world.roomLocs.add(new Location(i, 7));
					world.roomLocs.add(new Location(i, 5));
				}	
			}
				
			return world;
		}
		
		public void print(PrintStream out) {
			for (int i=0; i<map.length; i++)
				out.println(map[i]);
		}
		
		/**
		 * Creates a key we can use to uniquely identify the current state.  This
		 * is used in our search to make sure we don't return to the same location
		 * twice.  (Actually, I'm not sure that's possible with our improved logic
		 * that prevents us moving into a room unless we are moving into the our
		 * final location, but I kept the taboo logic in here anyway.)
		 * 
		 * We only include locations where characters can be in our key.
		 */
		public String toTabooKey() {
			StringBuilder s = new StringBuilder();
			
			for (Location loc : hallwayLocs) 
				s.append(map[loc.i][loc.j]);
			
			for (Location loc : roomLocs)
				s.append(map[loc.i][loc.j]);
			
			return s.toString();
		}
		
		public boolean isSolution() {
			for (int i=2; i<map.length-1; i++) {
				if (map[i][3] != 'A' ||
					map[i][5] != 'B' ||
					map[i][7] != 'C' ||
					map[i][9] != 'D')
					return false;
			}
			
			return true;
		}
		
		/**
		 * Return the minimum cost needed to move from the current state to
		 * our destination.  This puts all the creatures in the right rooms
		 * and assumes that they just move into the top-most room.  The actual
		 * cost of getting to the destination will be at least this high
		 * (and probably a bit higher).
		 */
		public int getMinRemainingCost() {
			int cost = 0;
			
			//estimate cost of creatures in hallway moving to bottom row of room
			for (Location from : hallwayLocs) {
				if (isOccupied(from.i, from.j)) {
					char type = map[from.i][from.j];
					int jTo = getDestinationColumnFor(type);
					
					int steps = Math.abs(jTo - from.j) + 1;	//assume moving into top row
					int stepCost = getStepCost(type);
					cost += steps * stepCost;
				}
			}
			
			//estimate cost for creatures in wrong room
			for (Location from : roomLocs) {
				if (isOccupied(from.i, from.j)) {
					char type = map[from.i][from.j];
					int jDest = getDestinationColumnFor(type);
					if (jDest != from.j) {
						int steps = Math.abs(jDest - from.j) + 1 + (from.i - 1);
						int stepCost = getStepCost(type);
						cost += steps * stepCost;
					}
				}
			}
			
			return cost;
		}
		
		public void move(int iFrom, int jFrom, int iTo, int jTo) {
			map[iTo][jTo] = map[iFrom][jFrom];
			map[iFrom][jFrom] = '.';
		}
		
		public boolean isEmpty(int i, int j) {
			return map[i][j] == '.';
		}
		public boolean isOccupied(int i, int j) {
			return map[i][j] != '.';
		}
		
		public List<Move> listMoves(boolean sortByCost) {
			List<Move> moves = new ArrayList<>();
			
			//move from room to hallway:
			for (Location from : roomLocs) {
				if (isOccupied(from.i, from.j))
					addMovesFromRoom(moves, from.i, from.j);
			}
			
			//move from hallway to room:
			for (Location from : hallwayLocs) {
				if (isOccupied(from.i, from.j))
					addMovesFromHallway(moves, from.i, from.j);
			}
			
			if (sortByCost) {
				List<MoveAndCost> list = new ArrayList<>();
				for (Move move : moves) {
					move.apply(this);
					int cost = move.cost + this.getMinRemainingCost();
					move.undo(this);
					
					list.add(new MoveAndCost(move, cost));
				}
				list.sort(new Comparator<MoveAndCost>() {
					public int compare(MoveAndCost o1, MoveAndCost o2) {
						return o1.cost - o2.cost;
					}
				});
				
				List<Move> sortedMoves = new ArrayList<>(moves.size());
				for (MoveAndCost o : list)
					sortedMoves.add(o.move);
				return sortedMoves;
			}
			
			return moves;
		}
		
		public List<Move> listMoves() {
			return listMoves(false);
		}
		
		static class MoveAndCost {
			
			Move move;
			int cost;
			
			public MoveAndCost(Move move, int cost) {
				this.move = move;
				this.cost = cost;
			}
			
		}
		
		protected void addMovesFromRoom(List<Move> moves, int iFrom, int jFrom) {
			
			//don't move out of solution:
			char type = map[iFrom][jFrom];
			int jDest = getDestinationColumnFor(type);
			
			if (jDest == jFrom) {
				boolean solution = true;
				
				for (int i=iFrom+1; i<map.length-1; i++) {
					if (map[i][jDest] != type) {
						solution = false;
						break;
					}
				}
				
				if (solution)
					return;
			}
			
			//try moves to hallway:
			for (Location to : hallwayLocs) {
				if (isEmpty(to.i, to.j) && !isBlockingRoom(to.i, to.j)) {
				//if (isEmpty(to.i, to.j)) {
					tryAddMove(moves, iFrom, jFrom, to.i, to.j);
				}
			}
			
			//try moves to room (w/o stopping in hallway):
			Location to = getBestDestinationFor(type);
			if (to != null)
				tryAddMove(moves, iFrom, jFrom, to.i, to.j);
		}
		
		protected boolean isBlockingRoom(int i, int j) {
			return (i == 1) &&
				   (j == 3 || j == 5 || j == 7 || j == 9);
		}
		
		protected void addMovesFromHallway(List<Move> moves, int iFrom, int jFrom) {
			
			char type = map[iFrom][jFrom];
			Location to = getBestDestinationFor(type);
			if (to != null)
				tryAddMove(moves, iFrom, jFrom, to.i, to.j);
		}
		
		protected Location getBestDestinationFor(char type) {
			int jDest = getDestinationColumnFor(type);
			
			//don't move into room if wrong creature is in there.
			//need to clear room first
			for (int i=2; i<map.length-1; i++) {
				if (isOccupied(i, jDest) && map[i][jDest] != type)
					return null;
			}
			
			//move into the lowest empty room
			int iTo = 0;
			for (int i=2; i<map.length-1; i++) {
				if (map[i][jDest] == '.')
					iTo = i;
				else
					break;
			}
			
			return new Location(iTo, jDest);
		}
		
		/**
		 * Make sure the path is clear, calculate its cost, and add it to the list 
		 * (if possible).
		 */
		protected Move tryAddMove(List<Move> moves, int iFrom, int jFrom, int iTo, int jTo) {
			//System.out.println("Trying: (" + iFrom + "," + jFrom + ")-(" + iTo + "," + jTo + ")");
			//two types of move: "up and then over" or "over and then down"
			//int di = iTo > iFrom ? 1 : -1;
			int dj = jTo > jFrom ? 1 : -1;
			
			int i = iFrom;
			int j = jFrom;
			int steps = 0;
			
			//if starting in hallway: move over and then down
			if (iFrom == 1) {
				while (j != jTo) {
					j += dj;		//move sideways
					steps++;
					if (isOccupied(i, j))
						return null;
				}
				while (i != iTo) {
					i++;			//move down
					steps++;
					if (isOccupied(i, j))
						return null;
				}
			} 
			
			//up and then over (and then maybe down again)
			else {
				while (i != 1) {	//move to hallway row
					i--;
					steps++;
					if (isOccupied(i, j))
						return null;
				}
				while (j != jTo) {	//move left or right
					j += dj;
					steps++;
					if (isOccupied(i, j))
						return null;
				}
				while (i != iTo) {	//move down (if needed)
					i++;
					steps++;
					if (isOccupied(i, j))
						return null;
				}
			}
			
			int stepCost = getStepCost(map[iFrom][jFrom]);
			
			Move move = new Move(iFrom, jFrom, iTo, jTo, steps*stepCost);
			moves.add(move);
			return move;
		}
		
		protected int getStepCost(char type) {
			switch(type) {
			case 'A': return 1;
			case 'B': return 10;
			case 'C': return 100;
			case 'D': return 1000;
			}
			throw new IllegalArgumentException("Unknown creature type: " + type);
		}
		protected int getDestinationColumnFor(char type) {
			switch(type) {
			case 'A': return 3;
			case 'B': return 5;
			case 'C': return 7;
			case 'D': return 9;
			}
			throw new IllegalArgumentException("Unknown creature type: " + type);
		}
		
	}
	
	static class Move {
		
		int iFrom;
		int jFrom;
		int iTo;
		int jTo;
		
		int cost;
		
		public Move(int iFrom, int jFrom, int iTo, int jTo, int cost) {
			this.iFrom = iFrom;
			this.jFrom = jFrom;
			this.iTo = iTo;
			this.jTo = jTo;
			this.cost = cost;
		}
		
		public void apply(World world) {
			world.move(iFrom, jFrom, iTo, jTo);
		}
		
		public void undo(World world) {
			world.move(iTo, jTo, iFrom, jFrom);
		}
		
		public String toString() {
			return "(" + iFrom + "," + jFrom + ")-(" + iTo + "," + jTo + ") cost=" + cost;
		}
		
		public boolean isUndoOf(Move move) {
			return this.iFrom == move.iTo &&
				   this.jFrom == move.jTo &&
				   this.iTo == move.iFrom &&
				   this.jTo == move.jFrom;
		}
		
	}
	
	static class MoveSequence {
		
		List<Move> moves = new ArrayList<>();
		int totalCost = 0;
		
		public void add(Move move) {
			moves.add(move);
			totalCost += move.cost;
		}
		public Move removeLast() {
			Move move = moves.remove(moves.size()-1);
			totalCost -= move.cost;
			return move;
		}
		public Move getLastMove() {
			return moves.get(moves.size()-1);
		}
		public int size() {
			return moves.size();
		}
		
		public int getTotalCost() {
			return totalCost;
		}
		
		public MoveSequence copy() {
			MoveSequence copy = new MoveSequence();
			copy.moves.addAll(this.moves);
			copy.totalCost = this.totalCost;
			return copy;
		}
		
		public void print(PrintStream out) {
			System.out.println("Move Sequence:");
			for(Move move : moves)
				out.println(move.toString());
		}
		
	}
	
	static class TabooList {
		
		List<String> keys = new ArrayList<>();
		
		public void add(String key) {
			keys.add(key);
		}
		public void removeLast() {
			keys.remove(keys.size()-1);
		}
		
		public boolean isTaboo(String key) {
			for (int i=keys.size()-1; i>=0; i--) {
				if (keys.get(i).equals(key))
					return true;
			}
			return false;
		}
		
	}
	
	static void extend(MoveSequence seq, World world, SolutionVisitor visitor, TabooList tabooList) throws InterruptedException {
		List<Move> nextMoves = world.listMoves(true);
		
		/*
		if (nextMoves.isEmpty()) {
			System.out.println("Stuck:");
			world.print(System.out);
			System.out.println();
		}
		*/
		
		for (Move nextMove : nextMoves) {
			
			//don't get stuck in infinite loops
			if (seq.size() > 1 && nextMove.isUndoOf(seq.getLastMove()))
				continue;
			
			seq.add(nextMove);
			nextMove.apply(world);
			
			//if (visitor.best == null || seq.totalCost < visitor.best.totalCost) { 	//early stopping
			if (visitor.best == null || seq.totalCost + world.getMinRemainingCost() < visitor.best.totalCost) {
				
				//world.print(System.out);
				//System.out.println();
				//Thread.sleep(500);
				
				//don't revisit previous states:
				String tabooKey = world.toTabooKey();
				if (!tabooList.isTaboo(tabooKey)) {
					tabooList.add(tabooKey);

					//world.print(System.out);
					//System.out.println();
					
					if (world.isSolution()) {
						visitor.visit(seq);
						//world.print(System.out);
					}
					else
						extend(seq, world, visitor, tabooList);
					
					tabooList.removeLast();
				}
					
			}
			
			seq.removeLast();
			nextMove.undo(world);
		}
	}
	
	static class SolutionVisitor {
	
		MoveSequence best = null;
		
		public void visit(MoveSequence moves) {
			System.out.println("Solution found! " + moves.getTotalCost());
			if (best == null || moves.getTotalCost() < best.getTotalCost()) {
				best = moves.copy();
				//best.print(System.out);
				//System.out.println();
			}
		}
		
	}
	
	public static void test() throws Exception {
		World world = World.readFromFile(new File("files/day23/test2.txt"), false);
		world.print(System.out);
		
		List<Move> moves = world.listMoves();
		for (Move move : moves)
			System.out.println(move);
		
		moves.get(0).apply(world);
		world.print(System.out);
		
		moves = world.listMoves();
		for (Move move : moves)
			System.out.println(move);
	}
	
	public static void solvePart1() throws Exception {
		World world = World.readFromFile(new File("files/day23/test.txt"), false);
		world.print(System.out);
		System.out.println();
		
		SolutionVisitor visitor = new SolutionVisitor();
		TabooList tabooList = new TabooList();
		tabooList.add(world.toTabooKey());
		extend(new MoveSequence(), world, visitor, tabooList);
		if (visitor.best == null)
			System.out.println("No solution found... :(");
		else
			System.out.println("Best solution: " + visitor.best.getTotalCost());
	}
	
	public static void solvePart2() throws Exception {
		World world = World.readFromFile(new File("files/day23/input.txt"), true);
		world.print(System.out);
		System.out.println();
		
		SolutionVisitor visitor = new SolutionVisitor();
		TabooList tabooList = new TabooList();
		tabooList.add(world.toTabooKey());
		extend(new MoveSequence(), world, visitor, tabooList);
		if (visitor.best == null)
			System.out.println("No solution found... :(");
		else
			System.out.println("Best solution: " + visitor.best.getTotalCost());
	}
	
	public static void main(String [] args) {
		try {
			test();
			solvePart1();
			
			long start = System.currentTimeMillis();
			solvePart2();
			System.out.println(System.currentTimeMillis() - start);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
