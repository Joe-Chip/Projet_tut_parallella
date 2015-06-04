package test;

import parser.JavaE;

public class Test {
	public static void main(String args[]) {
		JavaE parser;
		try {
			parser = new JavaE("src/epiphany", "sum");
			parser.read();
			parser.write();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
