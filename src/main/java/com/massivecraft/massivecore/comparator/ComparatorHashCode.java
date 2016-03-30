package com.massivecraft.massivecore.comparator;

import java.util.Objects;

public class ComparatorHashCode extends ComparatorAbstract<Object>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static transient ComparatorHashCode i = new ComparatorHashCode();
	public static ComparatorHashCode get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Integer compareInner(Object object1, Object object2)
	{
		return Integer.compare(Objects.hashCode(object1), Objects.hashCode(object2));
	}

}
