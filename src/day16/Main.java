package day16;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import common.FileUtil;

public class Main {
	
	static class Packet {
		
		int version;
		int type;
		
		long value = -1;
		List<Packet> childPackets = new ArrayList<>();
		
		public Packet(int version, int type) {
			this.version = version;
			this.type = type;
		}
		
		public int getVersionNumberSum() {
			int sum = version;
			for (Packet child : childPackets)
				sum += child.getVersionNumberSum();
			return sum;
		}
		
		public long evaluate() {
			//literal:
			if (type == 4)
				return value;
			
			//sum
			if (type == 0) {
				long sum = 0;
				for (Packet child : childPackets)
					sum += child.evaluate();
				return sum;
			}
			
			//product:
			if (type == 1) {
				long product = 1;
				for (Packet child : childPackets)
					product *= child.evaluate();
				return product;
			}
			
			//min:
			if (type == 2) {
				long min = childPackets.get(0).evaluate();
				for (int i=1; i<childPackets.size(); i++)
					min = Math.min(min, childPackets.get(i).evaluate());
				return min;
			}
			
			//max:
			if (type == 3) {
				long max = childPackets.get(0).evaluate();
				for (int i=1; i<childPackets.size(); i++)
					max = Math.max(max, childPackets.get(i).evaluate());
				return max;
			}
			
			//greater than:
			if (type == 5) {
				long x1 = childPackets.get(0).evaluate();
				long x2 = childPackets.get(1).evaluate();
				return x1 > x2 ? 1 : 0;
			}
			
			//less than:
			if (type == 6) {				
				long x1 = childPackets.get(0).evaluate();
				long x2 = childPackets.get(1).evaluate();
				return x1 < x2 ? 1 : 0;
			}
			
			//equal:
			if (type == 7) {
				long x1 = childPackets.get(0).evaluate();
				long x2 = childPackets.get(1).evaluate();
				return x1 == x2 ? 1 : 0;
			}
			
			throw new IllegalStateException("Invalid type: " + type);
		}
				
		public void print(PrintStream out) {
			print(out, "");
		}
		
		public void print(PrintStream out, String indent) {
			out.println(indent + "Version: " + version);
			out.println(indent + "Type: " + type);
			out.println(indent + "Value: " + value);
			if (!childPackets.isEmpty()) {
				out.println(indent + "Children:");
				out.println();
				for (Packet child : childPackets) {
					child.print(out, indent + "  ");
					out.println();
				}
			}
		}
		
	}
	
	static class PacketReader {
		
		Reader in;
		char [] cbuffer = new char[15];
		StringBuilder sbuffer = new StringBuilder();
		
		public PacketReader(Reader in) {
			this.in = in;
		}
		
		public void close() throws IOException {
			in.close();
		}
		
		public Packet read() throws IOException {
			
			//read header:
			int charsRead = in.read(cbuffer, 0, 6);
			if (charsRead < 0)
				return null;	//EOF
			
			if (charsRead != 6)
				throw new IOException("Invalid packet header: too short");
			
			int version = Integer.parseInt(new String(cbuffer, 0, 3), 2);
			int type = Integer.parseInt(new String(cbuffer, 3, 3), 2);
			
			Packet packet = new Packet(version, type);
			
			//literal:
			if (type == 4) {
				do {
					if (in.read(cbuffer, 0, 5) != 5)
						throw new IOException("Invalid packet");
					
					sbuffer.append(cbuffer, 1, 4);
				}
				while (cbuffer[0] != '0');
				
				packet.value = Long.parseLong(sbuffer.toString(), 2);
				sbuffer.setLength(0);
			}
			//operator:
			else {
				if (in.read(cbuffer, 0, 1) != 1)
					throw new IOException("Operator packet missing type indicator");
				
				char typeId = cbuffer[0];
				if (typeId == '0') {
					if (in.read(cbuffer, 0, 15) != 15)
						throw new IOException("Invalid packet");
					
					int subPacketBits = Integer.parseInt(new String(cbuffer, 0, 15), 2);
					PacketReader childReader = new PacketReader(new LimitedReader(this.in, subPacketBits));
					Packet child;
					while ((child = childReader.read()) != null)
						packet.childPackets.add(child);
					
				}
				else if (typeId == '1') {
					if (in.read(cbuffer, 0, 11) != 11)
						throw new IOException("Invalid packet");
					
					int subPacketCount = Integer.parseInt(new String(cbuffer, 0, 11), 2);
					for (int i=0; i<subPacketCount; i++)
						packet.childPackets.add(this.read());
				}
				else {
					throw new IOException("Invalid type ID: " + typeId);
				}
			}
			
			return packet;
		}
		
		public static PacketReader forHexString(String hex) throws IOException {
			String binary = hexToBinary(hex);
			return new PacketReader(new CharArrayReader(binary.toCharArray()));
		}
		public static Packet readHexString(String hex) throws IOException {
			PacketReader in = forHexString(hex);
			Packet packet = in.read();
			in.close();
			return packet;
		}
		
	}
	

	/**
	 * Wrapper around a Reader that limits the number of characters 
	 * we can read. (Used for child packet parsing when typeId = 0).
	 */
	static class LimitedReader extends Reader {

		Reader in;
		int maxChars;
		int charCount = 0;
		
		public LimitedReader(Reader in, int maxChars) {
			this.in = in;
			this.maxChars = maxChars;
		}
		
		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			//ensure we don't read past limit:
			len = Math.min(len, maxChars-charCount);
			if (len == 0)
				return -1;	//EOF
			
			int count = in.read(cbuf, off, len);
			if (count > 0)
				charCount += count;
			
			return count;
		}

		@Override
		public void close() throws IOException {
			in.close();
		}
		
	}
	

	
	public static String hexToBinary(String hex) {
		StringBuilder s = new StringBuilder();
		for (int i=0; i<hex.length(); i++) {
			int value = Integer.parseInt(hex.substring(i,i+1), 16);
			String bin = Integer.toBinaryString(value);
			for (int j=0; j<4-bin.length(); j++)
				s.append('0');
			s.append(bin);
		}
		return s.toString();
	}
	
	public static void runTests() throws Exception {
		
		System.out.println("Hex to binary: D2FE28 => 110100101111111000101000");
		System.out.println(hexToBinary("D2FE28"));
		System.out.println();
		
		System.out.println("Parse literal: D2FE28");
		PacketReader.readHexString("D2FE28").print(System.out);
		System.out.println();
		
		System.out.println("Parse oparator: 38006F45291200");
		PacketReader.readHexString("38006F45291200").print(System.out);
		System.out.println();
		
		System.out.println("Parse oparator: EE00D40C823060");
		PacketReader.readHexString("EE00D40C823060").print(System.out);
		System.out.println();
		
		System.out.println("Add Version Numbers (Part 1)");
		System.out.println(addVersionNumbers("A0016C880162017C3686B18A3D4780"));
	}
	
	public static int addVersionNumbers(String hex) throws IOException {
		Packet packet = PacketReader.readHexString(hex);
		return packet.getVersionNumberSum();
	}
	
	public static long evaluate(String hex) throws IOException {
		Packet packet = PacketReader.readHexString(hex);
		return packet.evaluate();
	}
	
	public static void solvePart1() throws Exception {
		String line = FileUtil.readLineFromFile(new File("files/day16/input.txt"));
		int count = addVersionNumbers(line);
		System.out.println("Part 1: " + count);
	}
	
	public static void solvePart2() throws Exception {
		String line = FileUtil.readLineFromFile(new File("files/day16/input.txt"));
		long value = evaluate(line);
		System.out.println("Part 1: " + value);
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
