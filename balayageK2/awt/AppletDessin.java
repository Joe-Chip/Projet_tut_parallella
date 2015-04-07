package balayageK2.awt;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import balayageK2.FonctionBalayage;
import balayageK2.rmi.ServeurPrincipalImpl;

public class AppletDessin extends JApplet implements HyperlinkListener, ActionListener {
	public static boolean avecPerf = true;
	public static boolean avecTableCache = false;
	private static final long serialVersionUID = 1L;
	PanelHTML panelHTML = new PanelHTML("HTML/index.html");
	FonctionBalayage fonctionCourante;
	public JButton boutonMainHTML = new JButton("Menu Principal");
	JPanel panelCalcul;
	public ServeurPrincipalImpl serveurPrincipal;
	
	public AppletDessin() {
	}

	public void init() {
		panelHTML.addHyperlinkListener((HyperlinkListener)this);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelHTML, BorderLayout.CENTER);
		validate(); 
		boutonMainHTML.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==boutonMainHTML) {
			fonctionCourante.stopCalcul();
			getContentPane().removeAll();
			getContentPane().add(panelHTML, BorderLayout.CENTER);
			validate();
			repaint();
		} 
	}
	
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			//String nom = "balayageK2.fonctions."+evt.getURL().toString().substring(7); // 7 pour sauter "file://"
			//placerRunner(nom);
			String opération = evt.getDescription().substring(0,evt.getDescription().indexOf(':'));
			String parametre = evt.getDescription().substring(evt.getDescription().indexOf(':')+1);
			if (opération.equals("restaure")) restaureData();
			else if (opération.equals("calcul")) {
				miseEnPlace(parametre);
				//System.out.println("==> lancement de "+parametre);
			} else System.out.println("protocole "+opération+" non traité");
		}
	}
	
	public void restaureData() {
		try {
			JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "DATA", "data");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		       File f = chooser.getSelectedFile();
		       FileReader fin = new FileReader(f);
		       BufferedReader bf = new BufferedReader(fin);
		       String nomClasse = bf.readLine();
		       miseEnPlace(nomClasse, bf);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void miseEnPlace(String nomClasse, BufferedReader bf) {
		try {
			Class<?> classeFonction = Class.forName(nomClasse);
			Constructor<FonctionBalayage> constructeur = null;
			Class<?>[] pf = { BufferedReader.class };
			Object[] pe = { bf };
			try {
				constructeur = (Constructor<FonctionBalayage>) classeFonction.getConstructor(pf);
				fonctionCourante = constructeur.newInstance(pe);
				//getContentPane().remove(panelHTML);
				//System.out.println("b1==>"+getContentPane().getComponentCount());
				getContentPane().removeAll();
				//System.out.println("b2==>"+getContentPane().getComponentCount());
	 			panelCalcul = fonctionCourante.getPanel(this);
				getContentPane().add(panelCalcul, BorderLayout.CENTER);
				validate();
				fonctionCourante.readData(bf);
				repaint();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			System.out.println("Erreur, classe non trouvée : "+nomClasse);
		}

	}

	@SuppressWarnings("unchecked")
	public void miseEnPlace(String nomClasse) {
		try {
			Class<?> classeFonction = Class.forName(nomClasse);
			Constructor<FonctionBalayage> constructeur = null;
			try {
				constructeur = (Constructor<FonctionBalayage>) classeFonction.getConstructor(new Class[0]);
				fonctionCourante = constructeur.newInstance(new Object[0]);
				//System.out.println("a1==>"+getContentPane().getComponentCount());
				getContentPane().removeAll();
				//System.out.println("a2==>"+getContentPane().getComponentCount());
				panelCalcul = fonctionCourante.getPanel(this);
				getContentPane().add(panelCalcul, BorderLayout.CENTER);
				validate();
				repaint();
				//System.out.println("Mise en place AppletDessin "+nomClasse);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			System.out.println("Erreur, classe non trouvée : "+nomClasse);
		}

	}
	
	public BufferedImage getImage(){
		   int width =  this.getWidth();
		   int height = this.getHeight();
		   BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		   Graphics g = image.getGraphics();
		   paintComponents(g);
		   g.dispose();
		   return image;
		}

	public void saveImage() throws IOException {
		BufferedImage bi = getImage();
		String nomDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		File f = new File("Image_"+ nomDate +".jpg");
		ImageIO.write(bi, "jpg", f);
		System.out.println("Fichier "+f.getAbsolutePath()+" créé !");
		/*
		if (!panelTexte.getTexte().equals("")) {
			f = new File("Image_"+ nomDate +".txt");
			FileWriter fout = new FileWriter(f);
			fout.write(panelTexte.getTexte());
			fout.close();
			System.out.println("Fichier "+f.getAbsolutePath()+" créé !");
		}
		*/
	}

	public static String textePerf(
			Date dateDebut,
			double ctrAppel,
			double ctrCalculs,
			double nbrAppelsRestants) {
		StringBuffer sb = new StringBuffer();
		Date dateFin = new Date();
		double durée = (double) (dateFin.getTime()-dateDebut.getTime());
		double duréeAppel = durée/((double)ctrAppel);
		String lf = System.getProperty("line.separator");
		sb.append("Nbr appels        : "+ctrAppel);
		sb.append(lf);
		sb.append("Nbr calculs       : "+ctrCalculs);
		sb.append(lf);
		sb.append("Durée             : "+formatDurée(durée));
		sb.append(lf);
		sb.append("Durée d'un appel  : "+formatDurée(duréeAppel));
		sb.append(lf);
		sb.append("Durée d'un calcul : "+format(((1000000.0*durée)/((double)ctrCalculs)),3)+" ns");
		sb.append(lf);
		sb.append("Ctr2D             : "+Coordonnees2D.ctr2D);
		sb.append(lf);
		sb.append("Ctr3D             : "+Coordonnees3D.ctr3D);
		if (((int)nbrAppelsRestants)>0) {
			sb.append(lf);
			sb.append(lf);
			sb.append("Nombre appels restants : "+((int)nbrAppelsRestants));
			sb.append(lf);
			sb.append("Estimation Temps restant : "+formatDurée(nbrAppelsRestants*duréeAppel));
		}
		return sb.toString();
	}
	
	public static String textePerfCompteurCyclesHV(
			Date dateDebut,
			int ctrV,
			int ctrH,
			int ctrD,
			int ctrNouv,
			double ctrAppel,
			double nbrAppelsRestants) {
		StringBuffer sb = new StringBuffer();
		Date dateFin = new Date();
		double durée = (double) (dateFin.getTime()-dateDebut.getTime());
		double duréeAppel = durée/((double)ctrAppel);
		String lf = System.getProperty("line.separator");
		sb.append("Nbr cycles V      : "+ctrV);
		sb.append(lf);
		sb.append("Nbr cycles H      : "+ctrH);
		sb.append(lf);
		sb.append("Nbr cycles D      : "+ctrD);
		sb.append(lf);
		sb.append("Nbr cycles Nouv      : "+ctrNouv);
		sb.append(lf);
		sb.append("Durée             : "+formatDurée(durée));
		sb.append(lf);
		sb.append("Ctr2D             : "+Coordonnees2D.ctr2D);
		sb.append(lf);
		sb.append("Ctr3D             : "+Coordonnees3D.ctr3D);
		if (((int)nbrAppelsRestants)>0) {
			sb.append(lf);
			sb.append(lf);
			sb.append("Nombre appels restants : "+((int)nbrAppelsRestants));
			sb.append(lf);
			sb.append("Estimation Temps restant : "+formatDurée(nbrAppelsRestants*duréeAppel));
		}
		return sb.toString();
	}
	
	public static String formatDurée(double val) {
		long duréeSec = Math.round(val);
		long ms = duréeSec % 1000;
		long sec = (duréeSec/1000) % 60;
		long mn = (duréeSec/60000)%60;
		long h = duréeSec/3600000;
		StringBuffer sb = new StringBuffer();
		if (h>0) {
			sb.append(h);
			sb.append("h ");
		}
		if (mn!=0) {
			sb.append(mn);
			sb.append("' ");
		}
		if (sec!=0) {
			sb.append(sec);
			sb.append('"');
			sb.append(' ');
		}
		if (ms!=0) {
			sb.append(ms);
			sb.append("ms");
		}
		return sb.toString();
	}

	public static String format(double val, int décimale) {
		for (int i=0; i<décimale; i++) val = val * 10.0;
		long longVal = (long) val;
		val = (double) longVal;
		for (int i=0; i<décimale; i++) val = val / 10.0;
		return ""+val;
	}
	

}
