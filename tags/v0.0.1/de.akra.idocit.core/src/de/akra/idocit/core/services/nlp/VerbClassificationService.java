/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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
package de.akra.idocit.core.services.nlp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.akra.idocit.nlp.stanford.structure.PennTreebankTag;
import de.akra.idocit.nlp.stanford.structure.TaggedOperationIdentifier;
import de.akra.idocit.nlp.stanford.structure.TaggedToken;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public final class VerbClassificationService {
	// Constants
	public static final String CLASS_UNCLASSIFIED = "unclassified";
	
	public static final String EMPTY_VERB = "";

	private static final VerbClassificationService instance = new VerbClassificationService();

	private VerbClassificationService() {

	}

	public static VerbClassificationService getInstance() {
		return instance;
	}

	public void initialize(File[] verbNetFiles) throws SAXException, IOException, ParserConfigurationException {
		VerbNetSystem.getInstance().initialize(verbNetFiles);

	}
	
	public void initialize(InputStream[] verbNetFiles) throws SAXException, IOException, ParserConfigurationException {
		VerbNetSystem.getInstance().initialize(verbNetFiles);

	}

	/**
	 * Returns the {@link List} of verb-classes for the given
	 * <code>verb</code>.
	 * 
	 * @param verbClassSystem
	 *            The verb-class-system to use for the classification
	 * @param verb
	 *            The verb to classify
	 * @return The {@link List} of verb-classes for the given
	 *         <code>verb</code>
	 * @throws VerbClassSystemException
	 *             If an error during the classification occurs
	 */
	private List<String> getVerbClassBySynonym(VerbNetSystem verbClassSystem, String verb) {
		WordNetDatabase wordNetDatabase = WordNetDatabase.getFileInstance();
		List<String> verbClasses = new ArrayList<String>();

		// Get the verb-synonyms.
		Synset[] verbSynonyms = wordNetDatabase.getSynsets(verb, SynsetType.VERB);

		// For each context of the verb ...
		for (Synset verbSynonym : verbSynonyms) {
			VerbSynset verbSyn = (VerbSynset) verbSynonym;
			String[] synonyms = verbSyn.getWordForms();

			// For each contextual synonym ...
			for (String synonym : synonyms) {
				// Check if the current synonym is not the word itsself.
				// TODO: Think about to use contains() here!
				if (!synonym.equals(verb)) {
					// TODO: Quote the ' here!
					synonym = synonym.replace('\'', ' ');

					// Get the verb-classes of the synonym ...
					List<String> synVerbClasses = verbClassSystem.queryVerbClassByWord(synonym);

					// ... and add them to the result.
					for (String synVerbClass : synVerbClasses) {
						if (!verbClasses.contains(synVerbClass)) {
							verbClasses.add(synVerbClass);
						}
					}
				}
			}
		}

		// If no synonym could be classified ...
		if (verbClasses.size() <= 0) {
			// ... ok, then the verb is unclassified :(.
			verbClasses.add(CLASS_UNCLASSIFIED);
		}

		return verbClasses;
	}

	public List<String> classifyVerb(String verb) {
		// ... get the verb-classes for this verb-class-system.
		List<String> verbClasses = VerbNetSystem.getInstance().queryVerbClassByWord(verb);

		// If the verb could not be classified ...
		if (verbClasses.size() <= 0) {
			// ... and classify the synonyms.
			verbClasses = getVerbClassBySynonym(VerbNetSystem.getInstance(), verb);
		}

		return verbClasses;
	}

	public static String findFirstVerb(TaggedOperationIdentifier taggedIdentifier) {
		for (TaggedToken token : taggedIdentifier.getTaggedTokens()) {
			if (PennTreebankTag.VB.equals(token.getTag()) || PennTreebankTag.VBP.equals(token.getTag()) || PennTreebankTag.VBZ.equals(token.getTag())) {
				return token.getToken();
			}
		}
	
		return EMPTY_VERB;
	}
	

}
