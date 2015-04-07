package balayageK2.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

public interface ServeurPrincipal extends Remote {
	public Hashtable<String, Object> getCalcul() throws RemoteException;
	public void calculTerminé(String id) throws RemoteException;
}
