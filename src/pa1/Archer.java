// TODO: Complete based on UML Diagram.
// TODO: Archer has no Type Advantage/Disadvantage to other Units.
// TODO: Archer doesn't clash if out-of-range from other Melee Units. Archers will clash with each other at range.

package pa1;

public class Archer extends Unit
{
	protected static final int ATTACK_ARCHER = 9;
	protected static final int DEFENSE_ARCHER = 1;
	protected static final int ATTACK_RANGE_ARCHER = 5;
	protected static final int MOVEMENT_RANGE_ARCHER = 3;
	
	public Archer(char id, int locationX, int locationY)
	{
		super(id, locationX, locationY, ATTACK_ARCHER, DEFENSE_ARCHER, ATTACK_RANGE_ARCHER, MOVEMENT_RANGE_ARCHER);
	}
	
	@Override
	public void attackUnit(Unit defender)
	{
		double defenderRawDamageReceived = (ATTACK - defender.DEFENSE) * health * 0.1;
		double attackerRawDamageReceived = (defender.ATTACK - DEFENSE) * defender.health * 0.1;
		
		defender.receiveDamage(defenderRawDamageReceived);
		
		if ((Math.abs(locationX - defender.locationX) + Math.abs(locationY - defender.locationY)) <= defender.getAttackRange())
		{
			this.receiveDamage(attackerRawDamageReceived);
		}
	}
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		receiveDamage(rawDamage);
	}
	
	@Override
	public String toString()
	{
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", id, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Archer", getStatus());
	}
}
