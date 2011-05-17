package de.akra.idocit.java.services;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.xml.sax.SAXException;

import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Parser for {@link Javadoc}. It converts a Javadoc to a list of {@link Documentation}s.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class JavadocParser
{
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(JavadocParser.class.getName());

	private static final String JAVADOC_TAG_PARAM = TagElement.TAG_PARAM + "\\s*";
	private static final String JAVADOC_TAG_RETURN = TagElement.TAG_RETURN + "\\s*";
	private static final String JAVADOC_TAG_THROWS = TagElement.TAG_THROWS + "\\s*";

	/**
	 * Converts the {@link Javadoc} to a list of {@link Documentation}s. The generated
	 * Documentations can then be used to attach them to the corresponding
	 * {@link SignatureElement} in the object structure of the method parameters.
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to parse.
	 * @return List of {@link Documentation}s. If <code>javadoc == null</code> then
	 *         {@link Collections#EMPTY_LIST} is returned.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static List<Documentation> parse(Javadoc javadoc) throws SAXException,
			IOException, ParserConfigurationException
	{
		if (javadoc == null)
		{
			return Collections.emptyList();
		}

		String html = extractHTMLTables(javadoc);
		return HTMLTableParser.convertJavadocToDocumentations(html);
	}

	/**
	 * Extracts the plain text out if the <code>javadoc</code> from all supported tags. As
	 * the iDocIt! format is written in HTML tables, the method extracts these tables and
	 * summarize it in a String.
	 * 
	 * @param javadoc
	 *            The {@link Javadoc} to read.
	 * @return The read text.
	 */
	@SuppressWarnings("unchecked")
	private static String extractHTMLTables(Javadoc javadoc)
	{
		StringBuffer html = new StringBuffer();
		for (TagElement tag : (List<TagElement>) javadoc.tags())
		{
			if (tag.getTagName() == null || tag.getTagName().matches(JAVADOC_TAG_RETURN))
			{
				html.append(readFragments((List<ASTNode>) tag.fragments(), 0));
			}
			else if (tag.getTagName().matches(JAVADOC_TAG_PARAM)
					|| tag.getTagName().matches(JAVADOC_TAG_THROWS))
			{
				html.append(readFragments((List<ASTNode>) tag.fragments(), 1));
			}
			else
			{
				logger.log(Level.WARNING, "Unknown Javadoc tag: " + tag.getTagName());
			}
		}
		return html.toString();
	}

	/**
	 * Extracts the plain text from the <code>fragments</code>.
	 * 
	 * @param fragments
	 *            The fragments to read.
	 * @param offset
	 *            The index at which should be started to read. If the fragments are e.g.
	 *            from a "@param" tag, then it is followed by the the variable name which
	 *            should be skipped. Therefore the <code>offset</code> should be 1.
	 * @return The text from the <code>fragments</code>.
	 */
	private static String readFragments(List<ASTNode> fragments, int offset)
	{
		StringBuffer html = new StringBuffer();

		for (ASTNode fragment : fragments.subList(offset, fragments.size()))
		{
			switch (fragment.getNodeType())
			{
			case ASTNode.TEXT_ELEMENT:
			{
				TextElement textElem = (TextElement) fragment;
				html.append(textElem.getText());
				break;
			}
			case ASTNode.SIMPLE_NAME:
			case ASTNode.QUALIFIED_NAME:
			{
				Name name = (Name) fragment;
				html.append(name.getFullyQualifiedName());
				break;
			}
			case ASTNode.METHOD_REF:
			{
				// TODO handle more fragments
				logger.log(Level.INFO,
						"Unhandled fragement in Javadoc: ASTNode.METHOD_REF -> "
								+ fragment.toString());
				break;
			}
			case ASTNode.MEMBER_REF:
			{
				logger.log(Level.INFO,
						"Unhandled fragement in Javadoc: ASTNode.MEMBER_REF -> "
								+ fragment.toString());
				break;
			}
			case ASTNode.TAG_ELEMENT:
			{
				logger.log(Level.INFO,
						"Unhandled fragement in Javadoc: ASTNode.TAG_ELEMENT -> "
								+ fragment.toString());
				break;
			}
			}
		}

		return html.toString();
	}

}