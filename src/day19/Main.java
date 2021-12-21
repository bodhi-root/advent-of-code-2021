package day19;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	static class XYZ {
		
		int x;
		int y;
		int z;
		
		public XYZ(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public static XYZ parse(String line) {
			String [] parts = line.split(",");
			return new XYZ(
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2])
				);
		}
		
		public String toString() {
			return x + "," + y + "," + z;
		}
		
		public XYZ translate(int dx, int dy, int dz) {
			return new XYZ(x+dx, y+dy, z+dz);
		}
		public XYZ rotate(Rotation rotation) {
			return rotation.rotate(this);
		}
		
		public boolean equals(XYZ o) {
			if (o == null || !(o instanceof XYZ))
				return false;
			
			XYZ that = (XYZ)o;
			return this.x == that.x && this.y == that.y && this.z == that.z;
		}
		
		public int hashCode() {
			return x ^ 7 + y ^ 8 + z;
		}
		
	}
	
	static class SensorReading {
		
		String id;
		List<XYZ> beaconLocs = new ArrayList<>();
		
		public SensorReading(String id) {
			this.id = id;
		}
		
		public void add(XYZ loc) {
			beaconLocs.add(loc);
		}
		
		public List<XYZ> getBeaconLocations(Rotation rotation) {
			List<XYZ> rotated = new ArrayList<>(beaconLocs.size());
			for (XYZ loc : beaconLocs)
				rotated.add(rotation.rotate(loc));
			return rotated;
		}
		
	}
	
	static class AllInput {
		
		List<SensorReading> sensorReadings = new ArrayList<>();
		
		public static AllInput readFromFile(File file) throws IOException {
			AllInput input = new AllInput();
			SensorReading currentSensor = null;
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if (line.isEmpty())
						continue;
					
					if (line.charAt(0) == '-' && line.charAt(1) == '-') {
						currentSensor = new SensorReading("Sensor " + input.sensorReadings.size());
						input.sensorReadings.add(currentSensor);
					} else {
						currentSensor.add(XYZ.parse(line));
					}
				}
				
			}
			finally {
				in.close();
			}
			
			return input;
		}
		
		public void print(PrintStream out) {
			for (int i=0; i<sensorReadings.size(); i++) {
				SensorReading sensor = sensorReadings.get(i);
				out.println("=== Sensor " + i + " ===");
				for (XYZ loc : sensor.beaconLocs)
					out.println(loc.toString());
				
				out.println();
			}
		}
		
		public Solution solve() {
			System.out.println("Solving...");
			
			for (int firstSensorIndex=0; firstSensorIndex<sensorReadings.size(); firstSensorIndex++) {
				Solution solution = trySolve(firstSensorIndex);
				if (solution != null)
					return solution;
			}
			
			throw new IllegalStateException("solution not found!");
		}
		
		public Solution trySolve(int firstSensorIndex) {
			System.out.println("Trying: " + firstSensorIndex);
			List<SensorReading> readings = new ArrayList<>(sensorReadings);	//make copy so we can edit
			
			Solution solution = new Solution();
			
			//add sensor 1 at origin with default orientation.
			//every world location will be relative to this location and orientation.
			solution.add(new Sensor(new XYZ(0, 0, 0)));
			SensorReading reading = readings.remove(firstSensorIndex);
			for (XYZ loc : reading.beaconLocs)
				solution.add(new Beacon(loc));
			
			//solution.printBeaconLocations(System.out);
			
			while (!readings.isEmpty()) {
				
				boolean foundOne = false;
				for (int i=0; i<readings.size(); i++) {
					SensorReading tmpReading = readings.get(i);
					if (solution.tryToAddSensor(tmpReading)) {
						System.out.println("Aligned sensor: " + tmpReading.id);
						readings.remove(tmpReading);
						foundOne = true;
						break;
					}
					if (tmpReading.id.equals("Sensor 17")) {
						System.exit(0);
					}
				}
				
				if (!foundOne)
					return null;
			}
			
			return solution;
		}
		
	}
	
	static class Sensor {
		
		XYZ location;
		//XYZ orientation;	//vector of 1's and 0's indicating orientation
		Rotation rotation;
		
		public Sensor(XYZ location, Rotation rotation) {
			this.location = location;
			//this.orientation = new XYZ(1, 0, 0);
			this.rotation = rotation;
		}
		public Sensor(XYZ location) {
			this(location, ALL_ROTATIONS.get(0));
		}
		
	}
	
	static class Beacon {
		
		XYZ location;
		
		public Beacon(XYZ location) {
			this.location = location;
		}
		
	}
	
	static class Solution {
		
		List<Sensor> sensors = new ArrayList<>();
		List<Beacon> beacons = new ArrayList<>();
		
		public void add(Sensor sensor) {
			sensors.add(sensor);
		}
		public void add(Beacon beacon) {
			beacons.add(beacon);
		}
		
		public boolean containsBeaconAt(XYZ loc) {
			for (Beacon beacon : beacons) {
				if (beacon.location.equals(loc))
					return true;
			}
			return false;
		}
		
		public List<XYZ> getBeaconLocationsVisibleFrom(XYZ p) {
			List<XYZ> locs = new ArrayList<>(beacons.size());
			for (Beacon beacon : beacons) {
				XYZ p2 = beacon.location;
				if ((Math.abs(p2.x - p.x) <= 1000) &&
				    (Math.abs(p2.y - p.y) <= 1000) && 
				    (Math.abs(p2.z - p.z) <= 1000)) {
					locs.add(p2);
				}
			}
			return locs;
		}
		
		
		/*
		//this way of checking should be better...
		//not sure why this doesn't work...
		public boolean tryToAddSensor(SensorReading readings) {
					
			//try all rotations of sensor readings
			for (Rotation rotation : ALL_ROTATIONS) {
				List<XYZ> sensorBeaconLocs = readings.getBeaconLocations(rotation);
				
				//try to align each sensor reading to a known beacon location
				for (XYZ relativeBeaconLoc : sensorBeaconLocs) {
					for (Beacon knownBeacon : this.beacons) {
						
						//translation needed to transform relative location to fixed/known location
						int dx = knownBeacon.location.x - relativeBeaconLoc.x;
						int dy = knownBeacon.location.y - relativeBeaconLoc.y;
						int dz = knownBeacon.location.z - relativeBeaconLoc.z;

						XYZ position = new XYZ(dx, dy, dz);
						List<XYZ> shouldRead = this.getBeaconLocationsVisibleFrom(position);
						
						if (shouldRead.size() >= 12) {
							//if (readings.id.equals("Sensor 17"))
							//	System.out.println("Should see " + shouldRead.size() + " beacons");

							//see if everything else aligns:
							int matchCount = 0;
							for (XYZ tmpRelativeLoc : sensorBeaconLocs) {
								XYZ globalLoc = tmpRelativeLoc.translate(dx, dy, dz);
								if (shouldRead.remove(globalLoc))
									matchCount++;
							}
							
							//if (matchCount > 0 && readings.id.equals("Sensor 17"))
							//	System.out.println("Matched " + matchCount + " (all but " + shouldRead.size() + " beacons");
							
							if (matchCount >= 12) {// && shouldRead.isEmpty()) {
								System.out.println("Aligned sensor!");
								add(new Sensor(position, rotation));
								for (XYZ tmpRelativeLoc : sensorBeaconLocs) {
									XYZ globalLoc = tmpRelativeLoc.translate(dx, dy, dz);
									if (!this.containsBeaconAt(globalLoc))
										add(new Beacon(globalLoc));
								}
								return true;
							}
						}
					}
				}
			}
			
			return false;
		}	
		*/	
		
		/**
		 * Tries to add a sensor with the given readings to our data set.  We do this
		 * by trying every rotation of coordinates that could bring the sensor into 
		 * alignment with our global coordinate system.  We then try to align each
		 * beacon in the sensor input to a known beacon in our solution.  If this alignment
		 * results in the other beacons all lining up, the solution is good and we can
		 * add the sensor and all of its beacons to our solution.
		 */
		public boolean tryToAddSensor(SensorReading readings) {
						
			//try all rotations of sensor readings
			for (Rotation rotation : ALL_ROTATIONS) {
				List<XYZ> sensorBeaconLocs = readings.getBeaconLocations(rotation);
				
				//try to align each sensor reading to a known beacon location
				for (XYZ relativeBeaconLoc : sensorBeaconLocs) {
					for (Beacon knownBeacon : this.beacons) {
						
						//translation needed to transform relative location to fixed/known location
						int dx = knownBeacon.location.x - relativeBeaconLoc.x;
						int dy = knownBeacon.location.y - relativeBeaconLoc.y;
						int dz = knownBeacon.location.z - relativeBeaconLoc.z;
					
						//see if everything else aligns:
						int matchCount = 0;
						for (XYZ tmpRelativeLoc : sensorBeaconLocs) {
							XYZ globalLoc = tmpRelativeLoc.translate(dx, dy, dz);
							if (this.containsBeaconAt(globalLoc))
								matchCount++;
						}
						
						if (matchCount >= 12) {
							System.out.println("Aligned sensor!");
							System.out.println("  " + matchCount + " matches");
							add(new Sensor(new XYZ(dx, dy, dz), rotation));
							for (XYZ tmpRelativeLoc : sensorBeaconLocs) {
								XYZ globalLoc = tmpRelativeLoc.translate(dx, dy, dz);
								if (!this.containsBeaconAt(globalLoc))
									add(new Beacon(globalLoc));
							}
							return true;
						}
					}
				}
			}
			
			return false;
		}
	
		public void printBeaconLocations(PrintStream out) {
			out.println("Beacons:");
			for (Beacon beacon : beacons)
				out.println(beacon.location.toString());
		}
		
		/**
		 * Find the longest manhattan distance between any two beacons
		 */
		public long getPart2Answer() {
			long maxDistance = 0;
			for (int i=0; i<sensors.size(); i++) {
				XYZ p1 = sensors.get(i).location;
				for (int j=i+1; j<sensors.size(); j++) {
					XYZ p2 = sensors.get(j).location;
					long distance = Math.abs(p2.x - p1.x) +
							        Math.abs(p2.y - p1.y) +
							        Math.abs(p2.z - p1.z);
					maxDistance = Math.max(maxDistance, distance);
				}
			}
			return maxDistance;
		}
		
	}
	
	static List<XYZ> ALL_ORIENTATIONS = new ArrayList<>();
	static List<Rotation> ALL_ROTATIONS = new ArrayList<>();
	
	static interface Rotation {
		public XYZ rotate(XYZ p);
	}
	
	static class RotationMatrix implements Rotation {
		
		int [][] m;
		
		// I don't know the directions of these... but who cares
		static int [][] IDENTITY = new int [][] {
			{1, 0, 0},
			{0, 1, 0},
			{0, 0, 1}
		};
		static int [][] X90 = new int [][] {
			{ 1, 0, 0},
			{ 0, 0,-1},
			{ 0, 1, 0}
		};
		static int [][] Y90 = new int [][] {
			{ 0, 0, -1},
			{ 0, 1, 0},
			{ 1, 0, 0}
		};
		static int [][] Z90 = new int [][] {
			{ 0, -1, 0},
			{ 1, 0, 0},
			{ 0, 0, 1}
		};
		
		public RotationMatrix(int [][] m) {
			this.m = m;
		}
		
		public RotationMatrix rotate90X() {
			return new RotationMatrix(multiply(X90, m));
		}
		public RotationMatrix rotate90Y() {
			return new RotationMatrix(multiply(Y90, m));
		}
		public RotationMatrix rotate90Z() {
			return new RotationMatrix(multiply(Z90, m));
		}
		
		public static int [][] multiply(int [][] x, int [][] y) {
			int [][] z = new int[3][3];
			for (int i=0; i<3; i++) {
				for (int j=0; j<3; j++) {
					for (int k = 0; k<3; k++) {
			            z[i][j] += x[i][k] * y[k][j];
			        }
				}
			}
			return z;
		}
		
		public XYZ rotate(XYZ p) {
			return new XYZ(
					p.x * m[0][0] + p.y * m[0][1] + p.z * m[0][2],
					p.x * m[1][0] + p.y * m[1][1] + p.z * m[1][2],
					p.x * m[2][0] + p.y * m[2][1] + p.z * m[2][2]
					);
		}
		
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (int i=0; i<3; i++) {
				s.append("| ");
				for (int j=0; j<3; j++) {
					if (j > 0)
						s.append(",");
					String text = String.valueOf(m[i][j]);
					for (int k=0; k<3-text.length(); k++)
						s.append(' ');
					s.append(m[i][j]);
				}
				s.append(" |\n");
			}
			return s.toString();
		}
		
	}
	
	static {
		ALL_ORIENTATIONS.add(new XYZ( 1, 0, 0));
		ALL_ORIENTATIONS.add(new XYZ( 0, 1, 0));
		ALL_ORIENTATIONS.add(new XYZ(-1, 0, 0));
		ALL_ORIENTATIONS.add(new XYZ( 0,-1, 0));
		ALL_ORIENTATIONS.add(new XYZ( 0, 0, 1));
		ALL_ORIENTATIONS.add(new XYZ( 0, 0,-1));
		
		//primary rotations to align any axis to the fixed X axis:
		RotationMatrix fixed = new RotationMatrix(RotationMatrix.IDENTITY);
		ALL_ROTATIONS.add(fixed);
		
		RotationMatrix m = fixed;
		for (int i=0; i<3; i++) {
			m = m.rotate90Y();
			ALL_ROTATIONS.add(m);
		}
		ALL_ROTATIONS.add(fixed.rotate90Z());
		ALL_ROTATIONS.add(fixed.rotate90Z().rotate90Z().rotate90Z());
		
		for (Rotation rotation : ALL_ROTATIONS) {
			System.out.println(rotation.toString());
			System.out.println();
		}
		
		//each primary rotation gets 4 rotations around the X axis
		//(we already have the first 1 stored)
		for (int i=0; i<6; i++) {
			m = (RotationMatrix)ALL_ROTATIONS.get(i);
			for (int j=0; j<3; j++) {
				m = m.rotate90X();
				ALL_ROTATIONS.add(m);
			}
		}
		
	}
	
	public static void runTests() {
		//test rotations:
		SensorReading reading = new SensorReading("Test");
		reading.add(XYZ.parse("-1,-1,1"));
		reading.add(XYZ.parse("-2,-2,2"));
		reading.add(XYZ.parse("-3,-3,3"));
		reading.add(XYZ.parse("-2,-3,1"));
		reading.add(XYZ.parse("5,6,-4"));
		reading.add(XYZ.parse("8,0,7"));
		
		
		for (int i=0; i<ALL_ROTATIONS.size(); i++) {
			System.out.println("Rotation " + (i+1));
			Rotation rotation = ALL_ROTATIONS.get(i);
			List<XYZ> locs = reading.getBeaconLocations(rotation);
			for (XYZ loc : locs)
				System.out.println(loc);
			System.out.println();
		}
	
	}
	
	public static void solvePart1And2() throws Exception {
		AllInput input = AllInput.readFromFile(new File("files/day19/input.txt"));
		input.print(System.out);
		Solution solution = input.solve();
		System.out.println("Part 1: " + solution.beacons.size());
		System.out.println("Part 2: " + solution.getPart2Answer());	//16952 is wrong
	}
	
	public static void main(String [] args) {
		try {
			runTests();
			solvePart1And2();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
