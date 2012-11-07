/*******************************************************************************
 * Copyright 2012 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.akra.idocit.core.services.impl;

import java.util.List;
import java.util.Map;

import de.akra.idocit.common.structure.Addressee;
import de.akra.idocit.common.structure.Documentation;
import de.akra.idocit.common.structure.Interface;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.common.structure.Operation;
import de.akra.idocit.common.structure.Parameter;

/**
 * 
 * @author Ann-Cathrin Pape
 * 
 */
public class HTMLDocGenerator
{

	/**
	 * The artifact to convert to HTML.
	 */
	private InterfaceArtifact artifact;

	public HTMLDocGenerator(final InterfaceArtifact artifact)
	{
		this.artifact = artifact;
	}

	/**
	 * Generates the whole HTML document.
	 * 
	 * @return HTML document as string
	 */
	public String generateHTML()
	{
		final StringBuffer html = new StringBuffer("<!DOCTYPE>\n");
		html.append("<html>\n");
		generateHTMLHead(html);
		generateHTMLBody(html);
		html.append("</html>\n");
		return html.toString();
	}

	/**
	 * Generate HTML head
	 * 
	 * @param htmlDoc
	 *            StringBuffer to that the head should be appended.
	 */
	private void generateHTMLHead(final StringBuffer htmlDoc)
	{
		htmlDoc.append("<head>\n");
		htmlDoc.append("<title>");
		htmlDoc.append("Documentation of " + artifact.getDisplayName());
		htmlDoc.append("</title>\n");
		htmlDoc.append("<meta name=\"author\" content=\"AKRA GmbH\"/>\n");
		htmlDoc.append("<meta name=\"generator\" content=\"iDocIt!\"/>\n");
		htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n");
		htmlDoc.append("<link rel=\"stylesheet\" media=\"screen\" href=\"stylesheet.css\"/>\n");
		htmlDoc.append("</head>\n");
	}

	/**
	 * Triggers the generating of the HTML body.
	 * 
	 * @param htmlDoc
	 *            String which contains the HTML body
	 */
	private void generateHTMLBody(final StringBuffer htmlDoc)
	{
		htmlDoc.append("<body>\n");
		htmlDoc.append("<div id=\"header\">\n" + "<h1 id=\"docTitle\">"
				+ artifact.getDisplayName() + "</h1>\n"
				+ "<h2 id=\"interfaceTitle\">Interface "
				+ artifact.getInterfaces().get(0).getDisplayName() + "</h2>\n"
				+ "</div>\n");
		htmlDoc.append(generateHTMLNavigation());
		htmlDoc.append(generateHTMLContent());
		htmlDoc.append("</body>\n");
	}

	/**
	 * Generates the navigation bar at the right side of the HTML page.
	 * 
	 * @return String which contains the HTML for navigation bar
	 */
	private String generateHTMLNavigation()
	{
		StringBuffer navigation = new StringBuffer();
		navigation.append("<div id=\"nav\">\n");
		navigation.append("<ul id=\"navElements\">\n");
		// add operations and inner interfaces with operations for each interface to
		// navigation bar
		for (final Interface interf : artifact.getInterfaces())
		{
			// navigation.append("<li><a href=\"#"+ interf.getDisplayName() + "\">"+
			// interf.getDisplayName() + "</a></li>\n");
			navigation
					.append("<label class=\"navTitle\" for=\"operations\">Operations</label>\n");
			for (final Operation operation : interf.getOperations())
				navigation.append("<li><a href=\"#" + operation.getDisplayName() + "\">"
						+ operation.getDisplayName() + "</a></li>\n");
			if (!interf.getInnerInterfaces().isEmpty())
			{
				navigation
						.append("<label class=\"navTitle\" for=\"innerInterfaces\">Inner Interfaces</label>\n");
				for (Interface innerInterf : interf.getInnerInterfaces())
				{
					navigation.append("<li><a href=\"#" + innerInterf.getDisplayName()
							+ "\">" + innerInterf.getDisplayName() + "</a></li>\n");
					navigation
							.append("<label class=\"navTitle\" for=\"operations\">Operations</label>\n");
					for (Operation operation : interf.getOperations())
						navigation.append("<li><a href=\"#" + operation.getDisplayName()
								+ "\">" + operation.getDisplayName() + "</a></li>\n");
				}
			}
		}
		navigation.append("</ul>\n");
		navigation.append("</div>\n");
		return navigation.toString();
	}

	/**
	 * Generates the actual content of the HTML page.
	 * 
	 * @return String which contains the HTML for the actual content
	 */
	private String generateHTMLContent()
	{
		final StringBuffer content = new StringBuffer();
		content.append("<div id=\"content\">\n");
		content.append("<ul class=\"interfaceList\">\n");

		// add for each interface title, operations and inner interfaces
		for (final Interface interf : artifact.getInterfaces())
			content.append(generateInterfaceDocHTML(interf));

		content.append("</ul>\n"); // end interface list
		content.append("</div>\n");
		return content.toString();
	}

	/**
	 * Generates the HTML for an interface.
	 * 
	 * @param interf
	 * @return String which contains the HTML for the documentation of an interface.
	 */
	private String generateInterfaceDocHTML(final Interface interf)
	{
		final StringBuffer interfaceDoc = new StringBuffer();
		interfaceDoc.append("<li>\n"); // start single interface
		interfaceDoc.append("<h3 class=\"interfaceTitle\">Interface "
				+ interf.getDisplayName() + "</h3>\n");
		interfaceDoc.append(generateDocumentationHTML(interf.getDocumentations()));

		if (!interf.getOperations().isEmpty())
		{
			interfaceDoc.append("<ul class=\"operationList\">\n"); // start operation list

			for (final Operation operation : interf.getOperations())
			{
				interfaceDoc.append("<li>\n"); // start single operation
				interfaceDoc.append("<h4 id=\"" + operation.getDisplayName() + "\">"
						+ operation.getDisplayName() + "</h4>\n");

				interfaceDoc.append("<div class=\"opDescription\">\n");
				interfaceDoc.append(generateDocumentationHTML(operation
						.getDocumentations()));
				interfaceDoc.append("</div>\n");

				// Input
				if (operation.getInputParameters() != null
						&& operation.getInputParameters().getDocumentations() != null
						&& (!operation.getInputParameters().getDocumentations().isEmpty() || !operation
								.getInputParameters().getParameters().isEmpty()))
				{
					interfaceDoc.append("<div class=\"input\">\n<h5>Input</h5>\n");
					interfaceDoc.append(generateDocumentationHTML(operation
							.getInputParameters().getDocumentations()));
					// start parameter description
					interfaceDoc.append("<ul class=\"paramDescription\">\n");

					for (final Parameter param : operation.getInputParameters()
							.getParameters())
					{
						interfaceDoc.append("<li>\n<p>\n" + param.getDisplayName()
								+ "<br />\n");
						interfaceDoc.append(generateParametersDocHTML(param));
						interfaceDoc.append("</p>\n</li>\n");
					}
					interfaceDoc.append("</ul>\n"); // end parameter description
					interfaceDoc.append("</div>\n"); // end input parameters
				}

				// Output
				if (operation.getOutputParameters() != null
						&& operation.getOutputParameters().getDocumentations() != null
						&& (!operation.getOutputParameters().getDocumentations()
								.isEmpty() || !operation.getOutputParameters()
								.getParameters().isEmpty()))
				{
					interfaceDoc.append("<div class=\"output\">\n<h5>Output</h5>\n");
					interfaceDoc.append(generateDocumentationHTML(operation
							.getOutputParameters().getDocumentations()));
					// start parameter description
					interfaceDoc.append("<ul class=\"paramDescription\">\n");

					for (final Parameter param : operation.getOutputParameters()
							.getParameters())
					{
						interfaceDoc.append("<li>\n<p>\n" + param.getDisplayName()
								+ "<br />\n");
						interfaceDoc.append(generateParametersDocHTML(param));
						interfaceDoc.append("</p>\n</li>\n");
					}
					interfaceDoc.append("</ul>\n"); // end parameter description
					interfaceDoc.append("</div>\n"); // end input parameters
				}
				interfaceDoc.append("</li>\n"); // end single operation
			}
			interfaceDoc.append("</ul>\n"); // end operation list

			// Inner Interfaces
			if (interf.getInnerInterfaces() != null
					&& !interf.getInnerInterfaces().isEmpty())
			{
				interfaceDoc.append("<h3>Embedded Interfaces</h3>");
				// start inner interfaces list
				interfaceDoc.append("<ul class=\"innerInterfaceList\">\n");

				for (final Interface i : interf.getInnerInterfaces())
				{
					generateInterfaceDocHTML(i);
				}
				interfaceDoc.append("</ul>"); // end inner interfaces list
			}
		}
		interfaceDoc.append("</li>\n"); // end single interface

		return interfaceDoc.toString();
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private String generateParametersDocHTML(final List<Parameter> parameters)
	{
		final StringBuffer parameterDoc = new StringBuffer();
		for (final Parameter param : parameters)
			parameterDoc.append(generateParametersDocHTML(param));
		return parameterDoc.toString();
	}

	/**
	 * 
	 * @param param
	 * @return
	 */
	private String generateParametersDocHTML(final Parameter param)
	{
		final StringBuffer parameterDoc = new StringBuffer();
		if (param.getDocumentations() != null && !param.getDocumentations().isEmpty())
			parameterDoc.append(generateDocumentationHTML(param.getDocumentations()));
		if (param.getComplexType() != null && !param.getComplexType().isEmpty())
			parameterDoc.append(generateParametersDocHTML(param.getComplexType()));
		return parameterDoc.toString();
	}

	/**
	 * General method to generate the actual documentation in HTML.
	 * 
	 * @param documentations
	 * @return String which contains the HTML documentation of the given element.
	 */
	private String generateDocumentationHTML(final List<Documentation> documentations)
	{
		final StringBuffer htmlDoc = new StringBuffer();

		if (documentations != null && !documentations.isEmpty())
		{
			for (final Documentation doc : documentations)
			{
				// write only if there is something to write
				if (doc.getThematicRole() != null || !doc.getDocumentation().isEmpty())
				{
					htmlDoc.append("<p>\n");
					if (doc.getSignatureElementIdentifier() != null)
					{
						htmlDoc.append("Element: ");
						htmlDoc.append(doc.getSignatureElementIdentifier());
						htmlDoc.append("<br />\n");
					}

					if (doc.getThematicRole() != null)
					{
						htmlDoc.append("<label class=\"title\">Role: </label>");
						htmlDoc.append(doc.getThematicRole().getName());
						htmlDoc.append("<br />\n");
					}

					final Map<Addressee, String> docMap = doc.getDocumentation();
					for (final Addressee addressee : doc.getAddresseeSequence())
					{
						final String text = docMap.get(addressee);
						if (!text.isEmpty())
						{
							htmlDoc.append(addressee.getName());
							htmlDoc.append(": ");
							htmlDoc.append(docMap.get(addressee));
							htmlDoc.append("<br />\n");
						}
					}
					htmlDoc.append("</p>\n");
				}
			}
		}
		return htmlDoc.toString();
	}
}
