

package io.github.kuohsuanlo.restorenature;

import java.util.LinkedList;
import java.util.Queue;
import org.bukkit.Chunk;
import org.bukkit.Location;
import io.github.kuohsuanlo.restorenature.util.Lag;
import io.github.kuohsuanlo.restorenature.util.RestoreNatureUtil;


public class RestoreNatureDequeuer implements Runnable {

	public Queue<Location> FullRestoreQueue = new LinkedList<Location>();
	public Queue<Location> EntityRestoreQueue = new LinkedList<Location>();
	public RestoreNaturePlugin rnplugin;
	public int MAX_TASK_IN_QUEUE ;
	public int processCount = 1;
	public int currentCount = 0;
	
	public double lastSecondTPS = 20;
	public int tpsCount = 20;
	public int tpsCurrentCount = 0;
	
	public int lastFullChunkRestored =0;
	public int lastEntityChunkRestored =0;
	public int lastEntityRespawn =0;
	public int lastBannedBlockRemoved =0;
	
    public RestoreNatureDequeuer(RestoreNaturePlugin plugin) {
    	rnplugin = plugin;
    	MAX_TASK_IN_QUEUE=0;
    	for(int i=0;i<rnplugin.config_maintain_worlds.size();i++){
    		int cr = rnplugin.config_maintain_worlds.get(i).chunk_radius;
    		MAX_TASK_IN_QUEUE+= Math.round(cr*cr*Math.PI);
    	}
    	rnplugin.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"Maximum number of tasks could be in TaskQueue : "+MAX_TASK_IN_QUEUE);

		
    }
	public boolean addFullRestoreTask(Location ChunkMid){
		if(FullRestoreQueue.size()<MAX_TASK_IN_QUEUE){
			FullRestoreQueue.add(ChunkMid);
			return true;
		}
		return false;
	}
	public boolean addEntityRestoreTask(Location ChunkMid){
		if(EntityRestoreQueue.size()<MAX_TASK_IN_QUEUE){
			EntityRestoreQueue.add(ChunkMid);
			return true;
		}
		return false;
	}
    public void run() {
    	tpsCurrentCount++;
    	if(tpsCurrentCount>=tpsCount){
    		tpsCurrentCount=0;
    		lastSecondTPS = Lag.getTPS();
    	}
    	
    	currentCount++;
    	processCount = calculateProcessCount();
    	if(currentCount>=processCount){
    		currentCount=0;
    		processFullRequest();
    		processEntityRequest();
    	}
    	

    }
    private int calculateProcessCount(){
    	if(lastSecondTPS>=19){
    		return 1;
    	}
    	else if(lastSecondTPS>=18){
    		return 4;
    	}
    	else if(lastSecondTPS>=17){
    		return 20;
    	}
    	else if(lastSecondTPS>=16){
    		return 40;
    	}
    	else if(lastSecondTPS>=15){
    		return 160;
    	}
    	else if(lastSecondTPS>=14){
    		return 400;
    	}
    	else{
    		return 1000;
    	}
    }
    private void processFullRequest(){
    	if(FullRestoreQueue.size()>0){
    		Location location = FullRestoreQueue.poll();
        	Chunk restored = location.getChunk();
        	Chunk natrue = rnplugin.getServer().getWorld( restored.getWorld().getName()+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(restored.getX(),restored.getZ());
        	RestoreNatureUtil.restoreChunk(restored,natrue,rnplugin.getMapChunkInfoFromWorldName(restored.getWorld().getName()),RestoreNatureUtil.convertChunkIdxToArrayIdx(restored.getX()),RestoreNatureUtil.convertChunkIdxToArrayIdx(restored.getZ()));
        	
        	lastFullChunkRestored++;
        	if(RestoreNaturePlugin.Verbosity>=1)
        		rnplugin.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"FullRestoreQueue done task : "+restored.getWorld().getName()+" "+restored.getX()+" "+restored.getZ());
    	}
    }
    private void processEntityRequest(){
    	if(EntityRestoreQueue.size()>0){
    		Location location = EntityRestoreQueue.poll();
        	Chunk restored = location.getChunk();
        	Chunk restoring = rnplugin.getServer().getWorld( restored.getWorld().getName()+RestoreNaturePlugin.WORLD_SUFFIX).getChunkAt(restored.getX(),restored.getZ());

        	lastEntityChunkRestored++;
        	lastEntityRespawn += RestoreNatureUtil.restoreChunkEntity(restored,restoring);
        	lastBannedBlockRemoved+=RestoreNatureUtil.removeBannedBlockedInChunk(restored,restoring);
        	
        	if(RestoreNaturePlugin.Verbosity>=1)
        		rnplugin.getServer().getConsoleSender().sendMessage(RestoreNaturePlugin.PLUGIN_PREFIX+"EntityRestoreQueue done task : "+restored.getWorld().getName()+" "+restored.getX()+" "+restored.getZ());
    	}
    }

}
