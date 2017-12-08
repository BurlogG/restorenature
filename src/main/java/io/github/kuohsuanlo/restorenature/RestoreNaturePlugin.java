
package io.github.kuohsuanlo.restorenature;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.xlib.mongodb.util.JSON;

import io.github.kuohsuanlo.restorenature.util.Lag;


class Maintained_World{
	public String world_name = "";
	public ArrayList<String> nature_factions;
	public int chunk_radius;
	public Maintained_World(String name,ArrayList<String> factions,int radius){
		world_name =name;
		nature_factions = factions;
		chunk_radius = radius;
	}
}

public class RestoreNaturePlugin extends JavaPlugin {

	
    public static final String WORLD_SUFFIX = "_rs";
    public static int MAX_SECONDS_UNTOUCHED = 43200;
    public static int MAX_SECONDS_ENTITYRECOVER = 86400;

    public static int BLOCK_EVENT_EFFECTING_RADIUS = 2;
    public static int Verbosity = 0; 
    public static boolean USING_FEATURE_FACTION = true;
    public static boolean USING_FEATURE_GRIEFPREVENTION = true;
    public static boolean ONLY_RESTORE_AIR = true;
    private static FileConfiguration config;
    
    public static final String VERSION = "1.0.1a";
    public static final String DEFAULT_WORLDS_INFO = "{\"maintained_worlds\":[{\"world_name\": \"my_cool_world\",\"check_radius\": \""+200+"\",\"nature_factions\": [{\"faction_name\": \"Wilderness\"},{\"faction_name\": \"some_resource_area_faction\"}]},{\"world_name\": \"my_wrecked_nether\",\"check_radius\": \""+200+"\",\"nature_factions\": []}]}";
    
    public static String PLUGIN_PREFIX = "[RestoreNature] : ";
    public static String UNTOUCHED_TIME_NOT_ENOUGH = "Untouched time not enough. Time in seconds : ";
    public static String UNTOUCHED_TIME_ENOUGH = "Untouched time enough.  Time in seconds : ";
    public static String WILL_BE_RESTORED_SOON = "This chunk would be restored soon!";
    public static String CLAIMED = "This chunk contains claimed lands!";
    public static String OUT_OF_BOUND = "This chunk is not in maintained radius : ";
    
    
    
    public ArrayList<Maintained_World> config_maintain_worlds = new ArrayList<Maintained_World>();
	public ArrayList<MapChunkInfo> maintain_world_chunk_info = new ArrayList<MapChunkInfo>();
	public RestoreNatureEnqueuer ChunkUpdater; 
    public RestoreNatureDequeuer ChunkTimeTicker;
    public Lag LagTicker;
    private static int UpdaterId=-1;
    private static int TickerId=-1;
    private static int TpsCounterId=-1;

    private final RestoreNatureBlockListener blockListener = new RestoreNatureBlockListener(this); 
    
    private static RestoreNatureCommand CommandExecutor;
    public static HashMap<String, String> messageData = new HashMap<String, String>();
    @Override
    public void onDisable() {
    	for(int i=0;i<ChunkUpdater.maintained_worlds.size();i++){
			try {
	        	FileOutputStream fos;
				fos = new FileOutputStream("./plugins/RestoreNature/worlds_chunk_info/"+ChunkUpdater.maintained_worlds.get(i).world_name+".chunkinfo");
	        	ObjectOutputStream oos = new ObjectOutputStream(fos);
	        	oos.writeObject(ChunkUpdater.maintained_worlds.get(i));
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
    	createConfig();
    	loadConfig();
    	registerCommands();
        enableWorlds();
        startUpdaterRoutine();
        
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
 
    }

    
    private void createConfig(){

    	new File(".plugins/RestoreNature").mkdirs();
        new File("./plugins/RestoreNature/worlds_chunk_info").mkdirs();  
        
    	config = this.getConfig();
    	config.addDefault("version",VERSION);
    	config.addDefault("Verbosity",Verbosity);
    	
    	config.addDefault("PLUGIN_PREFIX",PLUGIN_PREFIX);
    	config.addDefault("UNTOUCHED_TIME_NOT_ENOUGH",UNTOUCHED_TIME_NOT_ENOUGH);
    	config.addDefault("UNTOUCHED_TIME_ENOUGH",UNTOUCHED_TIME_ENOUGH);
    	config.addDefault("WILL_BE_RESTORED_SOON",WILL_BE_RESTORED_SOON);
    	config.addDefault("OUT_OF_BOUND",OUT_OF_BOUND);
    	config.addDefault("CLAIMED",CLAIMED);
    	
    	
    	config.addDefault("MAX_SECONDS_UNTOUCHED",MAX_SECONDS_UNTOUCHED);
    	config.addDefault("MAX_SECONDS_ENTITYRECOVER",MAX_SECONDS_ENTITYRECOVER);
    	
    	config.addDefault("BLOCK_EVENT_EFFECTING_RADIUS",BLOCK_EVENT_EFFECTING_RADIUS);
    	config.addDefault("USING_FEATURE_FACTION",USING_FEATURE_FACTION);
    	config.addDefault("USING_FEATURE_GRIEFPREVENTION",USING_FEATURE_GRIEFPREVENTION);
    	config.addDefault("ONLY_RESTORE_AIR",ONLY_RESTORE_AIR);
    	config.addDefault("WORLDS_INFO",DEFAULT_WORLDS_INFO);
       	config.options().copyDefaults(true);
    	saveConfig();
    }

	private void loadConfig(){

    	PLUGIN_PREFIX = config.getString("PLUGIN_PREFIX");
    	UNTOUCHED_TIME_NOT_ENOUGH = config.getString("UNTOUCHED_TIME_NOT_ENOUGH");
    	UNTOUCHED_TIME_ENOUGH = config.getString("UNTOUCHED_TIME_ENOUGH");
    	WILL_BE_RESTORED_SOON = config.getString("WILL_BE_RESTORED_SOON");
    	OUT_OF_BOUND = config.getString("OUT_OF_BOUND");
    	CLAIMED = config.getString("CLAIMED");
    	

		Verbosity = config.getInt("Verbosity");
    	MAX_SECONDS_UNTOUCHED 			= config.getInt("MAX_SECONDS_UNTOUCHED");
    	MAX_SECONDS_ENTITYRECOVER 		= config.getInt("MAX_SECONDS_ENTITYRECOVER");
    	BLOCK_EVENT_EFFECTING_RADIUS 	= config.getInt("BLOCK_EVENT_EFFECTING_RADIUS");
    	USING_FEATURE_FACTION 			= config.getBoolean("USING_FEATURE_FACTION");
    	USING_FEATURE_GRIEFPREVENTION 	= config.getBoolean("USING_FEATURE_GRIEFPREVENTION");
    	ONLY_RESTORE_AIR 				= config.getBoolean("ONLY_RESTORE_AIR");

    	
		JSONParser parser = new JSONParser();
		JSONObject J_maintained_worlds = null;
		JSONArray J_worlds = null ;
		try {
			J_maintained_worlds = (JSONObject)parser.parse(config.getString("WORLDS_INFO"));
			J_worlds = (JSONArray)J_maintained_worlds.get("maintained_worlds");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.getServer().getConsoleSender().sendMessage(PLUGIN_PREFIX+"JSON worlds size : "+ J_worlds.size());
    	for(int i=0;i<J_worlds.size();i++){
    		JSONObject world = (JSONObject)J_worlds.get(i);
    		ArrayList<String> n_factions = new ArrayList<String>();
    		JSONArray factions = (JSONArray) world.get("nature_factions");
    		for(int j=0;j<factions.size();j++){
    			JSONObject faction = (JSONObject)factions.get(j);
    			n_factions.add(faction.get("faction_name")+"");
    		}
    		String world_name = world.get("world_name")+"";
    		String radius = world.get("check_radius")+"";
    		config_maintain_worlds.add( new Maintained_World(world_name,n_factions,Integer.valueOf(radius)));     	    	
    	}

    	
    	
    	
    }
    private void registerCommands(){
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blockListener, this);

        // Register our commands
        CommandExecutor = new RestoreNatureCommand(this);
        getCommand("restorenature").setExecutor(CommandExecutor);

    }


    private void enableWorlds(){
    	MapChunkInfo world_chunk_info;
    	maintain_world_chunk_info.clear();

        for(int i =0;i<config_maintain_worlds.size();i++){
        	String world_name = config_maintain_worlds.get(i).world_name;
        	int radius = config_maintain_worlds.get(i).chunk_radius;
    		try {
    	  	   	String path = "./plugins/RestoreNature/worlds_chunk_info/"+world_name+".chunkinfo";
    	  	   	File file = new File(path);
    	  	   	
    	  	   	
    	  	   	if(file.exists()){
    	  	   		this.getServer().getConsoleSender().sendMessage(PLUGIN_PREFIX+"successfully load from "+"./plugins/RestoreNature/worlds_chunk_info/"+world_name+".chunkinfo");	
    	  	   		
    				FileInputStream fis;
    				fis = new FileInputStream("./plugins/RestoreNature/worlds_chunk_info/"+world_name+".chunkinfo");
    		    	ObjectInputStream ois = new ObjectInputStream(fis);
    		    	world_chunk_info = (MapChunkInfo) ois.readObject();
    		    	ois.close();
    	  	   	}
    	  	   	else{
    	  	   		this.getServer().getConsoleSender().sendMessage(PLUGIN_PREFIX+"no existing file, creating new chunk info");
    				world_chunk_info = createMapChunkInfo(world_name,radius, config_maintain_worlds.get(i).nature_factions);

    	  	   	}

    	    	maintain_world_chunk_info.add(world_chunk_info);
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
    private MapChunkInfo createMapChunkInfo(String world_name,int radius,ArrayList<String> fname){
    	
       	int min_chunk_x = 0;
       	int min_chunk_z = 0; 
       	int max_chunk_x = radius*2;
       	int max_chunk_z = radius*2; 

    	int[][] world_chunks = new int[max_chunk_x+1][max_chunk_z+1];
    	for (int x=min_chunk_x; x <= max_chunk_x; x++){
		   for (int z=min_chunk_z; z <= max_chunk_z; z++){
			   world_chunks[x][z] = 0;
		   }
    	}
    	return new MapChunkInfo(world_name,world_chunks,radius,fname,max_chunk_x,max_chunk_z,min_chunk_x,min_chunk_z);
    	
    	
    	
    }
    private void startUpdaterRoutine(){
    	if(UpdaterId>=0) Bukkit.getServer().getScheduler().cancelTask(UpdaterId);
    	ChunkUpdater = new RestoreNatureEnqueuer(MAX_SECONDS_UNTOUCHED,maintain_world_chunk_info,this);
        UpdaterId = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, ChunkUpdater, 0, 1);
        
        if(TickerId>=0)  Bukkit.getServer().getScheduler().cancelTask(TickerId);
        ChunkTimeTicker = new RestoreNatureDequeuer(this);
        TickerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, ChunkTimeTicker, 0, 1);
        
        if(TpsCounterId>=0)  Bukkit.getServer().getScheduler().cancelTask(TpsCounterId);
        LagTicker = new Lag();
        TpsCounterId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, LagTicker, 0, 1);

    }
    public MapChunkInfo getMapChunkInfoFromWorldName(String world_name){
    	for(int i=0;i<maintain_world_chunk_info.size();i++){
    		if(maintain_world_chunk_info.get(i).world_name.equals(world_name)){
    			return maintain_world_chunk_info.get(i);
    		}
    	}
    	return null;
    }
}
