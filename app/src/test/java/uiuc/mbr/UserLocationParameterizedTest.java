package uiuc.mbr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import uiuc.mbr.event_selection.UserLocation;

import static org.junit.Assert.assertEquals;

/**Parameterized unit test that tests the UserLocation.compareTo().*/
@RunWith(Parameterized.class)
public class UserLocationParameterizedTest {
	private String name1;
	private String name2;
	private int expectedSign;

	public UserLocationParameterizedTest(String s1, String s2, Integer i) {
		name1 = s1;
		name2 = s2;
		expectedSign = i;
	}

	@Parameterized.Parameters
	public static Collection namesList() {
		return Arrays.asList(new Object[][]{
				{"asdf", "refds", -1},
				{"hgf", "asdff", 1},
				{"", "", 0},
				{"", "asfggggds", -1},
				{"gdsgf", "", 1},
				{"asdf", "asdf", 0}
		});
	}

	@Test
	public void testCompare() {
		UserLocation ul1 = new UserLocation(name1);
		UserLocation ul2 = new UserLocation(name2);
		int result = ul1.compareTo(ul2);
		if (result > 0) result = 1;
		if (result < 0) result = -1;
		assertEquals(expectedSign, result);
	}

}
