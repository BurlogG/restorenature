package com.massivecraft.massivecore.command.editor;

import org.bukkit.command.CommandSender;

import com.massivecraft.massivecore.command.type.sender.TypeSender;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.store.SenderEntity;

public class PropertyThisSenderEntity<O extends SenderEntity<O>> extends Property<CommandSender, O>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final SenderColl<O> coll;
	public SenderColl<O> getColl() { return this.coll; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public PropertyThisSenderEntity(SenderColl<O> coll)
	{
		super(TypeSender.get(), coll.getTypeEntity(), "this");
		this.coll = coll;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public O getRaw(CommandSender object)
	{
		return coll.get(object);
	}

	@Override
	public void setRaw(CommandSender object, O value)
	{
		return;
	}

}
