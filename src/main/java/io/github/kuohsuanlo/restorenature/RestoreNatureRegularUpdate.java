

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

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import java.time.Instant;



class RestoreNatureRegularUpdate implements Runnable {
	private int max_time_in_seconds;
	private int period_in_seconds;
	private RestoreNaturePlugin rnplugin;
	public ArrayList<MapChunkInfo> maintained_worlds = new ArrayList<MapChunkInfo>();



	private Faction faction =null;
	
	private GriefPrevention gp;
	private final String notClaimedOwner = "administrator";
	
	
	public static final int chunk_center_x = 8;
	public static final int chunk_center_y = 64;
	public static final int chunk_center_z = 8;
	public long last_time=Instant.now().getEpochSecond(); 
    public RestoreNatureRegularUpdate(int period,int max_time,ArrayList<MapChunkInfo> existing_worlds,RestoreNaturePlugin plugin) {
    	max_time_in_seconds = max_time;
    	period_in_seconds = period;
    	rnplugin= plugin;
	
    	maintained_worlds = existing_worlds;

    }
    public boolean checkLocationClaimed(Chunk checkedChunk){
    	
        	Location location = checkedChunk.getBlock(chunk_center_x, chunk_center_y, chunk_center_z).getLocation();
        	
        	if(RestoreNaturePlugin.USING_FEATURE_FACTION){
            	faction = BoardColl.get().getFactionAt(PS.valueOf(location));
        	}
        	
        	if(RestoreNaturePlugin.USING_FEATURE_GRIEFPREVENTION){
        		gp = GriefPrevention.instance;
        	}
        	
        	boolean fc_claimed = true;
        	boolean gp_claimed = true;
        	for(int i=0;i<maintained_worlds.size();i++){
        		if(checkedChunk.getWorld().getName().equals(maintained_worlds.get(i).world_name)){
            		if(maintained_worlds.get(i).factions_name.size()==0){
            			fc_claimed = false;
            		}
            		else{
                		for(int j=0;j<maintained_worlds.get(i).factions_name.size();j++){
                        	if(faction==null){
                        		fc_claimed = false;
                        	}else if(faction.getName().equals(maintained_worlds.get(i).factions_name.get(j))){
                        		fc_claimed = false;
                        		
                        	}
                        	
                        	if(gp==null){
                        		gp_claimed = false;
                        	}
                        	else{
                        		boolean isOthersLand = false;
                        		for(int x=0;x<16;x++){
                        			for(int z=0;z<16;z++){
                        				Claim claim = gp.dataStore.getClaimAt(
                								checkedChunk.getBlock( x, chunk_center_y,  z).getLocation(), true, null
												);
                        				if(claim==null){
                        					
                        				}
                        				else{
                        					//System.out.println(claim.getOwnerName());
                            				isOthersLand = isOthersLand  ||  ( !claim.getOwnerName().equals(notClaimedOwner) );
                            				
                        				}
                        				
                        				if(isOthersLand) break;
                        			}
                        			if(isOthersLand) break;
                        		}
                        		gp_claimed = isOthersLand;
                        		//System.out.println(gp_claimed);
                        	}
                			
                		}
            		}         		
            	}
        	}        	
        	
        	
    		return gp_claimed || fc_claimed ;

    }
    public void run() {
    	long now_time = Instant.now().getEpochSecond();
    	for(int i=0;i<maintained_worlds.size();i++){
    		MapChunkInfo chunksInfo = maintained_worlds.get(i);
    		//rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : Checking world "+chunksInfo.world_name+" "+chunksInfo.now_min_x+" ; "+chunksInfo.now_min_z);
    		
    		for(int x=0; x < chunksInfo.max_x; x++){
    		    for(int z=0; z < chunksInfo.max_z; z++){
    		    	chunksInfo.chunk_untouchedtime[x][z]+=now_time-last_time;
    		    }
    		}
    		
	    
        	int array_x = chunksInfo.now_min_x;
        	int array_z = chunksInfo.now_min_z;
	    	int chunk_x = RestoreNaturePlugin.transformation_from_arrayidx_to_chunkidx(array_x,chunksInfo.chunk_radius);
	    	int chunk_z = RestoreNaturePlugin.transformation_from_arrayidx_to_chunkidx(array_z,chunksInfo.chunk_radius);
	    	
	    	World currentWorld = rnplugin.getServer().getWorld(chunksInfo.world_name);
	    	boolean allUnclaimed = true;
	    	for(int x=0;x<RestoreNaturePlugin.IDX_MAX+2;x++){
	    		for(int z=0;z<RestoreNaturePlugin.IDX_MAX+2;z++){
	    			Chunk checkedChunk = currentWorld.getChunkAt(chunk_x+RestoreNaturePlugin.i2v[x], chunk_z+RestoreNaturePlugin.i2v[z]);
	    			if(checkLocationClaimed(checkedChunk)){
	    				allUnclaimed = false;
	    				rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : Passed : "+chunk_x+","+chunk_z+" / Land Claimed : "+(chunk_x+RestoreNaturePlugin.i2v[x])+","+(chunk_z+RestoreNaturePlugin.i2v[z]));
	    			}
	    			
	    		}
	    		
	    	}
	    	
	    	Chunk centerChunk = currentWorld.getChunkAt(chunk_x, chunk_z);
	    	if(allUnclaimed){
	    		if(chunksInfo.chunk_untouchedtime[array_x][array_z]>=max_time_in_seconds){
					//System.out.println("MaxReached : world"+chunksInfo.world_name+", chunk : "+chunksInfo.now_min_x+","+chunksInfo.now_min_z);
		    		
					if(rnplugin.RestoringTaskQueue.addTask(centerChunk)){
					}
					else{
						rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : Maximum number of tasks in TaskQueue reached. Please increase CHECK_PERIOD_IN_SECONDS" );
					}

				}
	    	}
			
			//System.out.println("Updater : world"+chunksInfo.world_name+", chunk : "+chunksInfo.now_min_x+","+chunksInfo.now_min_z);
    		chunksInfo.now_min_z++;
    		if(chunksInfo.now_min_z>=chunksInfo.max_z){
        		chunksInfo.now_min_x++;
    			chunksInfo.now_min_z=0;
    		}
    		if(chunksInfo.now_min_x>=chunksInfo.max_x){
    			chunksInfo.now_min_x=0;
    		}

    	}
    	last_time = Instant.now().getEpochSecond();
	


  	
    	
    	
    }
	public void setWorldsChunkUntouchedTime(Block touched_block){
		for(int i=0;i<maintained_worlds.size();i++){
    		if(maintained_worlds.get(i).world_name.equals(touched_block.getWorld().getName())){
    			MapChunkInfo chunksInfo = maintained_worlds.get(i);
    			int x = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx( touched_block.getChunk().getX(),chunksInfo.chunk_radius);
    			int z = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx( touched_block.getChunk().getZ(),chunksInfo.chunk_radius);
    			if(		chunksInfo.isLegalArrayXZ(x, z)){
    				
        			int R = RestoreNaturePlugin.BLOCK_EVENT_EFFECTING_RADIUS-1;
        			for(int r_x=(-1)*R;r_x<=R;r_x++){
            			for(int r_z=(-1)*R;r_z<=R;r_z++){
            				if((x+r_x)>=0  &&  (x+r_x)<=maintained_worlds.get(i).max_x  &&  (z+r_z)>=0  &&  (z+r_z)<=maintained_worlds.get(i).max_z){
            					maintained_worlds.get(i).chunk_untouchedtime[x+r_x][z+r_z] = 0;
            				}
            			}
        			}
    				
    			} 
    			
    		}
    	}
	
		
	}
}
