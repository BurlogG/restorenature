
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


class Maintained_World{
	public String world_name = "";
	public ArrayList<String> nature_factions;
	
	public Maintained_World(String name,ArrayList<String> factions){
		world_name =name;
		nature_factions = factions;
	}
}

public class RestoreNaturePlugin extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    public static final int DEFAULT_MAX_SECONDS_UNTOUCHED = 864000;
    public static final int DEFAULT_CHECK_PERIOD_IN_SECONDS = 3600;
    public static final int DEFAULT_MAX_CHUNK_RADIUS = 200;
    public static final String VERSION = "1.0.0";
    public static final String DEFAULT_WORLDS_INFO = "{\"maintained_worlds\":[{\"world_name\": \"my_cool_world\",\"nature_factions\": [{\"faction_name\": \"Wilderness\"},{\"faction_name\": \"some_resource_area_faction\"}]},{\"world_name\": \"my_wrecked_nether\",\"nature_factions\": []}]}";
    public static int MAX_SECONDS_UNTOUCHED = DEFAULT_MAX_SECONDS_UNTOUCHED;
    public static int CHECK_PERIOD_IN_SECONDS = DEFAULT_CHECK_PERIOD_IN_SECONDS;
    public static int MAX_CHUNK_RADIUS = DEFAULT_MAX_CHUNK_RADIUS;
    private FileConfiguration config;
    public Faction faction = null;
    
    public ArrayList<Maintained_World> maintain_worlds = new ArrayList<Maintained_World>();
	public ArrayList<MapChunkInfo> maintain_world_chunk_info = new ArrayList<MapChunkInfo>();
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
        getCommand("restorenature").setExecutor(new RestoreNatureCommand(this));

    }
    public void reloadingConfig(){
		/*Reading worlds*/

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
		

    	for(int i=0;i<J_worlds.size();i++){
    		JSONObject world = (JSONObject)J_worlds.get(i);
    		ArrayList<String> n_factions = new ArrayList<String>();
    		JSONArray factions = (JSONArray) world.get("nature_factions");
    		for(int j=0;j<factions.size();j++){
    			JSONObject faction = (JSONObject)factions.get(j);
    			n_factions.add(faction.get("faction_name")+"");
    		}
    		String world_name = world.get("world_name")+"";
    		maintain_worlds.add( new Maintained_World(world_name,n_factions ));     	
    	}

    	
    }
    @SuppressWarnings("unchecked")
	private void readingConfig(){
    	new File("./plugins/RestoreNature").mkdirs();
        new File("./plugins/RestoreNature/worlds_chunk_info").mkdirs();  
    	
    	config = this.getConfig();
    	
    	//config.createSection("#The following time period and max_untouched_chunk tolerance before it gets restored");
    	//config.createSection("#needs a server restart to change.");
    	config.addDefault("version",VERSION);
    	config.addDefault("MAX_SECONDS_UNTOUCHED",DEFAULT_MAX_SECONDS_UNTOUCHED);
    	config.addDefault("CHECK_PERIOD_IN_SECONDS",DEFAULT_CHECK_PERIOD_IN_SECONDS);
    	config.addDefault("MAX_CHUNK_RADIUS",DEFAULT_MAX_CHUNK_RADIUS);
    	config.addDefault("WORLDS_INFO",DEFAULT_WORLDS_INFO);
    	
    	config.options().copyDefaults(true);
    	saveConfig();
    	
    	MAX_SECONDS_UNTOUCHED = config.getInt("MAX_SECONDS_UNTOUCHED");
    	CHECK_PERIOD_IN_SECONDS = config.getInt("CHECK_PERIOD_IN_SECONDS");
    	MAX_CHUNK_RADIUS = config.getInt("MAX_CHUNK_RADIUS");

		
		/*Reading worlds*/

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
		

    	for(int i=0;i<J_worlds.size();i++){
    		JSONObject world = (JSONObject)J_worlds.get(i);
    		ArrayList<String> n_factions = new ArrayList<String>();
    		JSONArray factions = (JSONArray) world.get("nature_factions");
    		for(int j=0;j<factions.size();j++){
    			JSONObject faction = (JSONObject)factions.get(j);
    			n_factions.add(faction.get("faction_name")+"");
    		}
    		String world_name = world.get("world_name")+"";
    		maintain_worlds.add( new Maintained_World(world_name,n_factions ));     	
    	}

    	
    	
    	
    }
    private void enablingWorlds(){
    	MapChunkInfo world_chunk_info;
      

        for(int i =0;i<this.maintain_worlds.size();i++){
        	String world_name = this.maintain_worlds.get(i).world_name;
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
        BukkitSchedulerSuck = new RestoreNatureRegularUpdate(CHECK_PERIOD_IN_SECONDS,MAX_SECONDS_UNTOUCHED,MAX_CHUNK_RADIUS,maintain_world_chunk_info,this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, BukkitSchedulerSuck, 0, 20*CHECK_PERIOD_IN_SECONDS);

    }
}
