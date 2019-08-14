// TODO: Complete based on UML Diagram.
// TODO: Constructor: Subclasses need to call this constructor in order to initialize the Unit Stats, as they are declared final.
// TODO: getStatus(): Return a String signifying the status of the Unit. DEAD, READY, DONE. READY/DONE status tracked by boolean isReady.
// TODO: beginTurn(): Change status of Unit to READY.
// TODO: endTurn(): Change status of Unit to DONE.
// TODO: isAlive(): Check if Unit is still alive.
// TODO: moveDelta(): Change the location of the Unit. Path-checking done in GameMap.
// TODO: attackUnit(): Damage linearly scaled based on health. Attacker and Defender clash simultaneously (damage each other at same time).
// TODO: abstract receiveDamage(): x1.5 damage dealt, /2.0 damage received, for Advantaged/Disadvantaged TroopType.
// TODO: receiveDamage(): // Round to nearest Integer. Clamp health so it doesn't go negative.
// TODO: heal(): Heal based on constant HEAL_RATE. Remember to not over-heal beyond MAX_HEALTH.
// TODO: equals(): Units with same ID are equal.

package pa1;

public abstract class Unit 
{
	protected static final int MAX_HEALTH = 10;
	protected static final int HEAL_RATE = 3;
	
	protected final char id;
	protected int health = MAX_HEALTH;
	protected final int ATTACK;
	protected final int DEFENSE;
	protected final int ATTACK_RANGE;
	protected final int MOVEMENT_RANGE;
	protected int locationX;
	protected int locationY;
	protected boolean isReady = false;
	
	protected Unit(char id, int locationX, int locationY, int attack, int defense, int attackRange, int movementRange)
	{
		this.id = id;
		this.locationX = locationX;
		this.locationY = locationY;
		this.ATTACK = attack;
		this.DEFENSE = defense;
		this.ATTACK_RANGE = attackRange;
		this.MOVEMENT_RANGE = movementRange;
	}
	
	protected String getStatus()
	{
		if (!isAlive())
		{
			return "DEAD";
		}
		
		if (isReady)
		{
			return "READY";
		}
		else
		{
			return "DONE";
		}
	}
	
	public char getId() 
	{
		return id;
	}

	public int getAttackRange()
	{
		return ATTACK_RANGE;
	}
	
	public int getMovementRange() 
	{
		return MOVEMENT_RANGE;
	}

	public int getLocationX() 
	{
		return locationX;
	}

	public int getLocationY()
	{
		return locationY;
	}

	public boolean isReady() 
	{
		return isReady;
	}
	
	public void beginTurn()
	{
		isReady = true;
	}
	
	public void endTurn()
	{
		isReady = false;
	}
	
	public boolean isAlive()
	{
		return (health > 0);
	}
	
	public void moveDelta(int x, int y)
	{
		locationX += x;
		locationY += y;
	}
	
	public void attackUnit(Unit defender)
	{
		double defenderRawDamageReceived = (ATTACK - defender.DEFENSE) * health * 0.1;
		double attackerRawDamageReceived = (defender.ATTACK - DEFENSE) * defender.health * 0.1;
		
		defender.receiveDamage(defenderRawDamageReceived, this);
		this.receiveDamage(attackerRawDamageReceived, defender);
	}

	public abstract void receiveDamage(double rawDamage, Unit attacker);
	protected void receiveDamage(double rawDamage)
	{
		int damage = (int)(Math.round(rawDamage));
		health -= damage;
		if (health < 0)
		{
			health = 0;
		}
	}
	
	public void heal()
	{
		health += HEAL_RATE;
		if (health > MAX_HEALTH)
		{
			health = MAX_HEALTH;
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Unit)
		{
			return (id == ((Unit)(obj)).getId());
		}
		else
		{
			return false;
		}
	}
}
