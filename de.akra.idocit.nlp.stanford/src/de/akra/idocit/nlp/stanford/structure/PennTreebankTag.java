/*******************************************************************************
 * Copyright 2011 AKRA GmbH 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.akra.idocit.nlp.stanford.structure;

/**
 * Represents the Penn Treebank Tagset.
 * 
 * @author Jan Christian Krause
 * @since 12.08.2009
 * 
 */
public enum PennTreebankTag
{
	CC("CC"), CD("CD"), DT("DT"), EX("EX"), FW("FW"), IN("IN"), JJ("JJ"), JJR("JJR"), JJS(
			"JJS"), LS("LS"), MD("MD"), NN("NN"), NNS("NNS"), NNP("NNP"), NNPS("NNPS"), PDT(
			"PDT"), POS("POS"), PRP("PRP"), PRP$("PRP$"), RB("RB"), RBR("RBR"), RBS("RBS"), RP(
			"RP"), SYM("SYM"), TO("TO"), UH("UH"), VB("VB"), VBD("VBD"), VBG("VBG"), VBN(
			"VBN"), VBP("VBP"), VBZ("VBZ"), WDT("WDT"), WP("WP"), WP$("WP$"), WRB("WRB");

	private String ivXMLTag = null;

	/**
	 * The constructor
	 * 
	 * @param pvXMLTag
	 *            The XML-Tag of this STT
	 */
	private PennTreebankTag(String pvXMLTag)
	{
		ivXMLTag = pvXMLTag;
	}

	/**
	 * Returns the XML-tag of this STT.
	 * 
	 * @return The XML-tag of this STT
	 */
	public String getXMLTag()
	{
		return ivXMLTag;
	}
}
