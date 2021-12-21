package day17;

public class Main {
	
	static class Target {
		
		int x1;
		int y1;
		int x2;
		int y2;
		
		public Target(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		public boolean contains(int x, int y) {
			return x >= x1 && x <= x2 &&
				   y >= y2 && y <= y1;
		}
		
	}
	
	static class Probe {
		
		int x;
		int y;
		
		int vx;
		int vy;
		
		public Probe(int x, int y, int vx, int vy) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
		}
		
		public void step() {
			//The probe's x position increases by its x velocity.
			//The probe's y position increases by its y velocity.
			this.x += this.vx;
			this.y += this.vy;
			
			//Due to drag, the probe's x velocity changes by 1 toward the value 0; that is, it decreases by 1 if it is greater than 0, increases by 1 if it is less than 0, or does not change if it is already 0.
			if (this.vx > 0)
				this.vx--;
			
			//Due to gravity, the probe's y velocity decreases by 1.
			this.vy--;
		}
		
	}
	
	public static void solvePart1(Target target) throws Exception {
		
		int maxVelocityThatHits = 0;
		int maxHeightForHit = 0;
		
		for (int yVelocity0=1; yVelocity0<=1000; yVelocity0++) {
			Probe probe = new Probe(target.x1, 0, 0, yVelocity0);
			int yMax = 0;
			
			//boolean hits = false;
			while (probe.y >= target.y2) {
				probe.step();
				yMax = Math.max(yMax, probe.y);
				if (target.contains(probe.x, probe.y)) {
					//hits = true;
					maxVelocityThatHits = Math.max(maxVelocityThatHits, yVelocity0);
					maxHeightForHit = Math.max(maxHeightForHit, yMax);
					break;
				}
			}
			//System.out.println(yVelocity0 + ": " + hits);
		}
		
		System.out.println("Part 1: ");
		System.out.println("Max Velocity: " + maxVelocityThatHits);
		System.out.println("Max Height: " + maxHeightForHit);
		System.out.println();
	}
	
	/**
	 * Find the range of x velocities just like we did for y velocities
	 * in part 1.
	 */
	public static void solveXRange(Target target) throws Exception {
		
		int maxVelocityThatHits = 0;
		int minVelocityThatHits = Integer.MAX_VALUE;
		
		for (int xVelocity0=1; xVelocity0<=1000; xVelocity0++) {
			Probe probe = new Probe(0, target.y1, xVelocity0, 0);
			
			//boolean hits = false;
			while (probe.x <= target.x2 && probe.vx > 0) {
				probe.step();
				
				if (probe.x >= target.x1 && probe.x <= target.x2) {
					//hits = true;
					maxVelocityThatHits = Math.max(maxVelocityThatHits, xVelocity0);
					minVelocityThatHits = Math.min(minVelocityThatHits, xVelocity0);
					break;
				}
			}
			//System.out.println(xVelocity0 + ": " + hits);
		}
		
		System.out.println("X Velocity:");
		System.out.println("Min Velocity: " + minVelocityThatHits);
		System.out.println("Max Velocity: " + maxVelocityThatHits);
		System.out.println();
	}
	
	
	public static void solvePart2(
			Target target, 
			int vxMin, int vxMax, 
			int vyMin, int vyMax) throws Exception {
		
		int count = 0;
		
		for (int vx0=vxMin; vx0<=vxMax; vx0++) {
			for (int vy0=vyMin; vy0<=vyMax; vy0++) {
				
				Probe probe = new Probe(0, 0, vx0, vy0);
				while (probe.y >= target.y2) {
					probe.step();
					if (target.contains(probe.x, probe.y)) {
						//System.out.println(vx0 + "," + vy0);
						count++;
						break;
					}
				}
				
			}
		}
		
		System.out.println("Part 2:");
		System.out.println("Count = " + count);
	}
	
	public static void main(String [] args) {
		try {
			//testing:
			Target target = new Target(20, -5, 30, -10);
			solvePart1(target);
			solveXRange(target);
			solvePart2(target, 6, 30, -10, 10);
			
			System.out.println();
			System.out.println("### My Target ####################################################");
			System.out.println();
			
			target = new Target(48, -148, 70, -189);
			
			solvePart1(target);
			solveXRange(target);
			solvePart2(target, 10, 70, -189, 189);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
