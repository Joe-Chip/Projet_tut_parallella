package balayageK2.awt;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PanelParametres extends JPanel {
	InDouble minX = new InDouble("min A", -1.0d); // -2.0d
	InDouble maxX = new InDouble("max A", +1.0d); //+2.0d
	InDouble pasX = new InDouble("pas A", 0.1d, 8);//0.004d, 8);
	InDouble minY = new InDouble("min B", -2.0d);
	InDouble maxY = new InDouble("max B", +2.0d);
	InDouble pasY = new InDouble("pas B", 0.004d, 8);
	InDouble epsilon = new InDouble("epsilon", 1E-10, 8);
	InInteger nbrIter = new InInteger("Iterations", 30);
	JComboBox lstChoix;
	JPanel contenu = new JPanel();
	JLabel titre = new JLabel();
	
	public JRadioButton afficherCourbes = new JRadioButton("afficher Courbes", true);
	
	private static final long serialVersionUID = 1L;

	protected PanelParametres() {}
	
	public PanelParametres(String[] lstChoixInit) {
		lstChoix = new JComboBox(lstChoixInit);
		initialize();
	}

	public void setTitre(String texte) {
		titre.setHorizontalTextPosition(JLabel.CENTER);
		titre.setText(texte);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	protected void initialize() {
		this.setLayout(new BorderLayout());
		add(titre, BorderLayout.NORTH);
		add(contenu, BorderLayout.CENTER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(2);
		gridLayout.setHgap(2);
		gridLayout.setVgap(2);
		gridLayout.setColumns(4);
		contenu.setLayout(gridLayout);
		contenu.setSize(800, 40);
		contenu.add(minX);
		contenu.add(maxX);
		contenu.add(pasX);
		contenu.add(epsilon);
		contenu.add(nbrIter);
		contenu.add(minY);
		contenu.add(maxY);
		contenu.add(pasY);
		JPanel jp = new JPanel();
		jp.add(lstChoix);
		contenu.add(jp);
		contenu.add(afficherCourbes);
	}

	public int getNbrIter() {return nbrIter.getVal();}
	
	public double getMinY() {return minY.getVal();}
	
	public void setMinY(double val) {minY.setVal(val);}
	
	public double getMaxY() {return maxY.getVal();}
	
	public void setMaxY(double val) {maxY.setVal(val);}
	
	public double getPasY() {return pasY.getVal();}
	
	public void setPasY(double val) {pasY.setVal(val);}
	
	public double getMinX() {return minX.getVal();}
	
	public void setMinX(double val) {minX.setVal(val);}
	
	public double getMaxX() {return maxX.getVal(); }
	
	public void setMaxX(double val) {maxX.setVal(val);}
	
	public double getPasX() {return pasX.getVal();}
	
	public void setPasX(double val) {pasX.setVal(val);}

	public double getEpsilon() {return epsilon.getVal();}
	
	public int getChoixValInit() { return lstChoix.getSelectedIndex();}
	
	/*
	public void addActionListener(ActionListener l) {
		minX.addActionListener(l);
		maxX.addActionListener(l);
		pasX.addActionListener(l);
		minY.addActionListener(l);
		maxY.addActionListener(l);
		pasY.addActionListener(l);
		epsilon.addActionListener(l);
		lstChoix.addActionListener(l);
		nbrIter.addActionListener(l);
		afficherCourbes.addActionListener(l);
	}
	*/
    public void saveData (BufferedWriter bw) throws IOException {
    	bw.write(""+minX.getVal()); bw.newLine();
    	bw.write(""+maxX.getVal()); bw.newLine();
    	bw.write(""+pasX.getVal()); bw.newLine();
    	bw.write(""+minY.getVal()); bw.newLine();
    	bw.write(""+maxY.getVal()); bw.newLine();
    	bw.write(""+pasY.getVal()); bw.newLine();
    	bw.write(""+epsilon.getVal()); bw.newLine();
    	bw.write(""+nbrIter.getVal()); bw.newLine();
    	bw.write(""+lstChoix.getSelectedIndex()); bw.newLine();
    	bw.write(""+afficherCourbes.isSelected()); bw.newLine();
    }
    
    public void readData (BufferedReader bf) throws IOException {
    	minX.setVal(Double.parseDouble(bf.readLine()));
    	maxX.setVal(Double.parseDouble(bf.readLine()));
    	pasX.setVal(Double.parseDouble(bf.readLine()));
    	minY.setVal(Double.parseDouble(bf.readLine()));
    	maxY.setVal(Double.parseDouble(bf.readLine()));
    	pasY.setVal(Double.parseDouble(bf.readLine()));
    	epsilon.setVal(Double.parseDouble(bf.readLine()));
    	nbrIter.setVal(Integer.parseInt(bf.readLine()));
    	lstChoix.setSelectedIndex(Integer.parseInt(bf.readLine()));   
    	afficherCourbes.setSelected(Boolean.parseBoolean(bf.readLine()));
    }
  
}
