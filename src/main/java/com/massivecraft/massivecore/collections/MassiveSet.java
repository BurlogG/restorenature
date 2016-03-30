package com.massivecraft.massivecore.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This subclass adds better constructors. 
 */
public class MassiveSet<E> extends LinkedHashSet<E>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// CONSTRUCT: BASE
	// -------------------------------------------- //
	
	public MassiveSet(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	public MassiveSet(int initialCapacity)
	{
		super(initialCapacity);
	}

	public MassiveSet()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	public MassiveSet(Collection<? extends E> c)
	{
		// Support Null
		super(c == null ? Collections.EMPTY_LIST : c);
	}
	
	// -------------------------------------------- //
	// CONSTRUCT: EXTRA
	// -------------------------------------------- //
	
	@SafeVarargs
	public MassiveSet(E... elements)
	{
		this(Arrays.asList(elements));
	}
	
	// -------------------------------------------- //
	// OPTIMIZE: REMOVE ALL & RETAIN ALL
	// -------------------------------------------- //
	// This will greatly reduce the complexity in cases with big sizes.
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		if (c instanceof List) c = new HashSet<Object>(c);
		return super.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		if (c instanceof List) c = new HashSet<Object>(c);
		return super.retainAll(c);
	}

}
