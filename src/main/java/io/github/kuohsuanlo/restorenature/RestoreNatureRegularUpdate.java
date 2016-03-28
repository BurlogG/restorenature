

package io.github.kuohsuanlo.restorenature;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


class RestoreNatureRegularUpdate implements Runnable {

	public ArrayList<int[][]> arraylist_chunk_untouchedtime;
	public ArrayList<String> arraylist_maintainedworlds_name = new ArrayList<String>();
	private int max_time_in_seconds;
	private int period_in_seconds;
	private RestoreNaturePlugin rnplugin;
	private int min_chunk_x;
	private int min_chunk_z;
	private int max_chunk_x;
	private int max_chunk_z;
	private int chunk_radius;

    public RestoreNatureRegularUpdate(int period,int max_time,int radius,ArrayList<String> worlds_name,RestoreNaturePlugin plugin) {
    	max_time_in_seconds = max_time;
    	period_in_seconds = period;
    	arraylist_maintainedworlds_name = worlds_name;
    	rnplugin= plugin;
    	
    	min_chunk_x = 0;
    	min_chunk_z = 0; 
    	max_chunk_x = radius*2+1;
    	max_chunk_z = radius*2+1; 
    	
    	chunk_radius = radius;
    	
    	arraylist_chunk_untouchedtime = new ArrayList<int[][]>();
    	
    	for(int i=0;i<arraylist_maintainedworlds_name.size();i++){
        	int[][] world_chunks = new int[max_chunk_x][max_chunk_z];
        	for (int x=min_chunk_x; x < max_chunk_x; x++){
    		   for (int z=min_chunk_z; z < max_chunk_z; z++){
    			   world_chunks[x][z] = 0;
    		   }
        	}
    		arraylist_chunk_untouchedtime.add(i, world_chunks);
    	}

    }
    public void run() {
    	rnplugin.getServer().getConsoleSender().sendMessage("¡±eChunk successfully start restoration calculation");			    
    	for(int i=0;i<arraylist_maintainedworlds_name.size();i++){
        	for (int x=min_chunk_x; x < max_chunk_x; x++){
    		   for (int z=min_chunk_z; z < max_chunk_z; z++){
    			   
    	        	arraylist_chunk_untouchedtime.get(i)[x][z]+=period_in_seconds;
    	        	
    	        	if(arraylist_chunk_untouchedtime.get(i)[x][z]>=max_time_in_seconds){
    	        		//Triggering console restorenature
    	        		int chunk_x = x-chunk_radius;
    	        		int chunk_z = z-chunk_radius;
    	        		rnplugin.getServer().dispatchCommand(rnplugin.getServer().getConsoleSender(), "restorenature "+arraylist_maintainedworlds_name.get(i)+" "+chunk_x+" "+chunk_z);
    	        		arraylist_chunk_untouchedtime.get(i)[x][z]=0;
    	        	}
    	        	
    			}
    		}

    	}
    }
	public void setWorldsChunkUntouchedTime(Block touched_block){
    	for(int i=0;i<arraylist_maintainedworlds_name.size();i++){
    		if(arraylist_maintainedworlds_name.get(i).equals(touched_block.getWorld().getName())){
    			int x = touched_block.getChunk().getX()+chunk_radius;
    			int z = touched_block.getChunk().getZ()+chunk_radius;
    			arraylist_chunk_untouchedtime.get(i)[x][z] = 0;
    		}
    	}
	
		
	}
}
