
package io.github.kuohsuanlo.restorenature;


import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RestoreNaturePlugin extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    public static int MAX_SECONDS_UNTOUCHED = 5;
    public static int MAX_CHUNK_RADIUS = 3;
    private ArrayList<String> arraylist_maintainedworlds_name = new  ArrayList<String> ();
    protected RestoreNatureRegularUpdate BukkitSchedulerSuck; 
    protected RestoreNatureBlockListener blockListener = new RestoreNatureBlockListener(this); 
    
    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() { 
    	
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blockListener, this);

        // Register our commands
        getCommand("restorenature").setExecutor(new RestoreNatureCommand());

        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
        
        enablingWorlds();
        startingRestoreRoutines();
    }
    private void enablingWorlds(){
        arraylist_maintainedworlds_name.add("wasteland"); 
    }
    private void startingRestoreRoutines(){
        BukkitSchedulerSuck = new RestoreNatureRegularUpdate(1,MAX_SECONDS_UNTOUCHED,MAX_CHUNK_RADIUS,arraylist_maintainedworlds_name,this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, BukkitSchedulerSuck, 0, 20);

    }
}
