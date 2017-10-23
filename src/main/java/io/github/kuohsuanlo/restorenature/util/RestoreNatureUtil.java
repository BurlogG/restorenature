package io.github.kuohsuanlo.restorenature.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.github.kuohsuanlo.restorenature.MapChunkInfo;
import io.github.kuohsuanlo.restorenature.RestoreNaturePlugin;

public class RestoreNatureUtil {

	private static void restoreChunkDetails(Chunk restoring_chunk, Chunk player_chunk, int x, int y, int z ){
		if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
    		CreatureSpawner restoring_spawner = (CreatureSpawner) restoring_chunk.getBlock(x, y, z).getState();
			CreatureSpawner restored_spawner = (CreatureSpawner) player_chunk.getBlock(x, y, z).getState();  
			
			restored_spawner.setSpawnedType(restoring_spawner.getSpawnedType());
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+RestoreNaturePlugin.PLUGIN_PREFIX+"restoring mobspawner "+restored_spawner.getSpawnedType().name());
			
			restored_spawner.update();
		}
		else if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.CHEST)){
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+RestoreNaturePlugin.PLUGIN_PREFIX+"restoring chest");
			Chest restoring_chest = (Chest) restoring_chunk.getBlock(x, y, z).getState();
			Chest restored_chest = (Chest) player_chunk.getBlock(x, y, z).getState();
			
			int itemNum = restoring_chest.getBlockInventory().getSize();
			Material mtmp;
			int ntmp;
			for(int i=0;i<itemNum;i++){
				if(restoring_chest.getBlockInventory().getItem(i) ==null){
					//RestoreNaturePlugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+RestoreNaturePlugin.PLUGIN_PREFIX+"null "+i);
				}
				else{
					mtmp = restoring_chest.getBlockInventory().getItem(i).getType();
					ntmp = restoring_chest.getBlockInventory().getItem(i).getAmount();
					//RestoreNaturePlugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+RestoreNaturePlugin.PLUGIN_PREFIX+"restoring "+mtmp.name()+","+ntmp);
					restored_chest.getInventory().addItem(new ItemStack(mtmp,ntmp));
				}
				
			}
			//restoring_chest.update();
			restored_chest.update();
		}
	}
	private static void restoreChunkEntity(Chunk restoring_chunk, Chunk restored_chunk){
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
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+RestoreNaturePlugin.PLUGIN_PREFIX+"restoring entitiy : "+entitiesRestoring[e].getType().name());
					restored_chunk.getWorld().spawnEntity(newLoc, entitiesRestoring[e].getType());
				}
				
				
			}
			
			
		}
		
		if(restoring_chunk.isLoaded()) restoring_chunk.unload();
		if(restored_chunk.isLoaded()) 	restored_chunk.unload();
	
	}
    @SuppressWarnings("deprecation")
	public static void restoreChunk(Chunk player_chunk, Chunk restoring_chunk, MapChunkInfo chunk_info,int array_x,int array_z){
    	for(int x=0;x<16;x++){
            for(int y=0;y<256;y++){
                for(int z=0;z<16;z++){
                	if(RestoreNaturePlugin.ONLY_RESTORE_AIR){
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
