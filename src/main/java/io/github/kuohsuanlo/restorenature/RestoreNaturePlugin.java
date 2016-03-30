
package io.github.kuohsuanlo.restorenature;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;



public class RestoreNaturePlugin extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    public static int MAX_SECONDS_UNTOUCHED = 10;//864000 = 10 days
    public static int CHECK_PERIOD_IN_SECONDS = 5;//
    public static int MAX_CHUNK_RADIUS = 3;
    public Faction faction = null;
    private ArrayList<String> worlds_name = new ArrayList<String>();
	public ArrayList<MapChunkInfo> maintained_worlds = new ArrayList<MapChunkInfo>();
    protected RestoreNatureRegularUpdate BukkitSchedulerSuck; 
    protected RestoreNatureBlockListener blockListener = new RestoreNatureBlockListener(this); 
    public static HashMap<String, String> messageData = new HashMap<String, String>();
    @Override
    public void onDisable() {
    	for(int i=0;i<BukkitSchedulerSuck.maintained_worlds.size();i++){
			try {
	        	FileOutputStream fos;
				fos = new FileOutputStream("./plugins/RestoreNature/worlds_chunk_info/"+BukkitSchedulerSuck.maintained_worlds.get(i).world_name+".chunkinfo");
	        	ObjectOutputStream oos = new ObjectOutputStream(fos);
	        	oos.writeObject(BukkitSchedulerSuck.maintained_worlds.get(i));
	        	oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() { 

    	readingConfig();
    	registeringCommands();
        enablingWorlds();
        startingRestoreRoutines();
        
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
 
    }

    private void registeringCommands(){
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blockListener, this);

        // Register our commands
        getCommand("restorenature").setExecutor(new RestoreNatureCommand());

    }
    private void readingConfig(){
    	new File("./plugins/RestoreNature").mkdirs();

	    worlds_name.add("world");     	
    }
    private void enablingWorlds(){
    	MapChunkInfo world_chunk_info;
	
        new File("./plugins/RestoreNature/worlds_chunk_info").mkdirs();        

        for(int i =0;i<worlds_name.size();i++){
        	String world_name = worlds_name.get(i);
    		try {
    	  	   	String path = "./plugins/RestoreNature/worlds_chunk_info/"+world_name+".chunkinfo";
    	  	   	File file = new File(path);
    	  	   	
    	  	   	
    	  	   	if(file.exists()){
    	  	   		this.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : successfully load from "+"./plugins/RestoreNature/worlds_chunk_info/"+world_name+".chunkinfo");	
    	  	   		
    				FileInputStream fis;
    				fis = new FileInputStream("./plugins/RestoreNature/worlds_chunk_info/"+world_name+".chunkinfo");
    		    	ObjectInputStream ois = new ObjectInputStream(fis);
    		    	world_chunk_info = (MapChunkInfo) ois.readObject();
    		    	ois.close();
    	  	   	}
    	  	   	else{
    	  	   		this.getServer().getConsoleSender().sendMessage("¡±e[RestoreNature] : no existing file, creating new chunk info");
    				world_chunk_info = createMapChunkInfo(world_name);

    	  	   	}

    	    	maintained_worlds.add(world_chunk_info);
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }


    }
    private MapChunkInfo createMapChunkInfo(String world_name){
    	
       	int min_chunk_x = 0;
       	int min_chunk_z = 0; 
       	int max_chunk_x = MAX_CHUNK_RADIUS*2;
       	int max_chunk_z = MAX_CHUNK_RADIUS*2; 

    	int[][] world_chunks = new int[max_chunk_x+1][max_chunk_z+1];
    	for (int x=min_chunk_x; x <= max_chunk_x; x++){
		   for (int z=min_chunk_z; z <= max_chunk_z; z++){
			   world_chunks[x][z] = 0;
		   }
    	}
    	return new MapChunkInfo(world_name,world_chunks);
    	
    	
    	
    }
    private void startingRestoreRoutines(){
        BukkitSchedulerSuck = new RestoreNatureRegularUpdate(CHECK_PERIOD_IN_SECONDS,MAX_SECONDS_UNTOUCHED,MAX_CHUNK_RADIUS,maintained_worlds,this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, BukkitSchedulerSuck, 0, 20*CHECK_PERIOD_IN_SECONDS);

    }
}
