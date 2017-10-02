![ScreenShot](http://i.imgur.com/pK1bLqy.png)

Hi, everyone. I have developed a plugin that would periodically check the chunks in worlds of its untouched time. And more, you could turn on factions feature to check whether it is claimed by factions plugin.

With configurable parameters, this plugin will slowly regenerate your world! (Actually, copy and paste from the other world.)

==============================================
Commands and corresponding permissions
​
/restorenature mr
restorenature.mr:
description: manually force the plugin to restore the chunk player standing in.
default: op

/restorenature rnworld
restorenature.rnworld:
description: manually make all the chunks exceeds the max untouched time and start to resotre the world(still according to the config).
default: op

/restorenature trymr
restorenature.trymr: (Player only)
description: try to restore the land, if not success, show why.
default: op
==============================================​
Your potential performance and memory issue :

1.
Not enough power of CPU?
Performance could be tuned to fit the server's computing power. (Check Install Steps-2 Carefully.)

2.
You might be afraid of all the chunks tasks will freeze or use up your memory.
2-1 : Freeze issue? Don't worry. This plugin will cut the tasks into tiny pieces for your server to consume.
2-2 : Memory issue? Not a problem either. This plugin will have a maximum task queue. It will drop the upcoming tasks if the queue is temporarily filled.

3. The progress will be saved in restorenature/world_chunk_info/...
You are free to stop the server and proceed the progress at any time :)


==============================================​

Dependency:

0. This plugin compiled in Java 8.
1. Factions (Optional, remember to turn on/off in the config)
2. Multiverse (You need this to generate the copy world if needed.)

==============================================​
Install Steps:
1.
Create the world you want to use as backup.
The backup world is assigned by the world's suffix. _restorenature
For example :
Run commands /mv create world_name_restorenature ...

2.
Read this CAREFULLY:

Unzip and open, edit and config.yml file

version: 1.0.0d
#Nothing. Just ignore it.

MAX_SECONDS_UNTOUCHED: 864000
#How many seconds the chunks will be restored after no one break/place the block.

RESTORING_PERIOD_PER_CHUNK_IN_SECONDS: 1
#Every X seconds will restore a chunk.
#If the server is not lagging. Set to 1 if you want. Only accept integer.

BLOCK_EVENT_EFFECTING_RADIUS: 1
#1.0.0e New feature. The affecting area of the player event.
# 1 = 16x16 blocks, 2 = 3x3 chunks with the event in the middle.

CHECK_RADIUS_PER_PERIOD: 1
#radius in chunks coordinate for each period
#Just leave it 1 if you would like to make the job easy for server to handle

USING_FEATURE_FACTION: true
#turn this false will ignore the faction name checks


In the following json structure : 
WORLDS_INFO:
{
"maintained_worlds":[
{
"world_name":"my_cool_world",
"check_radius":"200",
"nature_factions":[
{
"faction_name":"Wilderness"
},
{
"faction_name":"some_resource_area_faction"
}
]
},
{
"world_name":"my_wrecked_nether",
"check_radius":"200",
"nature_factions":[]
}
]
}
#world_name : The name in
/mstore/factions_faction/xxx.json file. 
{
"name": "Wilderness",
...
}
NOT the .json file's name.

#Check_raduis : 1 chunk = 16x16, Count your chunk radius.
#If say you have a world 160x160. "check_radius" should be 10.
#If you change "check_radius", you must manually delete the .chunkinfo file in worlds_chunk_info/

#nature_factions : The factions that be regarded as wilderness.
# Note that if "nature_factions" : [] is set. This means that all the faction check-out is cancel. All the chunks will be restored only depends on the untouched time.


The above case study: 
In world : "my_cool_world", my plugin will check whether the chunk is own by "Wilderness", or "some_resource_area_faction". If it is true, it will further check it has been untouched (blocks inside being broken, placed, interacted) for a configurable MAX_UNTOUCHED_TIME.
Also, there is a configurable radius. Outside this range, the plugin would check the untouched time.

And if both conditions pass, the plugin will restore the chunk by :
Copying chunk at the same coordinates from world "my_cool_world_restorenature".
You have to manually create that world first. It is done by a duplication because you might want to restore instead by "map regeneration" but a customized map.

**And if in your server, worlds like nether, or the_end is not claimable. So there would be no "Wilderness" factions to checks. In this situations, just leave the "nature_factions": [], with a empty array.

**With this mechanism, your world would not be accidentally restore because the restore processes would not be done by default. Only if they pass the factions name checks. No false positive situations that restore users factions land.

3.
Run the Server.

4.(Optional)
If you are not confident enough to run it directly.
Just use a check_radius = 3 for the test world.
And set
MAX_SECONDS_UNTOUCHED: 5
CHECK_RADIUS_PER_PERIOD: 1
RESTORING_PERIOD_PER_CHUNK_IN_SECONDS: 1


==============================================​
Future plans :
current progress :

1. More land claim plugins checks : Griefprevetion API. And the exempt players land
2. More land claim plugins checks : WorldGuard API. And the exempt region
3. More land claim plugins checks : Towny API. And the exempt Towny area
4. Default and exempt world fucntion.


==============================================
The event that make the chunks untouched time to zero 
​
public void onFurnaceSmeltEvent(FurnaceSmeltEvent event) {
public void onBrewEvent(BrewEvent event) {
public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
public void onBlockIgniteEvent(BlockIgniteEvent event) {
public void onBlockGrowEvent(BlockGrowEvent event) {
public void onBlockFormEvent(BlockFormEvent event) {
public void onBlockFromToEvent(BlockFromToEvent event) {
public void onBlockBreakEvent(BlockBreakEvent event) {
public void onBlockPlaceEvent(BlockPlaceEvent event) {



插件名稱 : RestoreNature 重回自然

原文網址 : https://www.spigotmc.org/resources/restore-nature.21215/

下載網址 : https://www.spigotmc.org/resources/restore-nature.21215/

支援版本 : Spigot 1.9, 1.8.X . Java 1.8 編譯


====
大家好，我是LogoCat，最近廢土伺服器有一些需要，就寫了一個插件。本來想放到Spigot弄成付費插件，後來覺得不如開放給大家使用，所以又花了一點時間讓大家可以方便的設定。

插件功能 : 自動還原無人區域。讓你的建築界跟資源界融為一體，玩家玩起來不必在兩個世界中切換，影響遊戲體驗。

====
使用情境 :

1. 拿來當成自動重生終界,地獄,會被破壞的活動場地的工具
  因為你可以自由設定要什麼世界，什麼半徑，拿什麼地圖來還原，多久一次

2.大型server會有需要重置重生點附近的資源

不過我有參數可以設定半徑，例如50x50chunk(800x800格)半徑以內都算自動重生資源區
提早告訴玩家這邊時間太久會重生資源，自行拿捏要不要住這(例如常上玩家就覺得很方便)。

====
簡要技術細節 :

一般的寫法會讓整個伺服器凍結。
插件難度主要在如何把7000x7000的地圖慢慢判斷,切成小工作,不要影響運算效率，
然後自動控制記憶體用量。並且能隨時關閉伺服器，儲存重生進度。

====
設計動機 :

Factions 插件官方伺服器　Massivecraft也有這個功能，
網路上也有人在問有沒有相關的插件。

而且Massivecraft不開放插件下載（算他們技術細節吧）。

我伺服器最近也用到，就花一兩天硬尻一個。
剩下又花一兩天做到讓別人方便使用，例如用JSON檔那些。
====
使用說明 : 

這插件功能會先確認有沒有factions插件宣告領地，如果沒有安裝factions
在config中改成     USING_FEATURE_FACTION: false 即可

插件會慢慢的將指定世界，指定半徑內，16x16久沒被人破壞/建造過回復成本來的樣子。

例如10天沒人開採，就會變回本來的資源。
至於資源從哪來，我現在主要是寫: 從另一個地圖拷貝一樣地點的地圖過來。
所以你要用一個一樣種子碼的地圖,配multiverse 協助還原。

會自動儲存目前進度，所以你隨時關閉server，下次再開就會繼續跑了。
很多config檔設定細節，技術細節，就不在這邊講了。


====
詳細設定說明 :

config 檔內容與設定(請非常仔細的閱讀)

1.
創建一個你打算用來當成資源的世界。
因為我的插件就會去對應的XZ座標搬東西過來。

資源未開採的世界名稱請依據你想還原的世界，後面加上_restorenature
所以你大概會想用multiverse，弄一樣的種子碼，複製一個世界。
/mv create world_name_restorenature ... -s 種子碼 ...


2. 解壓縮連結內檔案並打開，編輯和config.yml文件
version: 1.0.0
#忽略它。

MAX_SECONDS_UNTOUCHED: 864000
#多久沒被破壞/建造的16x16區域容忍的時間上限。86400秒就十天，你想改成一個月就自己算一下一個月有幾秒。

CHECK_PERIOD_IN_SECONDS：3600
#多少秒執行一輪檢查程序。
#如果你有一個6000x6000的世界，建議設定成12小時之類的。

RESTORING_PERIOD_PER_CHUNK_IN_SECONDS: 2
#每隔X秒還原一個chunk。
#如果你伺服器CPU可以應付，可以改成每一秒做一個chunk。基本非常非常非常充裕。

接下來就是設定需要還原的世界，他們的JSON結構：
WORLDS_INFO：
{
“maintained_worlds”：[
{
“world_name”：“my_cool_world”，
“check_radius”：“200”，
“nature_factions”：[
{
“faction_name”：“原野”
}，
{
“faction_name”：“some_resource_area_faction”
}
]
}，
{
“world_name”：“my_wrecked_nether”，
“check_radius”：“200”，
“nature_factions”：[]
}
]
}
#world_name：multiverse插件 mv list出來的那個名稱

#Check_raduis：1塊16×16 =，算你一塊半徑。
#如果說你有一個世界160x160的。 “check_radius”應該是10。
#“check_radius”必須> = 5。因為我一次會把25個chunk丟進排隊系統讓server消化。太小會出錯，5chunk = 80x80的地。
#如果你改變“check_radius”，你必須手動刪除.chunkinfo文件worlds_chunk_info /
#nature_factions：這算是荒野的派別。
至於faction_name則是你如果有裝factions, 那麼他只會對[ ]內指定的做判定。
我這裡會去抓
/mstore/factions_faction/xxx.json文件中。
{
“faction_name”：“原野”，
...
}
不是.json文件的名稱，注意。所以你要連前面的色碼符號一起貼。
如果什麼都不填，那他就會跳過這個判斷，只針對untouched_time來決定要不要復原。

**如果在您的Server，像nether，或the_end這種公共世界區域，不准任何factions宣告的話。你就設定“nature_factions”：[]，這樣就會跳過判斷。
**檢查機制很安全，你的世界就不會意外被洗掉，因為還原過程都會先假定不能還原，然後要滿足所有條件才會繼續。

3.
執行伺服器，開始還原吧!

4.（如何簡單測試）
如果你不夠有把握直接運行伺服器，你可以調一下下面的參數

只需使用一個check_radius = 5為測試的世界。
並設置
MAX_SECONDS_UNTOUCHED：60
CHECK_PERIOD_IN_SECONDS：10
RESTORING_PERIOD_PER_CHUNK_IN_SECONDS：1
這樣每10秒就會不斷判定，超過一分鐘沒被動過的區域就會進入排程。

====
