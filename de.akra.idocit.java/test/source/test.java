/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package source;

import java.util.List;
import java.util.Map;

/**
 * ein Text
 * 
 * @author diei
 * @since heute
 * 
 */
abstract class Test implements TestInterface<String>
{
	public Test(String name)
	{

	}
}

interface TestInterface<E>
{
	String getName(Map<String, ? extends Map<E, List<? super Integer[][]>>> input);

	int getCount();

	String[][][] getString3();

	int[] getInt1();

	List<String> getStringList();

	Map<String, Map<Integer, List<int[][]>>> getStrangeThings();

	Map<String, ? extends Map<E, List<? super Integer[][]>>> getStrangeThings2();
}

enum TestEnum
{
	ERSTER, ZEITER;

	TestEnum getERSTER()
	{
		return ERSTER;
	}
}