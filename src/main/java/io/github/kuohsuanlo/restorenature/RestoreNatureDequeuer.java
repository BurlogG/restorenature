

package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Server;
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

import io.github.kuohsuanlo.restorenature.util.Lag;
import io.github.kuohsuanlo.restorenature.util.RestoreNatureUtil;


public class RestoreNatureDequeuer implements Runnable {

	public Queue<Location> TaskQueue = new LinkedList<Location>();
	public RestoreNaturePlugin rnplugin;
	public int MAX_TASK_IN_QUEUE ;
	public int processCount = 1;
	public int currentCount = 0;
    public RestoreNatureDequeuer(RestoreNaturePlugin plugin) {
    	rnplugin = plugin;
    	MAX_TASK_IN_QUEUE=0;
    	for(int i=0;i<rnplugin.config_maintain_worlds.size();i++){
    		int cr = rnplugin.config_maintain_worlds.get(i).chunk_radius;
    		MAX_TASK_IN_QUEUE+= Math.round(cr*cr*Math.PI);
    	}
    	rnplugin.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"Maximum number of tasks could be in TaskQueue : "+MAX_TASK_IN_QUEUE);

		
    }
	public boolean addTask(Location ChunkMid){
		if(TaskQueue.size()<MAX_TASK_IN_QUEUE){
			TaskQueue.add(ChunkMid);
			return true;
		}
		return false;
	}
    public void run() {
    	currentCount++;
    	processCount = calculateProcessCount();
    	if(currentCount>=processCount){
    		currentCount=0;
    		processRequest();
    	}
    	

    }
    private int calculateProcessCount(){
    	if(Lag.getTPS()>=19){
    		return 1;
    	}
    	else if(Lag.getTPS()>=18){
    		return 4;
    	}
    	else if(Lag.getTPS()>=17){
    		return 12;
    	}
    	else if(Lag.getTPS()>=16){
    		return 20;
    	}
    	else if(Lag.getTPS()>=15){
    		return 40;
    	}
    	else if(Lag.getTPS()>=14){
    		return 80;
    	}
    	else{
    		return 1000;
    	}
    }
    private void processRequest(){
    	if(TaskQueue.size()>0){
    		Location location = TaskQueue.poll();
        	Chunk restored = location.getChunk();
        	Chunk natrue = rnplugin.getServer().getWorld( restored.getWorld().getName()+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(restored.getX(),restored.getZ());
        	RestoreNatureUtil.restoreChunk(restored,natrue,rnplugin.getMapChunkInfoFromWorldName(restored.getWorld().getName()),RestoreNatureUtil.convertChunkIdxToArrayIdx(restored.getX()),RestoreNatureUtil.convertChunkIdxToArrayIdx(restored.getZ()));
        	
        	if(RestoreNaturePlugin.Verbosity>=1)
        		rnplugin.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"TaskQueue done task : "+restored.getWorld().getName()+" "+restored.getX()+" "+restored.getZ());
    	}
    }

}
