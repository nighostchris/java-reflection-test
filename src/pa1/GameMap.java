package pa1;

import java.util.ArrayList;

import java.io.File;
import java.util.Scanner;

import java.io.IOException;
import exceptions.InvalidTerrainTypeException;
import exceptions.InvalidUnitLocationException;



// Origin (0, 0) is at top-left corner; X-axis increases from left-to-right; Y-axis increases from top-to-bottom.
public class GameMap 
{
	public static final int TERRAIN_OUT_OF_BOUNDS = -1;
	public static final int TERRAIN_EMPTY = 0;
	public static final int TERRAIN_BLOCKED = 1;
	public static final int TERRAIN_OCCUPIED = 2;
	
	public static final char TERRAIN_EMPTY_CHAR = ' ';
	public static final char TERRAIN_BLOCKED_CHAR = '#';
	
	private int height, width;
	private int[][] terrainMap;
	private char[][] displayMap;
	
	// TODO: Load the terrainMap based on specified input textfile, with correct formatting.
	// Throw an IOException if the input textfile format is wrong.
	// Use try-with-resources.
	public void loadTerrainMap(String filename) throws IOException
	{
		try (Scanner scanner = new Scanner(new File(filename)))
		{
			width = scanner.nextInt();
			height = scanner.nextInt();
			scanner.nextLine();
			
			terrainMap = new int[height][width];
			displayMap = new char[height][width];
			
			for (int y = 0; y < height; y++)
			{
				String line = scanner.nextLine();
				for (int x = 0; x < width; x++)
				{
					if (line.charAt(x) == TERRAIN_EMPTY_CHAR)
					{
						terrainMap[y][x] = TERRAIN_EMPTY;
					}
					else if (line.charAt(x) == TERRAIN_BLOCKED_CHAR)
					{
						terrainMap[y][x] = TERRAIN_BLOCKED;
					}
					else
					{
						throw new InvalidTerrainTypeException(line.charAt(x));
					}
				}
			} 
		} 
	}
	
	// TODO: Call this method from GameEngine when placing Units onto their Starting Locations.
	// Throw an IOException whenever there's a problem with the Starting Location.
	// 1) Outside of Game Map Boundary.
	// 2) Blocked by Terrain.
	// 3) Occupied by another Unit.
	public void checkStartingLocation(char id, int locationX, int locationY) throws IOException
	{
		if ((locationX < 0) || (locationY < 0) || (locationX >= width) || (locationY >= height))
		{
			throw new InvalidUnitLocationException(id, InvalidUnitLocationException.OUT_OF_BOUNDS);
		}
		
		if (terrainMap[locationY][locationX] == TERRAIN_BLOCKED)
		{
			throw new InvalidUnitLocationException(id, InvalidUnitLocationException.TERRAIN);
		}
		
		if (terrainMap[locationY][locationX] == TERRAIN_OCCUPIED)
		{
			throw new InvalidUnitLocationException(id, InvalidUnitLocationException.UNIT);
		}
	}
	
	// Use (TERRAIN_OUT_OF_BOUNDS, TERRAIN_OUT_OF_BOUNDS) for adding/removing Units from the Map.
	// (Old, Old) == (TERRAIN_OUT_OF_BOUNDS, TERRAIN_OUT_OF_BOUNDS) for Adding New Units.
	// (New, New) == (TERRAIN_OUT_OF_BOUNDS, TERRAIN_OUT_OF_BOUNDS) for Removing Dead Units.
	public void updateUnitLocationOnMap(int locationXOld, int locationYOld, int locationXNew, int locationYNew)
	{
		if ((locationXOld >= 0) && (locationYOld >= 0))
		{
			terrainMap[locationYOld][locationXOld] = TERRAIN_EMPTY;
		}
		
		if ((locationXNew >= 0) && (locationYNew >= 0))
		{
			terrainMap[locationYNew][locationXNew] = TERRAIN_OCCUPIED;
		}
	}
	
	// TODO: Check the validity of Target Location when Units are Moving.
	// 1) Outside Game Map Boundary.
	// 2) Outside of Movement Range.
	// 3) Blocked by Terrain.
	// 4) Occupied by another Unit.
	// BONUS) Path in-between is blocked by Terrain/Unit(s), and not enough Movement Range to go around (instead of teleporting through).
	public boolean isValidPath(int locationXOld, int locationYOld, int locationXNew, int locationYNew, int movementRange)
	{
		if (((locationXNew < 0) || (locationYNew < 0)) || ((locationXNew >= width) || (locationYNew >= height)))
		{
			System.out.println("Target Location outside of Game Map Boundary.");
			return false;
		}
		
		if ((Math.abs(locationXNew - locationXOld) + (Math.abs(locationYNew - locationYOld))) > movementRange)
		{
			System.out.println("Outside of Movement Range.");
			return false;
		}
		
		if (terrainMap[locationYNew][locationXNew] == TERRAIN_BLOCKED)
		{
			System.out.println("Target Location blocked by Terrain.");
			return false;
		}
		
		if (terrainMap[locationYNew][locationXNew] == TERRAIN_OCCUPIED)
		{
			System.out.println("Target Location occupied by another Unit.");
			return false;
		}
		
		/*if (Obstacle Detection)
		{
			System.out.println("Target Location Outside of Movement Range due to Terrain/Unit(s) in path.");
		}*/
		
		return true;
	}
	
	// TODO: Display the GameMap.
	public void renderMap(ArrayList<Player> players)
	{
		// TODO: Render terrainMap onto displayMap.
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				displayMap[y][x] = (terrainMap[y][x] == TERRAIN_BLOCKED) ? TERRAIN_BLOCKED_CHAR : TERRAIN_EMPTY_CHAR;
			}
		}
		
		// TODO: Render Units onto displayMap.
		for (Player player:players)
		{
			for (Unit unit:player.getUnitList())
			{
				if (unit.isAlive())
				{
					displayMap[unit.getLocationY()][unit.getLocationX()] = unit.getId();
				}
			}
		}
		
		// TODO: Output the Map. Leave a newline before and after the displayed Map.
		System.out.println();
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				System.out.print(displayMap[y][x]);
			}
			System.out.println();
		}
		
		System.out.println();
	}
}
