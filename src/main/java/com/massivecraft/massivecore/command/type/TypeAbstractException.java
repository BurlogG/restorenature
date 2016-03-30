package com.massivecraft.massivecore.command.type;

import org.bukkit.command.CommandSender;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public abstract class TypeAbstractException<T> extends TypeAbstract<T>
{
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract T valueOf(String arg, CommandSender sender) throws Exception;
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public T read(String arg, CommandSender sender) throws MassiveException
	{
		try
		{
			return this.valueOf(arg, sender);
		}
		catch (Exception ex)
		{
			throw new MassiveException().addMessage(this.extractErrorMessage(arg, sender, ex));
		}
	}
	
	// -------------------------------------------- //
	// MESSAGE (OVERRIDABLE)
	// -------------------------------------------- //

	public String extractErrorMessage(String arg, CommandSender sender, Exception ex)
	{
		return Txt.parse("<b>%s", ex.getMessage());
	}
	
}
