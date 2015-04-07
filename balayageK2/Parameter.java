package balayageK2;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Cette classe contient les différents paramètres utilisables dans le systemL lilas.
 */
public class Parameter {
	public static String versionJar = "Version ???";
	public static Logger logger; // initialisé dès qu'on charge la config
	public static Handler handler;
	public static Level level = Level.SEVERE;// par défaut
	/*
	 * Level.FINER => Signal forcé sensible, vérification sur equals non effectué
	 */
	
	public static String fileLog;
		
	/**
	 * true pour avoir les traces d'exécution lors des erreurs
	 */
	public static boolean verbose = false;
	
	public static PrintStream err = System.err;
	
	public static PrintStream out = System.out;
	
	public static void setLevel(Level level) {
		Parameter.level = level;
		handler.setLevel(level);
		logger.setLevel(level);
	}
}
