package io.github.kuohsuanlo.restorenature;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class RestoreNatureBlockListener implements Listener {
    private final RestoreNaturePlugin rnplugin;
    public RestoreNatureBlockListener(RestoreNaturePlugin plugin){
    	rnplugin  = plugin;
    } 
    
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }
}