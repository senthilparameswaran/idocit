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
package de.akra.idocit.java.ui.components;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import de.akra.idocit.java.constants.PreferenceStoreConstants;

public class JavadocGeneratorPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{
	public JavadocGeneratorPreferencePage()
	{
		super(FieldEditorPreferencePage.GRID);

		// Set the preference store for the preference page.
		setPreferenceStore(PlatformUI.getPreferenceStore());

		getPreferenceStore().setDefault(PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE_COMPLEX);
	}

	@Override
	protected void createFieldEditors()
	{
		RadioGroupFieldEditor editorJavadocGenerationMode = new RadioGroupFieldEditor(
				PreferenceStoreConstants.JAVADOC_GENERATION_MODE,
				"Which kind of Javadoc do you want to be generated?",
				1,
				new String[][] {
						{ "Complex Javadoc (supports many addressees)",
								PreferenceStoreConstants.JAVADOC_GENERATION_MODE_COMPLEX },
						{ "Simple Javadoc (supports only addressee \"Developer\")",
								PreferenceStoreConstants.JAVADOC_GENERATION_MODE_SIMPLE } },
				getFieldEditorParent());

		addField(editorJavadocGenerationMode);
	}

	@Override
	public void init(IWorkbench workbench)
	{
		// Nothing to do!

	}
}
