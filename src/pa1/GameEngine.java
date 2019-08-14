package pa1;

import java.util.ArrayList;

import java.io.File;
import java.util.Scanner;

import java.io.IOException;
import exceptions.DuplicatePlayerNameException;
import exceptions.DuplicateUnitIDException;
import exceptions.InvalidUnitTypeException;



public class GameEngine 
{
	private final GameMap gameMap = new GameMap();
	private final ArrayList<Player> players = new ArrayList<>();
	private static final Scanner userInputScanner = new Scanner(System.in);
	
	// TODO: Load the Players and Units based on specified input textfile, with correct formatting.
	// Throw an IOException if errors are found.
	// Use try-with-resources.
	// Call checkStartingLocation() from GameMap when initializing Unit Starting Locations. Hint: this method already throws the IOExceptions.
	// Call updateUnitLocationOnMap() when Units passed all error-checking and added to game.
	// Hint: ArrayList has the contains() method, which uses the equals() method to check if an object is already inside the ArrayList.
	// 1) Duplicate Player Names are not allowed.
	// 2) Duplicate Unit ID is not allowed.
	// 3) Check if Unit Type is invalid. Hint: Java supports switch-case on a String.
	private void loadPlayersAndUnits(String filename) throws IOException
	{
		try (Scanner scanner = new Scanner(new File(filename)))
		{
			// Temporary Global Unit List for Duplicate Unit ID Checking.
			ArrayList<Unit> globalUnitList = new ArrayList<Unit>();
			
			int numPlayers = scanner.nextInt();
			for (int n = 0; n < numPlayers; n++)
			{
				String name = scanner.next();
				Player player = new Player(name);
				
				if (players.contains(player))
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
						unit = new Infantry(unitId, locationX, locationY);
						break;
					case "Pikeman":
						unit = new Pikeman(unitId, locationX, locationY);
						break;
					case "Cavalry":
						unit = new Cavalry(unitId, locationX, locationY);
						break;
					case "Archer":
						unit = new Archer(unitId, locationX, locationY);
						break;
					default:
						throw new InvalidUnitTypeException(unitType);
					}
					
					if (globalUnitList.contains(unit))
					{
						throw new DuplicateUnitIDException(unitId);
					}
					gameMap.checkStartingLocation(unitId, locationX, locationY);
					
					globalUnitList.add(unit);
					player.addUnit(unit);
					gameMap.updateUnitLocationOnMap(GameMap.TERRAIN_OUT_OF_BOUNDS, GameMap.TERRAIN_OUT_OF_BOUNDS, locationX, locationY);
				}
				
				players.add(player);
			}
		}
	}
	
	private void processPlayerTurns()
	{
		for (Player player:players)
		{
			if (player.hasUnitsRemaining())
			{				
				System.out.println(player.getName() + " Turn:");
				player.readyAllUnits();
				
				while (player.hasReadyUnits())
				{
					gameMap.renderMap(players);
					displayPlayers();
					
					Unit unit = selectUnit(player);
					
					while (true)
					{
						System.out.print("Heal, Move&Attack [H,M]: ");
						char choice = (userInputScanner.next()).charAt(0);
						
						if (choice == 'H')
						{
							unit.heal();
							unit.endTurn();
							break;
						}
						else if (choice == 'M')
						{
							processUnitMovementPhase(unit);
							processUnitAttackPhase(player, unit);
							unit.endTurn();
							
							// Game Over potentially occurs when a Unit dies. No need to keep processing the turn if Game Over.
							if (isGameOver())
							{
								return;
							}
							
							break;
						}
						else
						{
							System.out.println("Invalid command.");
						}
					}
				}
			}
			
			// More newlines between each player's turns.
			System.out.print("\n\n\n");
		}
	}
	
	private Unit selectUnit(Player currentPlayer)
	{
		String readyUnits = "";
		for (Unit unit:currentPlayer.getUnitList())
		{
			if (unit.isAlive() && unit.isReady())
			{
				readyUnits += unit.getId();
			}
		}
		
		while (true)
		{
			System.out.print("Select Unit [" + readyUnits + "]: ");
			char inputId = (userInputScanner.next()).charAt(0);
		
			if (readyUnits.contains(Character.toString(inputId)))
			{
				return currentPlayer.getUnitById(inputId);
			}
			else
			{
				System.out.println("Invalid selection.");
			}
		} 
	}
	
	private void processUnitMovementPhase(Unit currentUnit)
	{
		int deltaX, deltaY;
		do
		{
			System.out.print("Enter Movement X Y Amount (0 0 to stay): ");
			deltaX = userInputScanner.nextInt();
			deltaY = userInputScanner.nextInt();
		
			if ((deltaX == 0) && (deltaY == 0))
			{
				return;
			}
		}
		while (!gameMap.isValidPath(currentUnit.getLocationX(), currentUnit.getLocationY(), currentUnit.getLocationX() + deltaX, currentUnit.getLocationY() + deltaY, currentUnit.getMovementRange()));
		
		gameMap.updateUnitLocationOnMap(currentUnit.getLocationX(), currentUnit.getLocationY(), currentUnit.getLocationX() + deltaX, currentUnit.getLocationY() + deltaY);
		currentUnit.moveDelta(deltaX, deltaY);
		
		gameMap.renderMap(players);
		displayPlayers();
	}
	
	private void processUnitAttackPhase(Player currentPlayer, Unit currentUnit)
	{
		// Get list of Enemy Units in range.
		ArrayList<Unit> enemyUnitsInRange = new ArrayList<>(); 
		String enemyUnitsInRangeById = "";
		for (Player player:players)
		{
			if (!player.equals(currentPlayer))
			{
				for (Unit unit:player.getUnitList())
				{
					if (unit.isAlive())
					{
						if ((Math.abs(unit.getLocationX() - currentUnit.getLocationX()) + Math.abs(unit.getLocationY() - currentUnit.getLocationY())) <= currentUnit.getAttackRange())
						{
							enemyUnitsInRange.add(unit);
							enemyUnitsInRangeById += unit.getId();
						}
					}
				}
			}
		}
		
		// No Enemy Units in Attack Range.
		if (enemyUnitsInRangeById.equals(""))
		{
			System.out.println("No Enemy Units in Attack Range. Ending Turn for Unit " + currentUnit.getId() + "");
			return;
		}
		
		while (true)
		{
			System.out.print("Choose Attack Target [" + enemyUnitsInRangeById + " , / to Not Attack]: ");
			char enemyUnitId = (userInputScanner.next()).charAt(0);
			
			if (enemyUnitsInRangeById.contains(Character.toString(enemyUnitId)))
			{
				for (Unit enemyUnit:enemyUnitsInRange)
				{
					if (enemyUnit.getId() == enemyUnitId)
					{
						currentUnit.attackUnit(enemyUnit);
						
						// If the attacker or defender died, remove from Game Map.
						if (!currentUnit.isAlive())
						{
							gameMap.updateUnitLocationOnMap(currentUnit.getLocationX(), currentUnit.getLocationY(), GameMap.TERRAIN_OUT_OF_BOUNDS, GameMap.TERRAIN_OUT_OF_BOUNDS);
						}
						
						if (!enemyUnit.isAlive())
						{
							gameMap.updateUnitLocationOnMap(enemyUnit.getLocationX(), enemyUnit.getLocationY(), GameMap.TERRAIN_OUT_OF_BOUNDS, GameMap.TERRAIN_OUT_OF_BOUNDS);
						}
						
						return;
					}
				}
			}
			else if (enemyUnitId == '/')
			{
				return;
			}
			else
			{
				System.out.println("Invalid Attack Target.");
			}
		}
	}
	
	private void displayPlayers() 
	{
		for (Player player:players) 
		{
			System.out.println(player.getName() + ":");
			
			for (Unit unit:player.getUnitList())
			{
				System.out.println("\t" + unit);
			}
			
			System.out.println();
		}
	}
	
	// TODO: If only 1 player has Units Remaining, then the game has ended.
	private boolean isGameOver() 
	{
		int playersRemaining = 0;
		
		for (Player player : players) 
		{
			if (player.hasUnitsRemaining())
			{
				playersRemaining += 1;
			}
		}
		
		return (playersRemaining <= 1);
	}

	private void displayGameOver() 
	{
		System.out.print("\n\n\n");
		gameMap.renderMap(players);
		displayPlayers();
		System.out.println("GAME OVER\n");
		
		for (Player player:players) 
		{
			if (player.hasUnitsRemaining())
			{
				System.out.println(player.getName() + " WON.\n");
			}
			else
			{
				System.out.println(player.getName() + " LOST.\n");
			}
		}
	}
	
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("args[0] is textfile for GameMap, args[1] is textfile for Players&Units.");
			return;
		}
		
		GameEngine game = new GameEngine();
		
		// Load the GameMap, Players, and Units, from user-input textfiles.
		try
		{
			game.gameMap.loadTerrainMap(args[0]);
			game.loadPlayersAndUnits(args[1]);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		// Run the game.
		while (!game.isGameOver()) 
		{
			game.processPlayerTurns();
		}
		
		// Game Over.
		game.displayGameOver();
		
		// Close userInput.
		userInputScanner.close();
	}
}
