package de.akra.idocit.nlp.stanford.structure;

/**
 * Represents the Penn Treebank Tagset.
 * 
 * @author Jan Christian Krause
 * @since 12.08.2009
 * 
 */
public enum PennTreebankTag {
	CC("CC"), CD("CD"), DT("DT"), EX("EX"), FW("FW"), IN("IN"), JJ("JJ"), JJR(
			"JJR"), JJS("JJS"), LS("LS"), MD("MD"), NN("NN"), NNS("NNS"), NNP(
			"NNP"), NNPS("NNPS"), PDT("PDT"), POS("POS"), PRP("PRP"), PRP$(
			"PRP$"), RB("RB"), RBR("RBR"), RBS("RBS"), RP("RP"), SYM("SYM"), TO(
			"TO"), UH("UH"), VB("VB"), VBD("VBD"), VBG("VBG"), VBN("VBN"), VBP(
			"VBP"), VBZ("VBZ"), WDT("WDT"), WP("WP"), WP$("WP$"), WRB("WRB");

	private String ivXMLTag = null;

	/**
	 * The constructor
	 * 
	 * @param pvXMLTag
	 *            The XML-Tag of this STT
	 */
	private PennTreebankTag(String pvXMLTag) {
		ivXMLTag = pvXMLTag;
	}

	/**
	 * Returns the XML-tag of this STT.
	 * 
	 * @return The XML-tag of this STT
	 */
	public String getXMLTag() {
		return ivXMLTag;
	}
}
