package uiuc.mbr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import uiuc.mbr.directions.Directions;

import static org.junit.Assert.*;

/**Parameterized unit test for Directions, ensuring it adds entries the way we expect.*/
@RunWith(Parameterized.class)
public class DirectionsParameterizedTest
{
	private final int count;

	public DirectionsParameterizedTest(Integer count) {
		this.count = count;
	}


	@Parameterized.Parameters
	public static Collection alarms() {
		return Arrays.asList(new Object[][]{
				{0},
				{1},
				{2},
				{3},
				{17}
		});
	}


	@Test
	public void test() {
		Directions dir = new Directions(123);
		for(int c = 0; c < count; c++)
			dir.add("duration", "coordinates");
		assertEquals(count, dir.getCoordinates().size());
		assertEquals(count, dir.getDirections().size());
	}
}
