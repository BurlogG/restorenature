
package io.github.kuohsuanlo.restorenature;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
 
public class RestoreNatureCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    private RestoreNaturePlugin rnplugin;
    public RestoreNatureCommand(RestoreNaturePlugin plugin){
    	rnplugin = plugin;
    }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

	    //System.out.println(cmd.toString());    
		if (cmd.getName().equalsIgnoreCase("restorenature")) { // If the player typed /basic then do the following...   
		    if (args.length == 3 ) {
		    	if (sender instanceof Player) {
		    		 if (!sender.hasPermission("restorenature.manualrestore")){
						 sender.sendMessage("¡±cYou don't have the permission.");
		    			 return false;
		    		 }
		    	}
	        	String world_name = args[0];
	        	int chunk_x = Integer.valueOf(args[1]);
	        	int chunk_z = Integer.valueOf(args[2]);
	        	
	        	Chunk player_chunk = sender.getServer().getWorld(world_name).getChunkAt(chunk_x, chunk_z);		        	
	        	Chunk restoring_chunk = sender.getServer().getWorld(world_name+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(chunk_x, chunk_z);	
	        	restoreChunk(player_chunk,restoring_chunk,null,-1,-1);
				
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
				        	
				        	restoreChunk(player_chunk,restoring_chunk,null,-1,-1);
				        	
				        	sender.sendMessage("¡±eChunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
				            return true;    
			    			
			    		}
				        else{
							sender.sendMessage("¡±cYou don't have the permission.");
							return false;
						}
			        	
			    	}
		    		else if (args[0].equals("rnworld")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 rnplugin.rnworld(player);
		    				 return true;
		    			 }
		    			 else{
							 sender.sendMessage("¡±cYou don't have the permission.");
							 return false;
		    			 }
		    		}
		    		else if (args[0].equals("trymr")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 
		    				 Location player_location = player.getLocation();
					         String player_world_name = player.getWorld().getName();
					        	
					         Chunk player_chunk = player.getWorld().getChunkAt(player_location);
					         Chunk restoring_chunk = sender.getServer().getWorld(player_world_name+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(player_location) ;

		 					if(!rnplugin.BukkitSchedulerSuck.checkLocationClaimed(player_chunk)){ // Land not claimed
						         
						     	for(int i=0;i<rnplugin.maintain_world_chunk_info.size();i++){
						     		if(player_world_name.equals( rnplugin.maintain_world_chunk_info.get(i).world_name)){

							    		MapChunkInfo chunksInfo = rnplugin.maintain_world_chunk_info.get(i);
							    		int x = rnplugin.transformation_from_chunkidx_to_arrayidx(player_chunk.getX());
							    		int z = rnplugin.transformation_from_chunkidx_to_arrayidx(player_chunk.getZ());

							    		if(chunksInfo.chunk_untouchedtime[x][z]>=rnplugin.MAX_SECONDS_UNTOUCHED){
							    			restoreChunk(player_chunk,restoring_chunk,chunksInfo,x,z);
								        	
								        	sender.sendMessage("¡±eChunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
								            return true;    
							    			

										}
							    		else{
							    			sender.sendMessage("¡±eChunk untouch time not enough : "+chunksInfo.chunk_untouchedtime[x][z]+" < "+rnplugin.MAX_SECONDS_UNTOUCHED);	
								            return true; 
							    		}
						     		}
						     	}
		 					}
		 					else{
				    			sender.sendMessage("¡±eChunk claimed");	
					            return true; 
		 					}
		 					
		    			 }
		    			 else{
								sender.sendMessage("¡±cYou don't have the permission.");
								return false;
		    			 }
		    		}

		        }
		        
	        }
        }
		return false;
        	

    }

			
    @SuppressWarnings("deprecation")
	public void restoreChunk(Chunk player_chunk, Chunk restoring_chunk, MapChunkInfo chunk_info,int array_x,int array_z){
    	for(int x=0;x<16;x++){
            for(int y=0;y<256;y++){
                for(int z=0;z<16;z++){
                	if(rnplugin.ONLY_RESTORE_AIR){
                    	if(player_chunk.getBlock(x, y, z).getType().equals(Material.AIR)){
                    		player_chunk.getBlock(x, y, z).setTypeId(restoring_chunk.getBlock(x, y, z).getTypeId());
                        	player_chunk.getBlock(x, y, z).setData(restoring_chunk.getBlock(x, y, z).getData());
                        	if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){

                        		rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : restoring mobspawner");
                    			CreatureSpawner restoring_spawner = ((CreatureSpawner)restoring_chunk.getBlock(x, y, z).getState());
                    			CreatureSpawner restored_spawner = ((CreatureSpawner)player_chunk.getBlock(x, y, z).getState());                			
                    			restored_spawner.setSpawnedType(restoring_spawner.getSpawnedType());
                    			player_chunk.getBlock(x, y, z).getState().update();
                            }
                    	}

                    	
                	}
                	else{
                		player_chunk.getBlock(x, y, z).setTypeId(restoring_chunk.getBlock(x, y, z).getTypeId());
                    	player_chunk.getBlock(x, y, z).setData(restoring_chunk.getBlock(x, y, z).getData());
                		if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){

                    		rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : restoring mobspawner");
                			CreatureSpawner restoring_spawner = ((CreatureSpawner)restoring_chunk.getBlock(x, y, z).getState());
                			CreatureSpawner restored_spawner = ((CreatureSpawner)player_chunk.getBlock(x, y, z).getState());                			
                			restored_spawner.setSpawnedType(restoring_spawner.getSpawnedType());
                			player_chunk.getBlock(x, y, z).getState().update();

                		}

                	}
                	
        		}
        	}
    	}
    	if(chunk_info  !=null){
        	chunk_info.chunk_untouchedtime[array_x][array_z]=0;
    	}
    }

}
