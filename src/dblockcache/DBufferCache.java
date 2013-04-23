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
	public DBufferCache(int cacheSize, boolean format) {
		System.out.println("DBufferCache constructor called");
		_cacheSize = cacheSize * Constants.BLOCK_SIZE;

		myDisk = VirtualDisk.getInstance(format);
				
		myDBuffers = new LinkedList<DBuffer>();// create a list of MyDBuffers of
												// size cacheSize
		for (int i = 0; i < cacheSize; i++) {
			myDBuffers.add(new DBuffer(myDisk));
		}
		
	}

	/*
	 * Get buffer for block specified by blockID. The buffer is "held" until the
	 * caller releases it. A "held" buffer cannot be evicted: its block ID
	 * cannot change.
	 */
	public DBuffer getBlock(int blockID) {// returns a DBuffer from cache if it
											// exists or returns the least
											// recently used one
		DBuffer DB = blockInCache(blockID);
		if (DB == null) {
			DB = myDBuffers.poll();
		}
		DB.setBlockID(blockID);
		myDBuffers.add(DB);
		DB.holdBuffer();
		return DB;

	}

	public DBuffer blockInCache(int blockID)// searches for DBuffer that
											// represents block is "blockID" in
											// cache. Null if it is not
	{
		LinkedList<DBuffer> DBuffers = (LinkedList) myDBuffers;
		for (int i = 0; i < myDBuffers.size(); i++) {
			DBuffer DB = DBuffers.get(i);
			if (DB.getBlockID() == blockID) {
				return DBuffers.remove(i);
			}
		}
		return null;
	}

	/* Release the buffer so that others waiting on it can use it */
	public void releaseBlock(DBuffer buf) {
		buf.releaseBuffer();
	}

	/*
	 * sync() writes back all dirty blocks to the volume and wait for
	 * completion. The sync() method should maintain clean block copies in
	 * DBufferCache.
	 */
	public void sync() {
		LinkedList<DBuffer> DBuffers = (LinkedList) myDBuffers;
		for (int i = 0; i < DBuffers.size(); i++) {
			DBuffer db = DBuffers.get(i);
			if (!db.checkClean()) {
				db.startPush();
			}
		}
	}
	
}
