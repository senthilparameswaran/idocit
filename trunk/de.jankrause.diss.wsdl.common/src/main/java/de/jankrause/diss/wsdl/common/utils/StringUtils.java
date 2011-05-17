package de.jankrause.diss.wsdl.common.utils;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {

	public static String concat(String ...strings){
		StringBuffer buffer = new StringBuffer();
		
		for(String string : strings){
			buffer.append(string);
		}
		
		return buffer.toString();
	}
	
	public static List<String> replaceColons(List<String> unstructuredSentences) {
		List<String> colonFreeSentences = new ArrayList<String>();

		for (String unstructuredSentence : unstructuredSentences) {
			String colonFreeSentence = unstructuredSentence.replaceAll("\\.", " ");

			colonFreeSentences.add(colonFreeSentence);
		}

		return colonFreeSentences;
	}
	
	private static String addBlanksToCamelSyntax(String camelSyntaxLabel) {
		StringBuffer labelWithBlanks = new StringBuffer();

		if (camelSyntaxLabel != null) {
			camelSyntaxLabel = camelSyntaxLabel.replace('_', ' ');

			for (int letter = 0; letter < camelSyntaxLabel.length(); letter++) {
				char currentLetter = camelSyntaxLabel.charAt(letter);

				if (isBigCharacter(currentLetter)) {
					char prevChar = (letter > 0) ? camelSyntaxLabel.charAt(letter - 1) : 'a';

					if (!isBigCharacter(prevChar) || Character.isDigit(prevChar)) {
						labelWithBlanks.append(' ');
					}
				}

				labelWithBlanks.append(currentLetter);
			}
		}

		return labelWithBlanks.toString().trim();
	}

	private static boolean isBigCharacter(char currentLetter) {
		return (currentLetter >= 65) && (currentLetter <= 90);
	}

	public static List<String> addBlanksToCamelSyntax(List<String> camelSyntaxLabels) {
		List<String> blankCamelSyntax = new ArrayList<String>();

		for (String camelLabel : camelSyntaxLabels) {
			blankCamelSyntax.add(addBlanksToCamelSyntax(camelLabel));
		}

		return blankCamelSyntax;
	}
}
