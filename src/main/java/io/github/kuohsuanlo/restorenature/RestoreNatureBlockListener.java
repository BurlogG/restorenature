package io.github.kuohsuanlo.restorenature;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public class RestoreNatureBlockListener implements Listener {
    private final RestoreNaturePlugin rnplugin;
    public RestoreNatureBlockListener(RestoreNaturePlugin plugin){
    	rnplugin  = plugin;
    }

    @EventHandler
    public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }   
    @EventHandler
    public void onBrewEvent(BrewEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }   
    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent event) {
        Block block = event.getEnchantBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }      
    
    
    
    @EventHandler
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }     
    @EventHandler
    public void onBlockIgniteEvent(BlockIgniteEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }     
    @EventHandler
    public void onBlockGrowEvent(BlockGrowEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }   
    @EventHandler
    public void onBlockFormEvent(BlockFormEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
    }    
    @EventHandler
    public void onBlockFromToEvent(BlockFromToEvent event) {
        Block block = event.getBlock();
        rnplugin.BukkitSchedulerSuck.setWorldsChunkUntouchedTime(block);
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