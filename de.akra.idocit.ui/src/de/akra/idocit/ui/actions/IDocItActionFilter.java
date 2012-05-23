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
package de.akra.idocit.ui.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IResourceActionFilter;

import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.services.impl.ServiceManager;

/**
 * This should be a filter to disable the Action if the file is not supported.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public class IDocItActionFilter implements IResourceActionFilter {
	private static IDocItActionFilter instance;

	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(IDocItActionFilter.class
			.getName());

	/**
	 * @return the singleton instance of IDocItActionFilter.
	 */
	public IDocItActionFilter getInstance() {
		if (instance == null) {
			instance = new IDocItActionFilter();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.ui.IActionFilter#testAttribute(java.lang.Object,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public boolean testAttribute(Object target, String name, String value) {
		if (target instanceof IFile) {
			IFile file = (IFile) target;
			String type = file.getFileExtension();
			try {
				return (type != null)
						&& ServiceManager.getInstance().getParsingService()
								.isSupported(type);
			} catch (UnitializedIDocItException e) {
				logger.log(
						Level.WARNING,
						"Could not test the attributes, becaue the ParsingService seems not to be initialized.",
						e);

				return false;
			}
		}

		return false;
	}

}
