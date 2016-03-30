package com.massivecraft.massivecore.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * This subclass adds better constructors. 
 */
public class MassiveList<E> extends ArrayList<E>
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// CONSTRUCT: BASE
	// -------------------------------------------- //
	
	public MassiveList(int initialCapacity)
	{
		super(initialCapacity);
	}

	public MassiveList()
	{
		super();
	}
	
	@SuppressWarnings("unchecked")
	public MassiveList(Collection<? extends E> c)
	{
		// Support Null
		super(c == null ? Collections.EMPTY_LIST : c);
	}
	
	// -------------------------------------------- //
	// CONSTRUCT: EXTRA
	// -------------------------------------------- //
	
	@SafeVarargs
	public MassiveList(E... elements)
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
