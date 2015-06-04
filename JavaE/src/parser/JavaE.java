package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
			return new Collection(nodeToType(collection),
					Integer.parseInt(collection.getAttribute("size")));
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

	public void write() throws CircularReference {
		FileWriter fw;
		BufferedWriter bw;
		try {
			System.out.println("Starting generating E" + this.name + ".java");
			fw = new FileWriter(path + "/E" + this.name + ".java", false);
			bw = new BufferedWriter(fw);
			bw.write(this.generateClass());
			bw.flush();
			bw.close();
			System.out.println("E" + this.name
					+ ".java was succesfully generated");
			System.out.println("Starting generating s" + name.toLowerCase()
					+ ".h");
			fw = new FileWriter(path + "/s" + this.name.toLowerCase() + ".h",
					false);
			bw = new BufferedWriter(fw);
			bw.write(this.generateHeader());
			bw.flush();
			bw.close();
			System.out.println("s" + this.name.toLowerCase()
					+ ".h was succesfully generated");
			System.out.println("Starting generating j" + name.toLowerCase()
					+ ".h");
			fw = new FileWriter(path + "/j" + this.name.toLowerCase() + ".h",
					false);
			bw = new BufferedWriter(fw);
			bw.write(this.generateJNI());
			bw.flush();
			bw.close();
			System.out.println("j" + this.name.toLowerCase()
					+ ".h was succesfully generated");
			System.out.println("Starting generating h"
					+ this.name.toLowerCase() + ".c");
			fw = new FileWriter(path + "/h" + this.name.toLowerCase() + ".c",
					false);
			bw = new BufferedWriter(fw);
			bw.write(this.generateLibrary());
			bw.flush();
			bw.close();
			System.out.println("h" + this.name.toLowerCase()
					+ ".c was succesfully generated");
			System.out.println("Starting generating modules \".c\"");
			for (Entry<String, Module> e : Module.modules.entrySet()) {
				Module mod = e.getValue();
				System.out.println("Starting generating "
						+ mod.getName().toLowerCase() + ".c");
				fw = new FileWriter(mod.getPath() + "/"
						+ mod.getName().toLowerCase() + ".c", false);
				bw = new BufferedWriter(fw);
				bw.write(this.generateModule(mod));
				bw.flush();
				bw.close();
				System.out.println(mod.getName().toLowerCase()
						+ ".c was succesfully generated");
			}
			System.out.println("modules \".c\" were succesfully generated");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}

	private String generateClass() {
		StringBuilder dotJava = new StringBuilder();
		dotJava.append("package epiphany;\n");
		dotJava.append("\n");
		for (Entry<String, Struct> e : Struct.objects.entrySet())
			dotJava.append("import "
					+ e.getValue().getFullyQualifiedName().substring(1) + ";\n");
		dotJava.append("\n");
		dotJava.append("public class E" + this.name + " {\n");
		dotJava.append("\n");
		dotJava.append("\tprivate static E" + this.name + " instance;\n");
		dotJava.append("\tstatic {\n");
		dotJava.append("\t\tinstance = null;\n");
		dotJava.append("\t\tSystem.loadLibrary(\"" + path + "/"
				+ this.name.toLowerCase() + ".so\");\n");
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
		for (Entry<String, Struct> e : Struct.objects.entrySet()) {
			Struct struct = e.getValue();
			dotJava.append("\tprivate final Class<" + struct.base() + "> "
					+ struct.base().toLowerCase() + " = " + struct.base()
					+ ".class;\n");
		}
		dotJava.append("\n");
		dotJava.append("\tprivate native void einit();\n");
		dotJava.append("\tprivate native void eclose();\n");
		dotJava.append("\n");
		dotJava.append("\tpublic void close() {\n");
		dotJava.append("\t\tthis.eclose();\n");
		dotJava.append("\t\tinstance = null;\n");
		dotJava.append("\t}\n");
		dotJava.append("\n");
		for (Entry<String, Module> e : Module.modules.entrySet()) {
			String name = e.getKey();
			Module mod = e.getValue();
			dotJava.append("\tpublic native int call" + name + "(");
			boolean start = true;
			for (Entry<String, Struct> a : mod.getArgs()) {
				if (start) {
					dotJava.append(a.getValue().base() + " " + a.getKey());
					start = false;
				} else
					dotJava.append(", " + a.getValue().base() + " "
							+ a.getKey());
			}
			dotJava.append(");\n");
			dotJava.append("\tpublic native int join" + name + "(int id");
			for (Entry<String, Struct> r : mod.getRess()) {
				dotJava.append(", " + r.getValue().base() + " " + r.getKey());
			}
			dotJava.append(");\n");
			dotJava.append("\n");
		}
		dotJava.append("}");
		return dotJava.toString();
	}

	private String generateHeader() throws CircularReference {
		StringBuilder dotH = new StringBuilder();
		dotH.append("#ifndef E" + name.toUpperCase() + "_H\n");
		dotH.append("#define E" + name.toUpperCase() + "_H\n");
		dotH.append("\n");
		for (Entry<String, Struct> e : Struct.objects.entrySet()) {
			dotH.append(e.getValue().getDeclare());
			dotH.append("\n");
		}
		dotH.append("\n");
		for (Entry<String, Struct> e : Struct.objects.entrySet()) {
			dotH.append(e.getValue().getStruct());
			dotH.append("\n\n");
		}
		dotH.append("#endif");
		return dotH.toString();
	}

	private String generateJNI() {
		StringBuilder dotH = new StringBuilder();
		dotH.append("#include <jni.h>\n");
		dotH.append("\n");
		dotH.append("#ifndef _Included_" + this.name + "\n");
		dotH.append("#define _Included_" + this.name + "\n");
		dotH.append("#ifdef __cplusplus\n");
		dotH.append("extern \"C\" {\n");
		dotH.append("#endif\n");
		dotH.append("\n");
		dotH.append("JNIEXPORT void JNICALL Java_" + this.name + "_einit\n");
		dotH.append("\t(JNIEnv * env, jobject this);\n\n");
		dotH.append("JNIEXPORT void JNICALL Java_" + this.name + "_eclose\n");
		dotH.append("\t(JNIEnv * env, jobject this);\n\n");
		for (Entry<String, Module> e : Module.modules.entrySet()) {
			dotH.append(e.getValue().getCallCSignature(this.name) + ";\n\n");
			dotH.append(e.getValue().getJoinCSignature(this.name) + ";\n\n");
		}
		dotH.append("#ifdef __cplusplus\n");
		dotH.append("}\n");
		dotH.append("#endif\n");
		dotH.append("#endif");
		return dotH.toString();
	}

	private String generateLibrary() throws CircularReference {
		StringBuilder dotC = new StringBuilder();
		dotC.append("#include \"s" + this.name.toLowerCase() + ".h\"\n");
		dotC.append("#include \"j" + this.name.toLowerCase() + ".h\"\n");
		dotC.append("#include \"e-hal.h\"\n");
		dotC.append("\n");
		dotC.append("e_epiphany_t ** cores;\n");
		dotC.append("e_platform_t platform;\n");
		dotC.append("\n");
		for (Entry<String, Struct> e : Struct.objects.entrySet()) {
			Struct struct = e.getValue();
			for (Entry<String, Type> f : struct.getFields()) {
				String field = f.getKey();
				dotC.append("jfieldID " + struct.base().toLowerCase() + "_"
						+ field + "ID;\n");
			}
			dotC.append("\n");
		}
		dotC.append("\n");
		dotC.append("JNIEXPORT void JNICALL Java_" + this.name + "_einit\n");
		dotC.append("\t(JNIEnv * env, jobject this) {\n");
		dotC.append("\te_init(NULL);\n");
		dotC.append("\te_reset_system();\n");
		dotC.append("\te_get_platform_info(&platform);\n");
		dotC.append("\tcores = malloc(platform.rows*platform.cols*sizeof(e_epiphany_t *));\n");
		dotC.append("\tint r, c;\n");
		dotC.append("\tfor (r = 0; r < platform.rows; r++) {\n");
		dotC.append("\t\tfor (c = 0; c < platform.cols; c++) {\n");
		dotC.append("\t\t\tcores[r*platform.cols+c] = NULL;\n");
		dotC.append("\t\t}\n");
		dotC.append("\t}\n");
		dotC.append("\tjclass ejava = (*env)->GetObjectClass(env, this);\n\n");
		for (Entry<String, Struct> e : Struct.objects.entrySet()) {
			Struct struct = e.getValue();
			dotC.append("\tjfieldID " + struct.base().toLowerCase()
					+ "ID = (*env)->GetFieldID(env, ejava, \""
					+ struct.base().toLowerCase()
					+ "\", \"Ljava.lang.Class\");\n");
			dotC.append("\tjclass " + struct.base().toLowerCase()
					+ "Class = (jclass) (*env)->GetObjectField(env, this, "
					+ struct.base().toLowerCase() + "ID);\n");
			for (Entry<String, Type> f : struct.getFields()) {
				String field = f.getKey();
				Type type = f.getValue();
				dotC.append("\t" + struct.base().toLowerCase() + "_" + field
						+ "ID = (*env)->GetFieldID(env, "
						+ struct.base().toLowerCase() + "Class, \"" + field
						+ "\", \"" + type.getFullyQualifiedName() + "\");\n");
			}
			dotC.append("\n");
		}
		dotC.append("}\n\n");
		dotC.append("JNIEXPORT void JNICALL Java_" + this.name + "_eclose\n");
		dotC.append("\t(JNIEnv * env, jobject this) {\n");
		dotC.append("\tint r, c;\n");
		dotC.append("\tfor (r = 0; r < platform.rows; r++) {\n");
		dotC.append("\t\tfor (c = 0; c < platform.cols; c++) {\n");
		dotC.append("\t\t\tif (cores[r*platform.cols+c] != NULL) {\n");
		dotC.append("\t\t\t\te_close(cores[r*platform.cols+c]);\n");
		dotC.append("\t\t\t\tfree(cores[r*platform.cols+c]);\n");
		dotC.append("\t\t\t}\n");
		dotC.append("\t\t}\n");
		dotC.append("\t}\n");
		dotC.append("\tfree(cores);\n");
		dotC.append("\te_finalize();\n");
		dotC.append("}\n\n");
		for (Entry<String, Module> e : Module.modules.entrySet()) {
			Module mod = e.getValue();
			int address = 3001;
			dotC.append(mod.getJoinCSignature(this.name) + "; {\n");
			dotC.append("\tchar end = 0;\n");
			dotC.append("\tint r = id/platform.cols;\n");
			dotC.append("\tint c = id%platform.cols;\n");
			dotC.append("\te_read(cores[id], r, c, " + 3000 + ", &end, sizeof(char));\n");
			dotC.append("\tif (end) {\n");
			ArrayList<Entry<String,Struct>> ress = new ArrayList<Entry<String,Struct>>(mod.getRess());
			Collections.reverse(ress);
			for (Entry<String, Struct> r : ress) {
				String name = r.getKey();
				Struct struct = r.getValue();
				dotC.append("\t\t" + struct.base() + " s" + name + ";\n");
				dotC.append("\t\te_read(cores[id], r, c, " + address + ", &s" + name + ", sizeof(" + struct.base() + "));\n");
				dotC.append(struct.extract("\t\t", "s" + name, name, null));
				address += struct.getSize();
			}
			dotC.append("\t\treturn id;\n");
			dotC.append("\t}\n");
			dotC.append("\telse return -1;\n");
			dotC.append("}\n\n");
			dotC.append(mod.getCallCSignature(this.name) + " {\n");
			dotC.append("\tint r, c;\n");
			dotC.append("\tfor (r = 0; r < platform.rows; r++) {\n");
			dotC.append("\t\tfor (c = 0; c < platform.cols; c++) {\n");
			dotC.append("\t\t\tif (cores[r*platform.cols+c] == NULL) {\n");
			dotC.append("\t\t\t\tcores[r*platform.cols+c] = malloc(siseof(e_epiphany_t));\n");
			dotC.append("\t\t\t\te_open(cores[r*platform.cols+c], r, c, 1, 1);\n");
			dotC.append("\t\t\t\te_load(\"" + mod.getPath() + "/"
					+ mod.getName().toLowerCase()
					+ ".srec\", cores[r*platform.cols+c], r, c, FALSE);\n");
			ArrayList<Entry<String,Struct>> args = new ArrayList<Entry<String,Struct>>(mod.getArgs());
			Collections.reverse(args);
			for (Entry<String, Struct> a : args) {
				String name = a.getKey();
				Struct struct = a.getValue();
				dotC.append("\t\t\t\t" + struct.base() + " s" + name + ";\n");
				dotC.append(struct.fulfill("\t\t\t\t", "s" + name, name, null));
				dotC.append("\t\t\t\te_write(cores[r*platform.cols+c], r, c, " + address + ", &s" + name + ", sizeof(" + struct.base() + "));\n");
				address += struct.getSize();
			}
			dotC.append("\t\t\t\tchar end = 0;\n");
			dotC.append("\t\t\t\te_write(cores[r*platform.cols+c], r, c, 3000, &end, sizeof(char));\n");
			dotC.append("\t\t\t\te_start(cores[r*platform.cols+c], r, c);\n");
			dotC.append("\t\t\t\treturn r * platform.cols + c;\n");
			dotC.append("\t\t\t}\n");
			dotC.append("\t\t}\n");
			dotC.append("\t}\n");
			dotC.append("\treturn -1;\n");
			dotC.append("}\n\n");
		}
		return dotC.toString();
	}

	

	private String generateModule(Module m) {
		StringBuilder dotC = new StringBuilder();
		dotC.append("#include \"s" + this.name.toLowerCase() + ".h\"\n");
		dotC.append("#include \"e-lib.h\"\n");
		dotC.append("\n");
		for (Entry<String, Struct> e : m.getArgs()) {
			Struct s = e.getValue();
			dotC.append(s.base() + " " + e.getKey() + s.suffix()
					+ " __attribute__ ((section (“.data_bank3”)));\n");
		}
		for (Entry<String, Struct> e : m.getRess()) {
			Struct s = e.getValue();
			dotC.append(s.base() + " " + e.getKey() + s.suffix()
					+ " __attribute__ ((section (“.data_bank3”)));\n");
		}
		dotC.append("char end __attribute__ ((section (“.data_bank3”)));\n");
		return dotC.toString();
	}

}
