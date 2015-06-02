package balayageK2.rmi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Configuration extends DefaultHandler {
	SAXParserFactory factory;
	Vector<Process> lstProcess;
	String ipMachineCourante;
	
	public Configuration () {
		factory = SAXParserFactory.newInstance();
		try {
			ipMachineCourante = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			ipMachineCourante = "localhost";
		}
	}
	
	public void créer(String nomFich) {
		System.out.println("nom fichier "+  nomFich);
		lstProcess = new Vector<Process>();
	
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( new File(nomFich), this ); 
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void startElement(String namespaceURI,
	        String sName, 
	        String qName, 
	        Attributes attrs)
	throws SAXException
	{
		System.out.println("startElement");
	  String eName = sName; 
	  if ("".equals(eName)) eName = qName;
	  if (eName.equals("serveur")) {
		  final String machine = attrs.getValue("machine");
		  String nbrThreadStr = attrs.getValue("nbrThread");
		  //System.out.println("==>"+nbrThreadStr+"<==");
		  int nbrThread=1;
		  if (nbrThreadStr!=null) nbrThread = Integer.parseInt(nbrThreadStr);
		  String cmd = attrs.getValue("exec");
		  try {
			  cmd = "rsh "+machine+" "+cmd+" "+ipMachineCourante+" "+((nbrThreadStr==null)?"1":nbrThread);
			  System.out.println("==>"+cmd);
			  final Process processus = Runtime.getRuntime().exec(cmd);
			  lstProcess.add(processus);
			  new Thread() {

				@Override
				public void run() {
					  InputStreamReader outExec = new InputStreamReader(processus.getInputStream());
					  try {
						  int c = outExec.read();
						  while (c!=-1) {
							  System.out.print((char) c);
							  c = outExec.read();
						  }
					  } catch (Exception e) {
						  System.out.println("Exception "+e.getMessage()+" dans thread output stream serveur "+machine);
					  }
				}
				  
			  }.start();
		} catch (IOException e) {
			System.out.println("**** Erreur ssh sur "+machine);
		}
	  }
	}

	public static void main(String argv[]) throws IOException	{
		//Configuration conf = new Configuration();
		//conf.créer(argv[0]);
		String ipMachineCourante = InetAddress.getLocalHost().getHostAddress();
		System.out.println("==>"+ipMachineCourante);
	}
}
