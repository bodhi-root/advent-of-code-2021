package day23;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import common.FileUtil;

/**
 * When solving part 1 I hard-coded a lot of things based on the assumption
 * that the map would always be the same size.  This was not the case in 
 * part 2.  I re-wrote all of the logic into this class so it could be
 * extended to part 2.  I thought it would be cool to preserve my original
 * code too though.  It's easier to read and understand (I think).
 * 
 * NOTE: There is one logic error here. The rule that amphipods will not
 * stop outside of anyone's room unless they proceed directly into that
 * room.  The absence of this rule messed me up a bit on part 2 as I was
 * finding solutions that used less energy than the actual solution.
 */
public class Part1Main {
	
	static class World {
		
		char [][] map;
		
		static int [][] HALLWAY_LOCATIONS = new int [][] {
			{1, 1},
			{1, 2},
			{1, 3},
			{1, 4},
			{1, 5},
			{1, 6},
			{1, 7},
			{1, 8},
			{1, 9},
			{1, 10},
			{1, 11}
		};
		
		static int [][] ROOM_LOCATIONS = new int [][] {
			{2, 3},
			{3, 3},
			{2, 5},
			{3, 5},
			{2, 7},
			{3, 7},
			{2, 9},
			{3, 9}
		};
		
		public World(char [][] map) {
			this.map = map;
		}
		
		public static World readFromFile(File file) throws IOException {
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
		public String toTabooKey() {
			StringBuilder s = new StringBuilder();
			int i, j;
			for (int iHall=0; iHall<HALLWAY_LOCATIONS.length; iHall++) {
				i = HALLWAY_LOCATIONS[iHall][0];
				j = HALLWAY_LOCATIONS[iHall][1];
				s.append(map[i][j]);
			}
			for (int iRoom=0; iRoom<ROOM_LOCATIONS.length; iRoom++) {
				i = ROOM_LOCATIONS[iRoom][0];
				j = ROOM_LOCATIONS[iRoom][1];
				s.append(map[i][j]);
			}
			return s.toString();
		}
		
		public boolean isSolution() {
			return map[2][3] == 'A' && map[3][3] == 'A' &&
				   map[2][5] == 'B' && map[3][5] == 'B' &&
				   map[2][7] == 'C' && map[3][7] == 'C' &&
				   map[2][9] == 'D' && map[3][9] == 'D';
		}
		
		public int getMinRemainingCost() {
			int cost = 0;
			
			//estimate cost of creatures in hallway moving to bottom row of room
			int iFrom, jFrom;
			for (int iHall=0; iHall<HALLWAY_LOCATIONS.length; iHall++) {
				iFrom = HALLWAY_LOCATIONS[iHall][0];
				jFrom = HALLWAY_LOCATIONS[iHall][1];
				if (isOccupied(iFrom, jFrom)) {
					char type = map[iFrom][jFrom];
					int jTo = getDestinationColumnFor(type);
					
					int steps = Math.abs(jTo - jFrom) + 1;	//assume moving into top row
					int stepCost = getStepCost(type);
					cost += steps * stepCost;
				}
			}
			
			//estimate cost for creatures in wrong room
			for (int iRoom=0; iRoom<ROOM_LOCATIONS.length; iRoom++) {
				iFrom = ROOM_LOCATIONS[iRoom][0];
				jFrom = ROOM_LOCATIONS[iRoom][1];
				if (isOccupied(iFrom, jFrom)) {
					char type = map[iFrom][jFrom];
					int jDest = getDestinationColumnFor(type);
					if (jDest != jFrom) {
						int steps = Math.abs(jDest - jFrom) + 2;	//assuming moving from and to top row
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
			int iFrom, jFrom;
			for (int iRoom=0; iRoom<ROOM_LOCATIONS.length; iRoom++) {
				iFrom = ROOM_LOCATIONS[iRoom][0];
				jFrom = ROOM_LOCATIONS[iRoom][1];
				if (isOccupied(iFrom, jFrom))
					addMovesToHallway(moves, iFrom, jFrom);
			}
			
			//move from hallway to room:
			for (int iHall=0; iHall<HALLWAY_LOCATIONS.length; iHall++) {
				iFrom = HALLWAY_LOCATIONS[iHall][0];
				jFrom = HALLWAY_LOCATIONS[iHall][1];
				if (isOccupied(iFrom, jFrom))
					addMovesToRoom(moves, iFrom, jFrom);
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
		
		protected void addMovesToHallway(List<Move> moves, int iFrom, int jFrom) {
			
			//don't move out of solution:
			char type = map[iFrom][jFrom];
			if (type == 'A') {
				if (iFrom == 3 && jFrom == 3)
					return;
				if (iFrom == 2 && jFrom == 3 && map[3][3] == 'A')
					return;
			} else if (type == 'B') {
				if (iFrom == 3 && jFrom == 5)
					return;
				if (iFrom == 2 && jFrom == 5 && map[3][5] == 'B')
					return;
			} else if (type == 'C') {
				if (iFrom == 3 && jFrom == 7)
					return;
				if (iFrom == 2 && jFrom == 7 && map[3][7] == 'C')
					return;
			} else if (type == 'D') {
				if (iFrom == 3 && jFrom == 9)
					return;
				if (iFrom == 2 && jFrom == 9 && map[3][9] == 'D')
					return;
			}
			
			int iTo, jTo;
			for (int iHall=0; iHall<HALLWAY_LOCATIONS.length; iHall++) {
				iTo = HALLWAY_LOCATIONS[iHall][0];
				jTo = HALLWAY_LOCATIONS[iHall][1];
				
				if (isEmpty(iTo, jTo)) {
					tryAddMove(moves, iFrom, jFrom, iTo, jTo);
				}
			}
		}
		
		protected void addMovesToRoom(List<Move> moves, int iFrom, int jFrom) {
			
			char type = map[iFrom][jFrom];
			if (type == 'A') {
				if (map[3][3] == 'A')	//don't move to top room position if bottom position is empty
					tryAddMove(moves, iFrom, jFrom, 2, 3);
				tryAddMove(moves, iFrom, jFrom, 3, 3);
			} else if (type == 'B') {
				if (map[3][5] == 'B')
					tryAddMove(moves, iFrom, jFrom, 2, 5);
				tryAddMove(moves, iFrom, jFrom, 3, 5);
			} else if (type == 'C') {
				if (map[3][7] == 'C')
					tryAddMove(moves, iFrom, jFrom, 2, 7);
				tryAddMove(moves, iFrom, jFrom, 3, 7);
			} else if (type == 'D') {
				if (map[3][9] == 'D')
					tryAddMove(moves, iFrom, jFrom, 2, 9);
				tryAddMove(moves, iFrom, jFrom, 3, 9);
			} else {
				throw new IllegalStateException("Unknown creature: " + type);
			}
			
		}
		
		/**
		 * Make sure the path is clear, calculate its cost, and add it to the list 
		 * (if possible).
		 */
		protected Move tryAddMove(List<Move> moves, int iFrom, int jFrom, int iTo, int jTo) {
			//System.out.println("Trying: (" + iFrom + "," + jFrom + ")-(" + iTo + "," + jTo + ")");
			//two types of move: "up and then over" or "over and then down"
			int di = iTo > iFrom ? 1 : -1;
			int dj = jTo > jFrom ? 1 : -1;
			
			int i = iFrom;
			int j = jFrom;
			int steps = 0;
			
			//over and then down
			if (di > 0) {
				while (j != jTo) {
					j += dj;
					steps++;
					if (isOccupied(i, j))
						return null;
				}
				while (i != iTo) {
					i += di;
					steps++;
					if (isOccupied(i, j))
						return null;
				}
			} 
			
			//up and then over
			else {
				while (i != iTo) {
					i += di;
					steps++;
					if (isOccupied(i, j))
						return null;
				}
				while (j != jTo) {
					j += dj;
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
		
	}
	
	public static void test() throws Exception {
		World world = World.readFromFile(new File("files/day23/input.txt"));
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
	
	static void extend(MoveSequence seq, World world, SolutionVisitor visitor, TabooList tabooList) {
		List<Move> nextMoves = world.listMoves(true);
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
				
				//don't revisit previous states:
				String tabooKey = world.toTabooKey();
				if (!tabooList.isTaboo(tabooKey)) {
					tabooList.add(tabooKey);

					if (world.isSolution())
						visitor.visit(seq);
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
			if (best == null || moves.getTotalCost() < best.getTotalCost())
				best = moves.copy();
		}
		
	}
	
	public static void solvePart1() throws Exception {
		World world = World.readFromFile(new File("files/day23/test.txt"));
		world.print(System.out);
		
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
			solvePart1();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
