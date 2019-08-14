// TODO: Complete based UML Diagram.
// TODO: Pikeman is Strong against Cavalry, Weak against Infantry.

package pa1;

public class Pikeman extends Unit 
{
	protected static final int ATTACK_PIKEMAN = 8;
	protected static final int DEFENSE_PIKEMAN = 7;
	protected static final int ATTACK_RANGE_PIKEMAN = 1;
	protected static final int MOVEMENT_RANGE_PIKEMAN = 3;
	
	public Pikeman(char id, int locationX, int locationY)
	{
		super(id, locationX, locationY, ATTACK_PIKEMAN, DEFENSE_PIKEMAN, ATTACK_RANGE_PIKEMAN, MOVEMENT_RANGE_PIKEMAN);
	}
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		if (attacker instanceof Infantry)
		{
			receiveDamage(rawDamage * 1.5);
		}
		else if (attacker instanceof Cavalry)
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
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", id, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Pikeman", getStatus());
	}
}
