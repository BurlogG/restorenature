# RestoreNature --- Powered by [LogoCat](https://mcuuid.net/?q=logocat) 
[![mcfallout](https://i.imgur.com/o6S7V07.png)](https://mcfallout.net)
Jar Download link: [Spigot Page](https://www.spigotmc.org/resources/restore-nature.21215/)
![ScreenShot](http://i.imgur.com/pK1bLqy.png)
This plugin is a minecraft server-side spigot plugin in order to enhance the experience by:
  - Periodically check the chunks in worlds of its untouched time and respawn the resources
  - Including blocks ![](https://www.csie.ntu.edu.tw/~b98902055/items/2-0.png) , ores ![](https://www.csie.ntu.edu.tw/~b98902055/items/56-0.png) , entities ![](https://www.csie.ntu.edu.tw/~b98902055/items/383-120.png), minecraft structures like nether fortress ![](https://www.csie.ntu.edu.tw/~b98902055/items/112-0.png) .

###### Slowly regenerate your world by well-designed scheduling systemeven 
###### (tested with a 10000x10000 map. CPU usage <0.5%). 
###### Tested on 70 players with positive feedbacks from hardcore redstone players (mcfallout.net)

---------
  ### Features : 
  - Restore blocks 
  - Respawn entities 
  - Two modes of restoration- 
    1. Only-Restore-Air (Mode 1): will not remove players buildings. Only restoring the removed natural resources blocks.
    2. Restore-All (Mode 2): all of the world would become the same as it first generated. Removing players' buildings and blocks.
  - Supporting [faction], [griefprevention] claim system.
  - High flexibility on the configuration
  - Well-designed plugin performance :
      1. Not enough power of CPU? Performance could be tuned to fit the server's computing power. 
      2. Freeze issue? 
         Don't worry. This plugin will cut the tasks into tiny pieces for your server to consume. 
      3. Memory issue? 
         Not a problem either. This plugin will maintain a maximum task queue. 
It will drop the upcoming tasks if the queue is temporarily filled.

The progress will be saved in restorenature/world_chunk_info/... 
You are free to stop the server and proceed the progress at any time :)
---------
### Environment 
This build is compiled and tested on these environments.
* [java] - JVM 1.8
* [spigot] - 1.12.x

### Hard-Dependency
This plugin needs to run with the following plugins with the latest version to work properly:
* [worldedit] (NOT AsyncWorldEdit)
* [multiverse-core]
### Supporting
This plugin supports the following plugins that increase the gaming experiences
* [faction]
* [griefprevention]
----
### Installation
1. Drop the plugin jar file in your server folder /plugins/ and run once.
2. Create the world you want to use as backup. The backup world with name suffix "_rs" should be assigned with the same map generator and same seeds. 
   You could do this by runngin commands 
   /mv create WORLDNAME_rs [type] [-s SEED] [-g GENERATOR]
2. After the plugin folder and default config.yml is generated, stop the server.
3. Start to set your own config withing config.yml.
4. Start the server again.

### Configuration setting
Edit config.yml file
```sh
MAX_SECONDS_UNTOUCHED: 864000 
#How many seconds the chunks will be restored after no one break/place the block.
RESTORING_PERIOD_PER_CHUNK_IN_SECONDS: 1 
#Every X seconds will restore a chunk. #If the server is not lagging. Set to 1 if you want. Only accept integer.
BLOCK_EVENT_EFFECTING_RADIUS: 1 
#1.0.0e New feature. The affecting radius( measured in chunk radius) that recording the players activity so the chunk won't get restored. 1 = 1x1 chunk (16x16 blocks), 2 = 3x3 chunks (48x48 blocks).
CHECK_RADIUS_PER_PERIOD: 1 #radius in chunks coordinate for each period 
#Just leave it 1 if you would like to make the job easy for server to handle
USING_FEATURE_FACTION: false 
#turn this false if you are not using faction plugin.
USING_FEATURE_GRIEFPREVENTION: true
#turn this false if you are not using GriefPrevention plugin.
ONLY_RESTORE_AIR: true
# true = Mode 1, false = Mode 2
WORLDS_INFO: '{"maintained_worlds": [ {"world_name": "world","check_radius": "50","nature_factions":
  [ {"faction_name": "Wilderness"}, {"faction_name": "some_resource_area_faction"}
  ] },  {"world_name": "world_nether","check_radius": "20","nature_factions":
  [ {"faction_name": "Wilderness"}, {"faction_name": "some_resource_area_faction"}
  ] } ] }'
# JSON format. Mostly for faction server. "maintained_worlds" is a json array. You could apply this plugin to multiple worlds. Take the first world "world" as example. 
#The faction name "Wilderness" would be regarded as no-one's land, and could be restored after it reaches the untouched time you set. 
#"check_radius" is the radius in chunks, that defined how far this plugin is going to check. For example, set it to 100, meaning that the (100+100)x(100+100) = (3200+3200)x(3200+3200) = 6400x6400 blocks map.
#You could have multiple "faction_name" defined as no-one's land.
#Note that if you set "nature_factions":[] . This means you disable the faction check. All the chunks will be restored only depends on the untouched time unless you are using Griefprevention or other checking features.
```
----
### Permission nodes
restorenature.mr : node for /restorenature mr
restorenature.rnworld : node for /restorenature rnworld
restorenature.trymr : node fore /restorenature trymr

### Commands
| command |description| required permission |
| ------ | ------ |---|
| /restorenature mr | manually force the plugin to restore the chunk player standing in | op |
| /restorenature rnworld | manually make all the chunks exceeds the max untouched time and start to resotre the world(still according to the config) | op |
| /restorenature trymr | try to restore the land, if not success, show why. | op |

----
### Todos
 - Your suggestions are welcome at any time.

----
### License

MIT licenses https://opensource.org/licenses/MIT
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [item]: <https://www.csie.ntu.edu.tw/~b98902055/items/>

   [vault]: <https://www.spigotmc.org/resources/vault.41918/>
   [multiverse-core]: <https://www.spigotmc.org/resources/multiverse-core.390/>
   [faction]: <https://www.spigotmc.org/resources/factions.1900/>
   [griefprevention]: <https://www.spigotmc.org/resources/griefprevention.1884/>
   [worldedit]: <https://dev.bukkit.org/projects/worldedit/files/2460562>
   [placeholderapi]: <https://www.spigotmc.org/resources/placeholderapi.6245/>
   [titlemanager]: <https://www.spigotmc.org/resources/titlemanager.1049/>
   [spigot]: <https://spigotmc.org>
   [java]: <https://java.com/zh_TW/>
   [license]: <https://opensource.org/licenses/MIT>