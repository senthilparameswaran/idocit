/*******************************************************************************
 * Copyright 2011 AKRA GmbH and Jan Christian Krause
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
package de.akra.idocit.nlp.stanford.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.akra.idocit.nlp.stanford.exception.UnitializedServiceException;
import de.akra.idocit.nlp.stanford.structure.PennTreebankTag;
import de.akra.idocit.nlp.stanford.structure.TaggedOperationIdentifier;
import de.akra.idocit.nlp.stanford.structure.TaggedToken;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Provides operations for tagging identifiers.
 * 
 * @author Jan Christian Krause
 * 
 */
public final class WSDLTaggingService
{

	private static final WSDLTaggingService instance = new WSDLTaggingService();

	private MaxentTagger tagger = null;

	/**
	 * Default constructor (private due to singleton-pattern)
	 */
	private WSDLTaggingService()
	{

	}
	
	/**
	 * Returns the singleton-instance of this service.
	 * 
	 * @return The singleton-instance of this service
	 */
	public static WSDLTaggingService getInstance(){
		return instance;
	}

	public void init(String taggerModelPath) throws IOException
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
	 * Returns the {@link List} of {@link TaggedOperationIdentifier}s from the given
	 * {@link File} <code>plainTextTaggedIdentifiers</code>. It creates one
	 * {@link TaggedOperationIdentifier} for each line of the file. The tokens in file are
	 * expected to be seperated by a "_" from their {@link PennTreebankTag}.
	 * 
	 * @param plainTextTaggedIdentifiers
	 *            The {@link File} of tagged identifiers
	 * 
	 * @return The {@link List} of converted {@link TaggedOperationIdentifier}s
	 */
	public List<TaggedOperationIdentifier> convertToTaggedIdentifiers(
			List<String> plainTextTaggedIdentifiers)
	{
		List<TaggedOperationIdentifier> convertedTaggedIdentifiers = new ArrayList<TaggedOperationIdentifier>();

		for (String identifier : plainTextTaggedIdentifiers)
		{
			TaggedOperationIdentifier taggedIdentifier = convertToTaggedIdentifier(identifier);
			convertedTaggedIdentifiers.add(taggedIdentifier);
		}

		return convertedTaggedIdentifiers;
	}

	public TaggedOperationIdentifier convertToTaggedIdentifier(String identifier)
	{
		String[] tokens = identifier.split(" ");
		TaggedOperationIdentifier taggedIdentifier = new TaggedOperationIdentifier();
		List<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();

		for (String token : tokens)
		{
			if (token != null)
			{
				String[] tags = token.split("/");

				if (!".".equals(tags[0]) && !"-".equals(tags[0]))
				{
					TaggedToken taggedToken = new TaggedToken();
					taggedToken.setToken(tags[0].toLowerCase());
					taggedToken.setTag(PennTreebankTag.valueOf(tags[1]));
					taggedTokens.add(taggedToken);
				}
			}
		}

		taggedIdentifier.setTaggedTokens(taggedTokens);
		return taggedIdentifier;
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
	 * @throws UnitializedServiceException
	 *             If this service had not been initialized with a valid tagger-model via
	 *             {@link WSDLTaggingService#init(String)}.
	 */
	public List<TaggedOperationIdentifier> tagIdentifiers(List<String> identifiers)
			throws IOException, UnitializedServiceException
	{
		if (tagger != null)
		{
			List<String> taggedIdentifiers = new ArrayList<String>();

			for (String identifier : identifiers)
			{
				taggedIdentifiers.add(tagger.tagString(identifier));
			}

			return convertToTaggedIdentifiers(taggedIdentifiers);
		}
		else
		{
			throw new UnitializedServiceException(
					"No valid tagging-model has been set yet. Therefore no tagging-operations could be performed");
		}
	}

	/**
	 * Tags the given identifier and returns it with tags.
	 * 
	 * @param identifier
	 *            The identifier to tag
	 * @return The tagged identifier
	 * @throws IOException
	 *             If an error occurs
	 * @throws UnitializedServiceException
	 *             If this service had not been initialized with a valid tagger-model via
	 *             {@link WSDLTaggingService#init(String)}.
	 */
	public TaggedOperationIdentifier tagIdentifier(String identifier) throws IOException,
			UnitializedServiceException
	{
		if (tagger != null)
		{
			String taggedIdentifier = tagger.tagString(identifier);

			return convertToTaggedIdentifier(taggedIdentifier);
		}
		else
		{
			throw new UnitializedServiceException(
					"No valid tagging-model has been set yet. Therefore no tagging-operations could be performed");
		}
	}

	private String getIdentifier(TaggedOperationIdentifier taggedIdentifier)
	{
		StringBuilder builder = new StringBuilder();

		for (TaggedToken token : taggedIdentifier.getTaggedTokens())
		{
			builder.append(token.getToken());
			builder.append(' ');
		}

		return builder.toString().trim();
	}

	public boolean containsVerb(TaggedOperationIdentifier taggedIdentifier)
	{
		for (TaggedToken token : taggedIdentifier.getTaggedTokens())
		{
			if (PennTreebankTag.VB.equals(token.getTag())
					|| PennTreebankTag.VBP.equals(token.getTag())
					|| PennTreebankTag.VBZ.equals(token.getTag()))
			{
				return true;
			}
		}

		return false;
	}

	public List<TaggedOperationIdentifier> removeVerblessIdentifiers(
			List<TaggedOperationIdentifier> taggedIdentifiers)
	{
		List<TaggedOperationIdentifier> verblessIdentifiers = new ArrayList<TaggedOperationIdentifier>();

		for (TaggedOperationIdentifier taggedIdentifier : taggedIdentifiers)
		{
			if (containsVerb(taggedIdentifier))
			{
				verblessIdentifiers.add(taggedIdentifier);
			}
		}

		return verblessIdentifiers;
	}

	public List<String> identifyVerblessIdentifiers(
			List<TaggedOperationIdentifier> taggedIdentifiers)
	{
		List<String> verblessIdentifiers = new ArrayList<String>();

		for (TaggedOperationIdentifier taggedIdentifier : taggedIdentifiers)
		{
			if (!containsVerb(taggedIdentifier))
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
	 * @throws UnitializedServiceException
	 *             If this service had not been initialized with a valid tagger-model via
	 *             {@link WSDLTaggingService#init(String)}.
	 */
	public List<TaggedOperationIdentifier> tagIdentifiersinWeMode(List<String> identifiers)
			throws IOException, UnitializedServiceException
	{
		List<String> weIdentifiers = new ArrayList<String>();

		for (String identifier : identifiers)
		{
			weIdentifiers.add("We " + identifier);
		}

		return tagIdentifiers(weIdentifiers);
	}

	/**
	 * 
	 * @param untaggedIdentifiers
	 * @return
	 * @throws IOException
	 * @throws UnitializedServiceException
	 *             If this service had not been initialized with a valid tagger-model via
	 *             {@link WSDLTaggingService#init(String)}.
	 */
	public List<TaggedOperationIdentifier> performTwoPhaseIdentifierTagging(
			List<String> untaggedIdentifiers) throws IOException,
			UnitializedServiceException
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
