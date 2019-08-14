package pa1;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PlayerTest 
{
	@Rule
	public Timeout globalTimeout = Timeout.seconds(3);
	
	static Constructor<Player> playerConstructor;
	static Constructor<Archer> archerConstructor;
	
	static Field name;
	static Field units;
	static Field health;
	static Field isReady;
	
	static Method getName;
	static Method addUnit;
	static Method getUnitById;
	static Method readyAllUnits;
	static Method hasReadyUnits;
	static Method hasUnitsRemaining;
	static Method equals;
	
	Player player;
	Unit archerA;
	Unit archerB;
	Unit archerC;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		archerConstructor = Archer.class.getDeclaredConstructor(char.class, int.class, int.class);
		archerConstructor.setAccessible(true);
		playerConstructor = Player.class.getDeclaredConstructor(String.class);
		playerConstructor.setAccessible(true);
		
		name = Player.class.getDeclaredField("name");
		name.setAccessible(true);
		units = Player.class.getDeclaredField("units");
		units.setAccessible(true);
		health = Unit.class.getDeclaredField("health");
		health.setAccessible(true);
		isReady = Unit.class.getDeclaredField("isReady");
		isReady.setAccessible(true);
		
		getName = Player.class.getDeclaredMethod("getName");
		getName.setAccessible(true);
		addUnit = Player.class.getDeclaredMethod("addUnit", Unit.class);
		addUnit.setAccessible(true);
		getUnitById = Player.class.getDeclaredMethod("getUnitById", char.class);
		getUnitById.setAccessible(true);
		readyAllUnits = Player.class.getDeclaredMethod("readyAllUnits");
		readyAllUnits.setAccessible(true);
		hasReadyUnits = Player.class.getDeclaredMethod("hasReadyUnits");
		hasReadyUnits.setAccessible(true);
		hasUnitsRemaining = Player.class.getDeclaredMethod("hasUnitsRemaining");
		hasUnitsRemaining.setAccessible(true);
		equals = Player.class.getDeclaredMethod("equals", Object.class);
		equals.setAccessible(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
		archerConstructor = null;
		playerConstructor = null;
		
		name = null;
		units = null;
		health = null;
		isReady = null;
		
		getName = null;
		addUnit = null;
		getUnitById = null;
		readyAllUnits = null;
		hasReadyUnits = null;
		hasUnitsRemaining = null;
		equals = null;
	}

	@Before
	public void setUp() throws Exception 
	{
		player = playerConstructor.newInstance("Test");
		archerA = archerConstructor.newInstance('A', 0, 0);
		archerB = archerConstructor.newInstance('B', 0, 0);
		archerC = archerConstructor.newInstance('C', 0, 0);
	}

	@After
	public void tearDown() throws Exception 
	{
		player = null;
		archerA = null;
		archerB = null;
		archerC = null;
	}

	@Test
	public void testGetName() throws Exception
	{
		assertEquals("Test", (String)(getName.invoke(player)));
	}

	@Test
	public void testAddUnit() throws Exception
	{
		addUnit.invoke(player, archerA);
		addUnit.invoke(player, archerB);
		addUnit.invoke(player, archerC);
		
		ArrayList<Unit> actualUnits = (ArrayList<Unit>)(units.get(player));
		
		assertSame(archerA, actualUnits.get(0));
		assertSame(archerB, actualUnits.get(1));
		assertSame(archerC, actualUnits.get(2));
	}

	@Test
	public void testGetUnitById() throws Exception
	{
		addUnit.invoke(player, archerA);
		addUnit.invoke(player, archerB);
		addUnit.invoke(player, archerC);
		
		assertSame(archerA, (Unit)(getUnitById.invoke(player, 'A')));
		assertSame(archerB, (Unit)(getUnitById.invoke(player, 'B')));
		assertSame(archerC, (Unit)(getUnitById.invoke(player, 'C')));
	}

	@Test
	public void testReadyAllUnits() throws Exception
	{
		addUnit.invoke(player, archerA);
		addUnit.invoke(player, archerB);
		addUnit.invoke(player, archerC);
		
		readyAllUnits.invoke(player);
		
		assertTrue(isReady.getBoolean(archerA));
		assertTrue(isReady.getBoolean(archerB));
		assertTrue(isReady.getBoolean(archerC));
	}

	@Test
	public void testHasReadyUnits() throws Exception
	{
		assertFalse((boolean)(hasReadyUnits.invoke(player)));
		
		// 3C0
		addUnit.invoke(player, archerA);
		isReady.setBoolean(archerA, false);
		assertFalse((boolean)(hasReadyUnits.invoke(player)));
		
		addUnit.invoke(player, archerB);
		isReady.setBoolean(archerB, false);
		assertFalse((boolean)(hasReadyUnits.invoke(player)));
		
		addUnit.invoke(player, archerC);
		isReady.setBoolean(archerC, false);
		assertFalse((boolean)(hasReadyUnits.invoke(player)));
		
		// 3C1
		isReady.setBoolean(archerA, false);
		isReady.setBoolean(archerB, false);
		isReady.setBoolean(archerC, false);
		
		isReady.setBoolean(archerA, true);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
		isReady.setBoolean(archerA, false);
		
		isReady.setBoolean(archerB, true);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
		isReady.setBoolean(archerB, false);
		
		isReady.setBoolean(archerC, true);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
		isReady.setBoolean(archerC, false);
		
		// 3C2
		isReady.setBoolean(archerA, true);
		isReady.setBoolean(archerB, true);
		isReady.setBoolean(archerC, true);
		
		isReady.setBoolean(archerA, false);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
		isReady.setBoolean(archerA, true);

		isReady.setBoolean(archerB, false);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
		isReady.setBoolean(archerB, true);
		
		isReady.setBoolean(archerC, false);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
		isReady.setBoolean(archerC, true);
		
		// 3C3
		isReady.setBoolean(archerA, true);
		isReady.setBoolean(archerB, true);
		isReady.setBoolean(archerC, true);
		assertTrue((boolean)(hasReadyUnits.invoke(player)));
	}

	@Test
	public void testHasUnitsRemaining() throws Exception
	{
		assertFalse((boolean)(hasUnitsRemaining.invoke(player)));
		
		// 3C3
		addUnit.invoke(player, archerA);
		health.setInt(archerA, 10);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		
		addUnit.invoke(player, archerB);
		health.setInt(archerB, 10);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		
		addUnit.invoke(player, archerC);
		health.setInt(archerC, 10);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		
		// 3C2
		health.setInt(archerA, 10);
		health.setInt(archerB, 10);
		health.setInt(archerC, 10);
		
		health.setInt(archerA, 0);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		health.setInt(archerA, 10);
		
		health.setInt(archerB, 0);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		health.setInt(archerB, 10);
		
		health.setInt(archerC, 0);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		health.setInt(archerC, 10);
		
		// 3C1
		health.setInt(archerA, 0);
		health.setInt(archerB, 0);
		health.setInt(archerC, 0);
		
		health.setInt(archerA, 10);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		health.setInt(archerA, 0);
		
		health.setInt(archerB, 10);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		health.setInt(archerB, 0);
		
		health.setInt(archerC, 10);
		assertTrue((boolean)(hasUnitsRemaining.invoke(player)));
		health.setInt(archerC, 0);
		
		// 3C0
		health.setInt(archerA, 0);
		health.setInt(archerB, 0);
		health.setInt(archerC, 0);
		assertFalse((boolean)(hasUnitsRemaining.invoke(player)));
	}

	@Test
	public void testEqualsObject() throws Exception
	{
		Player player2 = playerConstructor.newInstance("Test2");
		Player playerSame = playerConstructor.newInstance("Test");
		
		assertFalse((boolean)(equals.invoke(player, player2)));
		assertTrue((boolean)(equals.invoke(player, playerSame)));
	}
}
