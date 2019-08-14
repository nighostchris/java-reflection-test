// TODO: Complete based on UML Diagram.
// TODO: Cavalry is Strong against Infantry, Weak against Pikeman.

package pa1;

public class Cavalry extends Unit 
{
	protected static final int ATTACK_CAVALRY = 14;
	protected static final int DEFENSE_CAVALRY = 3;
	protected static final int ATTACK_RANGE_CAVALRY = 1;
	protected static final int MOVEMENT_RANGE_CAVALRY = 10;
	
	public Cavalry(char id, int locationX, int locationY)
	{
		super(id, locationX, locationY, ATTACK_CAVALRY, DEFENSE_CAVALRY, ATTACK_RANGE_CAVALRY, MOVEMENT_RANGE_CAVALRY);
	}
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		if (attacker instanceof Pikeman)
		{
			receiveDamage(rawDamage * 1.5);
		}
		else if (attacker instanceof Infantry)
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
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", id, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Cavalry", getStatus());
	}
}
