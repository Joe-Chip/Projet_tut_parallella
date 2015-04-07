package balayageK2.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Vector;

import balayageK2.CalculBase;

public class ServeurPrincipalImpl implements ServeurPrincipal, Serializable {
	Hashtable<String, Hashtable<String, Object>> lstCalculsEnCours = new Hashtable<String, Hashtable<String, Object>>();
	int nbrClientsEnAttente = 0;
	Vector<Hashtable<String, Object>> lstCalculsAfaire = new Vector<Hashtable<String, Object>>();

	public Hashtable<String, Object> getCalcul() throws RemoteException {
		synchronized (lstCalculsAfaire) {
			if (lstCalculsAfaire.size()==0) {
				nbrClientsEnAttente++;
				try {
					lstCalculsAfaire.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			Hashtable<String, Object> calculData = lstCalculsAfaire.remove(lstCalculsAfaire.size()-1);
			synchronized (lstCalculsEnCours) {
				lstCalculsEnCours.put((String)calculData.get("ID"), calculData);
			}
			return calculData;
		}
	}
	
	public void calculTerminé(String id) throws RemoteException {
			synchronized (lstCalculsEnCours) {
				lstCalculsEnCours.remove(id);
				lstCalculsEnCours.notify();
				//System.out.println("C'est notifié !"+lstCalculsEnCours.size());
			}
	}
	
	public void attenteFinCalcul(CalculBase base) {
			synchronized (lstCalculsEnCours) {
				while (lstCalculsAfaire.size()!=0 ||
						lstCalculsEnCours.size()!=0) {
					try {	
						if (base.arrêtRunner) {
							synchronized (lstCalculsAfaire) {
								//System.out.println("on va enlever "+lstCalculsAfaire.size());
								lstCalculsAfaire.removeAllElements();
							}						
						}
						
						if (lstCalculsAfaire.size()!=0 ||
								lstCalculsEnCours.size()!=0) {
							//System.out.println("On attend que tout soit terminé ! "+lstCalculsEnCours.size());
							lstCalculsEnCours.wait();
						} else {
							//System.out.println("Pas de calcul en cours !");
						}
						//System.out.println("C'est fini !");
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
	}
	
	public void addCalcul(Hashtable<String, Object> initialisation) {
		synchronized (lstCalculsAfaire) {
			initialisation.put("ID", ""+initialisation.hashCode());
			lstCalculsAfaire.add(initialisation);
			if (nbrClientsEnAttente>0) {
				lstCalculsAfaire.notify();
				nbrClientsEnAttente--;
			}
		}
	}
	
	public void arretCalculs() {
		//System.out.println("On va arreter");
		
		synchronized (lstCalculsEnCours) {
			lstCalculsEnCours.notify();
		}
		//System.out.println("On a notifié");
	}
	
	public static void main(String args[]) {
		try {
    	    ServeurPrincipalImpl obj = new ServeurPrincipalImpl();
    	    ServeurPrincipal stub = (ServeurPrincipal) UnicastRemoteObject.exportObject(obj, 0);

    	    // Bind the remote object's stub in the registry
    	    Registry registry = LocateRegistry.getRegistry();
    	    registry.bind("ServeurPrincipal", stub);

    	    System.err.println("Serveur Principal prêt");
    	} catch (Exception e) {
    	    System.err.println("Serveur Principal erreur : " + e.toString());
    	    e.printStackTrace();
    	}
    }

}
