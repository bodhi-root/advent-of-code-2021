package day12;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.FileUtil;

public class Main {
	
	static class Edge {
		
		final String start;
		final String end;
		
		public Edge(String start, String end) {
			this.start = start;
			this.end = end;
		}
		
		public Edge reverse() {
			return new Edge(end, start);
		}
		
		public static Edge parse(String line) {
			int index = line.indexOf('-');
			return new Edge(line.substring(0,index), line.substring(index+1));
		}
		
	}
	
	static class Path {
		
		List<Edge> edges;
		boolean part2 = false;
		
		public Path(List<Edge> edges) {
			this.edges = edges;
		}
		public Path() {
			this(new ArrayList<>());
		}
		
		public boolean canAdd(Edge edge) {
			//make sure edge starts where we ended:
			if (isEmpty())
				return edge.start.equals("start");
			else if (!getLastEdge().end.equals(edge.start) || edge.start.equals("start"))
				return false;

			if (!part2) {
				//make sure we don't visit lowercase places twice
				for (Edge e : edges) {
					if (Character.isLowerCase(e.end.charAt(0)) && e.end.equals(edge.end))
						return false;
				}
			}
			else {
				//make sure we visit at most one lowercase place twice
				Set<String> locs = new HashSet<>();
				if (Character.isLowerCase(edge.end.charAt(0)))
					locs.add(edge.end);
				int dups = 0;
				for (Edge e : edges) {
					if (Character.isLowerCase(e.end.charAt(0))) {
						if (!locs.add(e.end))
							dups++;
					}
				}
				if (dups > 1)
					return false;
			}
			
			return true;
		}
		public void add(Edge edge) {
			edges.add(edge);
		}
		public Edge removeLast() {
			return edges.remove(edges.size()-1);
		}
		
		public boolean isEmpty() {
			return edges.isEmpty();
		}
		public Edge getLastEdge() {
			return edges.get(edges.size()-1);
		}
		
		public String toString() {
			if (isEmpty())
				return "";
			
			StringBuilder s = new StringBuilder();
			s.append(edges.get(0).start);
			for (Edge edge : edges)
				s.append(",").append(edge.end);
			return s.toString();
		}
		
	}
	
	public static void solvePart1() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day12/input.txt"));
		List<Edge> edges = new ArrayList<>(lines.size()*2);
		for (String line : lines) {
			Edge edge = Edge.parse(line);
			edges.add(edge);
			edges.add(edge.reverse());
		}
		
		Context ctx = new Context();
		Path path = new Path();
		extendPath(path, edges, ctx);
		
		System.out.println(ctx.pathCount);
	}
	
	static class Context {
		int pathCount = 0;
	}
	
	public static void extendPath(Path path, List<Edge> edges, Context ctx) {
				
		for (Edge edge : edges) {
			if (path.canAdd(edge)) {
				
				if (edge.end.equals("end")) {
					System.out.println(path.toString());
					ctx.pathCount++;
				}
				else {
					path.add(edge);
					extendPath(path, edges, ctx);
					path.removeLast();
				}

			}
		}

	}
	
	public static void solvePart2() throws Exception {
		List<String> lines = FileUtil.readLinesFromFile(new File("files/day12/input.txt"));
		List<Edge> edges = new ArrayList<>(lines.size()*2);
		for (String line : lines) {
			Edge edge = Edge.parse(line);
			edges.add(edge);
			edges.add(edge.reverse());
		}
		
		Context ctx = new Context();
		Path path = new Path();
		path.part2 = true;
		extendPath(path, edges, ctx);
		
		System.out.println(ctx.pathCount);
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
