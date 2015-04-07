package balayageK2;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogFormatter extends Formatter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static boolean isInfo() {return Level.INFO.intValue()>=Parameter.level.intValue();}
	public static boolean isFine() {return Level.FINE.intValue()>=Parameter.level.intValue();}
	public static boolean isFiner() {return Level.FINER.intValue()>=Parameter.level.intValue();}
	public static boolean isFinest() {return Level.FINEST.intValue()>=Parameter.level.intValue();}
	
	@Override
	public String format(LogRecord record) {
		StackTraceElement[] pile;
		StringBuilder sb = new StringBuilder();
		if (record.getThrown() != null) {
			pile = record.getThrown().getStackTrace();
			sb.append("*** "+record.getLevel().getLocalizedName()+" depuis "+str(pile[pile.length-2])+" : "+record.getThrown());
			if (Parameter.verbose || record.getLevel()==Level.SEVERE) {
				for (int i=0; i<pile.length; i++) {
					sb.append(LINE_SEPARATOR+"   ** "+str(pile[i]));
				}
				sb.append(LINE_SEPARATOR+"-----------------");
			}
		} else {
			pile = Thread.currentThread().getStackTrace();
			sb.append("*** "+record.getLevel().getLocalizedName()+" depuis "+str(pile[8])+" : "+record.getMessage());
			if (Parameter.verbose || record.getLevel()==Level.SEVERE) {
				for (int i=8; i<pile.length; i++) {
					sb.append(LINE_SEPARATOR+"   ** "+str(pile[i]));
				}
				sb.append(LINE_SEPARATOR+"-----------------");
			}
		}
		/*
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
				// ignore
			}
		}
*/
		sb.append(LINE_SEPARATOR);
		return sb.toString();
	}

	public static String str(StackTraceElement ste) {
		return ste.getClassName()+"."+ste.getMethodName()+":"+ste.getLineNumber();
	}
}
