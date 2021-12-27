package day24;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALU {

	static class Register {
		
		String name;
		long value = 0;
		
		public Register(String name) {
			this.name = name;
		}
		
	}
	
	Map<String, Register> registers = new HashMap<>();
	List<Step> program = new ArrayList<>();
	Reader input;
	
	public ALU() {
		registers.put("w", new Register("w"));
		registers.put("x", new Register("x"));
		registers.put("y", new Register("y"));
		registers.put("z", new Register("z"));
	}
	
	public void reset() {
		setValue("w", 0);
		setValue("x", 0);
		setValue("y", 0);
		setValue("z", 0);
	}
	
	public Register getRegister(String name) {
		return registers.get(name);
	}
	public long getValue(String name) {
		Register register = registers.get(name);
		if (register != null)
			return register.value;
		
		return Long.parseLong(name);
	}
	public void setValue(String name, long value) {
		registers.get(name).value = value;
	}
	
	public void loadProgramFromFile(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		try {
			
			program.clear();
			
			String line;
			while ((line = in.readLine()) != null)  {
				line = line.trim();
				if (line.isEmpty())
					continue;
				
				program.add(parseStep(line));
			}
			
		} finally {
			in.close();
		}
	}
	
	public void setInput(Reader input) {
		this.input = input;
	}
	
	public void setInput(String input) {
		setInput(new StringReader(input));
	}
	
	public void run() {
		for (Step step : program)
			step.apply(this);
	}
	
	public boolean checkModelNumber(String value) {
		//System.out.println("Checking: " + value);
		reset();
		setInput(value);
		run();
		//System.out.println("z = " + getValue("z"));
		return getValue("z") == 0;
	}
	
	static interface Step {
		
		void apply(ALU alu);
		
	}
	
	static Step parseStep(String line) throws IOException {
		String [] parts = line.split("\\s+");
		String action = parts[0];
		if (action.equals("inp"))
			return new InputStep(parts[1]);
		else if (action.equals("add"))
			return new AddStep(parts[1], parts[2]);
		else if (action.equals("mul"))
			return new MultiplyStep(parts[1], parts[2]);
		else if (action.equals("div"))
			return new DivideStep(parts[1], parts[2]);
		else if (action.equals("mod")) 
			return new ModulusStep(parts[1], parts[2]);
		else if (action.equals("eql"))
			return new EqualsStep(parts[1], parts[2]);
		else
			throw new IOException("Invalid command: " + line);
	}
	
	//inp a - Read an input value and write it to variable a.
	static class InputStep implements Step {
		
		String dest;
		
		public InputStep(String dest) {
			this.dest = dest;
		}
		
		public void apply(ALU alu) {
			int value;
			try {
				value = alu.input.read();
			}
			catch(IOException e) {
				throw new IllegalStateException(e);
			}
			
			if (value < 0)
				throw new IllegalStateException("Unexpected EOF");
			
			String txt = "" + (char)value;
			alu.setValue(dest, Integer.parseInt(txt));
		}
		
	}
	
	//add a b - Add the value of a to the value of b, then store the result in variable a.
	static class AddStep implements Step {

		String r1, r2;

		public AddStep(String r1, String r2) {
			this.r1 = r1;
			this.r2 = r2;
		}

		public void apply(ALU alu) {
			alu.setValue(r1, alu.getValue(r1) + alu.getValue(r2));
		}
	}

	//mul a b - Multiply the value of a by the value of b, then store the result in variable a.
	static class MultiplyStep implements Step {

		String r1, r2;

		public MultiplyStep(String r1, String r2) {
			this.r1 = r1;
			this.r2 = r2;
		}

		public void apply(ALU alu) {
			alu.setValue(r1, alu.getValue(r1) * alu.getValue(r2));
		}
	}


	//div a b - Divide the value of a by the value of b, truncate the result to an integer, 
	//then store the result in variable a. (Here, "truncate" means to round the value toward zero.)
	static class DivideStep implements Step {

		String r1, r2;

		public DivideStep(String r1, String r2) {
			this.r1 = r1;
			this.r2 = r2;
		}

		public void apply(ALU alu) {
			alu.setValue(r1, alu.getValue(r1) / alu.getValue(r2));
		}
	}

	//mod a b - Divide the value of a by the value of b, then store the remainder in variable a.
	//(This is also called the modulo operation.)
	static class ModulusStep implements Step {

		String r1, r2;

		public ModulusStep(String r1, String r2) {
			this.r1 = r1;
			this.r2 = r2;
		}

		public void apply(ALU alu) {
			alu.setValue(r1, alu.getValue(r1) % alu.getValue(r2));
		}
	}

	//eql a b - If the value of a and b are equal, then store the value 1 in variable a. 
	//Otherwise, store the value 0 in variable a.
	static class EqualsStep implements Step {

		String r1, r2;

		public EqualsStep(String r1, String r2) {
			this.r1 = r1;
			this.r2 = r2;
		}

		public void apply(ALU alu) {
			alu.setValue(r1, alu.getValue(r1) == alu.getValue(r2) ? 1 : 0);
		}
	}
	
}
