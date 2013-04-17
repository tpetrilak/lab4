package dblockcache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import virtualdisk.VirtualDisk;
import common.Constants;

public class DBufferCache {
	
	private int _cacheSize;
	private VirtualDisk myDisk;
	private Queue<DBuffer> myDBuffers;
	
	
	/*
	 * Constructor: allocates a cacheSize number of cache blocks, each
	 * containing BLOCK-size bytes data, in memory
	 */
	public DBufferCache(int cacheSize) {
		_cacheSize = cacheSize * Constants.BLOCK_SIZE;
		try {
			myDisk = new VirtualDisk();
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myDBuffers = new LinkedList<DBuffer>();//create a list of MyDBuffers of size cacheSize
		for(int i = 0; i< cacheSize; i++)
		{
			myDBuffers.add(new DBuffer());
		}
	}
	
	/*
	 * Get buffer for block specified by blockID. The buffer is "held" until the
	 * caller releases it. A "held" buffer cannot be evicted: its block ID
	 * cannot change.
	 */
	public DBuffer getBlock(int blockID) {//returns a DBuffer from cache if it exists or returns the least recently used one
		DBuffer DB = blockInCache(blockID);
		if(DB==null)
		{
			DB =  myDBuffers.poll();
		}
		DB.
		DB.holdBuffer();
		return DB;
		
	}
	public DBuffer blockInCache(int blockID)//searches for DBuffer that represents block is "blockID" in cache. Null if it is not
	{
		LinkedList<DBuffer> DBuffers = (LinkedList) myDBuffers;
		for(int i = 0; i< myDBuffers.size(); i++)
		{
			DBuffer DB = DBuffers.get(i);
			if(DB.getBlockID() == blockID)
			{
				return DBuffers.get(i);
			}
		}
		return null;
	}

	/* Release the buffer so that others waiting on it can use it */
	public void releaseBlock(DBuffer buf) {
		
	}
	
	/*
	 * sync() writes back all dirty blocks to the volume and wait for completion.
	 * The sync() method should maintain clean block copies in DBufferCache.
	 */
	public void sync() {
	}
}
