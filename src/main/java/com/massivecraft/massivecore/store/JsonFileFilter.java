package com.massivecraft.massivecore.store;

import java.io.File;
import java.io.FileFilter;

public class JsonFileFilter implements FileFilter
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public final static String DOTJSON = ".json";
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static final JsonFileFilter i = new JsonFileFilter();
	public static JsonFileFilter get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean accept(File pathname)
	{
		return pathname.getName().toLowerCase().endsWith(DOTJSON);
	}
	
}
