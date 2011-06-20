package de.akra.idocit.core.services;

import java.util.List;
import java.util.Map;

import de.akra.idocit.core.structure.Addressee;
import de.akra.idocit.core.structure.Delimiters;
import de.akra.idocit.core.structure.Documentation;
import de.akra.idocit.core.structure.Interface;
import de.akra.idocit.core.structure.InterfaceArtifact;
import de.akra.idocit.core.structure.Operation;
import de.akra.idocit.core.structure.Parameter;
import de.akra.idocit.core.structure.Parameters;
import de.akra.idocit.core.structure.Scope;
import de.akra.idocit.core.structure.SignatureElement;

/**
 * Converts the {@link Documentation}s from the signature elements to HTML.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class HTMLDocGenerator
{
	/**
	 * The artifact to convert to HTML.
	 */
	private InterfaceArtifact artifact;
	private Delimiters delimiters;

	public HTMLDocGenerator(InterfaceArtifact artifact, Delimiters delimiters)
	{
		this.artifact = artifact;
		this.delimiters = delimiters;
	}

	public String generateHTML()
	{
		StringBuffer html = new StringBuffer(
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
		html.append("<html>\n");
		generateHTMLHead(html);
		generateHTMLBody(html);
		html.append("</html>\n");
		return html.toString();
	}

	private void generateHTMLHead(StringBuffer htmlDoc)
	{
		htmlDoc.append("<head>\n");
		htmlDoc.append("<title>");
		htmlDoc.append("Documentation of " + artifact.getDisplayName());
		htmlDoc.append("</title>\n");
		htmlDoc.append("<meta name=\"author\" content=\"AKRA GmbH\">\n");
		htmlDoc.append("<meta name=\"generator\" content=\"iDocIt!\">\n");
		htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n");
		// htmlDoc.append("<meta http-equiv=\"content-language\" content=\"en\">\n");
		htmlDoc.append("</head>\n");
	}

	private void generateHTMLBody(StringBuffer htmlDoc)
	{
		htmlDoc.append("<body>\n");
//		htmlDoc.append("<h1>Content</h1>\n");
//		htmlDoc.append("<pre>\n");
//		generateIndex(htmlDoc);
//		htmlDoc.append("</pre>\n");
//		htmlDoc.append("<hr width=\"100%\">");

		htmlDoc.append("<h1>");
		htmlDoc.append(artifact.getDisplayName());
		htmlDoc.append("</h1>\n");
		documentations2HTML(htmlDoc, artifact.getDocumentations());

		for (Interface i : artifact.getInterfaces())
		{
			interface2HTML(htmlDoc, i);
		}

		htmlDoc.append("</body>\n");
	}

	private void generateIndex(StringBuffer htmlDoc)
	{
		buildHierarchy(htmlDoc, artifact, 0);
	}

	private void interface2HTML(StringBuffer htmlDoc, Interface interf)
	{
		htmlDoc.append("<h2>Interface ");
		htmlDoc.append(interf.getDisplayName());
		htmlDoc.append("</h2>\n");
		documentations2HTML(htmlDoc, interf.getDocumentations());

		if (!interf.getOperations().isEmpty())
		{
			htmlDoc.append("<h3>Operations</h3>\n");
			htmlDoc.append("<ul style=\"list-style-type:none\">\n");
			for (Operation operation : interf.getOperations())
			{
				htmlDoc.append("<li>");
				operation2HTML(htmlDoc, operation);
				htmlDoc.append("</li>");
			}
			htmlDoc.append("</ul>\n");
		}

		if (!interf.getInnerInterfaces().isEmpty())
		{
			htmlDoc.append("<h3>Embedded Interfaces</h3>\n");
			htmlDoc.append("<ul style=\"list-style-type:none\">\n");
			for (Interface i : interf.getInnerInterfaces())
			{
				htmlDoc.append("<li>");
				interface2HTML(htmlDoc, i);
				htmlDoc.append("</li>");
			}
			htmlDoc.append("</ul>\n");
		}
	}

	private void operation2HTML(StringBuffer htmlDoc, Operation operation)
	{
		htmlDoc.append("<hr width=\"100%\">");
		htmlDoc.append("<h4>");
		htmlDoc.append(operation.getDisplayName());
		htmlDoc.append("</h4>\n");
		documentations2HTML(htmlDoc, operation.getDocumentations());

		if (!operation.getInputParameters().getDocumentations().isEmpty()
				|| !operation.getInputParameters().getParameters().isEmpty())
		{
			htmlDoc.append("<ul style=\"list-style-type:none\">\n");
			htmlDoc.append("<li><h5>Input</h5></li>\n");
			htmlDoc.append("<li>");
			documentations2HTML(htmlDoc, operation.getInputParameters()
					.getDocumentations());
			htmlDoc.append("</li>");

			for (Parameter p : operation.getInputParameters().getParameters())
			{
				htmlDoc.append("<li>");
				htmlDoc.append(p.getDisplayName());
				htmlDoc.append("<br />");
				parameter2HTML(htmlDoc, p);
				htmlDoc.append("</li>");
			}
			htmlDoc.append("</ul>\n");
		}

		if (!operation.getOutputParameters().getDocumentations().isEmpty()
				|| !operation.getOutputParameters().getParameters().isEmpty())
		{
			htmlDoc.append("<ul style=\"list-style-type:none\">\n");
			htmlDoc.append("<li><h5>Output</h5></li>\n");
			htmlDoc.append("<li>");
			documentations2HTML(htmlDoc, operation.getOutputParameters()
					.getDocumentations());
			htmlDoc.append("</li>");

			for (Parameter p : operation.getOutputParameters().getParameters())
			{
				htmlDoc.append("<li>");
				htmlDoc.append(p.getDisplayName());
				htmlDoc.append("<br />");
				parameter2HTML(htmlDoc, p);
				htmlDoc.append("</li>");
			}
			htmlDoc.append("</ul>\n");
		}
	}

	private void parameters2HTML(StringBuffer htmlDoc, List<Parameter> parameters)
	{
		for (Parameter p : parameters)
		{
			parameter2HTML(htmlDoc, p);
		}
	}

	private void parameter2HTML(StringBuffer htmlDoc, Parameter p)
	{
		htmlDoc.append("<li>");
		documentations2HTML(htmlDoc, p.getDocumentations());
		htmlDoc.append("</li>");
		parameters2HTML(htmlDoc, p.getComplexType());
	}

	private void documentations2HTML(StringBuffer htmlDoc,
			List<Documentation> documentations)
	{
		for (Documentation doc : documentations)
		{
			// write only if there is something to write
			if (doc.getScope() != null || doc.getThematicRole() != null
					|| !doc.getDocumentation().isEmpty())
			{

				StringBuffer textElem = new StringBuffer();
				textElem.append("<table border=\"1\" cellspacing=\"0\" width=\"100%\">");

				if (doc.getSignatureElementIdentifier() != null)
				{
					textElem.append("<tr><td width=\"20%\">Element:</td><td>");
					textElem.append(doc.getSignatureElementIdentifier());
					textElem.append("</td></tr>\n");
				}

				if (doc.getThematicRole() != null)
				{
					textElem.append("<tr><td width=\"20%\">Role:</td><td>");
					textElem.append(doc.getThematicRole().getName());
					textElem.append("</td></tr>\n");
				}

				if (doc.getScope() == Scope.IMPLICIT)
				{
					textElem.append("<tr><td width=\"20%\">Scope:</td><td>implicit</td></tr>\n");
				}

				Map<Addressee, String> docMap = doc.getDocumentation();
				for (Addressee addressee : doc.getAddresseeSequence())
				{
					String text = docMap.get(addressee);
					if (!text.isEmpty())
					{
						textElem.append("<tr><td><b>");
						textElem.append(addressee.getName());
						textElem.append("</b>:</td><td>");
						textElem.append(docMap.get(addressee));
						textElem.append("</td></tr>\n");
					}
				}
				textElem.append("</table>");

				htmlDoc.append(textElem);
			}
		}
	}

	/**
	 * Builds the hierarchy tree as string.
	 * 
	 * @param result
	 *            The tree as string.
	 * @param sigElem
	 *            The element that should be added to the tree.
	 * @param level
	 *            The tree level (indentation of the line).
	 */
	private void buildHierarchy(StringBuffer result, SignatureElement sigElem, int level)
	{
		if (sigElem != null)
		{
			// write tabs to the beginning of the line
			for (int i = 0; i < level; i++)
			{
				result.append("  ");
			}
			result.append(sigElem.getDisplayName() + "<br />\n");

			if (sigElem instanceof Parameter)
			{
				Parameter param = (Parameter) sigElem;
				if (param.getComplexType() != null)
				{
					for (Parameter p : param.getComplexType())
					{
						buildHierarchy(result, p, level + 1);
					}
				}
			}
			else if (sigElem instanceof Operation)
			{
				Operation op = (Operation) sigElem;
				buildHierarchy(result, op.getInputParameters(), level + 1);
				buildHierarchy(result, op.getOutputParameters(), level + 1);

				for (Parameters paramList : op.getExceptions())
				{
					buildHierarchy(result, paramList, level + 1);
				}
			}
			else if (sigElem instanceof Parameters)
			{
				Parameters paramList = (Parameters) sigElem;
				if (paramList.getParameters() != null)
				{
					for (Parameter p : paramList.getParameters())
					{
						buildHierarchy(result, p, level + 1);
					}
				}
			}
			else if (sigElem instanceof Interface)
			{
				Interface interf = (Interface) sigElem;

				if (interf.getOperations() != null)
				{
					for (Operation o : interf.getOperations())
					{
						buildHierarchy(result, o, level + 1);
					}
				}
				if (interf.getInnerInterfaces() != null)
				{
					for (Interface i : interf.getInnerInterfaces())
					{
						buildHierarchy(result, i, level + 1);
					}
				}
			}
			else if (sigElem instanceof InterfaceArtifact)
			{
				InterfaceArtifact iArtifact = (InterfaceArtifact) sigElem;
				if (iArtifact.getInterfaces() != null)
				{
					for (Interface i : iArtifact.getInterfaces())
					{
						buildHierarchy(result, i, level + 1);
					}
				}
			}
		}
	}
}
