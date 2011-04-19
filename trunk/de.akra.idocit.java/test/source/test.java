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