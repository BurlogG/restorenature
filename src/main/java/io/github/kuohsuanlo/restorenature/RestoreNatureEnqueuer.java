package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
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

import io.github.kuohsuanlo.restorenature.util.Lag;
import io.github.kuohsuanlo.restorenature.util.RestoreNatureUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import java.time.Instant;



class RestoreNatureEnqueuer implements Runnable {
	private int max_time_in_seconds;
	private RestoreNaturePlugin RestoreNaturePlugin;
	public ArrayList<MapChunkInfo> maintained_worlds = new ArrayList<MapChunkInfo>();
	
	private Faction faction =null;
	
	private GriefPrevention gp;
	private final String notClaimedOwner = "administrator";
	
	
	public static final int chunk_center_x = 8;
	public static final int chunk_center_y = 64;
	public static final int chunk_center_z = 8;
	public int processCount = 1;
	public int currentCount = 0;
	
	public double lastSecondTPS = 20;
	public int tpsCount = 20;
	public int tpsCurrentCount = 0;
	
	public int currentTimerLoopX = 0;
	
	public int recovered_chunks = 0;
	public long last_time=Instant.now().getEpochSecond(); 
	public long now_time = Instant.now().getEpochSecond();
    public RestoreNatureEnqueuer(int max_time,ArrayList<MapChunkInfo> existing_worlds,RestoreNaturePlugin plugin) {
    	max_time_in_seconds = max_time;
    	RestoreNaturePlugin= plugin;
	
    	maintained_worlds = existing_worlds;

    }
    
    public void run() {
    	tpsCurrentCount++;
    	if(tpsCurrentCount>=tpsCount){
    		tpsCurrentCount=0;
    		lastSecondTPS = Lag.getTPS();
    	}
    	
    	currentCount++;
    	processCount = calculateProcessCount();
    	if(currentCount>=processCount){
    		currentCount=0;
    		processRequest();
    	}
    	
    	
    	
    	
    }
    private void processRequest(){
    	
    	for(int i=0;i<maintained_worlds.size();i++){
    		MapChunkInfo chunksInfo = maintained_worlds.get(i);
    		
        	int x=chunksInfo.now_min_x;
        	int z=chunksInfo.now_min_z;

			int chunk_x = RestoreNatureUtil.convertArrayIdxToChunkIdx(x);
	    	int chunk_z = RestoreNatureUtil.convertArrayIdxToChunkIdx(z);
	    	
    		if(RestoreNatureUtil.isInRadius(chunk_x, chunk_z, chunksInfo.chunk_radius)) {
    	    	Location ChunkMid = new Location(Bukkit.getServer().getWorld(chunksInfo.world_name),chunk_x*16+8,60,chunk_z*16+8);

    	    	if(!checkLocationClaimed(ChunkMid)){ // Land not claimed
    				if(chunksInfo.chunk_untouchedtime[x][z]>=max_time_in_seconds){
    					recovered_chunks++;
    					if(RestoreNaturePlugin.ChunkTimeTicker.addTask(ChunkMid)){
    						if(RestoreNaturePlugin.Verbosity>=1)
    							Bukkit.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"TaskQueue add task : "+ ChunkMid.getWorld().getName()+" "+
    		    			RestoreNatureUtil.convertArrayIdxToChunkIdx(x)+" "+
    		    			RestoreNatureUtil.convertArrayIdxToChunkIdx(z));

    					}
    					else{
    						if(RestoreNaturePlugin.Verbosity>=1)
    							RestoreNaturePlugin.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"Maximum number of tasks in TaskQueue reached. Please increase CHECK_PERIOD_IN_SECONDS" );
    					}

    				}
    			}
        		
    		}
    		else{
    			chunksInfo.now_min_z =chunksInfo.max_z;
    		}
	    	
    		double elapsed = now_time-last_time;
    		for(int ticker_x=0; ticker_x < chunksInfo.max_x; ticker_x++){
  		    	chunksInfo.chunk_untouchedtime[ticker_x][chunksInfo.now_min_z]+=elapsed;
  		    }
        	chunksInfo.now_min_z +=1;
        	if(chunksInfo.now_min_z >=chunksInfo.max_z){
        		RestoreNaturePlugin.getServer().getConsoleSender().sendMessage(
        				ChatColor.LIGHT_PURPLE+RestoreNaturePlugin.PLUGIN_PREFIX+
        				" progress : "+chunksInfo.now_min_x+" / "+chunksInfo.max_x+" / "+
        				" elapsed time : "+(now_time-last_time)+" sec(s)"+" / "+
        				" recovered chunks : "+recovered_chunks);
        		
        		chunksInfo.now_min_z =0;
        		chunksInfo.now_min_x +=1;
        		
    	    	if(chunksInfo.now_min_x >=chunksInfo.max_x){
            		chunksInfo.now_min_x=0;
            	}
    	    	
    	    	last_time = Instant.now().getEpochSecond();
        	}
        	
        	
	    
        	
    	}
    	now_time = Instant.now().getEpochSecond();
	


  	
    }
	public void setWorldsChunkUntouchedTime(Block touched_block){
		

		for(int i=0;i<maintained_worlds.size();i++){
    		if(maintained_worlds.get(i).world_name.equals(touched_block.getWorld().getName())){
    			if(
					RestoreNatureUtil.convertChunkIdxToArrayIdx( touched_block.getLocation().getChunk().getX())<=maintained_worlds.get(i).max_x  &&
							RestoreNatureUtil.convertChunkIdxToArrayIdx( touched_block.getLocation().getChunk().getZ())<=maintained_worlds.get(i).max_z  
    					){
    				int x = RestoreNatureUtil.convertChunkIdxToArrayIdx( touched_block.getChunk().getX());
        			int z = RestoreNatureUtil.convertChunkIdxToArrayIdx( touched_block.getChunk().getZ());
        			//int x = touched_block.getChunk().getX()+maintained_worlds.get(i).chunk_radius;
        			//int z = touched_block.getChunk().getZ()+maintained_worlds.get(i).chunk_radius;
        			
        			int R = RestoreNaturePlugin.BLOCK_EVENT_EFFECTING_RADIUS-1;
        			for(int r_x=(-1)*R;r_x<=R;r_x++){
            			for(int r_z=(-1)*R;r_z<=R;r_z++){
            				if((x+r_x)>=0  &&  (x+r_x)<=maintained_worlds.get(i).max_x  &&  (z+r_z)>=0  &&  (z+r_z)<=maintained_worlds.get(i).max_z){
            					maintained_worlds.get(i).chunk_untouchedtime[x+r_x][z+r_z] = 0;
            				}
            			}
        			}
    				//RestoreNaturePlugin.getServer().getConsoleSender().sendMessage("��c[RestoreNature] : Array   coor "+ x+" ; "+z);	
        			//RestoreNaturePlugin.getServer().getConsoleSender().sendMessage("��c[RestoreNature] : Chunk   coor "+ touched_block.getChunk().getX()+" ; "+ touched_block.getChunk().getZ());
    				//RestoreNaturePlugin.getServer().getConsoleSender().sendMessage("��c[RestoreNature] : T-Chunk coor "+ transformation_from_arrayidx_to_chunkidx(x)+" ; "+transformation_from_arrayidx_to_chunkidx(z));	
    				//RestoreNaturePlugin.getServer().getConsoleSender().sendMessage("��c[RestoreNature] : T-Array coor "+ transformation_from_chunkidx_to_arrayidx(transformation_from_arrayidx_to_chunkidx(x))+" ; "+transformation_from_chunkidx_to_arrayidx(transformation_from_arrayidx_to_chunkidx(z)));	
    	    		
    			} 
    			
    		}
    	}
	
		
	}
	private int calculateProcessCount(){
	   	if(lastSecondTPS>=19){
			return 1;
		}
		else if(lastSecondTPS>=18){
			return 4;
		}
		else if(lastSecondTPS>=17){
			return 20;
		}
		else if(lastSecondTPS>=16){
			return 40;
		}
		else if(lastSecondTPS>=15){
			return 160;
		}
		else if(lastSecondTPS>=14){
			return 400;
		}
		else{
			return 1000;
		}
    }
	public boolean checkLocationClaimed(Location location){
    	
    	
    	if(RestoreNaturePlugin.USING_FEATURE_FACTION){
        	faction = BoardColl.get().getFactionAt(PS.valueOf(location));
    	}
    	
    	if(RestoreNaturePlugin.USING_FEATURE_GRIEFPREVENTION){
    		gp = GriefPrevention.instance;
    	}
    	
    	boolean fc_claimed = true;
    	boolean gp_claimed = true;
    	for(int i=0;i<maintained_worlds.size();i++){
    		if(location.getWorld().getName().equals(maintained_worlds.get(i).world_name)){
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
                    		for(int x=-8;x<8;x++){
                    			for(int z=-8;z<8;z++){
                    				Claim claim = gp.dataStore.getClaimAt(
                    						location.clone().add(x, 0, z), true, null
											);
                    				
                    				//no one's land
                    				if(claim==null){
                    					
                    				}
                    				//someone's land
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

}