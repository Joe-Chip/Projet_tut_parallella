package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ecom.Collection;
import ecom.Primitive;
import ecom.Struct;
import ecom.Type;
import exception.CircularReference;
import exception.MultipleDeclaration;

public class Parser {

	Document doc;

	public Parser(String path) throws FileNotFoundException,
			ParserConfigurationException, IOException, SAXException,
			SAXParseException {
		File xml = new File(path);
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
		String name;
		NodeList objects = doc.getElementsByTagName("object");
		for (int o = 0; o < objects.getLength(); o++) {
			Node node = objects.item(o);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element object = (Element) node;
				name = object.getAttribute("name");
				Struct.namedStruct(name);
			}
		}
		for (int o = 0; o < objects.getLength(); o++) {
			Node node = objects.item(o);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element object = (Element) node;
				name = object.getAttribute("name");
				Struct s = Struct.namedStruct(name);
				NodeList fields = object.getElementsByTagName("field");
				for (int f = 0; f < fields.getLength(); f++) {
					node = fields.item(f);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element field = (Element) node;
						name = field.getAttribute("name");
						s.addField(name, nodeToType(field));
					}
				}
			}
		}
		for (Entry<String, Struct> s : Struct.objects.entrySet()) {
			System.out.println(s.getValue().prefix());
		}
		for (Entry<String, Struct> s : Struct.objects.entrySet()) {
			try {
				System.out.println(s.getValue().getHeader());
				System.out.println(s.getValue().getSize());
			} catch (CircularReference e) {
				System.out.println(e.getMessage());
				System.exit(0);
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

}
