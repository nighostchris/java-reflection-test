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

public class UnitTest 
{	
	@Rule
	public Timeout globalTimeout = Timeout.seconds(3);
	
	static Constructor<Archer> archerConstructor;
	static Constructor<Cavalry> cavalryConstructor;
	static Constructor<Infantry> infantryConstructor;
	static Constructor<Pikeman> pikemanConstructor;
	
	static Field health;
	static Field locationX;
	static Field locationY;
	
	static Method isAlive;
	static Method moveDelta;
	static Method heal;
	static Method equals;
	
	static Method attackUnitMelee;
	static Method attackUnitArcher;
	static Method receiveDamageArcher;
	static Method receiveDamageCavalry;
	static Method receiveDamageInfantry;
	static Method receiveDamagePikeman;
	
	Unit archer;
	Unit cavalry;
	Unit infantry;
	Unit pikeman;
	
	Unit archer2;
	Unit cavalry2;
	Unit infantry2;
	Unit pikeman2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		archerConstructor = Archer.class.getDeclaredConstructor(char.class, int.class, int.class);
		archerConstructor.setAccessible(true);
		cavalryConstructor = Cavalry.class.getDeclaredConstructor(char.class, int.class, int.class);
		cavalryConstructor.setAccessible(true);
		infantryConstructor = Infantry.class.getDeclaredConstructor(char.class, int.class, int.class);
		infantryConstructor.setAccessible(true);
		pikemanConstructor = Pikeman.class.getDeclaredConstructor(char.class, int.class, int.class);
		pikemanConstructor.setAccessible(true);
		
		health = Unit.class.getDeclaredField("health");
		health.setAccessible(true);
		locationX = Unit.class.getDeclaredField("locationX");
		locationX.setAccessible(true);
		locationY = Unit.class.getDeclaredField("locationY");
		locationY.setAccessible(true);
		
		isAlive = Unit.class.getDeclaredMethod("isAlive");
		isAlive.setAccessible(true);
		moveDelta = Unit.class.getDeclaredMethod("moveDelta", int.class, int.class);
		moveDelta.setAccessible(true);
		heal = Unit.class.getDeclaredMethod("heal");
		heal.setAccessible(true);
		equals = Unit.class.getDeclaredMethod("equals", Object.class);
		equals.setAccessible(true);
		
		attackUnitMelee = Unit.class.getDeclaredMethod("attackUnit", Unit.class);
		attackUnitMelee.setAccessible(true);
		attackUnitArcher = Archer.class.getDeclaredMethod("attackUnit", Unit.class);
		attackUnitArcher.setAccessible(true);
		receiveDamageArcher = Archer.class.getDeclaredMethod("receiveDamage", double.class, Unit.class);
		receiveDamageArcher.setAccessible(true);
		receiveDamageCavalry = Cavalry.class.getDeclaredMethod("receiveDamage", double.class, Unit.class);
		receiveDamageCavalry.setAccessible(true);
		receiveDamageInfantry = Infantry.class.getDeclaredMethod("receiveDamage", double.class, Unit.class);
		receiveDamageInfantry.setAccessible(true);
		receiveDamagePikeman = Pikeman.class.getDeclaredMethod("receiveDamage", double.class, Unit.class);
		receiveDamagePikeman.setAccessible(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
		archerConstructor = null;
		cavalryConstructor = null;
		infantryConstructor = null;
		pikemanConstructor = null;
		
		health = null;
		locationX = null;
		locationY = null;
		
		isAlive = null;
		moveDelta = null;
		heal = null;
		equals = null;
		
		attackUnitMelee = null;
		attackUnitArcher = null;
		receiveDamageArcher = null;
		receiveDamageCavalry = null;
		receiveDamageInfantry = null;
		receiveDamagePikeman = null;
	}

	@Before
	public void setUp() throws Exception 
	{
		// All within melee range of archer.
		archer = archerConstructor.newInstance('A', 1, 1);
		cavalry = cavalryConstructor.newInstance('C', 0, 1);
		infantry = infantryConstructor.newInstance('I', 1, 0);
		pikeman = pikemanConstructor.newInstance('P', 2, 1);
		
		// All out of melee range of archer, except archer2 still within attack range.
		archer2 = archerConstructor.newInstance('B', 2, 2);
		cavalry2 = cavalryConstructor.newInstance('D', 3, 3);
		infantry2 = infantryConstructor.newInstance('J', 3, 2);
		pikeman2 = pikemanConstructor.newInstance('Q', 2, 3);
	}

	@After
	public void tearDown() throws Exception 
	{
		archer = null;
		cavalry = null;
		infantry = null;
		pikeman = null;
		
		archer2 = null;
		cavalry2 = null;
		infantry2 = null;
		pikeman2 = null;
	}

	@Test
	public void testIsAlive() throws Exception
	{
		assertTrue((boolean)(isAlive.invoke(archer)));
		
		for (int i = 10; i > 0; i--)
		{
			health.set(archer, i);
			assertTrue(archer.isAlive());
		}
		
		health.setInt(archer, 0);
		assertFalse(archer.isAlive());
	}

	@Test
	public void testMoveDelta() throws Exception
	{
		moveDelta.invoke(archer, 0, 0);
		assertEquals(1, locationX.getInt(archer));
		assertEquals(1, locationY.getInt(archer));
		
		moveDelta.invoke(archer, 2, 2);
		assertEquals(3, locationX.getInt(archer));
		assertEquals(3, locationY.getInt(archer));
		
		moveDelta.invoke(archer, 2, 0);
		assertEquals(5, locationX.getInt(archer));
		assertEquals(3, locationY.getInt(archer));
		
		moveDelta.invoke(archer, 0, 3);
		assertEquals(5, locationX.getInt(archer));
		assertEquals(6, locationY.getInt(archer));
		
		moveDelta.invoke(archer, 6, 5);
		assertEquals(11, locationX.getInt(archer));
		assertEquals(11, locationY.getInt(archer));
		
		moveDelta.invoke(archer, -2, 0);
		assertEquals(9, locationX.getInt(archer));
		assertEquals(11, locationY.getInt(archer));
		
		moveDelta.invoke(archer, -9, -5);
		assertEquals(0, locationX.getInt(archer));
		assertEquals(6, locationY.getInt(archer));
		
		moveDelta.invoke(archer, 0, -6);
		assertEquals(0, locationX.getInt(archer));
		assertEquals(0, locationY.getInt(archer));
	}

	@Test
	public void testAttackUnitMelee() throws Exception
	{
		// Cavalry
		health.setInt(cavalry, 10);
		health.setInt(archer2, 10);
		attackUnitMelee.invoke(cavalry, archer2);
		assertTrue(health.getInt(cavalry) < 10);
		assertTrue(health.getInt(archer2) < 10);
		
		health.setInt(cavalry, 10);
		health.setInt(cavalry2, 10);
		attackUnitMelee.invoke(cavalry, cavalry2);
		assertTrue(health.getInt(cavalry) < 10);
		assertTrue(health.getInt(cavalry2) < 10);
		
		health.setInt(cavalry, 10);
		health.setInt(infantry2, 10);
		attackUnitMelee.invoke(cavalry, infantry2);
		assertTrue(health.getInt(cavalry) < 10);
		assertTrue(health.getInt(infantry2) < 10);
		
		health.setInt(cavalry, 10);
		health.setInt(pikeman2, 10);
		attackUnitMelee.invoke(cavalry, pikeman2);
		assertTrue(health.getInt(cavalry) < 10);
		assertTrue(health.getInt(pikeman2) < 10);
		
		// Infantry
		health.setInt(infantry, 10);
		health.setInt(archer2, 10);
		attackUnitMelee.invoke(infantry, archer2);
		assertTrue(health.getInt(infantry) < 10);
		assertTrue(health.getInt(archer2) < 10);
		
		health.setInt(infantry, 10);
		health.setInt(cavalry2, 10);
		attackUnitMelee.invoke(infantry, cavalry2);
		assertTrue(health.getInt(infantry) < 10);
		assertTrue(health.getInt(cavalry2) < 10);
		
		health.setInt(infantry, 10);
		health.setInt(infantry2, 10);
		attackUnitMelee.invoke(infantry, infantry2);
		assertTrue(health.getInt(infantry) < 10);
		assertTrue(health.getInt(infantry2) < 10);
		
		health.setInt(infantry, 10);
		health.setInt(pikeman2, 10);
		attackUnitMelee.invoke(infantry, pikeman2);
		assertTrue(health.getInt(infantry) < 10);
		assertTrue(health.getInt(pikeman2) < 10);		
		
		// Pikeman
		health.setInt(pikeman, 10);
		health.setInt(archer2, 10);
		attackUnitMelee.invoke(pikeman, archer2);
		assertTrue(health.getInt(pikeman) < 10);
		assertTrue(health.getInt(archer2) < 10);
		
		health.setInt(pikeman, 10);
		health.setInt(cavalry2, 10);
		attackUnitMelee.invoke(pikeman, cavalry2);
		assertTrue(health.getInt(pikeman) < 10);
		assertTrue(health.getInt(cavalry2) < 10);
		
		health.setInt(pikeman, 10);
		health.setInt(infantry2, 10);
		attackUnitMelee.invoke(pikeman, infantry2);
		assertTrue(health.getInt(pikeman) < 10);
		assertTrue(health.getInt(infantry2) < 10);
		
		health.setInt(pikeman, 10);
		health.setInt(pikeman2, 10);
		attackUnitMelee.invoke(pikeman, pikeman2);
		assertTrue(health.getInt(pikeman) < 10);
		assertTrue(health.getInt(pikeman2) < 10);
	}
	
	@Test
	public void testAttackUnitArcherWithinMeleeUnitDistance() throws Exception
	{
		// Within Melee Counterattack Range
		health.setInt(archer, 10);
		health.setInt(cavalry, 10);
		attackUnitArcher.invoke(archer, cavalry);
		assertTrue(health.getInt(archer) < 10);
		assertTrue(health.getInt(cavalry) < 10);
		
		health.setInt(archer, 10);
		health.setInt(infantry, 10);
		attackUnitArcher.invoke(archer, infantry);
		assertTrue(health.getInt(archer) < 10);
		assertTrue(health.getInt(infantry) < 10);
		
		health.setInt(archer, 10);
		health.setInt(pikeman, 10);
		attackUnitArcher.invoke(archer, pikeman);
		assertTrue(health.getInt(archer) < 10);
		assertTrue(health.getInt(pikeman) < 10);
	}
	
	@Test
	public void testAttackUnitArcherOutsideMeleeUnitDistance() throws Exception
	{
		// Out of Melee Counterattack Range		
		health.setInt(archer, 10);
		health.setInt(cavalry2, 10);
		attackUnitArcher.invoke(archer, cavalry2);
		assertEquals(10, health.getInt(archer));
		assertTrue(health.getInt(cavalry2) < 10);
		
		health.setInt(archer, 10);
		health.setInt(infantry2, 10);
		attackUnitArcher.invoke(archer, infantry2);
		assertEquals(10, health.getInt(archer));
		assertTrue(health.getInt(infantry2) < 10);
		
		health.setInt(archer, 10);
		health.setInt(pikeman2, 10);
		attackUnitArcher.invoke(archer, pikeman2);
		assertEquals(10, health.getInt(archer));
		assertTrue(health.getInt(pikeman2) < 10);
	}
	
	@Test
	public void testAttackUnitArcherWithinArcherMeleeDistance() throws Exception
	{
		// Archer vs Archer, Melee Distance
		Unit archerMelee = archerConstructor.newInstance('M', 1, 2);
		health.setInt(archer, 10);
		health.setInt(archerMelee, 10);
		attackUnitArcher.invoke(archer, archerMelee);
		assertTrue(health.getInt(archer) < 10);
		assertTrue(health.getInt(archerMelee) < 10);
	}
	
	@Test
	public void testAttackUnitArcherWithinArcherRangedDistance() throws Exception
	{
		// Archer vs Archer, Within Archer Ranged
		health.setInt(archer, 10);
		health.setInt(archer2, 10);
		attackUnitArcher.invoke(archer, archer2);
		assertTrue(health.getInt(archer) < 10);
		assertTrue(health.getInt(archer2) < 10);
	}

	@Test
	public void testReceiveDamageArcher() throws Exception
	{
		for (int h = 10; h >= 0; h--)
		{
			for (double damage = 0; damage < 15; damage += 0.1)
			{
				int expectedDamage = (int)(Math.round(damage));
				int expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(archer, h);
				receiveDamageArcher.invoke(archer, damage, archer);
				assertEquals(expectedHealth, health.getInt(archer));
				
				health.setInt(archer, h);
				receiveDamageArcher.invoke(archer, damage, cavalry);
				assertEquals(expectedHealth, health.getInt(archer));
				
				health.setInt(archer, h);
				receiveDamageArcher.invoke(archer, damage, infantry);
				assertEquals(expectedHealth, health.getInt(archer));
				
				health.setInt(archer, h);
				receiveDamageArcher.invoke(archer, damage, pikeman);
				assertEquals(expectedHealth, health.getInt(archer));
			}
		}
	}
	
	@Test
	public void testReceiveDamageCavalry() throws Exception
	{
		for (int h = 10; h >= 0; h--)
		{
			for (double damage = 0; damage < 15; damage += 0.1)
			{
				// Pikeman
				int expectedDamage = (int)(Math.round(damage * 1.5));
				int expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(cavalry, h);
				receiveDamageCavalry.invoke(cavalry, damage, pikeman);
				assertEquals(expectedHealth, health.getInt(cavalry));
				
				// Infantry
				expectedDamage = (int)(Math.round(damage / 2.0));
				expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(cavalry, h);
				receiveDamageCavalry.invoke(cavalry, damage, infantry);
				assertEquals(expectedHealth, health.getInt(cavalry));
				
				// Cavalry and Archer
				expectedDamage = (int)(Math.round(damage));
				expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(cavalry, h);
				receiveDamageCavalry.invoke(cavalry, damage, cavalry);
				assertEquals(expectedHealth, health.getInt(cavalry));
				
				health.setInt(cavalry, h);
				receiveDamageCavalry.invoke(cavalry, damage, archer);
				assertEquals(expectedHealth, health.getInt(cavalry));
			}
		}
	}	
	
	@Test
	public void testReceiveDamageInfantry() throws Exception
	{
		for (int h = 10; h >= 0; h--)
		{
			for (double damage = 0; damage < 15; damage += 0.1)
			{
				// Cavalry
				int expectedDamage = (int)(Math.round(damage * 1.5));
				int expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(infantry, h);
				receiveDamageInfantry.invoke(infantry, damage, cavalry);
				assertEquals(expectedHealth, health.getInt(infantry));
				
				// Pikeman
				expectedDamage = (int)(Math.round(damage / 2.0));
				expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(infantry, h);
				receiveDamageInfantry.invoke(infantry, damage, pikeman);
				assertEquals(expectedHealth, health.getInt(infantry));
				
				// Infantry and Archer
				expectedDamage = (int)(Math.round(damage));
				expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(infantry, h);
				receiveDamageInfantry.invoke(infantry, damage, infantry);
				assertEquals(expectedHealth, health.getInt(infantry));
				
				health.setInt(infantry, h);
				receiveDamageInfantry.invoke(infantry, damage, archer);
				assertEquals(expectedHealth, health.getInt(infantry));
			}
		}
	}	
	
	@Test
	public void testReceiveDamagePikeman() throws Exception
	{
		for (int h = 10; h >= 0; h--)
		{
			for (double damage = 0; damage < 15; damage += 0.1)
			{
				// Infantry
				int expectedDamage = (int)(Math.round(damage * 1.5));
				int expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(pikeman, h);
				receiveDamagePikeman.invoke(pikeman, damage, infantry);
				assertEquals(expectedHealth, health.getInt(pikeman));
				
				// Cavalry
				expectedDamage = (int)(Math.round(damage / 2.0));
				expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(pikeman, h);
				receiveDamagePikeman.invoke(pikeman, damage, cavalry);
				assertEquals(expectedHealth, health.getInt(pikeman));
				
				// Pikeman and Archer
				expectedDamage = (int)(Math.round(damage));
				expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(pikeman, h);
				receiveDamagePikeman.invoke(pikeman, damage, pikeman);
				assertEquals(expectedHealth, health.getInt(pikeman));
				
				health.setInt(pikeman, h);
				receiveDamagePikeman.invoke(pikeman, damage, archer);
				assertEquals(expectedHealth, health.getInt(pikeman));
			}
		}
	}	
	
	/*@Test
	public void testReceiveDamage() throws Exception
	{
		Method receiveDamage = Unit.class.getDeclaredMethod("receiveDamage", double.class);
		receiveDamage.setAccessible(true);
		
		for (int h = 10; h >= 0; h--)
		{
			for (double damage = 0; damage < 15; damage += 0.1)
			{
				int expectedDamage = (int)(Math.round(damage));
				int expectedHealth = h - expectedDamage;
				expectedHealth = (expectedHealth < 0) ? 0 : expectedHealth;
				
				health.setInt(archer, h);
				receiveDamage.invoke(archer, damage);
				
				assertEquals(expectedHealth, health.getInt(archer));
			}
		}
	}*/

	@Test
	public void testHeal() throws Exception
	{
		Field health = Unit.class.getDeclaredField("health");
		health.setAccessible(true);
		
		for (int i = 0; i <= 7; i++)
		{
			health.setInt(archer, i);
			heal.invoke(archer);
			assertEquals(i + 3, health.getInt(archer));
		}
		
		for (int i = 8; i <= 10; i++)
		{
			health.setInt(archer, i);
			heal.invoke(archer);
			assertEquals(10, health.getInt(archer));
		}
	}

	@Test
	public void testEqualsObject() throws Exception
	{
		Unit archerElse = archerConstructor.newInstance('B', 0, 0);
		Unit archerSame = archerConstructor.newInstance('A', 0, 0);
		
		assertFalse((boolean)(equals.invoke(archer, archerElse)));
		assertTrue((boolean)(equals.invoke(archer, archerSame)));
	}
}
