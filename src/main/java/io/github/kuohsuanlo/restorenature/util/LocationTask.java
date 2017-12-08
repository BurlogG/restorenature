package io.github.kuohsuanlo.restorenature.util;

import org.bukkit.Location;

public class LocationTask {
	public final boolean onlyEntity;
	public final Location location;
	public LocationTask(boolean oe, Location l){
		onlyEntity = oe;
		location = l;
	}
}
