package balayageK2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;

import balayageK2.awt.AppletDessin;
import balayageK2.rmi.Configuration;
import balayageK2.rmi.ServeurPrincipal;
import balayageK2.rmi.ServeurPrincipalImpl;

/*
java -Djava.security.policy=/home_pers/castan/serveurPrincipal.policy -jar BalayageK2.jar -configPar
java -Djava.rmi.server.hostname=lesia-035 -Djava.security.policy=/home_pers/castan/client.policy -cp BalayageK2.jar  balayageK2/rmi/ServeurCalculImpl lesia-035
 */

public class Main {
	public static void main(String[] args) {
		String fichierConfiguration=null;
		JFrame window = new JFrame("BalayageK2");
		final AppletDessin applet = new AppletDessin();
		window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	System.exit(0);
            	}
        });
		String usageStr = "Usage : java -jar BalayageK2.jar [-avecCache] [-sansPerf] [-configPar[=fichConfig.xml]] [nom.classe]";
		int ctrArgs = 0;
		boolean modeServeurDistant = false;
		while (ctrArgs < args.length && args[ctrArgs].startsWith("-")) {
			if (args[ctrArgs].equals("-avecCache")) {
				AppletDessin.avecTableCache = true;
				ctrArgs++;
			} else if (args[ctrArgs].equals("-sansPerf")) {
				AppletDessin.avecPerf = false;
				ctrArgs++;
			} else if (args[ctrArgs].equals("-configPar")) {
				modeServeurDistant = true;
				ctrArgs++;
			} else if (args[ctrArgs].startsWith("-configPar=")) {
				modeServeurDistant = true;
				fichierConfiguration = args[ctrArgs].substring(11);
				ctrArgs++;
			} else if (args[ctrArgs].equals("-?")) {
				System.out.println(usageStr);
				System.exit(0);
			} else {
				System.out.println("Option inconnue : "+args[ctrArgs]);
				System.out.println(usageStr);
				System.exit(0);
			}
		}
		if (ctrArgs != args.length & ctrArgs != args.length-1) {
			System.out.println("Erreur paramètres");
			System.out.println(usageStr);
			System.exit(0);
		}
		window.getContentPane().add(applet);
        window.setVisible(true);
		applet.init();
		window.pack();
		if (modeServeurDistant) {
			/*
		    if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new SecurityManager());
	        }
	    	*/
			try {
				try {
					 //System.out.println("Création RMI registry.");
					 java.rmi.registry.LocateRegistry.createRegistry(1099);
					 System.out.println("RMI registry ready.");
				  } catch (Exception e) {
					 System.out.println("Exception starting RMI registry:");
					 System.exit(0);
				  }
				System.out.println("rmiregistry lancé !");
				applet.serveurPrincipal = new ServeurPrincipalImpl();
	    	    ServeurPrincipal stubServeur = (ServeurPrincipal) UnicastRemoteObject.exportObject(applet.serveurPrincipal, 0);

	    	    // Bind the remote object's stub in the registry
				System.out.println("avant locate !");
	    	    Registry registry = LocateRegistry.getRegistry();
				System.out.println("avant rebind !");
	    	    registry.rebind("ServeurPrincipal", stubServeur);
	    	    System.out.println("Serveur Principal prêt");
	    	    if (fichierConfiguration!=null) {
	    	    	Configuration conf = new Configuration();
	    	    	conf.créer(fichierConfiguration);
	    	    	System.out.println("Serveur calcul configuration prêt");
	    	    }
	    	} catch (Exception e) {
	    	    System.err.println("Serveur Principal erreur : " + e.toString());
	    	    e.printStackTrace();
	    	}
	    }
		if (ctrArgs != args.length) {
			applet.miseEnPlace(args[ctrArgs]);
			ctrArgs++;
		}

	}

	public static void debug(String msg) {
		StackTraceElement[] pile = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		sb.append("*** DEBUG depuis "+LogFormatter.str(pile[2])+" : "+msg);
		if (Parameter.verbose) {
			for (int i=3; i<pile.length; i++) {
				sb.append(System.getProperty("line.separator")+"   ** "+LogFormatter.str(pile[i]));
			}
			sb.append(System.getProperty("line.separator")+"-----------------");
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
		System.out.println(sb.toString());
	}

	public static void debug(String msg, boolean verbose) {
		StackTraceElement[] pile = Thread.currentThread().getStackTrace();
		StringBuilder sb = new StringBuilder();
		sb.append("*** DEBUG depuis "+LogFormatter.str(pile[2])+" : "+msg);
		if (verbose) {
			for (int i=3; i<pile.length; i++) {
				sb.append(System.getProperty("line.separator")+"   ** "+LogFormatter.str(pile[i]));
			}
			sb.append(System.getProperty("line.separator")+"-----------------");
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
		System.out.println(sb.toString());
	}
	

}
