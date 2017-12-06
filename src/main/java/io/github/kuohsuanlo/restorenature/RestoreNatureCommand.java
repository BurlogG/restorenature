
package io.github.kuohsuanlo.restorenature;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import io.github.kuohsuanlo.restorenature.util.RestoreNatureUtil;

 
public class RestoreNatureCommand implements CommandExecutor {
    private RestoreNaturePlugin rplugin;
    public RestoreNatureCommand(RestoreNaturePlugin plugin){
    	rplugin = plugin;
    }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

	    //System.out.println(cmd.toString());    
		if (cmd.getName().equalsIgnoreCase("restorenature")) { // If the player typed /basic then do the following...   
		    if (args.length == 3 ) {
		    	if (sender instanceof Player) {
		    		 if (!sender.hasPermission("restorenature.manualrestore")){
						 sender.sendMessage(ChatColor.RED+"You don't have the permission.");
		    			 return false;
		    		 }
		    	}
	        	String world_name = args[0];
	        	int chunk_x = Integer.valueOf(args[1]);
	        	int chunk_z = Integer.valueOf(args[2]);
	        	
	        	Chunk player_chunk = sender.getServer().getWorld(world_name).getChunkAt(chunk_x, chunk_z);		        	
	        	Chunk restoring_chunk = sender.getServer().getWorld(world_name+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(chunk_x, chunk_z);	
	        	RestoreNatureUtil.restoreChunk(player_chunk,restoring_chunk,null,-1,-1);
				
		    }
		    else if (args.length == 1 ) {

		        if (sender instanceof Player) {
			        Player player = (Player) sender;

		    		if (args[0].equals("mr")){
				        if (sender.hasPermission("restorenature.manualrestore")){

				        	Location player_location = player.getLocation();
				        	String player_world_name = player.getWorld().getName();
				        	
				        	Chunk player_chunk = player.getWorld().getChunkAt(player_location);
				        	Chunk restoring_chunk = sender.getServer().getWorld(player_world_name+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(player_location) ;
				        	
				        	RestoreNatureUtil.restoreChunk(player_chunk,restoring_chunk,null,-1,-1);
				        	
				        	sender.sendMessage(ChatColor.YELLOW+"Chunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
				            return true;    
			    			
			    		}
				        else{
							sender.sendMessage(ChatColor.RED+"You don't have the permission.");
							return false;
						}
			        	
			    	}
		    		else if (args[0].equals("rnworld")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 setWholeWorldToMaxUntouchedTime(player.getWorld());
		    				 return true;
		    			 }
		    			 else{
							 sender.sendMessage(ChatColor.RED+"You don't have the permission.");
							 return false;
		    			 }
		    		}
		    		else if (args[0].equals("trymr")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 
		    				 Location player_location = player.getLocation();
					         String player_world_name = player.getWorld().getName();
					        	
					         Chunk player_chunk = player.getWorld().getChunkAt(player_location);
					         Chunk restoring_chunk = sender.getServer().getWorld(player_world_name+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(player_location) ;

		 					if(!rplugin.ChunkUpdater.checkLocationClaimed(player_location)){ // Land not claimed
						         
						     	for(int i=0;i<rplugin.maintain_world_chunk_info.size();i++){
						     		if(player_world_name.equals( rplugin.maintain_world_chunk_info.get(i).world_name)){
						     			if(!RestoreNatureUtil.isValidLocation(player_chunk, rplugin.maintain_world_chunk_info.get(i))) continue;
							    		
						     			MapChunkInfo chunksInfo = rplugin.maintain_world_chunk_info.get(i);
							    		int x = RestoreNatureUtil.convertChunkIdxToArrayIdx(player_chunk.getX());
							    		int z = RestoreNatureUtil.convertChunkIdxToArrayIdx(player_chunk.getZ());

							    		if(chunksInfo.chunk_untouchedtime[x][z]>=RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED){
							    			RestoreNatureUtil.restoreChunk(player_chunk,restoring_chunk,chunksInfo,x,z);
								        	
								        	sender.sendMessage(ChatColor.YELLOW+"Chunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
								            return true;    
							    			

										}
							    		else{
							    			sender.sendMessage(ChatColor.YELLOW+"Chunk untouch time not enough : "+chunksInfo.chunk_untouchedtime[x][z]+" < "+RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED);	
								            return true; 
							    		}
						     		}
						     	}
		 					}
		 					else{
				    			sender.sendMessage(ChatColor.YELLOW+"Chunk claimed");	
					            return true; 
		 					}
		 					
		    			 }
		    			 else{
								sender.sendMessage(ChatColor.RED+"You don't have the permission.");
								return false;
		    			 }
		    		}
		    		else if (args[0].equals("check")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 
		    				 Location player_location = player.getLocation();
					         String player_world_name = player.getWorld().getName();
					        	
					         Chunk player_chunk = player.getWorld().getChunkAt(player_location);
					         
		 					if(!rplugin.ChunkUpdater.checkLocationClaimed(player_location)){ // Land not claimed
						         
						     	for(int i=0;i<rplugin.maintain_world_chunk_info.size();i++){
						     		if(player_world_name.equals( rplugin.maintain_world_chunk_info.get(i).world_name)){
						     			if(!RestoreNatureUtil.isValidLocation(player_chunk, rplugin.maintain_world_chunk_info.get(i))) continue;
							    		
							    		MapChunkInfo chunksInfo = rplugin.maintain_world_chunk_info.get(i);
							    		int x = RestoreNatureUtil.convertChunkIdxToArrayIdx(player_chunk.getX());
							    		int z = RestoreNatureUtil.convertChunkIdxToArrayIdx(player_chunk.getZ());

							    		if (x>chunksInfo.max_x ||  z>chunksInfo.max_z){
							    			sender.sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+RestoreNaturePlugin.OUT_OF_BOUND+chunksInfo.chunk_radius*16);
							    		}
							    		else if(chunksInfo.chunk_untouchedtime[x][z]>=RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED){
							    			sender.sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+RestoreNaturePlugin.UNTOUCHED_TIME_ENOUGH+chunksInfo.chunk_untouchedtime[x][z]+" >= "+RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED);	
							    			sender.sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+RestoreNaturePlugin.WILL_BE_RESTORED_SOON);	
							    			return true;  
										}
							    		else{
							    			sender.sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+RestoreNaturePlugin.UNTOUCHED_TIME_NOT_ENOUGH+chunksInfo.chunk_untouchedtime[x][z]+" < "+RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED);	
								            return true; 
							    		}
						     		}
						     	}
		 					}
		 					else{
		 						sender.sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+RestoreNaturePlugin.CLAIMED);
					            return true; 
		 					}
		 					
		    			 }
		    			 else{
								sender.sendMessage(ChatColor.RED+"You don't have the permission.");
								return false;
		    			 }
		    		}
		    		
		        }
		        
	        }
        }
		return false;
        	

    }
    public void setWholeWorldToMaxUntouchedTime(World world){
    	for(int i=0;i<rplugin.maintain_world_chunk_info.size();i++){
    		MapChunkInfo mcinfo = rplugin.maintain_world_chunk_info.get(i);
        	if(world.getName().equals(mcinfo.world_name)){
        		mcinfo.now_min_x=0;
        		mcinfo.now_min_z=0;
        		for(int x=0;x<mcinfo.max_x;x++){
        			for(int z=0;z<mcinfo.max_z;z++){
        				mcinfo.chunk_untouchedtime[x][z] = RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED+1;
        			}
        		}
        	}
    	}
    }

}
