// TODO: Complete based on UML Diagram.
// TODO: units contains an ArrayList of all Units belonging to this Player.
// TODO: readyAllUnits(): Only Ready Unit if it is still Alive.
// TODO: hasReadyUnits(): Ignore Dead Units.
// TODO: hasUnitsRemaining(): Eg, has Units that are still Alive.
// TODO: equals(): Players with same Name are equal.

package pa1;

import java.util.ArrayList;

public class Player 
{
	private String name = "";
	private final ArrayList<Unit> units = new ArrayList<>();
	
	public Player(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{ 
		return name; 
	}
	
	public ArrayList<Unit> getUnitList()
	{
		return units;
	}
	
	public void addUnit(Unit unit)
	{
		units.add(unit);
	}
	
	public Unit getUnitById(char id)
	{
		for (Unit unit:units)
		{
			if (id == unit.getId())
			{
				return unit;
			}
		}
		
		return null;
	}
	
	public void readyAllUnits()
	{
		for (Unit unit:units)
		{
			if (unit.isAlive())
			{
				unit.beginTurn();
			}
		}
	}
	
	public boolean hasReadyUnits()
	{
		for (Unit unit:units)
		{
			if (unit.isAlive() && unit.isReady())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasUnitsRemaining()
	{
		for (Unit unit:units)
		{
			if (unit.isAlive())
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Player)
		{
			return (name.equals(((Player)(obj)).getName()));
		}
		else
		{
			return false;
		}
	}
}
