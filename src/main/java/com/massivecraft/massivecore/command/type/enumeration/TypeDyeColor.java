package com.massivecraft.massivecore.command.type.enumeration;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

import com.massivecraft.massivecore.util.MUtil;

public class TypeDyeColor extends TypeEnum<DyeColor>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeDyeColor i = new TypeDyeColor();
	public static TypeDyeColor get() { return i; }
	public TypeDyeColor()
	{
		super(DyeColor.class);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public ChatColor getVisualColor(DyeColor value, CommandSender sender)
	{
		return MUtil.getChatColor(value);
	}

}
