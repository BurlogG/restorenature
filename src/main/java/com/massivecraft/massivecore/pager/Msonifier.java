package com.massivecraft.massivecore.pager;

import com.massivecraft.massivecore.mson.Mson;

public interface Msonifier<T>
{
	public Mson toMson(T item, int index);
}
