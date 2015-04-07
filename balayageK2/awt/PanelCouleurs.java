package balayageK2.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelCouleurs extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PanelDessin2D panelDessin2D;
	
	public static final Color[] listeCouleursPréférées = {
		Color.red, 
		Color.green, 
		Color.blue, 
		Color.orange, 
		Color.pink, 
		Color.yellow,
		Color.cyan, 
		Color.magenta};

	JButton[] lstBoutons = new JButton[listeCouleursPréférées.length];
	boolean[] statusCouleurs = new boolean[listeCouleursPréférées.length];
	
	public int cycleMax;
	
	public JComboBox boutonChoixNbreCycles;
	
	public PanelCouleurs(int cycleMax, PanelDessin2D panel) {
		panelDessin2D = panel;
		this.cycleMax = cycleMax;
		setLayout(new GridLayout(1,cycleMax+1));
		String[] lstNbrCycles = new String[listeCouleursPréférées.length];
		for (int i=0; i<lstNbrCycles.length; i++) {
			lstNbrCycles[i] = ""+(i+1);
		}
		boutonChoixNbreCycles = new JComboBox(lstNbrCycles);
		boutonChoixNbreCycles.addActionListener(this);
		boutonChoixNbreCycles.setSelectedIndex(cycleMax-1);
		setCycleMax(cycleMax);
		boutonChoixNbreCycles.addActionListener(this);
	}
	
	public PanelCouleurs(PanelDessin2D panel) {
		panelDessin2D = panel;
		panel.panelCouleurs = this;
		String[] lstNbrCycles = new String[listeCouleursPréférées.length];
		for (int i=0; i<lstNbrCycles.length; i++) {
			lstNbrCycles[i] = ""+(i+1);
		}
		boutonChoixNbreCycles = new JComboBox(lstNbrCycles);
		add(boutonChoixNbreCycles);
		boutonChoixNbreCycles.addActionListener(this);
	}
	
	public void setCycleMax(int nouvCycleMax) {
		removeAll();
		cycleMax = nouvCycleMax;
		setLayout(new GridLayout(1,cycleMax+1));
		add(boutonChoixNbreCycles);
		for (int i=0; i<cycleMax; i++) {
			int j = i+1;
			lstBoutons[i] = new JButton("cyc"+j);
			//JLabel texte = new JLabel(" cycle "+j+" ");
			//texte.setForeground(listeCouleursPréférées[i]);
			//add(texte);
			lstBoutons[i].setForeground(listeCouleursPréférées[i]);
			lstBoutons[i].setBorderPainted(false);
			statusCouleurs[i]=true;
			lstBoutons[i].addActionListener(this);
			//lstBoutons[i].setPreferredSize(new Dimension(50, 20));
			add(lstBoutons[i]);
		}	
		validate();
		if (getParent()!=null) getParent().validate();
	}

	
	/**
	 * permet de créer un tableau de couleurs pour le nombre de cycles maximum passé en paramètre
	 * @return le tableau de couleurs
	 */
	public Color[] getListeCouleurs() {
		Color[] lst = new Color[cycleMax];
		for (int c = 0; c<cycleMax; c++) lst[c] = listeCouleursPréférées[c];
		return lst;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==boutonChoixNbreCycles) {
			//System.out.println("Action panelCouleur");
			setCycleMax(boutonChoixNbreCycles.getSelectedIndex()+1);
		} else {
			for (int i=0; i<lstBoutons.length; i++) {
				if (e.getSource()==lstBoutons[i]) {
					statusCouleurs[i] = ! statusCouleurs[i];
					if (!statusCouleurs[i]) lstBoutons[i].setForeground(Color.LIGHT_GRAY);
					else lstBoutons[i].setForeground(listeCouleursPréférées[i]);
				}
			}
		}
	}
	
	public int getNoCouleur(Color c) {
		for (int i=0; i<lstBoutons.length; i++) {
			if (c==listeCouleursPréférées[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean getStatusCouleur(Color c) {
		int i = getNoCouleur(c);
		if (i==-1) return true;
		else return statusCouleurs[i];
	}
}


