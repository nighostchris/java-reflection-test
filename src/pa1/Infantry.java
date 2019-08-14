// TODO: Complete based on UML Diagram.
// TODO: Infantry is Strong against Pikeman, Weak against Cavalry.

package pa1;

public class Infantry extends Unit 
{
	protected static final int ATTACK_INFANTRY = 10;
	protected static final int DEFENSE_INFANTRY = 5;
	protected static final int ATTACK_RANGE_INFANTRY = 1;
	protected static final int MOVEMENT_RANGE_INFANTRY = 5;
	
	public Infantry(char id, int locationX, int locationY)
	{
		super(id, locationX, locationY, ATTACK_INFANTRY, DEFENSE_INFANTRY, ATTACK_RANGE_INFANTRY, MOVEMENT_RANGE_INFANTRY);
	}
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		if (attacker instanceof Cavalry)
		{
			receiveDamage(rawDamage * 1.5);
		}
		else if (attacker instanceof Pikeman)
		{
			receiveDamage(rawDamage / 2.0);
		}
		else
		{
			receiveDamage(rawDamage);
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", id, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Infantry", getStatus());
	}
}
