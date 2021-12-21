package day18;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static abstract class Value {
		
		Pair parent = null;
		
		abstract long magnitude();
		
		abstract Value simplify();
		
		public Pair add(Value x) {
			return new Pair(this, x);
		}
		
		public static Value parse(String text) {
			return new ValueParser(text).read();
		}
		
		public int getDepth() {
			if (parent == null)
				return 1;
			else
				return 1 + parent.getDepth();
		}
		
		public Value getRoot() {
			Value root = this;
			while (root.parent != null)
				root = root.parent;
			return root;
		}
		
	}
	
	static class Literal extends Value {
		
		long value;
		
		public Literal(long value) {
			this.value = value;
		}
		
		public Value simplify() {
			if (value >= 10)
				return split();
			else
				return this;
		}
		
		public Pair split() {
			double x = value / 2.0;
			return new Pair(
				new Literal((long)Math.floor(x)),
				new Literal((long)Math.ceil(x))
			);
		}
		
		public long magnitude() {
			return value;
		}
		
		public String toString() {
			return String.valueOf(value);
		}
		
	}
	
	static class Pair extends Value {
		
		Value left;
		Value right;
		
		public Pair(Value left, Value right) {
			this.left = left;
			this.right = right;
			
			this.left.parent = this;
			this.right.parent = this;
		}
		
		public void setLeft(Value left) {
			this.left = left;
			this.left.parent = this;
		}
		public void setRight(Value right) {
			this.right = right;
			this.right.parent = this;
		}
		public void replaceChild(Value child, Value newChild) {
			if (this.left == child)
				setLeft(newChild);
			else if (this.right == child)
				setRight(newChild);
			else
				throw new IllegalArgumentException("Node is not a child of this node");
		}
		
		public Value simplify() {
			boolean keepGoing = false;
			do {
			
				if (tryExplode())
					keepGoing = true;
				else if (trySplit())
					keepGoing = true;
				else
					keepGoing = false;
				
			} while (keepGoing);
			
			return this;
		}
		
		public List<Literal> getChildLiteralList() {
			List<Literal> list = new ArrayList<>();
			extendChildLiteralList(list, this);
			return list;
		}
		protected static void extendChildLiteralList(List<Literal> list, Pair pair) {
			if (pair.left instanceof Literal)
				list.add((Literal)pair.left);
			else
				extendChildLiteralList(list, (Pair)pair.left);
			
			if (pair.right instanceof Literal)
				list.add((Literal)pair.right);
			else
				extendChildLiteralList(list, (Pair)pair.right);
		}
		
		public void distributeValues() {
			List<Literal> literals = ((Pair)getRoot()).getChildLiteralList();
			
			Literal left = (Literal)this.left;
			Literal right = (Literal)this.right;
			
			int index = literals.indexOf(left);
			if (index >= 1) {
				Literal firstLeft = literals.get(index-1);
				Value newChild = new Literal(firstLeft.value + left.value);
				firstLeft.parent.replaceChild(firstLeft, newChild);
			}
			
			index++;	//index of right child
			//index = literals.indexOf(right);
			if (index < literals.size()-1) {
				Literal firstRight = literals.get(index+1);
				Value newChild = new Literal(firstRight.value + right.value);
				firstRight.parent.replaceChild(firstRight, newChild);
			}
			
		}
		
		public boolean tryExplode() {
			int depth = this.getDepth();
			
			if (this.left instanceof Pair) {
				Pair leftPair = (Pair)this.left;
				if (depth == 4) {
				
					leftPair.distributeValues();
					this.setLeft(new Literal(0));
					return true;
					
				} else if (depth < 4) {
					if (leftPair.tryExplode())
						return true;
				}
			}
			
			if (this.right instanceof Pair) {
				Pair rightPair = (Pair)this.right;
				if (depth == 4) {
				
					rightPair.distributeValues();
					this.setRight(new Literal(0));
					return true;
					
				} else if (depth < 4) {
					if (rightPair.tryExplode())
						return true;
				}
			}
			
			return false;
		}
		
		public boolean trySplit() {
			if (this.left instanceof Literal) {
				Literal x = (Literal)this.left;
				if (x.value >= 10) {
					Pair xNew = x.split();
					setLeft(xNew);
					return true;
				}
			}
			
			if (this.left instanceof Pair) {
				if (((Pair)this.left).trySplit())
					return true;
			}
			
			if (this.right instanceof Literal) {
				Literal x = (Literal)this.right;
				if (x.value >= 10) {
					Pair xNew = x.split();
					setRight(xNew);
					return true;
				}
			}
			
			if (this.right instanceof Pair) {
				if (((Pair)this.right).trySplit())
					return true;
			}
			
			return false;
		}
		
		public long magnitude() {
			return 3 * left.magnitude() + 2 * right.magnitude();
		}
		
		public String toString() {
			return "[" + left.toString() + "," + right.toString() + "]";
		}
		
	}
	
	static class ValueParser {
		
		char [] chars;
		int iNext;
		
		public ValueParser(String text) {
			this.chars = text.toCharArray();
			this.iNext = 0;
		}
		
		public Value read() {
			if (Character.isDigit(chars[iNext]))
				return readLiteral();
			else if (chars[iNext] == '[')
				return readPair();
			else
				throw new IllegalStateException("Unexpected character: " + chars[iNext]);
		}
		
		public Literal readLiteral() {
			StringBuilder s = new StringBuilder();
			while (iNext < chars.length && Character.isDigit(chars[iNext]))
				s.append(chars[iNext++]);
			long value = Long.parseLong(s.toString());
			return new Literal(value);
		}
		
		public Pair readPair() {
			//skip bracket
			if (chars[iNext] != '[')
				throw new IllegalStateException("Unexpected character: " + chars[iNext]);
			iNext++;	
			
			Value left = read();
			
			//skip comma
			if (chars[iNext] != ',')
				throw new IllegalStateException("Unexpected character: " + chars[iNext]);
			iNext++;
			
			Value right = read();
			
			if (chars[iNext] != ']')
				throw new IllegalStateException("Unexpected character: " + chars[iNext]);
			iNext++;
			
			return new Pair(left, right);
		}
		
	}
	
	public static void add(String x, String y) {
		Value vx = Value.parse(x);
		Value vy = Value.parse(y);
		Value sum = vx.add(vy);
		System.out.println(x + " + " + y);
		System.out.println("Sum:        " + sum.toString());
		Value simplified = sum.simplify();
		System.out.println("Simplified: " + simplified.toString());
		System.out.println();
	}
	
	public static void simplify(String text) {
		Value value = Value.parse(text);
		System.out.println("Value:      " + value.toString());
		System.out.println("Simplified: " + value.simplify());
		System.out.println();
	}

	public static void magnitude(String text) {
		Value value = Value.parse(text);
		System.out.println("Value: " + value);
		System.out.println("Magnitude: " + value.magnitude());
		System.out.println();
	}
	
	public static void runTests() throws Exception {
		simplify("9");
		simplify("10");
		simplify("11");
		
		simplify("[[[[[9,8],1],2],3],4]");	// [[[[0,9],2],3],4]
		simplify("[7,[6,[5,[4,[3,2]]]]]");	// [7,[6,[5,[7,0]]]]
		simplify("[[6,[5,[4,[3,2]]]],1]");	// [[6,[5,[7,0]]],3].
		simplify("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]");	// [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] (the pair [3,2] is unaffected because the pair [7,3] is further to the left; [3,2] would explode on the next action).
		simplify("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]");		// [[3,[2,[8,0]]],[9,[5,[7,0]]]].
		
		add("[1,2]", "[[3,4],5]"); //[[1,2],[[3,4],5]].
		
		add("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]");	//[[[[0,7],4],[[7,8],[6,0]]],[8,1]]
		
		magnitude("[[1,2],[[3,4],5]]");	// becomes 143.
		magnitude("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"); // becomes 1384.
		magnitude("[[[[1,1],[2,2]],[3,3]],[4,4]]"); // becomes 445.
		magnitude("[[[[3,0],[5,3]],[4,4]],[5,5]]"); // becomes 791.
		magnitude("[[[[5,0],[7,4]],[5,5]],[6,6]]"); // becomes 1137.
		magnitude("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"); // becomes 3488.
	}
	
	public static void solvePart1() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day18/input.txt"));
		Value value = Value.parse(lines.get(0));
		
		for (int i=1; i<lines.size(); i++) {
			System.out.println("  " + value);
			
			Value nextValue = Value.parse(lines.get(i));
			System.out.println("+ " + nextValue.toString());
			
			value = value.add(nextValue);
			value = value.simplify();
			System.out.println("= " + value.toString());
			System.out.println();
		}
		System.out.println();
		
		System.out.println("Part 1:");
		System.out.println("Solution: " + value.toString());
		System.out.println("Magnitude: " + value.magnitude());
	}
	
	public static void solvePart2() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day18/input.txt"));
		
		//FIXME: cannot re-use values because add() modifies().simplify() modifies them.
		//need to do deep copy before add().  then it's ok to have simplify modify it.
		//I don't feel like implementing this though, so we just re-parse the values
		//each time.
		
		//List<Value> values = new ArrayList<>(lines.size());
		//for (String line : lines)
		//	values.add(Value.parse(line));
		
		long largestMag = 0;
		String bestLine = null;
		String bestResult = null;
		
		for (int i=0; i<lines.size(); i++) {
			for (int j=0; j<lines.size(); j++) {
				if (i != j)  {
					Value left = Value.parse(lines.get(i));
					Value right = Value.parse(lines.get(j));
					String line = left.toString() + " + " + right.toString();
					Value result = left.add(right).simplify();
					long mag = result.magnitude();
					if (mag > largestMag) {
						largestMag = mag;
						bestLine = line;
						bestResult = result.toString();
					}
				}
			}
		}
		
		System.out.println("Part 2: " + largestMag);
		System.out.println(bestLine);
		System.out.println(bestResult);
	}
	
	public static void main(String [] args) {
		try {
			runTests();
			solvePart1();
			solvePart2();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
