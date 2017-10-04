package io.github.kuohsuanlo.restorenature;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

public class SimpleChunk {
	public Material [][][] block_type;
	public byte [][][] block_data;
	public BlockState[][][] block_state;
	
	public SimpleChunk(Chunk chunk){
		block_type = new Material[16][256][16];
		block_data = new byte[16][256][16];
		block_state = new BlockState[16][256][16];
		for(int x=0;x<16;x++){
			for(int y=0;y<256;y++){
				for(int z=0;z<16;z++){
					block_type[x][y][z] = chunk.getBlock(x, y, z).getType();
					block_data[x][y][z] = chunk.getBlock(x, y, z).getData();
					block_state[x][y][z] = chunk.getBlock(x, y, z).getState();
				}
			}
		}		
	}
	public void pasteChunk(Chunk chunk){
		for(int x=0;x<16;x++){
			for(int y=0;y<256;y++){
				for(int z=0;z<16;z++){
					chunk.getBlock(x, y, z).setType(block_type[x][y][z]);
					chunk.getBlock(x, y, z).setData(block_data[x][y][z]);
				}
			}
		}	
	}
}
