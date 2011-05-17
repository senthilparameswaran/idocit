package de.jankrause.diss.wsdl.common.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jankrause.diss.wsdl.common.services.verbs.VerbClassificationService;
import de.jankrause.diss.wsdl.common.structure.wsdl.TaggedOperationIdentifier;
import de.jankrause.diss.wsdl.common.structure.wsdl.TaggedToken;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Provides operations for tagging identifiers.
 * 
 * @author Jan Christian Krause
 * 
 */
public final class WSDLTaggingService
{

	private static MaxentTagger tagger = null;

	public static void init(String taggerModelPath) throws IOException
	{
		try
		{
			tagger = new MaxentTagger(taggerModelPath);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the {@link List} of {@link TaggedOperationIdentifier} for the given
	 * {@link List} of <code>identifiers</code>. Internally it uses the Stanford
	 * {@link MaxentTagger}.
	 * 
	 * @param identifiers
	 *            The identifiers to tag
	 * @return The {@link List} of tagged identifiers
	 * @throws IOException
	 *             If an error occurs.
	 */
	public static List<TaggedOperationIdentifier> tagIdentifiers(List<String> identifiers)
			throws IOException
	{
		List<String> taggedIdentifiers = new ArrayList<String>();

		for (String identifier : identifiers)
		{
			taggedIdentifiers.add(tagger.tagString(identifier));
		}

		return WSDLParsingService.convertToTaggedIdentifiers(taggedIdentifiers);
	}

	public static TaggedOperationIdentifier tagIdentifier(String identifier)
			throws IOException
	{
		String taggedIdentifier = tagger.tagString(identifier);

		return WSDLParsingService.convertToTaggedIdentifier(taggedIdentifier);
	}

	private static String getIdentifier(TaggedOperationIdentifier taggedIdentifier)
	{
		StringBuilder builder = new StringBuilder();

		for (TaggedToken token : taggedIdentifier.getTaggedTokens())
		{
			builder.append(token.getToken());
			builder.append(' ');
		}

		return builder.toString().trim();
	}

	public static List<TaggedOperationIdentifier> removeVerblessIdentifiers(
			List<TaggedOperationIdentifier> taggedIdentifiers)
	{
		List<TaggedOperationIdentifier> verblessIdentifiers = new ArrayList<TaggedOperationIdentifier>();

		for (TaggedOperationIdentifier taggedIdentifier : taggedIdentifiers)
		{
			if (VerbClassificationService.containsVerb(taggedIdentifier))
			{
				verblessIdentifiers.add(taggedIdentifier);
			}
		}

		return verblessIdentifiers;
	}

	public static List<String> identifyVerblessIdentifiers(
			List<TaggedOperationIdentifier> taggedIdentifiers)
	{
		List<String> verblessIdentifiers = new ArrayList<String>();

		for (TaggedOperationIdentifier taggedIdentifier : taggedIdentifiers)
		{
			if (!VerbClassificationService.containsVerb(taggedIdentifier))
			{
				verblessIdentifiers.add(getIdentifier(taggedIdentifier));
			}
		}

		return verblessIdentifiers;
	}

	/**
	 * 
	 * @param identifiers
	 * @return
	 * @throws IOException
	 */
	public static List<TaggedOperationIdentifier> tagIdentifiersinWeMode(
			List<String> identifiers) throws IOException
	{
		List<String> weIdentifiers = new ArrayList<String>();

		for (String identifier : identifiers)
		{
			weIdentifiers.add("We " + identifier);
		}

		return tagIdentifiers(weIdentifiers);
	}

	public static List<TaggedOperationIdentifier> performTwoPhaseIdentifierTagging(
			List<String> untaggedIdentifiers) throws IOException
	{
		List<TaggedOperationIdentifier> taggedOperationIdentifiers = new ArrayList<TaggedOperationIdentifier>();

		// 1st phase of the experiment: tag all identifiers!
		List<TaggedOperationIdentifier> taggedIdentifiers_phase1 = tagIdentifiers(untaggedIdentifiers);

		// Check if there are any verbless identifiers.
		List<String> verblessIdentifiers = identifyVerblessIdentifiers(taggedIdentifiers_phase1);

		taggedOperationIdentifiers
				.addAll(removeVerblessIdentifiers(taggedIdentifiers_phase1));

		if (verblessIdentifiers.size() > 0)
		{
			// 2nd phase of the experiment: put personal pronoun "we" before all
			// verbless identifiers
			// and tag it again.
			List<TaggedOperationIdentifier> taggedIdentifiers_phase2 = tagIdentifiersinWeMode(verblessIdentifiers);

			taggedOperationIdentifiers.addAll(taggedIdentifiers_phase2);
		}

		return taggedOperationIdentifiers;
	}
}
