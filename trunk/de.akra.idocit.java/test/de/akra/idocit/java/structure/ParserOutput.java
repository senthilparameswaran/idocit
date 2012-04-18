package de.akra.idocit.java.structure;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;

public class ParserOutput
{
	private CompilationUnit compilationUnit;
	private Document document;

	public CompilationUnit getCompilationUnit()
	{
		return compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit)
	{
		this.compilationUnit = compilationUnit;
	}

	public Document getDocument()
	{
		return document;
	}

	public void setDocument(Document document)
	{
		this.document = document;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compilationUnit == null) ? 0 : compilationUnit.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParserOutput other = (ParserOutput) obj;
		if (compilationUnit == null)
		{
			if (other.compilationUnit != null)
				return false;
		}
		else if (!compilationUnit.equals(other.compilationUnit))
			return false;
		if (document == null)
		{
			if (other.document != null)
				return false;
		}
		else if (!document.equals(other.document))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ParserOutput [compilationUnit=");
		builder.append(compilationUnit);
		builder.append(", document=");
		builder.append(document);
		builder.append("]");
		return builder.toString();
	}
}
