package com.massivecraft.massivecore.money;

import java.util.Collection;
import java.util.Collections;

public abstract class MoneyMixinAbstract implements MoneyMixin
{
	// -------------------------------------------- //
	// FORMAT AND NAME
	// -------------------------------------------- //
	
	public String format(double amount)
	{
		return this.format(amount, true);
	}
	
	// -------------------------------------------- //
	// FRACTIONAL DIGITS
	// -------------------------------------------- //
	
	@Override
	public double prepare(double amount)
	{
		final int fractionalDigits = this.fractionalDigits();
		
		// -1 means infinite amount of fractional digits
		if (fractionalDigits < 0) return amount;
		
		// 0 means no fractional digits
		if (fractionalDigits == 0) return moneyCeil(amount);
		
		// OK! I'll have to calculate :P
		int factor = (int) Math.round(Math.pow(10, fractionalDigits));
		amount = amount * factor;
		amount = Math.ceil(amount);
		amount = amount / factor;
		
		return amount;
	}
	
	public static double moneyCeil(double amount)
	{
		if (amount < 0) return Math.floor(amount);
		else return Math.ceil(amount);
	}
	
	// -------------------------------------------- //
	// MOVE
	// -------------------------------------------- //
	
	// this is the abstract one
	// public boolean move(String fromId, String toId, String byId, double amount, Collection<String> categories, String message);
	
	public boolean move(String fromId, String toId, String byId, double amount, String category, Object message)
	{
		return this.move(fromId, toId, byId, amount, (category == null ? null : Collections.singletonList(category)), message);
	}
	public boolean move(String fromId, String toId, String byId, double amount, Collection<String> categories)
	{
		return this.move(fromId, toId, byId, amount, categories, null);
	}
	public boolean move(String fromId, String toId, String byId, double amount, String category)
	{
		return this.move(fromId, toId, byId, amount, (category == null ? null : Collections.singletonList(category)), null);
	}
	public boolean move(String fromId, String toId, String byId, double amount)
	{
		return this.move(fromId, toId, byId, amount, Collections.<String>emptyList(), null);
	}
	
	// -------------------------------------------- //
	// SPAWN
	// -------------------------------------------- //
	
	public boolean spawn(String toId, String byId, double amount, Collection<String> categories, Object message)
	{
		return this.move(null, toId, byId, amount, categories, message);
	}
	public boolean spawn(String toId, String byId, double amount, String category, Object message)
	{
		return this.move(null, toId, byId, amount, category, message);
	}
	public boolean spawn(String toId, String byId, double amount, Collection<String> categories)
	{
		return this.move(null, toId, byId, amount, categories);
	}
	public boolean spawn(String toId, String byId, double amount, String category)
	{
		return this.move(null, toId, byId, amount, category);
	}
	public boolean spawn(String toId, String byId, double amount)
	{
		return this.move(null, toId, byId, amount);
	}
	
	// -------------------------------------------- //
	// DESPAWN
	// -------------------------------------------- //
	
	public boolean despawn(String fromId, String byId, double amount, Collection<String> categories, Object message)
	{
		return this.move(fromId, null, byId, amount, categories, message);
	}
	public boolean despawn(String fromId, String byId, double amount, String category, Object message)
	{
		return this.move(fromId, null, byId, amount, category, message);
	}
	public boolean despawn(String fromId, String byId, double amount, Collection<String> categories)
	{
		return this.move(fromId, null, byId, amount, categories);
	}
	public boolean despawn(String fromId, String byId, double amount, String category)
	{
		return this.move(fromId, null, byId, amount, category);
	}
	public boolean despawn(String fromId, String byId, double amount)
	{
		return this.move(fromId, null, byId, amount);
	}
	
	// -------------------------------------------- //
	// SET
	// -------------------------------------------- //
	
	public boolean set(String accountId, String byId, double amount, Collection<String> categories, Object message)
	{
		return this.move(null, accountId, byId, amount - this.get(accountId), categories, message);
	}
	public boolean set(String accountId, String byId, double amount, String category, Object message)
	{
		return this.move(null, accountId, byId, amount - this.get(accountId), category, message);
	}
	public boolean set(String accountId, String byId, double amount, Collection<String> categories)
	{
		return this.move(null, accountId, byId, amount - this.get(accountId), categories);
	}
	public boolean set(String accountId, String byId, double amount, String category)
	{
		return this.move(null, accountId, byId, amount - this.get(accountId), category);
	}
	public boolean set(String accountId, String byId, double amount)
	{
		return this.move(null, accountId, byId, amount - this.get(accountId));
	}
	
}