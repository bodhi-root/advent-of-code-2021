package day21;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static class Game {
		
		int [] playerLocs;
		int [] playerScores;
		int t = 0;
		
		boolean player1Turn = true;
		
		public Game(int player1, int player2) {
			this.playerLocs = new int[] {player1, player2};
			this.playerScores = new int[2];
		}
		
		public boolean step(int roll) {
			t++;
			int activePlayer = player1Turn ? 0 : 1;
			
			playerLocs[activePlayer] += (roll % 10);
			if (playerLocs[activePlayer] > 10)
				playerLocs[activePlayer] -= 10;
			playerScores[activePlayer] += playerLocs[activePlayer];
			
			player1Turn = !player1Turn;
			return playerScores[activePlayer] < 1000;
		}
		public List<GameStateAndCount> step(DiracDie die) {
			List<GameStateAndCount> list = new ArrayList<>();
			for (int roll=die.minValue; roll<=die.maxValue; roll++) {
				Game copy = copy();
				copy.step(roll);
				list.add(new GameStateAndCount(copy, die.getCount(roll)));
			}
			return list;
		}
		
		public void printState(PrintStream out) {
			out.println(t + ": locs=(" + playerLocs[0] + ", " + playerLocs[1] + 
					"), scores=(" + playerScores[0] + ", " + playerScores[1] + ")");
		}
		
		public Game copy() {
			Game copy = new Game(playerLocs[0], playerLocs[1]);
			copy.playerScores[0] = this.playerScores[0];
			copy.playerScores[1] = this.playerScores[1];
			copy.t = this.t;
			copy.player1Turn = this.player1Turn;
			return copy;
		}
		
	}
	
	static class Die {
		
		int nextValue;
		
		public Die(int nextValue) {
			this.nextValue = nextValue;
		}
		public Die() {
			this(1);
		}
		
		public int roll() {
			return nextValue++;
		}
		
		public Die copy() {
			return new Die(nextValue);
		}
		
	}
	
	static class GameStateAndCount {
		
		Game game;
		long count;
		
		public GameStateAndCount(Game game, long count) {
			this.game = game;
			this.count = count;
		}
		
	}
	
	/**
	 * Enumeration of 3 rolls of a die with values 1-3.
	 * (Calculated by printing out all possible calculations
	 * using a simple loop).
	 */
	static class DiracDie {
		
		int minValue = 3;
		int maxValue = 9;
		int [] counts = new int [] {1, 3, 6, 7, 6, 3, 1};
		
		public int getCount(int value) {
			return counts[value - minValue];
		}
		
		public static void printCombos() {
			for (int i=1; i<=3; i++) {
				for (int j=1; j<=3; j++) {
					for (int k=1; k<=3; k++) {
						System.out.println(i+j+k);
					}
				}
			}
		}
		
	}
	
	public static void solvePart1() throws Exception {
		//Game game = new Game(4, 8);
		Game game = new Game(7, 8);
		Die die = new Die();
		game.printState(System.out);
		while (game.step(die.roll() + die.roll() + die.roll())) {
			game.printState(System.out);
		}
		game.printState(System.out);
		
		int losingScore = game.playerScores[game.playerScores[0] >= 1000 ? 1 : 0];
		long result = game.t * 3 * losingScore;
		System.out.println("Part 1: " + result);
	}
	
	public static void solvePart2() throws Exception {
		//Game game = new Game(4, 8);
		Game game = new Game(7, 8);
		
		GameStateAndCount stateAndCount = new GameStateAndCount(game, 1);
		long [] outcomes = new long[2];
		extend(stateAndCount, new DiracDie(), outcomes);
		System.out.println("Player 1 Wins: " + outcomes[0]);
		System.out.println("Player 2 Wins: " + outcomes[1]);
	}
	
	public static void extend(GameStateAndCount stateAndCount, DiracDie die, long [] outcomes) {
		List<GameStateAndCount> nextStates = stateAndCount.game.step(die);
		for (GameStateAndCount nextState : nextStates) {
			nextState.count *= stateAndCount.count;
			if (nextState.game.playerScores[0] >= 21)
				outcomes[0] += nextState.count;
			else if (nextState.game.playerScores[1] >= 21)
				outcomes[1] += nextState.count;
			else
				extend(nextState, die, outcomes);
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
