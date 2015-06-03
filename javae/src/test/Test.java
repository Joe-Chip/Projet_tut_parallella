package test;

import parser.Parser;

public class Test {
	public static void main(String args[]) {
		try {
			new Parser("src/xml/test.xml").read();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
