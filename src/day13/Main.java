package day13;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	static class Key {
		
		int x;
		int y;
		
		public Key(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Key))
				return false;
			
			Key that = (Key)o;
			return this.x == that.x && this.y == that.y;
		}
		
		public int hashCode() {
			return x ^ 7 + y;
		}
	}
	
	static class Paper {
		
		Map<Key, Boolean> map = new HashMap<>();
		int xMax = 0;
		int yMax = 0;
		
		public void set(int x, int y) {
			this.xMax = Math.max(xMax, x);
			this.yMax = Math.max(yMax, y);
			
			map.put(toKey(x,y), true);
		}
		public boolean isSet(int x, int y) {
			Boolean value = map.get(toKey(x, y));
			return (value == null) ? false : value.booleanValue();
		}
		
		public int getCount() {
			return map.size();	//note: all values are true
		}
		
		public void fold(String text) {
			if (!text.startsWith("fold along "))
				throw new IllegalArgumentException("Not a fold instruction");
			
			text = text.substring("fold along ".length());
			String [] parts = text.split("=");
			int value = Integer.parseInt(parts[1]);
			if (parts[0].equals("x"))
				foldX(value);
			else if (parts[0].equals("y"))
				foldY(value);
			else
				throw new IllegalArgumentException("Bad input: " + text);
		}
		
		public void foldX(int xFold) {
			List<Key> toRemove = new ArrayList<>();
			List<Key> toAdd = new ArrayList<>();
			
			int xMax = 0;
			
			for (Map.Entry<Key, Boolean> entry : map.entrySet()) {
				Key key = entry.getKey();
				if (key.x == xFold) {
					toRemove.add(key);
				}
				if (key.x > xFold) {
					int xNew = xFold - (key.x - xFold);
					toAdd.add(new Key(xNew, key.y));
					toRemove.add(key);
				} else {
					xMax = Math.max(xMax, key.x);
				}
			}
			
			for (Key key : toRemove)
				map.remove(key);
			for (Key key : toAdd)
				map.put(key, true);
			
			this.xMax = xMax;
		}
		
		public void foldY(int yFold) {
			List<Key> toRemove = new ArrayList<>();
			List<Key> toAdd = new ArrayList<>();
			
			int yMax = 0;
			
			for (Map.Entry<Key, Boolean> entry : map.entrySet()) {
				Key key = entry.getKey();
				if (key.y == yFold) {
					toRemove.add(key);
				}
				if (key.y > yFold) {
					int yNew = yFold - (key.y - yFold);
					toAdd.add(new Key(key.x, yNew));
					toRemove.add(key);
				} else {
					yMax = Math.max(yMax, key.y);
				}
			}
			
			for (Key key : toRemove)
				map.remove(key);
			for (Key key : toAdd)
				map.put(key, true);
			
			this.yMax = yMax;
		}
		
		public static Key toKey(int x, int y) {
			return new Key(x, y);
		}
		
		public void print(PrintStream out) {
			StringBuilder s = new StringBuilder();
			
			for (int y=0; y<=yMax; y++) {
				for (int x=0; x<=xMax; x++) {
					s.append(isSet(x,y) ? '#' : '.');
				}
				out.println(s.toString());
				s.setLength(0);
			}
		}
		
	}
	
	public static void solve() throws Exception {
		Paper paper = new Paper();
		List<String> actions = new ArrayList<>();
		
		BufferedReader in = new BufferedReader(new FileReader(new File("files/day13/input.txt")));
		try {
			String line;
			String [] parts;
			int x, y;
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					break;
				
				parts = line.split(",");
				x = Integer.parseInt(parts[0]);
				y = Integer.parseInt(parts[1]);
				paper.set(x, y);
			}
			
			while ((line = in.readLine()) != null) {
				line = line.trim();
				actions.add(line);
			}
			
		}
		finally {
			in.close();
		}
		
		//paper.print(System.out);
	
		for (String action : actions) {
			System.out.println();
			System.out.println(action);
			//System.out.println();
			paper.fold(action);
			//paper.print(System.out);
			System.out.println("Dots: " + paper.getCount());
		}
		
		System.out.println();
		paper.print(System.out);
	}
	
	public static void main(String [] args) {
		try {
			solve();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
