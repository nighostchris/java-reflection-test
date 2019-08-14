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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import exceptions.InvalidTerrainTypeException;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class GameMapTest 
{
	@Rule
	public Timeout globalTimeout = Timeout.seconds(3);
	
	static Constructor<Player> playerConstructor;
	static Constructor<Archer> archerConstructor;
	
	static Field playerUnits;
	static Field unitID;
	static Field unitHealth;
	static Field unitLocationX;
	static Field unitLocationY;
	
	static Constructor<GameMap> gameMapConstructor;
	
	static Field height, width;
	static Field terrainMap;
	static Field displayMap;
	
	static Method loadTerrainMap;
	static Method checkStartingLocation;
	static Method isValidPath;
	static Method updateUnitLocationOnMap;
	static Method renderMap;
	
	int testHeight, testWidth;
	int[][] testTerrainMap;
	char[][] testDisplayMap;
	
	GameMap gameMap;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		archerConstructor = Archer.class.getDeclaredConstructor(char.class, int.class, int.class);
		archerConstructor.setAccessible(true);
		playerConstructor = Player.class.getDeclaredConstructor(String.class);
		playerConstructor.setAccessible(true);
		
		playerUnits = Player.class.getDeclaredField("units");
		playerUnits.setAccessible(true);
		unitID = Unit.class.getDeclaredField("id");
		unitID.setAccessible(true);
		unitHealth = Unit.class.getDeclaredField("health");
		unitHealth.setAccessible(true);
		unitLocationX = Unit.class.getDeclaredField("locationX");
		unitLocationX.setAccessible(true);
		unitLocationY = Unit.class.getDeclaredField("locationY");
		unitLocationY.setAccessible(true);
		
		gameMapConstructor = GameMap.class.getDeclaredConstructor();
		gameMapConstructor.setAccessible(true);
		
		height = GameMap.class.getDeclaredField("height");
		height.setAccessible(true);
		width = GameMap.class.getDeclaredField("width");
		width.setAccessible(true);
		terrainMap = GameMap.class.getDeclaredField("terrainMap");
		terrainMap.setAccessible(true);
		displayMap = GameMap.class.getDeclaredField("displayMap");
		displayMap.setAccessible(true);
		
		loadTerrainMap = GameMap.class.getDeclaredMethod("loadTerrainMap", String.class);
		loadTerrainMap.setAccessible(true);
		checkStartingLocation = GameMap.class.getDeclaredMethod("checkStartingLocation", char.class, int.class, int.class);
		checkStartingLocation.setAccessible(true);
		isValidPath = GameMap.class.getDeclaredMethod("isValidPath", int.class, int.class, int.class, int.class, int.class);
		isValidPath.setAccessible(true);
		updateUnitLocationOnMap = GameMap.class.getDeclaredMethod("updateUnitLocationOnMap", int.class, int.class, int.class, int.class);
		updateUnitLocationOnMap.setAccessible(true);
		renderMap = GameMap.class.getDeclaredMethod("renderMap", ArrayList.class);
		renderMap.setAccessible(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
		archerConstructor = null;
		playerConstructor = null;
		
		playerUnits = null;
		unitID = null;
		unitHealth = null;
		unitLocationX = null;
		unitLocationY = null;
		
		gameMapConstructor = null;
		
		height = null;
		width = null;
		terrainMap = null;
		displayMap = null;
		
		loadTerrainMap = null;
		checkStartingLocation = null;
		isValidPath = null;
		updateUnitLocationOnMap = null;
		renderMap = null;
	}

	@Before
	public void setUp() throws Exception 
	{
		try (Scanner scanner = new Scanner(new File("GameMap.txt")))
		{
			testWidth = scanner.nextInt();
			testHeight = scanner.nextInt();
			scanner.nextLine();
			
			testTerrainMap = new int[testHeight][testWidth];
			testDisplayMap = new char[testHeight][testWidth];
			
			for (int y = 0; y < testHeight; y++)
			{
				String line = scanner.nextLine();
				for (int x = 0; x < testWidth; x++)
				{
					if (line.charAt(x) == GameMap.TERRAIN_EMPTY_CHAR)
					{
						testTerrainMap[y][x] = GameMap.TERRAIN_EMPTY;
					}
					else if (line.charAt(x) == GameMap.TERRAIN_BLOCKED_CHAR)
					{
						testTerrainMap[y][x] = GameMap.TERRAIN_BLOCKED;
					}
					else
					{
						throw new InvalidTerrainTypeException(line.charAt(x));
					}
				}
			} 
		} 
		
		gameMap = gameMapConstructor.newInstance();
		height.set(gameMap, testHeight);
		width.set(gameMap, testWidth);
		terrainMap.set(gameMap, testTerrainMap);
		displayMap.set(gameMap, testDisplayMap);
	}

	@After
	public void tearDown() throws Exception 
	{
		gameMap = null;
		testWidth = 0;
		testHeight = 0;
		testTerrainMap = null;
		testDisplayMap = null;
	}

	@Test
	public void testLoadTerrainMapException() throws Exception
	{
		try
		{
			gameMap = gameMapConstructor.newInstance();
			loadTerrainMap.invoke(gameMap, "InvalidTerrainType.txt");
			fail("Invalid Terrain Type, Exception not thrown.");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testLoadTerrainMapDimensionsOnly() throws Exception
	{
		try
		{
			gameMap = gameMapConstructor.newInstance();
			loadTerrainMap.invoke(gameMap, "GameMap.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			assertEquals(testHeight, height.getInt(gameMap));
			assertEquals(testWidth, width.getInt(gameMap));
		}
	}
	
	@Test
	public void testLoadTerrainMapTerrainOnly() throws Exception
	{		
		try
		{
			gameMap = gameMapConstructor.newInstance();
			loadTerrainMap.invoke(gameMap, "GameMap.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			int[][] actualTerrainMap = (int[][])(terrainMap.get(gameMap));
			for (int y = 0; y < testHeight; y++)
			{
				for (int x = 0; x < testWidth; x++)
				{
					assertEquals(testTerrainMap[y][x], actualTerrainMap[y][x]);
				}
			}
		}
	}
	
	@Test
	public void testCheckStartingLocationGameMapBoundary() throws Exception
	{		
		int[][] testTerrainMap1 = new int[1][3];
		testTerrainMap1[0][0] = GameMap.TERRAIN_EMPTY;
		testTerrainMap1[0][1] = GameMap.TERRAIN_BLOCKED;
		testTerrainMap1[0][2] = GameMap.TERRAIN_OCCUPIED;
		terrainMap.set(gameMap, testTerrainMap1);
		height.setInt(gameMap, 1);
		width.setInt(gameMap, 3);
		
		try
		{
			checkStartingLocation.invoke(gameMap, 'A', -1, -1);
			fail("Outside GameMap Boundary, Exception not thrown.");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testCheckStartingLocationBlockedByTerrain() throws Exception
	{		
		int[][] testTerrainMap1 = new int[1][3];
		testTerrainMap1[0][0] = GameMap.TERRAIN_EMPTY;
		testTerrainMap1[0][1] = GameMap.TERRAIN_BLOCKED;
		testTerrainMap1[0][2] = GameMap.TERRAIN_OCCUPIED;
		terrainMap.set(gameMap, testTerrainMap1);
		height.setInt(gameMap, 1);
		width.setInt(gameMap, 3);
		
		try
		{
			checkStartingLocation.invoke(gameMap, 'A', 1, 0);
			fail("Blocked by Terrain, Exception not thrown.");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testCheckStartingLocationOccupiedByAnotherUnit() throws Exception
	{		
		int[][] testTerrainMap1 = new int[1][3];
		testTerrainMap1[0][0] = GameMap.TERRAIN_EMPTY;
		testTerrainMap1[0][1] = GameMap.TERRAIN_BLOCKED;
		testTerrainMap1[0][2] = GameMap.TERRAIN_OCCUPIED;
		terrainMap.set(gameMap, testTerrainMap1);
		height.setInt(gameMap, 1);
		width.setInt(gameMap, 3);
		
		try
		{
			checkStartingLocation.invoke(gameMap, 'A', 2, 0);
			fail("Occupied by Another Unit, Exception not thrown.");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testIsValidPathGameMapBoundary() throws Exception
	{
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, -1, -1, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 0, -1, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, -1, 0, 100)));

		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 30, 15, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 29, 15, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 30, 14, 100)));
	}
	
	@Test
	public void testIsValidPathMovementRange() throws Exception
	{	
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 12, 12, 10)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 12, 0, 10)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 0, 12, 10)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 7, 7, 10)));
	}
	
	@Test
	public void testIsValidPathTerrain() throws Exception
	{
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 19, 3, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 20, 3, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 20, 4, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 21, 4, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 21, 5, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 22, 5, 100)));
		
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 13, 6, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 14, 6, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 11, 7, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 12, 7, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 13, 7, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 14, 7, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 9, 8, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 10, 8, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 11, 8, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 12, 8, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 13, 8, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 10, 9, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 11, 9, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 12, 9, 100)));
		
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 21, 10, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 22, 10, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 23, 10, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 21, 11, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 21, 12, 100)));
	}
	
	@Test
	public void testIsValidPathUnit() throws Exception
	{	
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 7, 4);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 6, 5);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 6, 3);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 5, 4);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 21, 3);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 22, 4);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 22, 11);
		
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 7, 4, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 6, 5, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 6, 3, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 5, 4, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 21, 3, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 22, 4, 100)));
		assertFalse((boolean)(isValidPath.invoke(gameMap, 1, 1, 22, 11, 100)));
	}
	
	@Test
	public void testIsValidPath() throws Exception
	{
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 7, 4);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 6, 5);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 6, 3);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 5, 4);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 21, 3);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 22, 4);
		updateUnitLocationOnMap.invoke(gameMap, -1, -1, 22, 11);
		
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 2, 2, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 3, 3, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 4, 4, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 5, 5, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 6, 6, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 7, 7, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 8, 8, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 9, 9, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 10, 10, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 11, 11, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 12, 12, 100)));
		assertTrue((boolean)(isValidPath.invoke(gameMap, 1, 1, 13, 13, 100)));
	}
	
	@Test
	public void testRenderMapTerrainMap() throws Exception
	{
		ArrayList<Player> players = new ArrayList<>();
		renderMap.invoke(gameMap, players);
		char[][] checkDisplayMap = (char[][])(displayMap.get(gameMap));
		
		for (int y = 0; y < testHeight; y++)
		{
			for (int x = 0; x < testWidth; x++)
			{
				assertEquals((testTerrainMap[y][x] == GameMap.TERRAIN_BLOCKED) ? GameMap.TERRAIN_BLOCKED_CHAR : GameMap.TERRAIN_EMPTY_CHAR, checkDisplayMap[y][x]);
			}
		}
	}
	
	@Test
	public void testRenderMapUnits() throws Exception
	{
		ArrayList<Player> players = new ArrayList<>();
		Player red = playerConstructor.newInstance("Red");
		Player blue = playerConstructor.newInstance("Blue");
		Unit archer1 = archerConstructor.newInstance('A', 1, 1);
		Unit archer2 = archerConstructor.newInstance('B', 2, 2);
		Unit archer3 = archerConstructor.newInstance('C', 3, 3);
		Unit archer4 = archerConstructor.newInstance('D', 4, 4);
		Unit archer5 = archerConstructor.newInstance('E', 5, 5);
		Unit archer6 = archerConstructor.newInstance('F', 6, 6);
		Unit archer7 = archerConstructor.newInstance('G', 7, 7);
		
		ArrayList<Unit> redUnits = (ArrayList<Unit>)(playerUnits.get(red));
		ArrayList<Unit> blueUnits = (ArrayList<Unit>)(playerUnits.get(blue));
		
		redUnits.add(archer1);
		redUnits.add(archer2);
		redUnits.add(archer3);
		redUnits.add(archer4);
		
		blueUnits.add(archer5);
		blueUnits.add(archer6);
		blueUnits.add(archer7);
		
		players.add(red);
		players.add(blue);
		
		renderMap.invoke(gameMap, players);
		char[][] checkDisplayMap = (char[][])(displayMap.get(gameMap));
		
		for (Player player:players)
		{
			for (Unit unit:(ArrayList<Unit>)(playerUnits.get(player)))
			{
				if (unitHealth.getInt(unit) > 0)
				{
					assertEquals(unitID.getChar(unit), checkDisplayMap[unitLocationY.getInt(unit)][unitLocationX.getInt(unit)]);
				}
			}
		}
	}
}
