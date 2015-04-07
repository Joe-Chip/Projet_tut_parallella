package balayageK2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import balayageK2.awt.AppletDessin;
import balayageK2.awt.ListeCouleurs;
import balayageK2.awt.PanelCommande;
import balayageK2.awt.PanelCouleurs;
import balayageK2.awt.PanelDessin2D;
import balayageK2.awt.InDouble;
import balayageK2.awt.InInteger;
import balayageK2.awt.PanelDessin3D;
import balayageK2.awt.PanelHTML;
import balayageK2.awt.PanelTexte;

public abstract class CalculAncienMyrberg extends CalculBase {
	protected InDouble minX; // -2.0d
	protected InDouble maxX; //+2.0d
	protected InDouble pasX;//0.004d, 8);
	protected InDouble minY;
	protected InDouble maxY ;
	protected InInteger nbrIter;
	protected JLabel titre;
	protected JPanel contenu ;
	protected PanelDessin3D panelDessin;
	protected PanelCommande panelCommande;
	protected PanelCouleurs panelCouleurs;
	protected JPanel panel3D;
	protected JSlider sliderX;
	protected JSlider sliderY;
	protected JSlider sliderZ;
	protected JSlider sliderZoom;
	PanelTexte panelPerf;
	JTabbedPane tabbedPane;
	JPanel panelDessinCmd3D;

	public JRadioButton afficherCourbes;

	protected double mu;
	protected double y;
	protected int m;
	protected int nMax;
	
	public abstract void calculM ();
	public abstract void dessinerEquations(Graphics g, PanelDessin2D dessin);
	
	protected void initObjetsAWT() {
		minX = new InDouble("min Mu", 3.0d); // -2.0d
		maxX = new InDouble("max Mu", 3.678d); //+2.0d
		pasX = new InDouble("pas Mu", 0.001d, 8);//0.004d, 8);
		minY = new InDouble("min Y", 0.27d);
		maxY = new InDouble("max Y", 0.73d);
		nbrIter = new InInteger("Iterations", 500);
		contenu = new JPanel();
		panelDessin = new PanelDessin3D();
		panelCouleurs = new PanelCouleurs(panelDessin);
		afficherCourbes = new JRadioButton("afficher Courbes", true);
	}

	public void getHashtableCalcul(Hashtable<String, Object> initialisation) {
	}
	
	protected void putHashtableCalcul(Hashtable<String, Object> initialisation) {
	}
	
	public void ajouterPoint(double a, double b, ListeCouleurs c) {
		panelDessin.ajouterPoint(a, b, c);
	}
	
	public void checkParam() {
		minX.checkVal();
		maxX.checkVal();
		pasX.checkVal();
		if (pasX.getVal()==0.0) {
			pasX.setVal((maxX.getVal()-minX.getVal())/panelDessin.getWidth());
		}
		minY.checkVal();
		maxY.checkVal();
		//pasY.checkVal();
		//epsilon.checkVal();
		nbrIter.checkVal();
	}
	
	public static long ctrCalculs;
	public void run() {
		Date dateDebut = new Date();
		long ctrAppel = 0l;
		ctrCalculs = 0;
		checkParam();
		panelCommande.enleverBoutonGo();
		panelDessin.raz();
		panelDessin.redessiner();
		//panelTexte.raz();
		nMax = nbrIter.getVal();
		arrêtRunner = false;
		y=0.08d;
		
		boucleFor: for (double mu=minX.getVal(); 
			mu<=maxX.getVal(); 
			mu=mu+pasX.getVal()) {
			ctrAppel++;
			this.mu = mu;
			calcul();
			if (arrêtRunner) break boucleFor;
			if (panelPerf!=null) {
				panelPerf.raz();
				double nbrAppelsRestants = (maxX.getVal()-mu)/pasX.getVal();
				panelPerf.print(AppletDessin.textePerf(dateDebut, ctrAppel, ctrCalculs, nbrAppelsRestants));
			}
			panelDessin.repaint();
		}
		if (afficherCourbes.isSelected())
			panelDessin.dessinerLesAxes(panelDessin.getGraphics());
		String txtPerf = AppletDessin.textePerf(dateDebut, ctrAppel, ctrCalculs, 0.0);
		if (panelPerf!=null) {
			panelPerf.raz();
			panelPerf.print(txtPerf);
		}
		System.out.println(txtPerf);
		panelCommande.ajouterBoutonGo();
	}

	public void calcul() {
		// initialisation itération 0
		for (int m=0; m<300; m++) {
				calculM();
		}
		for (int m=0; m<500; m++) {
			double yprécédent = y;
			calculM();
			panelDessin.ajouterPoint3D(mu, y, yprécédent, new ListeCouleurs(Color.green));
			panelDessin.ajouterPoint3D(mu, y, y, new ListeCouleurs(Color.red));
		}
	}


	public PanelDessin2D créerDessin() {
		return new PanelDessin2D();
	}
	
	public String getNomFonction (){ 
		return "?";
		}

	public JPanel getPanel(AppletDessin applet) {		
		this.applet = applet;
		JPanel panelParam = new JPanel();
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(2);
		gridLayout.setHgap(2);
		gridLayout.setVgap(2);
		gridLayout.setColumns(4);
		panelParam.setLayout(gridLayout);
		panelParam.setSize(800, 40);
		panelParam.add(minX);
		panelParam.add(maxX);
		panelParam.add(pasX);
		//panelParam.add(epsilon);
		panelParam.add(nbrIter);
		panelParam.add(minY);
		panelParam.add(maxY);
		//panelParam.add(pasY);
		

		panel3D = new JPanel();
		panel3D.setLayout(new GridLayout(4, 2));
		sliderX = new JSlider(JSlider.VERTICAL, 0, 180, 0);
		sliderY = new JSlider(JSlider.VERTICAL, 0, 180, 0);
		sliderZ = new JSlider(JSlider.VERTICAL, 0, 180, 0);
		sliderZoom = new JSlider(JSlider.VERTICAL, -40, +40, 0);
		panel3D.add(sliderX);
		panel3D.add(new JLabel("X"));
		panel3D.add(sliderY);
		panel3D.add(new JLabel("Y"));
		panel3D.add(sliderZ);
		panel3D.add(new JLabel("Z"));
		panel3D.add(sliderZoom);
		panel3D.add(new JLabel("%"));
		sliderX.addChangeListener(this);
		sliderY.addChangeListener(this);
		sliderZ.addChangeListener(this);
		sliderZoom.addChangeListener(this);
		
		
		JPanel panelComplet = new JPanel();
		BorderLayout bl = new BorderLayout();
		panelComplet.setLayout(new BorderLayout());
		JPanel pt = new JPanel();
		pt.setLayout(bl);
		titre.setFont(new Font("Serif", Font.BOLD, 36));
		titre.setForeground(Color.blue);
		bl.setVgap(5);
		pt.add(titre, BorderLayout.NORTH);
		pt.add(new JLabel(" "), BorderLayout.SOUTH);
		panelComplet.add(pt, BorderLayout.NORTH);
		panelCouleurs.boutonChoixNbreCycles.setSelectedIndex(3);// pour 4 cycles max
		JPanel bas = new JPanel();
		bas.setLayout(new BorderLayout());
		//bas.add(panelCouleurs, BorderLayout.WEST);
		JPanel panelContenu = new JPanel();
		panelContenu.setLayout(new BorderLayout());
		panelDessin.setParametres(pasX, minX, maxX, minY, maxY, afficherCourbes, this);
		panelDessinCmd3D= new JPanel();
		panelDessinCmd3D.setLayout(new BorderLayout());
		panelDessinCmd3D.add(panelDessin, BorderLayout.CENTER);
		panelDessinCmd3D.add(panel3D, BorderLayout.EAST);
		panelCommande = new PanelCommande(applet.boutonMainHTML);
		panelCommande.addActionListener(this);
		bas.add(panelCommande, BorderLayout.CENTER);
		panelContenu.add(panelParam, BorderLayout.NORTH);
		tabbedPane = new JTabbedPane();
		tabbedPane.insertTab("Dessin", null, panelDessinCmd3D, null, 0);
		String nomFichHelp = "HTML/"+this.getClass().getName()+".html";
		if (ClassLoader.getSystemResource(nomFichHelp)!=null) 
			tabbedPane.insertTab("Aide", null, new PanelHTML(nomFichHelp), null, 1);

		if (AppletDessin.avecPerf) {
			panelPerf = new PanelTexte();
			String[] lst = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			for (int i=0; i<lst.length; i++) if (lst[i].startsWith("Mono")) panelPerf.setFont(new Font(lst[i], Font.BOLD, 16));
			tabbedPane.addTab("Perf", panelPerf);
			//System.out.println(panelPerf.getFont().getFontName());
		}
		tabbedPane.addChangeListener(this);
		panelContenu.add(tabbedPane, BorderLayout.CENTER);
		panelContenu.add(bas, BorderLayout.SOUTH);
		panelComplet.add(panelContenu, BorderLayout.CENTER);
		return panelComplet;
	}

	public void readData (BufferedReader br) {
		try {
			panelDessin.raz();
			panelDessin.readData(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readParam(BufferedReader bf) throws NumberFormatException, IOException {
    	minX.setVal(Double.parseDouble(bf.readLine()));
    	maxX.setVal(Double.parseDouble(bf.readLine()));
    	pasX.setVal(Double.parseDouble(bf.readLine()));
    	minY.setVal(Double.parseDouble(bf.readLine()));
    	maxY.setVal(Double.parseDouble(bf.readLine()));
    	//pasY.setVal(Double.parseDouble(bf.readLine()));
    	//epsilon.setVal(Double.parseDouble(bf.readLine()));
    	nbrIter.setVal(Integer.parseInt(bf.readLine()));
	}

	public void saveParam(BufferedWriter bw) throws IOException {
    	bw.write(""+minX.getVal()); bw.newLine();
    	bw.write(""+maxX.getVal()); bw.newLine();
    	bw.write(""+pasX.getVal()); bw.newLine();
    	bw.write(""+minY.getVal()); bw.newLine();
    	bw.write(""+maxY.getVal()); bw.newLine();
    	//bw.write(""+pasY.getVal()); bw.newLine();
    	//bw.write(""+epsilon.getVal()); bw.newLine();
    	bw.write(""+nbrIter.getVal()); bw.newLine();
    	//bw.write(""+afficherCourbes.isSelected()); bw.newLine();
    	//bw.write(""+panelCouleurs.cycleMax);  bw.newLine();
	}

	public void saveData() {
		String nomDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		File f = new File("Data_"+ nomDate +".data");
		try {
			FileWriter fout = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fout);
			bw.write(this.getClass().getName());bw.newLine();
			saveParam(bw);
			panelDessin.saveData(bw);
			bw.flush();
			bw.close();
			System.out.println("Fichier "+f.getAbsolutePath()+" créé !");
		} catch (IOException e) {
			System.out.println("Erreur IO sur Fichier "+f.getAbsolutePath()+" !");
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		//System.out.println("Action Performed => "+arg0.getActionCommand());
		if (arg0.getSource()==afficherCourbes) {
			panelDessin.redessiner();
		} else super.actionPerformed(arg0);
	}
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==sliderX || e.getSource()==sliderY || e.getSource()==sliderZ  || e.getSource()==sliderZoom) {
			double angleX = Math.toRadians((double)sliderX.getValue());
			double angleY = Math.toRadians((double)sliderY.getValue());
			double angleZ = Math.toRadians((double)sliderZ.getValue());
			panelDessin.zoom = Math.pow(2.0, ((double)sliderZoom.getValue())/10.0);
			//System.out.println("==>"+dessin.panelDessin.zoom+" "+dessin.panelDessin.deplX);
			panelDessin.rotate(angleX, angleY, angleZ);
		} else if (e.getSource()==tabbedPane) {
			if (tabbedPane.getSelectedComponent()==panelDessinCmd3D)
				panelDessin.setDoubleBufferVisible();
			else panelDessin.setDoubleBufferInvisible();
		}
	}
	

}
