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
package de.akra.idocit.common.structure;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import de.akra.idocit.common.utils.TestUtils;

/**
 * Tests for {@link InterfaceArtifact}.
 */
public class InterfaceArtifactTest
{
	private static Logger logger = Logger
			.getLogger(InterfaceArtifactTest.class.getName());

	/**
	 * Tests {@link InterfaceArtifact#copy(SignatureElement)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCopy() throws Exception
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Create test InterfaceArtifact, copy it and make an equals test.
		 * ******************************************************************************
		 */
		{
			InterfaceArtifact sourceArtifact = TestUtils.createInterfaceArtifact();

			InterfaceArtifact copiedArtifact = (InterfaceArtifact) sourceArtifact
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			logger.log(Level.FINE, sourceArtifact.toString());
			logger.log(Level.FINE, copiedArtifact.toString());

			boolean res = sourceArtifact.equals(copiedArtifact);
			assertEquals(true, res);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Create test InterfaceArtifact, copy it, change something in one
		 * structure and make an equals test.
		 * ****************************************************************************
		 */
		{
			InterfaceArtifact sourceArtifact = TestUtils.createInterfaceArtifact();

			InterfaceArtifact copiedArtifact = (InterfaceArtifact) sourceArtifact
					.copy(SignatureElement.EMPTY_SIGNATURE_ELEMENT);

			assertEquals(true, sourceArtifact.equals(copiedArtifact));

			sourceArtifact.getInterfaces().get(0).setIdentifier("other");
			logger.log(Level.FINE, sourceArtifact.toString());
			logger.log(Level.FINE, copiedArtifact.toString());

			boolean res = sourceArtifact.equals(copiedArtifact);
			assertEquals(false, res);
		}
	}
}
