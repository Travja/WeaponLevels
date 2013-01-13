package me.innoko.weaponlevels;

public class Twin<O1>
{
	private O1 object1;
	private O1 object2;

	public Twin(O1 obj1, O1 obj2)
	{
		object1 = obj1;
		object2 = obj2;
	}

	public O1 getFirst()
	{
		return object1;
	}

	public O1 getSecond()
	{
		return object2;
	}
}
