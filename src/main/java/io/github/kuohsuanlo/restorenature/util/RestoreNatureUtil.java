package io.github.kuohsuanlo.restorenature.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.kuohsuanlo.restorenature.MapChunkInfo;
import io.github.kuohsuanlo.restorenature.RestoreNaturePlugin;

public class RestoreNatureUtil {
	@SuppressWarnings("deprecation")
	private static void restoreChunkBlock(Chunk restoring_chunk, Chunk restored_chunk, int x, int y, int z ){
		Block restoringBlock = restoring_chunk.getBlock(x, y, z);
		Block restoredBlock  = restored_chunk.getBlock(x, y, z);
		restoredBlock.setType(restoringBlock.getType());
		restoredBlock.setData(restoringBlock.getData());
    	
	}
	public static boolean isInRadius(int x,int z, int radius){
		return x*x+z*z<=radius*radius;
	}
	public static boolean isValidLocation(Chunk chunk,MapChunkInfo cinfo){
		int x = RestoreNatureUtil.convertChunkIdxToArrayIdx(chunk.getX());
		int z = RestoreNatureUtil.convertChunkIdxToArrayIdx(chunk.getZ());
		if(x>cinfo.max_x  ||  z>cinfo.max_z) return false;
		return true;
	}
	private static void restoreChunkDetails(Chunk restoring_chunk, Chunk player_chunk, int x, int y, int z ){
		if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.MOB_SPAWNER)){
    		CreatureSpawner restoring_spawner = (CreatureSpawner) restoring_chunk.getBlock(x, y, z).getState();
			CreatureSpawner restored_spawner = (CreatureSpawner) player_chunk.getBlock(x, y, z).getState();  
			
			restored_spawner.setSpawnedType(restoring_spawner.getSpawnedType());
			
			if(RestoreNaturePlugin.Verbosity>=1)
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW+RestoreNaturePlugin.PLUGIN_PREFIX+"restoring mobspawner "+restored_spawner.getSpawnedType().name());
			
			restored_spawner.update();
		}
		else if(restoring_chunk.getBlock(x, y, z).getType().equals(Material.CHEST)){
			if(RestoreNaturePlugin.Verbosity>=1)
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
					restored_chest.getInventory().addItem(new ItemStack(mtmp,ntmp));
				}
				
			}
			restored_chest.update();
		}
	}
	private static int[] calculateChunkEntityTypesNumber(Chunk chunk){
		Entity[] entitiesRestored  = chunk.getEntities();
		int[] entityNum = new int[EntityType.values().length];
		for(int e=0;e<entitiesRestored.length;e++){
			int entityTypeID = convertEntityTypeToIdx(entitiesRestored[e].getType());
			if(entityTypeID>=0){
				entityNum[entityTypeID]++;
			}
		}
		return entityNum;
	}
	private static int convertEntityTypeToIdx(EntityType et){
		for(int i=0;i<EntityType.values().length;i++){
			if(EntityType.values()[i].equals(et)) return i;
		}
		return -1;
	}
	private static boolean isValidRestoredEntityType(EntityType e){
		return 
		e.equals(EntityType.BAT)  || 
		e.equals(EntityType.BLAZE)  ||
		e.equals(EntityType.CAVE_SPIDER)  ||
		e.equals(EntityType.CHICKEN)  ||
		e.equals(EntityType.COW)  ||
		e.equals(EntityType.DONKEY)  ||
		e.equals(EntityType.LLAMA)  ||
		e.equals(EntityType.HORSE)  ||
		e.equals(EntityType.GUARDIAN)  ||
		e.equals(EntityType.ELDER_GUARDIAN)  ||
		e.equals(EntityType.MULE)  ||
		e.equals(EntityType.MUSHROOM_COW)  ||
		e.equals(EntityType.OCELOT)  ||
		e.equals(EntityType.PIG)  ||
		e.equals(EntityType.PARROT)  ||
		e.equals(EntityType.POLAR_BEAR)  ||
		e.equals(EntityType.RABBIT)  ||
		e.equals(EntityType.SHEEP)  ||
		e.equals(EntityType.SQUID)  ||
		e.equals(EntityType.SHULKER)  ||
		e.equals(EntityType.WOLF)  ||
		e.equals(EntityType.VILLAGER) ||
		e.equals(EntityType.VINDICATOR)  ||
		e.equals(EntityType.EVOKER)  ||
		e.equals(EntityType.WITCH);
	}
	private static Location getCorrespondingLocation(World world, Location eLoc){
		return new Location(world, eLoc.getX(),  eLoc.getY(),  eLoc.getZ());
	}
	private static void restoreChunkEntity(Chunk restoring_chunk, Chunk restored_chunk){
		if(!restoring_chunk.isLoaded()) restoring_chunk.load();
		if(!restored_chunk.isLoaded()) 	restored_chunk.load();
		
		
		int[] entityNum = calculateChunkEntityTypesNumber(restored_chunk);
		
		//restoring missing entities in restored chunk from restoring chunk
		Entity[] entitiesRestoring = restoring_chunk.getEntities();
		for(int e=0;e<entitiesRestoring.length;e++){
			Entity currentEntity = entitiesRestoring[e];
		
			if( isValidRestoredEntityType(currentEntity.getType()) ){
				
				int entityTypeID = convertEntityTypeToIdx(entitiesRestoring[e].getType());
				if(entityNum[entityTypeID]>0){
					entityNum[entityTypeID]--;
				}
				else{
					Location newLoc = getCorrespondingLocation(restored_chunk.getWorld(),currentEntity.getLocation());
					if(RestoreNaturePlugin.Verbosity>=1)
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
                    		restoreChunkBlock(restoring_chunk,player_chunk,x,y,z);
                        	restoreChunkDetails(restoring_chunk,player_chunk,x,y,z);
                  
                    	}
                	}
                	else{
                		restoreChunkBlock(restoring_chunk,player_chunk,x,y,z);
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
    


    public static int convertArrayIdxToChunkIdx(int x){
	    int chunk_x =0;
	    int bool_mod_2;
	    if(x%2==1){
	    	bool_mod_2 = -1;
	    }
	    else{
	      bool_mod_2 = 1;
	    }
	    chunk_x = (-1)*bool_mod_2*((x+1)/2);
	    return chunk_x;

  	}
	public static int convertChunkIdxToArrayIdx(int chunk_x){
	    int x=0;
	    int bool_gtz;
	    if(chunk_x>0){
	        bool_gtz = -1;
	    }
	    else{
	      bool_gtz = 0;
	    }
	    x = Math.abs(chunk_x)*2+bool_gtz;
	    return x;

	}
}
