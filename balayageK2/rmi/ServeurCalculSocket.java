package balayageK2.rmi;

import java.lang.reflect.Constructor;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;

import balayageK2.FonctionBalayage;

public class ServeurCalculSocket {
	
    public static void main(String[] args) {
    	String host = (args.length < 1) ? null : args[0];
    	/*
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    	 */
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			ServeurPrincipal serveur = (ServeurPrincipal) registry.lookup("ServeurPrincipal");
			System.out.println("Serveur Principal trouvé");  
			while (true) {
				//System.out.println("Attente calcul ...");
				Hashtable<String, Object> initCalcul = serveur.getCalcul();
				//System.out.println("Calcul ID="+initCalcul.get("ID"));
				Class classeFonction = Class.forName((String)initCalcul.get("NomClasse"));
				Constructor<FonctionBalayage> constructeur = null;
				Class[] paramFormel = { Hashtable.class };
				constructeur = (Constructor<FonctionBalayage>) classeFonction.getConstructor(paramFormel);
				FonctionBalayage fonctionCourante = constructeur.newInstance(initCalcul);
				fonctionCourante.calcul();
				serveur.calculTerminé((String)initCalcul.get("ID"));
				//System.out.println("Calcul terminé ID="+initCalcul.get("ID"));
			}
		} catch (Exception e) {
			System.out.println("Terminé ! "+e);
			System.exit(1);
		}

    }

}
