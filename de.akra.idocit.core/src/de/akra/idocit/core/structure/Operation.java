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
package de.akra.idocit.core.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An operation in an interface with input and output parameters.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 * 
 */
public abstract class Operation extends SignatureElement
{
	/**
	 * List of input parameters.
	 */
	private Parameters inputParameters;

	/**
	 * List of output parameters.
	 */
	private Parameters outputParameters;

	/**
	 * {@link List} of thrown or returned exception messages.
	 */
	private List<? extends Parameters> exceptions;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            The parent of this SignatureElement.
	 * @param category
	 *            The category of this element.
	 */
	public Operation(SignatureElement parent, String category)
	{
		super(parent, category);
		exceptions = Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size()
	{
		int size = 0;
		if (inputParameters != null)
		{
			size += inputParameters.size() + 1;
		}
		if (outputParameters != null)
		{
			size += outputParameters.size() + 1;
		}
		for (Parameters paramList : exceptions)
		{
			size += paramList.size() + 1;
		}
		return size;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.akra.idocit.structure.SignatureElement#copy(de.akra.idocit.structure.SignatureElement)
	 */
	@Override
	public final SignatureElement copy(SignatureElement parent)
			throws IllegalArgumentException
	{
		SignatureElement newElem = super.copy(parent);
		checkIfAssignable(Operation.class, newElem);

		Operation op = (Operation) newElem;

		if (inputParameters != null)
		{
			op.setInputParameters((Parameters) inputParameters.copy(op));
		}

		if (outputParameters != null)
		{
			op.setOutputParameters((Parameters) outputParameters.copy(op));
		}

		List<Parameters> newExceptions = Collections.emptyList();
		if (exceptions != Collections.EMPTY_LIST)
		{
			newExceptions = new ArrayList<Parameters>(exceptions.size());
			for (Parameters paramList : exceptions)
			{
				newExceptions.add((Parameters) paramList.copy(op));
			}
		}
		op.setExceptions(newExceptions);
		return op;
	}

	/**
	 * @param outputParameters
	 *            the outputParameters to set
	 */
	public void setOutputParameters(Parameters outputParameters)
	{
		this.outputParameters = outputParameters;
	}

	/**
	 * @return the outputParameters
	 */
	public Parameters getOutputParameters()
	{
		return outputParameters;
	}

	/**
	 * @param inputParameters
	 *            the inputParameters to set
	 */
	public void setInputParameters(Parameters inputParameters)
	{
		this.inputParameters = inputParameters;
	}

	/**
	 * @return the inputParameters
	 */
	public Parameters getInputParameters()
	{
		return inputParameters;
	}

	/**
	 * @param exceptions
	 *            the exceptions to set
	 */
	public void setExceptions(List<? extends Parameters> exceptions)
	{
		this.exceptions = exceptions;
	}

	/**
	 * @return the exceptions
	 */
	public List<? extends Parameters> getExceptions()
	{
		return exceptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((exceptions == null) ? 0 : exceptions.hashCode());
		result = prime * result
				+ ((inputParameters == null) ? 0 : inputParameters.hashCode());
		result = prime * result
				+ ((outputParameters == null) ? 0 : outputParameters.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof Operation))
		{
			return false;
		}
		Operation other = (Operation) obj;
		if (exceptions == null)
		{
			if (other.exceptions != null)
			{
				return false;
			}
		}
		else if (!exceptions.equals(other.exceptions))
		{
			return false;
		}
		if (inputParameters == null)
		{
			if (other.inputParameters != null)
			{
				return false;
			}
		}
		else if (!inputParameters.equals(other.inputParameters))
		{
			return false;
		}
		if (outputParameters == null)
		{
			if (other.outputParameters != null)
			{
				return false;
			}
		}
		else if (!outputParameters.equals(other.outputParameters))
		{
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Operation [inputParameters=");
		builder.append(inputParameters);
		builder.append(", outputParameters=");
		builder.append(outputParameters);
		builder.append(", exceptions=");
		builder.append(exceptions);
		builder.append("]");
		return builder.toString();
	}
}
