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
package de.akra.idocit.nlp.stanford;

import org.eclipse.ui.IStartup;

public class NlpInitializationManager implements IStartup
{

	@Override
	public void earlyStartup()
	{
		// Nothing to do! This IStartup is just used to ensure that
		// this plugin is loaded at startup-time of the workbench!

	}

}
