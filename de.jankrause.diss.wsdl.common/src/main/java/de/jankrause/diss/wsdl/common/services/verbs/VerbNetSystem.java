package de.jankrause.diss.wsdl.common.services.verbs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import edu.stanford.nlp.ling.Word;

/**
 * Represents the Verb-Class-System VerbNet 3.0 developed by Martha Palmer, Anna
 * Korhonen and Ted Briscoe.
 * 
 * It is available at http://verbs.colorado.edu/~mpalmer/projects/verbnet.html.
 * 
 * @author Jan Christian Krause
 * 
 */
public class VerbNetSystem {

	private static final Logger logger = Logger.getLogger(VerbNetSystem.class.getName());

	private List<Document> verbNetXmlDocuments = new ArrayList<Document>();

	private static final VerbNetSystem instance = new VerbNetSystem();

	/**
	 * The constructor
	 */
	private VerbNetSystem() {

	}

	public static VerbNetSystem getInstance() {
		return instance;
	}

	/**
	 * Initializes this query service for VerbNet.
	 * 
	 * @param verbNetFiles
	 *            The array of VerbNet XML-files
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * 
	 * @see DocumentBuilder#parse(String) (for {@link SAXException})
	 * @see DocumentBuilder#parse(String) (for {@link IOException})
	 * @see DocumentBuilderFactory#newDocumentBuilder() (for
	 *      {@link ParserConfigurationException})
	 */
	public void initialize(File[] verbNetFiles) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse all given files.
		for (File verbNetFile : verbNetFiles) {
			if (verbNetFile.getAbsolutePath().endsWith(".xml")) {
				Document verbNetXmlDocument = builder.parse(verbNetFile.getAbsolutePath());
				verbNetXmlDocuments.add(verbNetXmlDocument);
			}
		}
	}

	/**
	 * Initializes this query service for VerbNet.
	 * 
	 * @param verbNetFiles
	 *            The array of VerbNet XML-files
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * 
	 * @see DocumentBuilder#parse(String) (for {@link SAXException})
	 * @see DocumentBuilder#parse(String) (for {@link IOException})
	 * @see DocumentBuilderFactory#newDocumentBuilder() (for
	 *      {@link ParserConfigurationException})
	 */
	public void initialize(InputStream[] verbNetFiles) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse all given files.
		for (InputStream verbNetFile : verbNetFiles) {
			Document verbNetXmlDocument = builder.parse(verbNetFile);
			verbNetXmlDocuments.add(verbNetXmlDocument);
		}
	}

	/**
	 * Returns the XML-{@link Node} "VNCLASS" containing the id of the
	 * verb-class with <code>{@link Word} word</code>.
	 * 
	 * @param word
	 *            The word to get verb-class of
	 * @return The {@link Set} of all matching XML-{@link Node}s "VNCLASS"
	 * @throws XPathExpressionException
	 *             If an error during querying the VerbNet-XML-documents occurs
	 * 
	 * @see XPath#compile(String) (for {@link XPathExpressionException})
	 * @see XPathExpression#evaluate(Object, javax.xml.namespace.QName) (for
	 *      {@link XPathExpressionException})
	 */
	// TODO: Refactore the return type also in the WSDLAnalyst-project (use
	// Set<Node>
	// instead of Node)
	private Set<Node> getVnClassNodeByWord(String word) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("/VNCLASS/@ID[count(//MEMBER[@name='" + word + "']) > 0]");
		Set<Node> matchingNodes = new HashSet<Node>();

		for (Document verbNetXmlDocument : verbNetXmlDocuments) {
			Node vnClassNode = (Node) expr.evaluate(verbNetXmlDocument, XPathConstants.NODE);

			if (vnClassNode != null) {
				matchingNodes.add(vnClassNode);
			}
		}

		return matchingNodes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> queryVerbClassByWord(String word) {
		List<String> verbClasses = new ArrayList<String>();

		try {
			// Query all documents
			Set<Node> vnClassNodes = getVnClassNodeByWord(word);

			for (Node vnClassNode : vnClassNodes) {
				verbClasses.add(vnClassNode.getNodeValue());
			}

			return verbClasses;
		} catch (XPathExpressionException xpathEx) {
			logger.log(Level.SEVERE, "Could not query VerbNet 3.0 for word " + String.valueOf(word), xpathEx);

			throw new RuntimeException(xpathEx.getLocalizedMessage());
		}
	}

}
