package com.massivecraft.massivecore.store.accessor;

import java.lang.reflect.Field;

import com.massivecraft.massivecore.util.ReflectionUtil;

public class FieldAccessor
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Field field;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FieldAccessor(Field field)
	{
		ReflectionUtil.makeAccessible(field);
		this.field = field;
	}
	
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //
	
	public Object get(Object entity)
	{
		try
		{
			return this.field.get(entity);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void set(Object entity, Object val)
	{
		try
		{
			this.field.set(entity, val);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
