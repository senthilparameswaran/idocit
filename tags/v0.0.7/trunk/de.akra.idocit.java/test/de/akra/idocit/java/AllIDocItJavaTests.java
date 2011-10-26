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
package de.akra.idocit.java;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.akra.idocit.java.services.HTMLTableParserTest;
import de.akra.idocit.java.services.JavaInterfaceParserTest;
import de.akra.idocit.java.services.JavaParserTest;
import de.akra.idocit.java.services.JavadocGeneratorTest;
import de.akra.idocit.java.services.JavadocParserTest;
import de.akra.idocit.java.structure.DocumentationTest;
import de.akra.idocit.java.structure.JavaInterfaceArtifactTest;
import de.akra.idocit.java.structure.JavaInterfaceTest;
import de.akra.idocit.java.structure.JavaMethodTest;
import de.akra.idocit.java.structure.JavaParameterTest;

/**
 * All tests.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ HTMLTableParserTest.class, JavadocGeneratorTest.class,
		JavadocParserTest.class, JavaInterfaceParserTest.class,
		JavaParserTest.class, DocumentationTest.class,
		JavaInterfaceArtifactTest.class, JavaInterfaceTest.class,
		JavaMethodTest.class, JavaParameterTest.class })
public class AllIDocItJavaTests {

}
