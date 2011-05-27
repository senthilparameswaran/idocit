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
package de.akra.idocit.nlp.stanford.exception;

public class UnitializedServiceException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3855010499735220326L;

	/**
	 * Constructor with message
	 * 
	 * @param message
	 *            The error-description
	 */
	public UnitializedServiceException(String message)
	{
		super(message);
	}
}
