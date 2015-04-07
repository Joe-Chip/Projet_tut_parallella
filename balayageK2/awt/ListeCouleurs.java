package balayageK2.awt;

import java.awt.Color;

public class ListeCouleurs {
	// Un objet de cette classe permet de représenter un ensemble couleur (chaque couleur étant codée sur un bit)
	
	public static final Color[] listeCouleursPréférées = {
		Color.black,
		Color.green,
		Color.blue,  
		Color.red, 
		Color.yellow,
		Color.orange, 
		Color.pink, 
		Color.cyan, 
		Color.magenta};

	public final int nbrCouleurs;
	// nbrCouleurs=1 alors la liste est identique à Color (une seule valeur de couleur)
	private int valeur = 0;
	private int masqueCouleur = 0;
	private int nbrBitsParCouleur = 0;
	
	public ListeCouleurs(int nbrCouleurs) {
		this.nbrCouleurs = nbrCouleurs;
		if (nbrCouleurs!=1) {
			nbrBitsParCouleur = 24 / nbrCouleurs;
			for (int i=0; i<nbrBitsParCouleur; i++) masqueCouleur= (masqueCouleur<<1) | 1;
		}
		//System.out.println("=="+nbrBitsParCouleur+" "+Integer.toHexString(masqueCouleur));
	}
	
	public ListeCouleurs(ListeCouleurs lc) {
		nbrCouleurs = lc.nbrCouleurs;
		valeur = lc.valeur;
		masqueCouleur = lc.masqueCouleur;
		nbrBitsParCouleur = lc.nbrBitsParCouleur;
	}
	
	public ListeCouleurs(Color c) {
		this.nbrCouleurs = 1;
		valeur = c.getRGB();
	}
	
	public ListeCouleurs(int nbrCouleurs, int no) {
		this.nbrCouleurs = nbrCouleurs;
		valeur = 0;
		int msk = 0;
		nbrBitsParCouleur = 24 / nbrCouleurs;
		for (int i=0; i<nbrBitsParCouleur; i++) msk = (msk<<1) | 1;
		masqueCouleur = msk;
		ajouterCouleur(no);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ListeCouleurs) {
			return valeur==((ListeCouleurs) obj).valeur;
		} else return false;
	}
	
	public void ajouterCouleur(int noCouleur) {
		valeur = valeur | 1 << noCouleur;
	}
	
	public void retirerCouleur(int noCouleur) {
		valeur = valeur & ~(1<<noCouleur);
	}
	
	public Color getCouleurs() {
		if (nbrCouleurs==1) return new Color(valeur);
		else return getCouleursSansMélange();
	}
		
	public Color getCouleursAvecMélange() {
		int rgb = 0;
		for (int i=0; i<nbrCouleurs; i++) {
			//System.out.println("1=>"+Integer.toHexString(rgb));
			if ((valeur & (1<<i))!=0) rgb = rgb | (masqueCouleur << (i *nbrBitsParCouleur));
		}
		return new Color(rgb);
	}
	
	public Color getCouleursSansMélange() {
		for (int i=nbrCouleurs-1; i>=0; i--) {
			if ((valeur & (1<<i))!=0) return listeCouleursPréférées[i];
		}
		return Color.white;
	}
	
	public static void main (String args[]) {
		ListeCouleurs l = new ListeCouleurs(5);
		System.out.println("=>"+Integer.toHexString(l.getCouleurs().getRGB()));
		l.ajouterCouleur(1);
		System.out.println("=>"+Integer.toHexString(l.getCouleurs().getRGB()));
		l.retirerCouleur(1);
		System.out.println("=>"+Integer.toHexString(l.getCouleurs().getRGB()));
	}

}
