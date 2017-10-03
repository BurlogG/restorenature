

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

	public Queue<Chunk> TaskQueue = new LinkedList<Chunk>();
	public RestoreNaturePlugin rnplugin;
	public int MAX_TASK_IN_QUEUE ;

    public RestoreNatureTaskQueue(RestoreNaturePlugin plugin) {
    	rnplugin = plugin;
    	MAX_TASK_IN_QUEUE=0;
    	for(int i=0;i<rnplugin.config_maintain_worlds.size();i++){
    		int cr = rnplugin.config_maintain_worlds.get(i).chunk_radius;
    		MAX_TASK_IN_QUEUE+= (2*cr+1)*(2*cr+1);
    	}
    	rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : Maximum number of tasks could be in TaskQueue : "+MAX_TASK_IN_QUEUE);

		
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
        	rnplugin.CommandExecutor.restoreChunk(
        			restored.getWorld(),
        			rnplugin.getMapChunkInfo(restored.getWorld().getName()),
        			restored.getX(),
        			restored.getZ(),
        			rnplugin.getServer().getConsoleSender());
        	rnplugin.getServer().getConsoleSender().sendMessage("[RestoreNature] : TaskQueue done task : "+restored.getWorld().getName()+" "+restored.getX()+" "+restored.getZ());

			
    	}

    }

}
