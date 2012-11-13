/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH                                          
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
package de.akra.idocit.ui;

/**
 * Global information for the iDocIt! UI.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public final class UIGlobals
{

	private static String lastSelectedPathInFileDialog;

	/**
	 * Get the path (without file name) that the user has selected in the last file dialog
	 * (e.g. save dialog).
	 * 
	 * @return [OBJECT] can be <code>null</code>
	 * @thematicgrid Getting Operations / Getter
	 */
	public static synchronized String getLastSelectedPathInFileDialog()
	{
		return lastSelectedPathInFileDialog;
	}

	/**
	 * Set the path (without file name) that the user has selected in the last file dialog
	 * (e.g. save dialog).
	 * 
	 * @param lastSelectedPathInFileDialog
	 *            [OBJECT]
	 * 
	 * @thematicgrid Setting Operation / Setter
	 */
	public static synchronized void setLastSelectedPathInFileDialog(
			final String lastSelectedPathInFileDialog)
	{
		UIGlobals.lastSelectedPathInFileDialog = lastSelectedPathInFileDialog;
	}
}
