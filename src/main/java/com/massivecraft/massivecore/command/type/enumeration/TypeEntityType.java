package com.massivecraft.massivecore.command.type.enumeration;

import java.util.Set;

import org.bukkit.entity.EntityType;

import com.massivecraft.massivecore.collections.MassiveSet;

public class TypeEntityType extends TypeEnum<EntityType>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeEntityType i = new TypeEntityType();
	public static TypeEntityType get() { return i; }
	public TypeEntityType()
	{
		super(EntityType.class);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<String> getNamesInner(EntityType value)
	{
		Set<String> ret = new MassiveSet<String>(super.getNamesInner(value));
		
		if (value == EntityType.PIG_ZOMBIE)
		{
			ret.add("pigman");
			ret.add("pigzombie");
			ret.add("manpig");
			ret.add("zombiepig");
		}
		
		return ret;
	}

}
