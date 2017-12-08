package io.github.kuohsuanlo.restorenature.util;

import org.bukkit.Location;

public class LocationTask {
	public boolean onlyEntity;
	public Location location;
	public LocationTask(boolean oe, Location l){
		onlyEntity = oe;
		location = l;
	}
}
