package balayageK2.rmi;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import balayageK2.awt.ListeCouleurs;

public interface PanelDistant extends Remote {
	   public void ajouterLstPoints3Ddistant(Vector<Double> lstX, Vector<Double> lstY, Vector<Double> lstZ, Vector<Integer>lstC) throws RemoteException;
	   public void ajouterLstPoints2Ddistant(Vector<Double> lstX, Vector<Double> lstY, Vector<Integer>lstC) throws RemoteException;
}
