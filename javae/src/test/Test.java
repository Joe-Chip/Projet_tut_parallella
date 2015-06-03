package test;

import parser.Parser;

public class Test {
	public static void main(String args[]) {
		Parser parser;
		try {
			parser = new Parser("src/test", "test");
			parser.read();
			parser.write();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
