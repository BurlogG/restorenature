package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;

public class MapChunkInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public int[][] chunk_untouchedtime;
	public String world_name;
	public ArrayList<String> factions_name;
	public int chunk_radius;
	public int max_x;
	public int max_z;
	public int now_min_x;
	public int now_min_z;
	public MapChunkInfo(String name,int[][] time,int radius,ArrayList<String> fname,int new_max_chunk_x,int new_max_chunk_z,int new_now_min_x,int new_now_min_z){
		chunk_untouchedtime = time;
		world_name = name;
		chunk_radius =  radius;
		factions_name = fname;
		max_x = new_max_chunk_x;
		max_z = new_max_chunk_z;
		now_min_x = new_now_min_x;
		now_min_z = new_now_min_z;
	}

} 