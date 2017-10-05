
package io.github.kuohsuanlo.restorenature;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.MaterialData;
 
public class RestoreNatureCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    private RestoreNaturePlugin rnplugin;
    
    public final int restoreType_RestoreAll = 0;
    public final int restoreType_OnlyRestoreAir = 1;
    public final int restoreType_OnlyRestoreNotAir = 2;
  
    private Random random = new Random();
    
    
    public RestoreNatureCommand(RestoreNaturePlugin plugin){
    	rnplugin = plugin;
    }
    public void restoreChunk(World currentWorld,MapChunkInfo map_chunk_info,int chunk_x, int chunk_z, CommandSender sender){
    	
    	if(RestoreNaturePlugin.BLOCK_EVENT_EFFECTING_RADIUS<2) {
    		sender.sendMessage("[RestoreNature] : BLOCK_EVENT_EFFECTING_RADIUS must >=2");	
    		return;
    	}
    	
    	Chunk player_chunk  = currentWorld.getChunkAt(chunk_x, chunk_z);
		
		//bug:  why x-1, z-1 help the issue?
		
    	if(RestoreNaturePlugin.ONLY_RESTORE_AIR==true){
        	currentWorld.regenerateChunk(player_chunk.getX(), player_chunk.getZ());
        	populateChunk(player_chunk);
        	
        	pasteChunk(player_chunk, // ,map_chunk_info,this.restoreType_OnlyRestoreNotAir,sender);
        	
    	}
    	else{
        	currentWorld.regenerateChunk(player_chunk.getX(), player_chunk.getZ());
        	populateChunk(player_chunk);
    	}
    
    	
    	sender.sendMessage("[RestoreNature] : Chunk successfully restored on world chunk : "+currentWorld.getName()+" "+chunk_x+" ; "+chunk_z);	
        
    	
    }
    
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

	    //System.out.println(cmd.toString());    
		if (cmd.getName().equalsIgnoreCase("restorenature")) { // If the player typed /basic then do the following...   
		    if (args.length == 3 ) {
		    	if (sender instanceof Player) {
		    		 if (!sender.hasPermission("restorenature.manualrestore")){
						 sender.sendMessage("[RestoreNature] : You don't have the permission.");
		    			 return false;
		    		 }
		    	}
	        	String world_name = args[0];
	        	int chunk_x = Integer.valueOf(args[1]);
	        	int chunk_z = Integer.valueOf(args[2]);
	        	World currentWorld = sender.getServer().getWorld(world_name);
	        	restoreChunk(currentWorld,
	        				rnplugin.getMapChunkInfo(currentWorld.getName()),
	        					chunk_x,chunk_z,sender);

				
		    }
		    else if (args.length == 2 ) {
		    	 if (sender instanceof Player) {
		    	 
		    	 }
		    	 else{
		    		 if (args[0].equals("rnworld")){
		    			 World world = rnplugin.getServer().getWorld(args[1]);
		    			 if(world!=null){
			    			 rnworld(world);
		    				 sender.sendMessage("[RestoreNature] : All of the world is tagged as ready-to-restore.");
		    				 return true;
		    			 }
		    			 else{
		    				 sender.sendMessage("[RestoreNature] : world name : "+args[1]+" not found.");
		    				 return false;
		    			 }
		    		 }
		    	 }
		    }
		    else if (args.length == 1 ) {

		        if (sender instanceof Player) {
			        Player player = (Player) sender;

		    		if (args[0].equals("mr")){
				        if (sender.hasPermission("restorenature.manualrestore")){
			    			Location player_location = player.getLocation();
			    			Chunk player_chunk = player_location.getChunk();
				        	World currentWorld = player.getWorld();
				        	

				        	restoreChunk(currentWorld,
			        				rnplugin.getMapChunkInfo(currentWorld.getName()),
			        				player_chunk.getX(),player_chunk.getZ(),sender);

				        	
				        	return true;    
			    			
			    		}
				        else{
							sender.sendMessage("[RestoreNature] : You don't have the permission.");
							return false;
						}
			        	
			    	}
		    		else if (args[0].equals("rnworld")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 rnworld(player);
		    				 sender.sendMessage("[RestoreNature] : All of the world is tagged as ready-to-restore.");
		    				 return true;
		    			 }
		    			 else{
							 sender.sendMessage("[RestoreNature] : You don't have the permission.");
							 return false;
		    			 }
		    		}
		    		else if (args[0].equals("trymr")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 
		    				 Location player_location = player.getLocation();
					         String player_world_name = player.getWorld().getName();
					         //World player_world = player.getWorld();
					        	
					         Chunk player_chunk = player.getWorld().getChunkAt(player_location);
					         
		 					 if(!rnplugin.BukkitSchedulerSuck.checkLocationClaimed(player_chunk)){ // Land not claimed
						         
		 						 for(int i=0;i<rnplugin.maintain_world_chunk_info.size();i++){
		 							 if(player_world_name.equals( rnplugin.maintain_world_chunk_info.get(i).world_name)){

							    		MapChunkInfo chunksInfo = rnplugin.maintain_world_chunk_info.get(i);

							    		int array_x = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx(player_chunk.getX(),chunksInfo.chunk_radius);
							    		int array_z = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx(player_chunk.getZ(),chunksInfo.chunk_radius);
	
							    		
							    		if(chunksInfo.isLegalArrayXZ(array_x, array_z)  &&
							    				chunksInfo.chunk_untouchedtime[array_x][array_z]>=RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED){
							    			restoreChunk(player.getWorld(),
							    					chunksInfo,
							        				player_chunk.getX(),player_chunk.getZ(),sender);

							    			return true;  
										}
							    		else{
							    			sender.sendMessage("[RestoreNature] : Chunk untouch time not enough : "+chunksInfo.chunk_untouchedtime[array_x][array_z]+" < "+RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED);	
								            return true; 
							    		}
						     		}
						     	}
		 					}
		 					else{
				    			sender.sendMessage("[RestoreNature] : Chunk claimed");	
					            return true; 
		 					}
		 					
		    			 }
		    			 else{
								sender.sendMessage("[RestoreNature] : You don't have the permission.");
								return false;
		    			 }
		    		}
		    		else if (args[0].equals("remove_data")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				sender.sendMessage("[RestoreNature] : removing existing file and world_chunk info, creating new chunk info");
	    					for(int i =0;i<rnplugin.config_maintain_worlds.size();i++){
	    			        	String world_name = rnplugin.config_maintain_worlds.get(i).world_name;
	    			        	int radius = rnplugin.config_maintain_worlds.get(i).chunk_radius;
	    			        	
    			    			rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : no existing file, creating new chunk info");
    			    			MapChunkInfo world_chunk_info = rnplugin.createMapChunkInfo(world_name,radius, rnplugin.config_maintain_worlds.get(i).nature_factions);
    			    			rnplugin.maintain_world_chunk_info.add(world_chunk_info);
	    			    		
	    			        }
		    				 return true;
		    			 }
		    			 else{
							 sender.sendMessage("[RestoreNature] : You don't have the permission.");
							 return false;
		    			 }
		    		}
		        }
		        
	        }
        }
		return false;
        	

    }
	

    private void populateChunk(Chunk chunk){
    	List<BlockPopulator> list = chunk.getWorld().getPopulators();
    	for(int i=0;i<list.size();i++){
    		random.setSeed(791205*chunk.getX()+80722*chunk.getZ());
    		list.get(i).populate(chunk.getWorld(),random , chunk) ;
    	}
    }
	@SuppressWarnings("deprecation")
	private void pasteChunk(Chunk replaced_chunk, Chunk pasted_chunk, MapChunkInfo chunk_info, int restore_type, CommandSender sender){
    	for(int x=0;x<16;x++){
            for(int y=0;y<256;y++){
                for(int z=0;z<16;z++){
                	if(restore_type==this.restoreType_OnlyRestoreAir){
                    	if(replaced_chunk.getBlock(x, y, z).getType().equals(Material.AIR)){

                    		replaced_chunk.getBlock(x, y, z).setType(pasted_chunk.getBlock(x, y, z).getType());
                        	replaced_chunk.getBlock(x, y, z).setData(pasted_chunk.getBlock(x, y, z).getData());
                        	if(pasted_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
                        		processMobSpawner(replaced_chunk, pasted_chunk, x, y, z);
                            }
                    	}

                    	
                	}
                	else if(restore_type==this.restoreType_RestoreAll){

                		replaced_chunk.getBlock(x, y, z).setType(pasted_chunk.getBlock(x, y, z).getType());
                    	replaced_chunk.getBlock(x, y, z).setData(pasted_chunk.getBlock(x, y, z).getData());
                    	if(pasted_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
                			processMobSpawner(replaced_chunk, pasted_chunk, x, y, z);

                		}

                	}
                	else if(restore_type==this.restoreType_OnlyRestoreNotAir){
                		if(!pasted_chunk.getBlock(x, y, z).getType().equals(Material.AIR)){
                			replaced_chunk.getBlock(x, y, z).setType(pasted_chunk.getBlock(x, y, z).getType());
                        	replaced_chunk.getBlock(x, y, z).setData(pasted_chunk.getBlock(x, y, z).getData());
                    		if(pasted_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
                    			processMobSpawner(replaced_chunk, pasted_chunk, x, y, z);

                    		}
                		}
                	}
                	
        		}
        	}
    	}
    	if(chunk_info  !=null){

    		int array_x = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx(replaced_chunk.getX(),chunk_info.chunk_radius);
        	int array_z = RestoreNaturePlugin.transformation_from_chunkidx_to_arrayidx(replaced_chunk.getZ(),chunk_info.chunk_radius);
        	
        	/*
        	int chunk_x = RestoreNaturePlugin.transformation_from_arrayidx_to_chunkidx(array_x,chunk_info.chunk_radius);
        	int chunk_z = RestoreNaturePlugin.transformation_from_arrayidx_to_chunkidx(array_z,chunk_info.chunk_radius);
        	sender.sendMessage("[RestoreNature] : debug "+ array_x+","+array_z+" : "+ chunk_x+","+chunk_z );	
        	 */

        	if(chunk_info.isLegalArrayXZ(array_x, array_z))
    			chunk_info.chunk_untouchedtime[array_x][array_z]=0;
    		else{
    			sender.sendMessage("[RestoreNature] : out of maintenance bound,"+"("+array_x+","+array_z+")" );	
    		}
    	}
    }
    private void rnworld(World world){
    	for(int i=0;i<rnplugin.maintain_world_chunk_info.size();i++){
    		MapChunkInfo mcinfo = rnplugin.maintain_world_chunk_info.get(i);
        	if(mcinfo.world_name.equals(world.getName())){
        		mcinfo.now_min_x=0;
        		mcinfo.now_min_z=0;
        		for(int x=0;x<mcinfo.max_x;x++){
        			for(int z=0;z<mcinfo.max_z;z++){
        				//System.out.println(mcinfo.world_name+" : "+x+","+z);
        				mcinfo.chunk_untouchedtime[x][z] = RestoreNaturePlugin.MAX_SECONDS_UNTOUCHED+1;
        			}
        		}
        	}
    	}
    }
    private void rnworld(Player player){
    	rnworld(player.getWorld());
    }
	private void processMobSpawner(Chunk replaced_chunk, Chunk pasted_chunk, int x,int y,int z){
		rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : restoring mobspawner");
    	
		CreatureSpawner restored_spawner = (CreatureSpawner) pasted_chunk.getBlock(x, y, z);
		restored_spawner.setSpawnedType(restored_spawner.getSpawnedType());
		replaced_chunk.getBlock(x, y, z).getState().update();
	}	
}
