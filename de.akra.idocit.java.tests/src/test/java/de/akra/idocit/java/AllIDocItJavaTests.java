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
package de.akra.idocit.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.akra.idocit.common.constants.Misc;
import de.akra.idocit.java.services.AddresseeUtilsTest;
import de.akra.idocit.java.services.HTMLTableParserTest;
import de.akra.idocit.java.services.JavaInterfaceParserTest;
import de.akra.idocit.java.services.JavaParserTest;
import de.akra.idocit.java.services.JavadocGeneratorTest;
import de.akra.idocit.java.services.JavadocParserTest;
import de.akra.idocit.java.services.SimpleJavadocGeneratorTest;
import de.akra.idocit.java.services.SimpleJavadocParserTest;
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
		JavadocParserTest.class, JavaInterfaceParserTest.class, JavaParserTest.class,
		DocumentationTest.class, JavaInterfaceArtifactTest.class,
		JavaInterfaceTest.class, JavaMethodTest.class, JavaParameterTest.class,
		SimpleJavadocGeneratorTest.class, SimpleJavadocParserTest.class,
		AddresseeUtilsTest.class })
public class AllIDocItJavaTests
{
	public static final String SOURCE_DIR = "src/test/resources/source/";
	private static final String SOURCE_DIR_BACKUP = "src/test/resources/source/backup";

	/**
	 * @action Convert all line separators in the source files to the operating system
	 *         default line separator. It is needed for comparing the generated Javadoc
	 *         with the reference Javadoc in the source files.
	 * 
	 * @throws IOException
	 */
	@BeforeClass
	public static void prepareSourceFiles() throws IOException
	{
		// backup reference files to avoid change reports in version control
		moveFiles(SOURCE_DIR, SOURCE_DIR_BACKUP);
		copyAndConvertReferenceFiles(SOURCE_DIR_BACKUP, SOURCE_DIR);
	}

	/**
	 * @action Delete the converted reference files and move all original reference files
	 *         back to source folder.
	 * 
	 * @throws IOException
	 */
	@AfterClass
	public static void recoverSourceFiles() throws IOException
	{
		moveFiles(SOURCE_DIR_BACKUP, SOURCE_DIR);
		final File backupFolder = new File(SOURCE_DIR_BACKUP);
		Assert.assertEquals("Not all reference files were recovered correctly.", 0,
				backupFolder.listFiles().length);

		if (!backupFolder.delete())
		{
			throw new RuntimeException("Could not delete folder"
					+ backupFolder.getAbsolutePath());
		}
	}

	/**
	 * @action Copies all files from <code>sourceDir</code> to <code>destDir</code> and
	 *         converts all line separators in the files to the operating system default
	 *         line separator.
	 * @param sourceDir
	 *            [SOURCE]
	 * @param destDir
	 *            [DESTINATION]
	 * @throws IOException
	 */
	private static void copyAndConvertReferenceFiles(final String sourceDir,
			final String destDir) throws IOException
	{
		final String lineSeparator = System.getProperty("line.separator");
		final File sourceFolder = new File(sourceDir);

		// copy and convert source files
		final File[] sourceFiles = sourceFolder.listFiles();
		for (final File file : sourceFiles)
		{
			final File dest = new File(destDir, file.getName());

			BufferedReader reader = null;
			Writer writer = null;
			try
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(
						file), Charset.forName(Misc.DEFAULT_CHARSET)));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						dest), Charset.forName(Misc.DEFAULT_CHARSET)));

				String line;
				while ((line = reader.readLine()) != null)
				{
					writer.write(line);
					writer.write(lineSeparator);
				}
			}
			finally
			{
				if (reader != null)
				{
					reader.close();
				}
				if (writer != null)
				{
					writer.close();
				}
			}
		}
	}

	/**
	 * @action Moves all files from <code>sourceDir</code> to <code>destDir</code>. If the
	 *         file exists already in the destination folder it is deleted before the file
	 *         is moved.
	 * 
	 * @param sourceDir
	 *            [SOURCE]
	 * @param destDir
	 *            [DESTINATION]
	 */
	private static void moveFiles(final String sourceDir, final String destDir)
	{
		final FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname)
			{
				return pathname.isFile() && !pathname.getName().startsWith(".")
						&& !pathname.getName().endsWith(".tmp");
			}
		};

		final File sourceFolder = new File(sourceDir);

		final File destFolder = new File(destDir);
		if (!destFolder.exists())
		{
			if (!destFolder.mkdir())
			{
				throw new RuntimeException("Could not create "
						+ destFolder.getAbsolutePath());
			}
		}

		// back up source files
		final File[] sourceFiles = sourceFolder.listFiles(filter);
		for (File file : sourceFiles)
		{
			final File dest = new File(destDir, file.getName());
			if (dest.exists())
			{
				if (dest.delete())
				{
					throw new RuntimeException("Could not delete "
							+ dest.getAbsolutePath());
				}
			}
			final boolean success = file.renameTo(dest);
			Assert.assertTrue(
					"Backup of source files for tests failed: " + file.getAbsolutePath(),
					success);
		}
	}
}
