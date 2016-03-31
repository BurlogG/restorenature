

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

	private int max_chunk_x;
	private int max_chunk_z;
	private int CHECK_RADIUS_PER_PERIOD = 5;
	private int now_min_x;
	private int now_min_z;
	private int chunk_radius;
	
	public static final int chunk_center_x = 8;
	public static final int chunk_center_y = 64;
	public static final int chunk_center_z = 8;
	
    public RestoreNatureRegularUpdate(int period,int max_time,int radius,ArrayList<MapChunkInfo> existing_worlds,RestoreNaturePlugin plugin) {
    	max_time_in_seconds = max_time;
    	period_in_seconds = period;
    	chunk_radius = radius; 
    	
    	rnplugin= plugin;
    	now_min_x = 0;
    	now_min_z = 0; 
       	max_chunk_x = radius*2;
       	max_chunk_z = radius*2; 
 
       	
    	maintained_worlds = existing_worlds;


    }
    private boolean checkLocationClaimed(Chunk checkedChunk){
    	
        	Location location = checkedChunk.getBlock(chunk_center_x, chunk_center_y, chunk_center_z).getLocation();
        	Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));
        	
        	//rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Checking whether it is wilderness faction : "+faction.getName());	
			
        	boolean claimed = true;

        	for(int i=0;i<rnplugin.maintain_worlds.size();i++){
        		if(checkedChunk.getWorld().getName().equals(rnplugin.maintain_worlds.get(i).world_name)){
            		if(rnplugin.maintain_worlds.get(i).nature_factions.size()==0){
            			claimed = false;
            		}
            		else{
                		for(int j=0;j<rnplugin.maintain_worlds.get(i).nature_factions.size();j++){
                        	if(faction.getName().equals(rnplugin.maintain_worlds.get(i).nature_factions.get(j))){
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
    		rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : Checking world "+maintained_worlds.get(i).world_name+" "+now_min_x+" ; "+now_min_z);
    		
        	for(int x=now_min_x; x < now_min_x+CHECK_RADIUS_PER_PERIOD; x++){
    		    for(int z=now_min_z; z < now_min_z+CHECK_RADIUS_PER_PERIOD; z++){
    		    	
    		    	
    		    	
    		    	maintained_worlds.get(i).chunk_untouchedtime[x][z]+=period_in_seconds * (max_chunk_x/CHECK_RADIUS_PER_PERIOD)* (max_chunk_x/CHECK_RADIUS_PER_PERIOD);

					int chunk_x = x-chunk_radius;
					int chunk_z = z-chunk_radius;
					Chunk checked_chunk = rnplugin.getServer().getWorld(this.maintained_worlds.get(i).world_name).getChunkAt(chunk_x, chunk_z);
					if(!checkLocationClaimed(checked_chunk)){ // Land not claimed
						if(maintained_worlds.get(i).chunk_untouchedtime[x][z]>=max_time_in_seconds){

							//rnplugin.getServer().dispatchCommand(rnplugin.getServer().getConsoleSender(), "restorenature "+maintained_worlds.get(i).world_name+" "+chunk_x+" "+chunk_z);
							if(rnplugin.RestoringTaskQueue.addTask(checked_chunk)){
								restore_chunks_number++;
								maintained_worlds.get(i).chunk_untouchedtime[x][z]=0;
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

    	}


    	now_min_z +=CHECK_RADIUS_PER_PERIOD;
    	if(now_min_z >=max_chunk_z){
    		now_min_z=0;
    		
        	now_min_x +=CHECK_RADIUS_PER_PERIOD;
        	if(now_min_x >=max_chunk_x){
        		now_min_x=0;
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
