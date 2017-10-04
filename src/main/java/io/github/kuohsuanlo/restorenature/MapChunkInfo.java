package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;

class MapChunkInfo implements Serializable {
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
	public boolean isLegalChunkXZ(int x,int z){
		int array_x = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx(x, chunk_radius);
		int array_z = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx(z, chunk_radius);
		return isLegalArrayXZ(array_x,array_z);
	}
	public boolean isLegalArrayXZ(int x, int z){
		return x<this.max_x  &&
				z<this.max_z  &&
				x>=0  &&
				z>=0; 
	}

} 