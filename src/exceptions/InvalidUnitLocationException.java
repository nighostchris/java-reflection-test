package exceptions;

import java.io.IOException;

public class InvalidUnitLocationException extends IOException 
{
	public static final String OUT_OF_BOUNDS = "outside of Game Map Boundary";
	public static final String TERRAIN = "Blocked by Terrain";
	public static final String UNIT = "Occupied by another Unit";
	
	public InvalidUnitLocationException(char id, String cause)
	{
		super("Unit " + id + " Starting Location " + cause + ".");
	}
}
