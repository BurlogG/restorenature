
package io.github.kuohsuanlo.restorenature;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class RestoreNatureCommand implements CommandExecutor {
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
			if (cmd.getName().equalsIgnoreCase("restorenature")) { // If the player typed /basic then do the following...
		        if (args.length == 3 ) {
		        	String world_name = args[0];
		        	int chunk_x = Integer.valueOf(args[1]);
		        	int chunk_z = Integer.valueOf(args[2]);
		        	
		        	Chunk player_chunk = sender.getServer().getWorld(world_name).getChunkAt(chunk_x, chunk_z);		        	
		        	Chunk restoring_chunk = sender.getServer().getWorld(world_name+"_restorenature").getChunkAt(chunk_x, chunk_z);	
		        	
		            
		        	restoreChunk(player_chunk,restoring_chunk);
		            	    
					sender.sendMessage("¡±eChunk successfully restored on world chunk : "+world_name+" ; "+restoring_chunk.getX()+" "+restoring_chunk.getZ());	
		            
		        }
	        }
			return false;
        }
        else{
	        Player player = (Player) sender;
	        
			if (cmd.getName().equalsIgnoreCase("restorenature")) { // If the player typed /basic then do the following...
		        if (args.length == 1 ) {

			    	if (sender.hasPermission("restorenature.manualrestore")){
			    		if (args[0].equals("manualrestore")){
			    			Location player_location = player.getLocation();
				        	String player_world_name = player.getWorld().getName();
				        	
				        	Chunk player_chunk = player.getWorld().getChunkAt(player_location);
				        	Chunk restoring_chunk = sender.getServer().getWorld(player_world_name+"_restorenature").getChunkAt(player_location) ;
				        	
				        	restoreChunk(player_chunk,restoring_chunk);
				        	
				        	sender.sendMessage("¡±eChunk successfully restored on world chunk : "+player_world_name+" "+restoring_chunk.getX()+" ; "+restoring_chunk.getZ());	
				            return true;    
			    			
			    		}
			        	
			    	}
					else{
						sender.sendMessage("¡±cYou don't have the permission.");
						return false;
					}
		        }
	        }
			return false;
			
		}
    }
    @SuppressWarnings("deprecation")
	private void restoreChunk(Chunk player_chunk, Chunk restoring_chunk){
    	for(int x=0;x<16;x++){
            for(int y=0;y<128;y++){
                for(int z=0;z<16;z++){
                	player_chunk.getBlock(x, y, z).setTypeId(restoring_chunk.getBlock(x, y, z).getTypeId());
                	player_chunk.getBlock(x, y, z).setData(restoring_chunk.getBlock(x, y, z).getData());
        		}
        	}
    	}
    }

}
