

package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


class MapChunkInfo implements Serializable {
	public int[][] chunk_untouchedtime;
	public String world_name;
	public MapChunkInfo(String name,int[][] time){
		chunk_untouchedtime = time;
		world_name = name;
	}

} 
class RestoreNatureRegularUpdate implements Runnable {
	private int max_time_in_seconds;
	private int period_in_seconds;
	private RestoreNaturePlugin rnplugin;
	public ArrayList<MapChunkInfo> maintained_worlds = new ArrayList<MapChunkInfo>();
	private int min_chunk_x;
	private int min_chunk_z;
	private int max_chunk_x;
	private int max_chunk_z;
	private int chunk_radius;

    public RestoreNatureRegularUpdate(int period,int max_time,int radius,ArrayList<MapChunkInfo> existing_worlds,RestoreNaturePlugin plugin) {
    	max_time_in_seconds = max_time;
    	period_in_seconds = period;
    	chunk_radius = radius; 
    	
    	rnplugin= plugin;
       	min_chunk_x = 0;
       	min_chunk_z = 0; 
       	max_chunk_x = radius*2;
       	max_chunk_z = radius*2; 
 
    	
    	maintained_worlds = existing_worlds;


    }
    public void run() {
    	rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Total # worlds : "+maintained_worlds.size());	
    	for(int i=0;i<maintained_worlds.size();i++){
        	for(int x=min_chunk_x; x <= max_chunk_x; x++){
    		    for(int z=min_chunk_z; z <= max_chunk_z; z++){
    		    	rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Checking "+maintained_worlds.get(i).world_name+" "+Integer.toString(x-chunk_radius)+" ; "+Integer.toString(z-chunk_radius));	
					maintained_worlds.get(i).chunk_untouchedtime[x][z]+=period_in_seconds;
					
					if(maintained_worlds.get(i).chunk_untouchedtime[x][z]>=max_time_in_seconds){
						//Triggering console restorenature
						int chunk_x = x-chunk_radius;
						int chunk_z = z-chunk_radius;
						rnplugin.getServer().dispatchCommand(rnplugin.getServer().getConsoleSender(), "restorenature "+maintained_worlds.get(i).world_name+" "+chunk_x+" "+chunk_z);
						maintained_worlds.get(i).chunk_untouchedtime[x][z]=0;
					}
    	        	
    			}
    		}

    	}
    }
	public void setWorldsChunkUntouchedTime(Block touched_block){
    	for(int i=0;i<maintained_worlds.size();i++){
    		if(maintained_worlds.get(i).world_name.equals(touched_block.getWorld().getName())){
    			int x = touched_block.getChunk().getX()+chunk_radius;
    			int z = touched_block.getChunk().getZ()+chunk_radius;
    			maintained_worlds.get(i).chunk_untouchedtime[x][z] = 0;
    		}
    	}
	
		
	}
}
