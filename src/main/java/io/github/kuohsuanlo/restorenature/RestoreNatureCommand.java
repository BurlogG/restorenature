
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
						 sender.sendMessage("��cYou don't have the permission.");
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
				        	
				        	sender.sendMessage(ChatColor.YELLOW+"Chunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
				            return true;    
			    			
			    		}
				        else{
							sender.sendMessage("��cYou don't have the permission.");
							return false;
						}
			        	
			    	}
		    		else if (args[0].equals("rnworld")){
		    			 if (sender.hasPermission("restorenature.rnworld")){
		    				 rnplugin.rnworld(player);
		    				 return true;
		    			 }
		    			 else{
							 sender.sendMessage("��cYou don't have the permission.");
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
								        	
								        	sender.sendMessage(ChatColor.YELLOW+"Chunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
								            return true;    
							    			

										}
							    		else{
							    			sender.sendMessage(ChatColor.YELLOW+"Chunk untouch time not enough : "+chunksInfo.chunk_untouchedtime[x][z]+" < "+rnplugin.MAX_SECONDS_UNTOUCHED);	
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
								sender.sendMessage("��cYou don't have the permission.");
								return false;
		    			 }
		    		}

		        }
		        
	        }
        }
		return false;
        	

    }

	private void restoreChunkDetails(Chunk restoring_chunk, Chunk player_chunk, int x, int y, int z ){
		if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
    		CreatureSpawner restoring_spawner = (CreatureSpawner) restoring_chunk.getBlock(x, y, z).getState();
			CreatureSpawner restored_spawner = (CreatureSpawner) player_chunk.getBlock(x, y, z).getState();  
			
			restored_spawner.setSpawnedType(restoring_spawner.getSpawnedType());
			rnplugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"[RestoreNature] : restoring mobspawner "+restored_spawner.getSpawnedType().name());
			
			restored_spawner.update();
		}
		else if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.CHEST)){
			rnplugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"[RestoreNature] : restoring chest");
			Chest restoring_chest = (Chest) restoring_chunk.getBlock(x, y, z).getState();
			Chest restored_chest = (Chest) player_chunk.getBlock(x, y, z).getState();
			
			int itemNum = restoring_chest.getBlockInventory().getSize();
			Material mtmp;
			int ntmp;
			for(int i=0;i<itemNum;i++){
				if(restoring_chest.getBlockInventory().getItem(i) ==null){
					//rnplugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"[RestoreNature] : null "+i);
				}
				else{
					mtmp = restoring_chest.getBlockInventory().getItem(i).getType();
					ntmp = restoring_chest.getBlockInventory().getItem(i).getAmount();
					//rnplugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"[RestoreNature] : restoring "+mtmp.name()+","+ntmp);
					restored_chest.getInventory().addItem(new ItemStack(mtmp,ntmp));
				}
				
			}
			//restoring_chest.update();
			restored_chest.update();
		}
	}
	private void restoreChunkEntity(Chunk restoring_chunk, Chunk restored_chunk){
		if(!restoring_chunk.isLoaded()) restoring_chunk.load();
		if(!restored_chunk.isLoaded()) 	restored_chunk.load();
		
		Entity[] entitiesRestoring = restoring_chunk.getEntities();
		Entity[] entitiesRestored  = restored_chunk.getEntities();
		
		//calculating current entities in restored chunk
		int[] entityNum = new int[Short.MAX_VALUE];
		for(int e=0;e<entitiesRestored.length;e++){
			if(entitiesRestored[e].getType().getTypeId()>=0){
				int entityTypeID = entitiesRestored[e].getType().getTypeId();
				entityNum[entityTypeID]++;
			}
		}
		
		//restoring missing entities in restored chunk from restoring chunk
		for(int e=0;e<entitiesRestoring.length;e++){
			World world = restored_chunk.getWorld();
			Location eLoc = entitiesRestoring[e].getLocation();
			Location newLoc = new Location(world, eLoc.getX(),  eLoc.getY(),  eLoc.getZ());
			
			//System.out.println(entities[e].getType().name()+" at "+newLoc.toString());
			
			if( entitiesRestoring[e].getType().equals(EntityType.BAT)  || 
				entitiesRestoring[e].getType().equals(EntityType.BLAZE)  ||
				entitiesRestoring[e].getType().equals(EntityType.CAVE_SPIDER)  ||
				entitiesRestoring[e].getType().equals(EntityType.CHICKEN)  ||
				entitiesRestoring[e].getType().equals(EntityType.COW)  ||
				entitiesRestoring[e].getType().equals(EntityType.DONKEY)  ||
				entitiesRestoring[e].getType().equals(EntityType.LLAMA)  ||
				entitiesRestoring[e].getType().equals(EntityType.HORSE)  ||
				entitiesRestoring[e].getType().equals(EntityType.GUARDIAN)  ||
				entitiesRestoring[e].getType().equals(EntityType.ELDER_GUARDIAN)  ||
				entitiesRestoring[e].getType().equals(EntityType.MULE)  ||
				entitiesRestoring[e].getType().equals(EntityType.MUSHROOM_COW)  ||
				entitiesRestoring[e].getType().equals(EntityType.OCELOT)  ||
				entitiesRestoring[e].getType().equals(EntityType.PIG)  ||
				entitiesRestoring[e].getType().equals(EntityType.PARROT)  ||
				entitiesRestoring[e].getType().equals(EntityType.POLAR_BEAR)  ||
				entitiesRestoring[e].getType().equals(EntityType.RABBIT)  ||
				entitiesRestoring[e].getType().equals(EntityType.SHEEP)  ||
				entitiesRestoring[e].getType().equals(EntityType.SQUID)  ||
				entitiesRestoring[e].getType().equals(EntityType.SHULKER)  ||
				entitiesRestoring[e].getType().equals(EntityType.WOLF)  ||
				entitiesRestoring[e].getType().equals(EntityType.VILLAGER) ||
				entitiesRestoring[e].getType().equals(EntityType.VINDICATOR)  ||
				entitiesRestoring[e].getType().equals(EntityType.EVOKER)  ||
				entitiesRestoring[e].getType().equals(EntityType.WITCH) ){
				
				int entityTypeID = entitiesRestoring[e].getType().getTypeId();
				if(entityNum[entityTypeID]>0){
					entityNum[entityTypeID]--;
				}
				else{
					rnplugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+"[RestoreNature] : restoring entitiy : "+entitiesRestoring[e].getType().name());
					restored_chunk.getWorld().spawnEntity(newLoc, entitiesRestoring[e].getType());
				}
				
				
			}
			
			
		}
		
		if(restoring_chunk.isLoaded()) restoring_chunk.unload();
		if(restored_chunk.isLoaded()) 	restored_chunk.unload();
	
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
                        	restoreChunkDetails(restoring_chunk,player_chunk,x,y,z);
                  
                    	}
                	}
                	else{
                		player_chunk.getBlock(x, y, z).setTypeId(restoring_chunk.getBlock(x, y, z).getTypeId());
                    	player_chunk.getBlock(x, y, z).setData(restoring_chunk.getBlock(x, y, z).getData());
                    	restoreChunkDetails(restoring_chunk,player_chunk,x,y,z);
                    	
                	}
        		}
        	}
    	}
    	restoreChunkEntity(restoring_chunk, player_chunk);
    	
    	if(chunk_info  !=null){
        	chunk_info.chunk_untouchedtime[array_x][array_z]=0;
    	}
    }

}
