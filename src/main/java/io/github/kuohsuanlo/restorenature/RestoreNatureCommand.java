
package io.github.kuohsuanlo.restorenature;

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
import org.bukkit.material.MaterialData;
 
public class RestoreNatureCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
    private RestoreNaturePlugin rnplugin;
    
    public final int restoreType_RestoreAll = 0;
    public final int restoreType_OnlyRestoreAir = 1;
    public final int restoreType_OnlyRestoreNotAir = 2;
    
    public RestoreNatureCommand(RestoreNaturePlugin plugin){
    	rnplugin = plugin;
    }
    public void restoreChunk(World currentWorld,MapChunkInfo map_chunk_info,int chunk_x, int chunk_z, CommandSender sender){
    	Chunk tmp = currentWorld.getChunkAt(1000000, 1000000);
    	Chunk player_chunk = currentWorld.getChunkAt(chunk_x, chunk_z);
    	if(RestoreNaturePlugin.ONLY_RESTORE_AIR==true){
    		pasteChunk(tmp,player_chunk,null,this.restoreType_RestoreAll,sender);
        	currentWorld.regenerateChunk(player_chunk.getX(), player_chunk.getZ());
        	pasteChunk(player_chunk,tmp,map_chunk_info,this.restoreType_OnlyRestoreNotAir,sender);
    	}
    	else{
    		currentWorld.regenerateChunk(player_chunk.getX(), player_chunk.getZ());
    	}
    	
    	sender.sendMessage("[RestoreNature] : Chunk successfully restored on world chunk : "+currentWorld.getName()+" "+player_chunk.getX()+" ; "+player_chunk.getZ());	
        
    	
    }
    public void restoreChunk(World currentWorld,int chunk_x, int chunk_z, CommandSender sender){
    	restoreChunk(currentWorld,null, chunk_x,  chunk_z,  sender);
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
	        	restoreChunk(currentWorld, chunk_x,chunk_z,sender);
				
		    }
		    else if (args.length == 1 ) {

		        if (sender instanceof Player) {
			        Player player = (Player) sender;

		    		if (args[0].equals("mr")){
				        if (sender.hasPermission("restorenature.manualrestore")){
			    			Location player_location = player.getLocation();
			    			Chunk player_chunk = player_location.getChunk();
				        	World currentWorld = player.getWorld();
				        	
				        	restoreChunk(currentWorld, player_chunk.getX(),player_chunk.getZ(),sender);
				        	
				        	return true;    
			    			
			    		}
				        else{
							sender.sendMessage("[RestoreNature] : You don't have the permission.");
							return false;
						}
			        	
			    	}
		    		else if (args[0].equals("rnworld")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 rnplugin.rnworld(player);
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
					         World player_world = player.getWorld();
					        	
					         Chunk player_chunk = player.getWorld().getChunkAt(player_location);
					         
		 					 if(!rnplugin.BukkitSchedulerSuck.checkLocationClaimed(player_chunk)){ // Land not claimed
						         
		 						 for(int i=0;i<rnplugin.maintain_world_chunk_info.size();i++){
		 							 if(player_world_name.equals( rnplugin.maintain_world_chunk_info.get(i).world_name)){

							    		MapChunkInfo chunksInfo = rnplugin.maintain_world_chunk_info.get(i);
							    		int x = rnplugin.transformation_from_chunkidx_to_arrayidx(player_chunk.getX(),chunksInfo.chunk_radius);
							    		int z = rnplugin.transformation_from_chunkidx_to_arrayidx(player_chunk.getZ(),chunksInfo.chunk_radius);
	
							    		if(chunksInfo.chunk_untouchedtime[x][z]>=rnplugin.MAX_SECONDS_UNTOUCHED){
							    			restoreChunk(player_world, player_chunk.getX(),player_chunk.getZ(),sender);
							    			return true;  
										}
							    		else{
							    			sender.sendMessage("[RestoreNature] : Chunk untouch time not enough : "+chunksInfo.chunk_untouchedtime[x][z]+" < "+rnplugin.MAX_SECONDS_UNTOUCHED);	
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

		        }
		        
	        }
        }
		return false;
        	

    }
	
    @SuppressWarnings("deprecation")
	public void pasteChunk(Chunk replaced_chunk, Chunk pasted_chunk, MapChunkInfo chunk_info, int restore_type, CommandSender sender){
    	for(int x=0;x<16;x++){
            for(int y=0;y<256;y++){
                for(int z=0;z<16;z++){
                	if(restore_type==this.restoreType_OnlyRestoreAir){
                    	if(replaced_chunk.getBlock(x, y, z).getType().equals(Material.AIR)){

                    		replaced_chunk.getBlock(x, y, z).setTypeId(pasted_chunk.getBlock(x, y, z).getTypeId());
                        	replaced_chunk.getBlock(x, y, z).setData(pasted_chunk.getBlock(x, y, z).getData());
                        	if(pasted_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
                        		processMobSpawner(replaced_chunk, pasted_chunk, x, y, z);
                            }
                    	}

                    	
                	}
                	else if(restore_type==this.restoreType_RestoreAll){

                		replaced_chunk.getBlock(x, y, z).setTypeId(pasted_chunk.getBlock(x, y, z).getTypeId());
                    	replaced_chunk.getBlock(x, y, z).setData(pasted_chunk.getBlock(x, y, z).getData());
                		if(pasted_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
                			processMobSpawner(replaced_chunk, pasted_chunk, x, y, z);

                		}

                	}
                	else if(restore_type==this.restoreType_OnlyRestoreNotAir){
                		if(!replaced_chunk.getBlock(x, y, z).getType().equals(Material.AIR)){
                			
                			replaced_chunk.getBlock(x, y, z).setTypeId(pasted_chunk.getBlock(x, y, z).getTypeId());
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
    		int array_x = rnplugin.transformation_from_chunkidx_to_arrayidx(replaced_chunk.getX(),chunk_info.chunk_radius);
        	int array_z = rnplugin.transformation_from_chunkidx_to_arrayidx(replaced_chunk.getZ(),chunk_info.chunk_radius);
        	
    		if(array_x<chunk_info.max_x &&  array_z<chunk_info.max_z &&  array_x>=0  &&  array_z>=0)
    			chunk_info.chunk_untouchedtime[array_x][array_z]=0;
    		else{
    			sender.sendMessage("[RestoreNature] : out of maintenance bound,"+"("+array_x+","+array_z+")" );	
    		}
    	}
    }

	private void processMobSpawner(Chunk replaced_chunk, Chunk pasted_chunk, int x,int y,int z){
		rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : restoring mobspawner");
    	
		CreatureSpawner restored_spawner = ((CreatureSpawner)pasted_chunk.getBlock(x, y, z).getState());                			
		restored_spawner.setSpawnedType(restored_spawner.getSpawnedType());
		replaced_chunk.getBlock(x, y, z).getState().update();
	}	
}
