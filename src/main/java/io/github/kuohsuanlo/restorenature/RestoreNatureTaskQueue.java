

package io.github.kuohsuanlo.restorenature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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


public class RestoreNatureTaskQueue implements Runnable {
    public static final String WORLD_SUFFIX = "_restorenature";
	public Queue<Chunk> TaskQueue = new LinkedList<Chunk>();
	public RestoreNaturePlugin rnplugin;
	public int MAX_TASK_IN_QUEUE ;

    public RestoreNatureTaskQueue(RestoreNaturePlugin plugin) {
    	rnplugin = plugin;
    	MAX_TASK_IN_QUEUE = (rnplugin.MAX_CHUNK_RADIUS*2+1)* (rnplugin.MAX_CHUNK_RADIUS*2+1);
    }
	public boolean addTask(Chunk newTask){
		if(TaskQueue.size()<MAX_TASK_IN_QUEUE){
			TaskQueue.add(newTask);
			return true;
		}
		return false;
	}
    public void run() {
    	if(TaskQueue.size()>0){
        	Chunk restored = TaskQueue.poll();
        	Chunk natrue = rnplugin.getServer().getWorld( restored.getWorld().getName()+WORLD_SUFFIX).getChunkAt(restored.getX(),restored.getZ());
        	restoreChunk(restored,natrue);
        	rnplugin.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : TaskQueue done task : "+restored.getWorld().getName()+" "+restored.getX()+" "+restored.getZ());
			
    	}

    }
    @SuppressWarnings("deprecation")
	private void restoreChunk(Chunk restored, Chunk natrue){
    	for(int x=0;x<16;x++){
            for(int y=0;y<128;y++){
                for(int z=0;z<16;z++){
                	restored.getBlock(x, y, z).setTypeId(natrue.getBlock(x, y, z).getTypeId());
                	restored.getBlock(x, y, z).setData(natrue.getBlock(x, y, z).getData());
        		}
        	}
    	}
    }
}
