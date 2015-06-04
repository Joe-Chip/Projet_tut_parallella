package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import module.Module;
import module.type.Collection;
import module.type.Primitive;
import module.type.Struct;
import module.type.Type;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import exception.CircularReference;
import exception.MultipleDeclaration;

public class JavaE {

	private Document doc;
	private String path;
	private String name;

	public JavaE(String path, String name) throws FileNotFoundException,
			ParserConfigurationException, IOException, SAXException,
			SAXParseException {
		this.path = path;
		this.name = name.substring(0, 1).toUpperCase()
				+ name.substring(1).toLowerCase();
		File xml = new File(path + "/" + name + ".xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setValidating(true);
		dbFactory.setNamespaceAware(true);
		dbFactory.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		dBuilder.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException e) throws SAXException {
				System.out.println(e.getMessage());
			}

			public void error(SAXParseException e) throws SAXException {
				throw e;
			}

			public void fatalError(SAXParseException e) throws SAXException {
				throw e;
			}
		});

		this.doc = dBuilder.parse(xml);
		this.doc.getDocumentElement().normalize();
	}

	public void read() throws MultipleDeclaration {
		NodeList objects = doc.getElementsByTagName("object");
		for (int o = 0; o < objects.getLength(); o++) {
			Element object = (Element) objects.item(o);
			String name = object.getAttribute("name");
			String fQN = object.getAttribute("fqn");
			Struct struct = Struct.namedStruct(name, fQN);
			NodeList fields = object.getElementsByTagName("field");
			for (int f = 0; f < fields.getLength(); f++) {
				Element field = (Element) fields.item(f);
				name = field.getAttribute("name");
				struct.addField(name, nodeToType(field));
			}
		}
		NodeList modules = doc.getElementsByTagName("module");
		for (int m = 0; m < modules.getLength(); m++) {
			Element module = (Element) modules.item(m);
			String name = module.getAttribute("name");
			String path = module.getAttribute("path");
			Module mod = Module.namedModule(name, path);
			NodeList args = module.getElementsByTagName("argument");
			for (int a = 0; a < args.getLength(); a++) {
				Element arg = (Element) args.item(a);
				name = arg.getAttribute("name");
				String type = arg.getAttribute("type");
				mod.addArg(name, Struct.namedStruct(type));
			}
			NodeList ress = module.getElementsByTagName("result");
			for (int r = 0; r < ress.getLength(); r++) {
				Element res = (Element) ress.item(r);
				name = res.getAttribute("name");
				String type = res.getAttribute("type");
				mod.addRes(name, Struct.namedStruct(type));
			}
		}
	}

	private Type nodeToType(Element field) {
		NodeList type = field.getElementsByTagName("collection");
		if (type.getLength() == 1) {
			Element collection = (Element) type.item(0);
			return new Collection(collection.getAttribute("type"),
					nodeToType(collection), Integer.parseInt(collection
							.getAttribute("size")));
		}
		type = field.getElementsByTagName("primitive");
		if (type.getLength() == 1)
			return new Primitive(type.item(0).getTextContent());
		type = field.getElementsByTagName("type");
		if (type.getLength() == 1)
			return Struct.namedStruct(((Element) type.item(0))
					.getAttribute("object"));
		return null;
	}

	public void write() {
		FileWriter fw;
		BufferedWriter bw;
		try {
			System.out.println("Starting generating E" + name + ".java");
			fw = new FileWriter(path + "/E" + name + ".java", false);
			bw = new BufferedWriter(fw);
			bw.write(this.generateClass());
			bw.flush();
			bw.close();
			System.out.println("E" + name + ".java was succefuly generated");
			System.out.println("Starting generating e" + name.toLowerCase() + ".h");
			fw = new FileWriter(path + "/e" + name.toLowerCase() + ".h", false);
			bw = new BufferedWriter(fw);
			bw.write(this.generateHeader());
			bw.flush();
			bw.close();
			System.out.println("e" + name.toLowerCase() + ".h was succefuly generated");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	private String generateClass() {
		HashSet<Struct> objects = new HashSet<Struct>();
		
		StringBuilder dotJava = new StringBuilder(); 
		dotJava.append("package epiphany;\n");
		dotJava.append("\n");
		for (Entry<String,Module> e : Module.modules.entrySet())
			objects.addAll(e.getValue().getObjects());
		for (Struct object : objects)
			dotJava.append("import " + object.getFullyQualifiedName() + ";\n");
		dotJava.append("\n");
		dotJava.append("public class E" + this.name + " {\n");
		dotJava.append("\n");
		dotJava.append("\tprivate static E" + this.name + " instance;\n");
		dotJava.append("\tstatic {\n");
		dotJava.append("\t\tinstance = null;\n");
		dotJava.append("\t\tSystem.loadLibrary(\"" + path + "/" + this.name.toLowerCase() + ".so\");\n");
		dotJava.append("\t}\n");
		dotJava.append("\n");
		dotJava.append("\tpublic static E" + this.name + " open() {\n");
		dotJava.append("\t\tif (instance == null)\n");
		dotJava.append("\t\t\tinstance = new E" + this.name + "();\n");
		dotJava.append("\t\treturn instance;\n");
		dotJava.append("\t}\n");
		dotJava.append("\n");
		dotJava.append("\tprivate E" + this.name + "() {\n");
		dotJava.append("\t\tthis.einit();\n");
		dotJava.append("\t}\n");
		dotJava.append("\n");
		dotJava.append("\tprivate native void einit();\n");
		dotJava.append("\tprivate native void eclose();\n");
		dotJava.append("\n");
		dotJava.append("\tpublic void close() {\n");
		dotJava.append("\t\tthis.eclose();\n");
		dotJava.append("\t}\n");
		dotJava.append("\n");
		for (Entry<String,Module> e : Module.modules.entrySet()) {
			String name = e.getKey();
			Module mod = e.getValue();
			dotJava.append("\tpublic native int call" + name + "(");
			boolean start = true;
			for (Entry<String,Type> a : mod.getArgs()) {
				if (start) {
					dotJava.append(a.getValue().base() + " " + a.getKey());
					start = false;
				}
				else dotJava.append(", " + a.getValue().base() + " " + a.getKey());
			}
			dotJava.append(");\n");
			dotJava.append("\tpublic native int join" + name + "(int id");
			for (Entry<String,Type> r : mod.getRess()) {
				dotJava.append(", " + r.getValue().base() + " " + r.getKey());
			}
			dotJava.append(");\n");
			dotJava.append("\n");
		}
		dotJava.append("}");
		return dotJava.toString();
	}
	
	private String generateHeader() {
		StringBuilder dotH = new StringBuilder(); 
		return dotH.toString();
	}

}
