

package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.ps.PS;

 

class RestoreNatureRegularUpdate implements Runnable {
	private int max_time_in_seconds;
	private int period_in_seconds;
	private RestoreNaturePlugin rnplugin;
	public ArrayList<MapChunkInfo> maintained_worlds = new ArrayList<MapChunkInfo>();

	private int CHECK_RADIUS_PER_PERIOD = 5;


	
	public static final int chunk_center_x = 8;
	public static final int chunk_center_y = 64;
	public static final int chunk_center_z = 8;
	
    public RestoreNatureRegularUpdate(int period,int max_time,ArrayList<MapChunkInfo> existing_worlds,RestoreNaturePlugin plugin) {
    	max_time_in_seconds = max_time;
    	period_in_seconds = period;
    	rnplugin= plugin;
	
    	maintained_worlds = existing_worlds;

    }
    private boolean checkLocationClaimed(Chunk checkedChunk){
    	
        	Location location = checkedChunk.getBlock(chunk_center_x, chunk_center_y, chunk_center_z).getLocation();
        	Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));
        	
        	//rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Checking whether it is wilderness faction : "+faction.getName());	
			
        	boolean claimed = true;

        	for(int i=0;i<maintained_worlds.size();i++){
        		if(checkedChunk.getWorld().getName().equals(maintained_worlds.get(i).world_name)){
            		if(maintained_worlds.get(i).factions_name.size()==0){
            			claimed = false;
            		}
            		else{
                		for(int j=0;j<maintained_worlds.get(i).factions_name.size();j++){
                        	if(faction.getName().equals(maintained_worlds.get(i).factions_name.get(j))){
                        		claimed = false;
                        		
                        	}
                			
                		}
            		}         		
            	}
        	}        	
        	
        
        	//rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : "+claimed);	
    		return claimed;

    }
    public void run() {
    	rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Total # worlds : "+maintained_worlds.size());
    	int restore_chunks_number =0;

    	
    	for(int i=0;i<maintained_worlds.size();i++){
    		MapChunkInfo chunksInfo = maintained_worlds.get(i);
    		rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Checking world "+chunksInfo.world_name+" "+chunksInfo.now_min_x+" ; "+chunksInfo.now_min_z);
    		
        	for(int x=chunksInfo.now_min_x; x < chunksInfo.now_min_x+CHECK_RADIUS_PER_PERIOD; x++){
    		    for(int z=chunksInfo.now_min_z; z < chunksInfo.now_min_z+CHECK_RADIUS_PER_PERIOD; z++){
    		    	
    		    	
    		    	
    		    	chunksInfo.chunk_untouchedtime[x][z]+=period_in_seconds * (chunksInfo.max_chunk_x/CHECK_RADIUS_PER_PERIOD)* (chunksInfo.max_chunk_x/CHECK_RADIUS_PER_PERIOD);

					int chunk_x = x-chunksInfo.chunk_radius;
					int chunk_z = z-chunksInfo.chunk_radius;
					Chunk checked_chunk = rnplugin.getServer().getWorld(chunksInfo.world_name).getChunkAt(chunk_x, chunk_z);
					if(!checkLocationClaimed(checked_chunk)){ // Land not claimed
						if(chunksInfo.chunk_untouchedtime[x][z]>=max_time_in_seconds){

							//rnplugin.getServer().dispatchCommand(rnplugin.getServer().getConsoleSender(), "restorenature "+maintained_worlds.get(i).world_name+" "+chunk_x+" "+chunk_z);
							if(rnplugin.RestoringTaskQueue.addTask(checked_chunk)){
								restore_chunks_number++;
								chunksInfo.chunk_untouchedtime[x][z]=0;
							}
							else{
								//Skip because of too much task;
							}

						}
					}
					
					

    	        	
    			}
 
    		}
        	rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Add "+restore_chunks_number+" chunks into queue, in world :"+maintained_worlds.get(i).world_name);	
        	restore_chunks_number = 0;

        	chunksInfo.now_min_z +=CHECK_RADIUS_PER_PERIOD;
        	if(chunksInfo.now_min_z >=chunksInfo.max_chunk_z){
        		chunksInfo.now_min_z=0;
        		
        		chunksInfo.now_min_x +=CHECK_RADIUS_PER_PERIOD;
            	if(chunksInfo.now_min_x >=chunksInfo.max_chunk_x){
            		chunksInfo.now_min_x=0;
            	}
        	} 

    	}



  	
    	
    	
    }
	public void setWorldsChunkUntouchedTime(Block touched_block){
    	for(int i=0;i<maintained_worlds.size();i++){
    		if(maintained_worlds.get(i).world_name.equals(touched_block.getWorld().getName())){
    			int x = touched_block.getChunk().getX()+maintained_worlds.get(i).chunk_radius;
    			int z = touched_block.getChunk().getZ()+maintained_worlds.get(i).chunk_radius;
    			maintained_worlds.get(i).chunk_untouchedtime[x][z] = 0;
    		}
    	}
	
		
	}
}
