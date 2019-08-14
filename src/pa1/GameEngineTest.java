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
import java.lang.reflect.InvocationTargetException;

import exceptions.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class GameEngineTest 
{	
	@Rule
	public Timeout globalTimeout = Timeout.seconds(3);
	
	static Constructor<GameEngine> gameEngineConstructor;
	static Constructor<GameMap> gameMapConstructor;
	static Constructor<Player> playerConstructor;
	static Constructor<Archer> archerConstructor;
	static Constructor<Cavalry> cavalryConstructor;
	static Constructor<Infantry> infantryConstructor;
	static Constructor<Pikeman> pikemanConstructor;
	
	static Field playerName;
	static Field playerUnits;
	static Field unitID;
	static Field unitLocationX;
	static Field unitLocationY;
	
	static GameMap gameMap;
	static Field height, width;
	static Field terrainMap;
	static Field displayMap;
	
	static Method loadPlayersAndUnits;
	static Field players;
	static ArrayList<Player> testPlayers;
	static Field gameEngineGameMap;
	GameEngine gameEngine;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		// Setup Reflection Constructors.
		gameEngineConstructor = GameEngine.class.getDeclaredConstructor();
		gameEngineConstructor.setAccessible(true);
		gameMapConstructor = GameMap.class.getDeclaredConstructor();
		gameMapConstructor.setAccessible(true);
		playerConstructor = Player.class.getDeclaredConstructor(String.class);
		playerConstructor.setAccessible(true);
		archerConstructor = Archer.class.getDeclaredConstructor(char.class, int.class, int.class);
		archerConstructor.setAccessible(true);
		cavalryConstructor = Cavalry.class.getDeclaredConstructor(char.class, int.class, int.class);
		cavalryConstructor.setAccessible(true);
		infantryConstructor = Infantry.class.getDeclaredConstructor(char.class, int.class, int.class);
		infantryConstructor.setAccessible(true);
		pikemanConstructor = Pikeman.class.getDeclaredConstructor(char.class, int.class, int.class);
		pikemanConstructor.setAccessible(true);
		
		// Setup Player and Unit Fields.
		playerName = Player.class.getDeclaredField("name");
		playerName.setAccessible(true);
		playerUnits = Player.class.getDeclaredField("units");
		playerUnits.setAccessible(true);
		unitID = Unit.class.getDeclaredField("id");
		unitID.setAccessible(true);
		unitLocationX = Unit.class.getDeclaredField("locationX");
		unitLocationX.setAccessible(true);
		unitLocationY = Unit.class.getDeclaredField("locationY");
		unitLocationY.setAccessible(true);
		
		// Setup gameMap Fields.
		height = GameMap.class.getDeclaredField("height");
		height.setAccessible(true);
		width = GameMap.class.getDeclaredField("width");
		width.setAccessible(true);
		terrainMap = GameMap.class.getDeclaredField("terrainMap");
		terrainMap.setAccessible(true);
		displayMap = GameMap.class.getDeclaredField("displayMap");
		displayMap.setAccessible(true);
		
		// Setup gameEngine Fields.
		gameEngineGameMap = GameEngine.class.getDeclaredField("gameMap");
		gameEngineGameMap.setAccessible(true);
		loadPlayersAndUnits = GameEngine.class.getDeclaredMethod("loadPlayersAndUnits", String.class);
		loadPlayersAndUnits.setAccessible(true);
		players = GameEngine.class.getDeclaredField("players");
		players.setAccessible(true);
		
		testPlayers = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("PlayersAndUnits.txt")))
		{
			// Temporary Global Unit List for Duplicate Unit ID Checking.
			ArrayList<Unit> globalUnitList = new ArrayList<Unit>();
			
			int numPlayers = scanner.nextInt();
			for (int n = 0; n < numPlayers; n++)
			{
				String name = scanner.next();
				Player player = playerConstructor.newInstance(name);
				
				if (testPlayers.contains(player))
				{
					throw new DuplicatePlayerNameException(name);
				}
				
				int numUnits = scanner.nextInt();
				for (int u = 0; u < numUnits; u++)
				{
					char unitId = (scanner.next()).charAt(0);
					String unitType = scanner.next();
					int locationX = scanner.nextInt();
					int locationY = scanner.nextInt();
					Unit unit;
					
					switch (unitType)
					{
					case "Infantry":
						unit = infantryConstructor.newInstance(unitId, locationX, locationY);
						break;
					case "Pikeman":
						unit = pikemanConstructor.newInstance(unitId, locationX, locationY);
						break;
					case "Cavalry":
						unit = cavalryConstructor.newInstance(unitId, locationX, locationY);
						break;
					case "Archer":
						unit = archerConstructor.newInstance(unitId, locationX, locationY);
						break;
					default:
						throw new InvalidUnitTypeException(unitType);
					}
					
					if (globalUnitList.contains(unit))
					{
						throw new DuplicateUnitIDException(unitId);
					}
					//gameMap.checkStartingLocation(unitId, locationX, locationY);
					
					globalUnitList.add(unit);
					player.addUnit(unit);
					//gameMap.updateUnitLocationOnMap(GameMap.TERRAIN_OUT_OF_BOUNDS, GameMap.TERRAIN_OUT_OF_BOUNDS, locationX, locationY);
				}
				
				testPlayers.add(player);
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
		gameEngineConstructor = null;
		gameMapConstructor = null;
		playerConstructor = null;
		archerConstructor = null;
		cavalryConstructor = null;
		infantryConstructor = null;
		pikemanConstructor = null;
		
		playerName = null;
		playerUnits = null;
		unitID = null;
		unitLocationX = null;
		unitLocationY = null;
		
		gameMap = null;
		height = null;
		width = null;
		terrainMap = null;
		displayMap = null;
		
		loadPlayersAndUnits = null;
		players = null;
		testPlayers = null;
		gameEngineGameMap = null;
	}

	@Before
	public void setUp() throws Exception 
	{
		// Setup gameMap. Need to reset on each test, because will get modified during loadPlayersAndUnits().
		int testHeight, testWidth;
		int[][] testTerrainMap;
		char[][] testDisplayMap;
		
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
		height.setInt(gameMap, testHeight);
		width.setInt(gameMap, testWidth);
		terrainMap.set(gameMap, testTerrainMap);
		displayMap.set(gameMap, testDisplayMap);
		
		gameEngine = gameEngineConstructor.newInstance();
		gameEngineGameMap.set(gameEngine, gameMap);
	}

	@After
	public void tearDown() throws Exception 
	{
		gameEngine = null;
	}
	
	@Test
	public void testLoadPlayersAndUnitsInvalidUnitType() throws Exception
	{	
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "InvalidUnitTypeTest.txt");
			fail("Invalid Unit Type, Exception not thrown");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testLoadPlayersAndUnitsDuplicatePlayerName() throws Exception
	{	
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "DuplicatePlayerNameTest.txt");
			fail("Duplicate Player Name, Exception not thrown");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testLoadPlayersAndUnitsDuplicateUnitID() throws Exception
	{	
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "DuplicateUnitIDTest.txt");
			fail("Invalid Unit Type, Exception not thrown");
		}
		catch (InvocationTargetException e) {}
	}
	
	@Test
	public void testLoadPlayersAndUnitsNumPlayers() throws Exception
	{		
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "PlayersAndUnits.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			ArrayList<Player> actualPlayers = (ArrayList<Player>)(players.get(gameEngine));
			assertEquals(testPlayers.size(), actualPlayers.size());
		}
	}
	
	@Test
	public void testLoadPlayersAndUnitsPlayerNames() throws Exception
	{		
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "PlayersAndUnits.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			ArrayList<Player> actualPlayers = (ArrayList<Player>)(players.get(gameEngine));
			for (int i = 0; i < testPlayers.size(); i++)
			{
				assertEquals((String)(playerName.get(testPlayers.get(i))), (String)(playerName.get(actualPlayers.get(i))));
			}
		}
	}
	
	@Test
	public void testLoadPlayersAndUnitsNumUnitsPerPlayer() throws Exception
	{		
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "PlayersAndUnits.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			ArrayList<Player> actualPlayers = (ArrayList<Player>)(players.get(gameEngine));
			for (int i = 0; i < testPlayers.size(); i++)
			{
				assertEquals(((ArrayList<Unit>)(playerUnits.get(testPlayers.get(i)))).size(), ((ArrayList<Unit>)(playerUnits.get(actualPlayers.get(i)))).size());
			}
		}
	}
	
	@Test
	public void testLoadPlayersAndUnitsUnitType() throws Exception
	{		
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "PlayersAndUnits.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			ArrayList<Player> actualPlayers = (ArrayList<Player>)(players.get(gameEngine));
			for (int i = 0; i < testPlayers.size(); i++)
			{
				Player expectedPlayer = testPlayers.get(i);
				Player actualPlayer = actualPlayers.get(i);
				ArrayList<Unit> expectedUnits = (ArrayList<Unit>)(playerUnits.get(expectedPlayer));
				ArrayList<Unit> actualUnits = (ArrayList<Unit>)(playerUnits.get(actualPlayer));

				for (int j = 0; j < expectedUnits.size(); j++)
				{
					assertTrue(actualUnits.get(i).getClass().isInstance(expectedUnits.get(i)));
				}
			}
		}
	}
	
	@Test
	public void testLoadPlayersAndUnitsUnitID() throws Exception
	{		
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "PlayersAndUnits.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			ArrayList<Player> actualPlayers = (ArrayList<Player>)(players.get(gameEngine));
			for (int i = 0; i < testPlayers.size(); i++)
			{
				Player expectedPlayer = testPlayers.get(i);
				Player actualPlayer = actualPlayers.get(i);
				ArrayList<Unit> expectedUnits = (ArrayList<Unit>)(playerUnits.get(expectedPlayer));
				ArrayList<Unit> actualUnits = (ArrayList<Unit>)(playerUnits.get(actualPlayer));
				
				for (int j = 0; j < expectedUnits.size(); j++)
				{
					assertEquals(unitID.getChar(expectedUnits.get(i)), unitID.getChar(actualUnits.get(i)));
				}
			}
		}
	}
	
	@Test
	public void testLoadPlayersAndUnitsUnitLocation() throws Exception
	{		
		try
		{
			loadPlayersAndUnits.invoke(gameEngine, "PlayersAndUnits.txt");
		}
		catch (InvocationTargetException e) {}
		finally
		{
			ArrayList<Player> actualPlayers = (ArrayList<Player>)(players.get(gameEngine));
			for (int i = 0; i < testPlayers.size(); i++)
			{
				Player expectedPlayer = testPlayers.get(i);
				Player actualPlayer = actualPlayers.get(i);
				ArrayList<Unit> expectedUnits = (ArrayList<Unit>)(playerUnits.get(expectedPlayer));
				ArrayList<Unit> actualUnits = (ArrayList<Unit>)(playerUnits.get(actualPlayer));
				
				for (int j = 0; j < expectedUnits.size(); j++)
				{
					assertEquals(unitLocationX.getInt(expectedUnits.get(i)), unitLocationX.getInt(actualUnits.get(i)));
					assertEquals(unitLocationY.getInt(expectedUnits.get(i)), unitLocationY.getInt(actualUnits.get(i)));
				}
			}
		}
	}	

	@Test
	public void testIsGameOver() throws Exception
	{	
		Method isGameOver = GameEngine.class.getDeclaredMethod("isGameOver");
		isGameOver.setAccessible(true);
		
		Field health = Unit.class.getDeclaredField("health");
		health.setAccessible(true);
	
		ArrayList<Player> testPlayers = new ArrayList<>();
		
		// Alive Units.
		Player red1 = playerConstructor.newInstance("Red1");
		Player blue1 = playerConstructor.newInstance("Blue1");
		
		Unit archer1 = archerConstructor.newInstance('A', 1, 1);
		Unit archer2 = archerConstructor.newInstance('B', 2, 2);
		Unit archer3 = archerConstructor.newInstance('C', 3, 3);
		Unit archer4 = archerConstructor.newInstance('D', 4, 4);
		
		ArrayList<Unit> red1Units = (ArrayList<Unit>)(playerUnits.get(red1));
		ArrayList<Unit> blue1Units = (ArrayList<Unit>)(playerUnits.get(blue1));
		
		red1Units.add(archer1);
		red1Units.add(archer2);
		blue1Units.add(archer3);
		blue1Units.add(archer4);
		
		// Dead Units.
		Player red0 = playerConstructor.newInstance("Red0");
		Player blue0 = playerConstructor.newInstance("Blue0");
		
		Unit archer5 = archerConstructor.newInstance('E', 5, 5);
		Unit archer6 = archerConstructor.newInstance('F', 6, 6);
		Unit archer7 = archerConstructor.newInstance('G', 7, 7);
		Unit archer8 = archerConstructor.newInstance('H', 8, 8);
		health.setInt(archer5, 0);
		health.setInt(archer6, 0);
		health.setInt(archer7, 0);
		health.setInt(archer8, 0);
		
		ArrayList<Unit> red0Units = (ArrayList<Unit>)(playerUnits.get(red0));
		ArrayList<Unit> blue0Units = (ArrayList<Unit>)(playerUnits.get(blue0));
		
		red0Units.add(archer5);
		red0Units.add(archer6);
		blue0Units.add(archer7);
		blue0Units.add(archer8);
		
		// 1 Player Remaining.
		testPlayers.clear();
		testPlayers.add(red0);
		testPlayers.add(blue1);
		players.set(gameEngine, testPlayers);
		assertTrue((boolean)(isGameOver.invoke(gameEngine)));
		
		testPlayers.clear();
		testPlayers.add(red1);
		testPlayers.add(blue0);
		players.set(gameEngine, testPlayers);
		assertTrue((boolean)(isGameOver.invoke(gameEngine)));
		
		// 2 Players Remaining.
		testPlayers.clear();
		testPlayers.add(red1);
		testPlayers.add(blue1);
		players.set(gameEngine, testPlayers);
		assertFalse((boolean)(isGameOver.invoke(gameEngine)));
	}
}
