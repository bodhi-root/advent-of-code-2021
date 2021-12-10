package day04;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static class BingoBoard {
		
		int [][] numbers;
		boolean [][] marked;
		
		public BingoBoard(int [][] numbers) {
			this.numbers = numbers;
			this.marked = new boolean[5][5];
		}
		
		public int markNumber(int number) {
			//mark number:
			for (int i=0; i<numbers.length; i++) {
				for (int j=0; j<numbers[i].length; j++) {
					if (numbers[i][j] == number)
						marked[i][j] = true;
				}
			}
			
			//check rows for winner:
			for (int i=0; i<numbers.length; i++) {
				boolean winner = true;
				for (int j=0; j<numbers[i].length; j++) {
					if (!marked[i][j]) {
						winner = false;
						break;
					}
				}
				
				if (winner) {
					return getUnmarkedSum() * number;
				}
			}
			
			//check columns for winner:
			for (int j=0; j<numbers[0].length; j++) {
				boolean winner = true;
				for (int i=0; i<numbers.length; i++) {
					if (!marked[i][j]) {
						winner = false;
						break;
					}
				}
				
				if (winner) {
					return getUnmarkedSum() * number;
				}
			}
			
			//no winner:
			return -1;
		}
		
		public void setMarked(int i, int j, boolean value) {
			this.marked[i][j] = value;
		}
		public boolean isMarked(int i, int j) {
			return marked[i][j];
		}
		
		protected int getUnmarkedSum() {
			
			int sum = 0;
			
			for (int i=0; i<numbers.length; i++) {
				for (int j=0; j<numbers[i].length; j++) {
					if (!marked[i][j])
						sum += numbers[i][j];
				}
			}
			
			return sum;
		}
		
	}
	
	static class Input {
		
		List<BingoBoard> boards = new ArrayList<>();
		List<Integer> numbers = new ArrayList<>();
		
		public static Input readFromFile(File file) throws IOException {
			Input input = new Input();
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			try {
				String line = in.readLine();
				String [] parts = line.split(",");
				for (String part : parts) {
					input.numbers.add(Integer.parseInt(part.trim()));
				}
				
				in.readLine();	//blank
				
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty()) {
						int [][] numbers = new int[5][5];
						for (int i=0; i<5; i++) {
							parts = line.trim().split("\\s+");
							for (int j=0; j<5; j++)
								numbers[i][j] = Integer.parseInt(parts[j].trim());
							line = in.readLine();	//ends reading blank line
						}
						input.boards.add(new BingoBoard(numbers));
					}
				}
			}
			finally {
				in.close();
			}
			
			return input;
		}
		
	}
	
	public static void solvePart1() throws Exception {
		Input input = Input.readFromFile(new File("files/day04/input.txt"));
		//Input input = Input.readFromFile(new File("files/day04/test.txt"));
		
		main:
		for (Integer number : input.numbers) {
			System.out.println(number);
			for (BingoBoard board: input.boards) {
				int score = board.markNumber(number);
				if (score >= 0) {
					System.out.println("Winning Board Score: " + score);
					break main;
				}
			}
		}
	}
	
	public static void solvePart2() throws Exception {
		Input input = Input.readFromFile(new File("files/day04/input.txt"));
		//Input input = Input.readFromFile(new File("files/day04/test.txt"));
		
		List<BingoBoard> winners = new ArrayList<>();
		
		for (Integer number : input.numbers) {
			System.out.println(number);
			
			for (BingoBoard board: input.boards) {
				int score = board.markNumber(number);
				if (score >= 0) {
					winners.add(board);
					System.out.println("Winning Board Score: " + score);
				}
			}
			
			input.boards.removeAll(winners);
			winners.clear();
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
