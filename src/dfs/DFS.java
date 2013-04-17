package dfs;

import java.util.List;

import common.Constants;
import common.DFileID;
import dblockcache.DBufferCache;

public class DFS {
		
	private boolean _format;
	private String _volName;
	
	private static DFS instance = null;
	private DBufferCache myBufferCache;

	/* 
	 * @volName: Explicitly overwrite volume name
	 * @format: If format is true, the system should earse the underlying disk contents and reinialize the volume.
	 */

	private DFS(String volName, boolean format) {
		_volName = volName;
		_format = format;
		myBufferCache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS);
		//TODO DFS should read in all the INodes upon construction and then store them so it does not have to read every time.
	}

	private DFS(boolean format) {
		this(Constants.vdiskName,format);
	}

	private DFS() {
		this(Constants.vdiskName,false);
	}
	
	public static DFS getInstance()
	{
		if(instance == null)
		{
			return new DFS();
		}
		return instance;
	}

	/* Initialize all the necessary structures with sizes as specified in the common/Constants.java */
	public  void init() {
	}

	/* creates a new DFile and returns the DFileID, which is useful to uniquely identify the DFile*/
	public  DFileID createDFile() {
		return null;
	}
	
	/* destroys the file specified by the DFileID */
	public void destroyDFile(DFileID dFID) {
	}

	/*
	 * reads the file dfile named by DFileID into the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public  int read(DFileID dFID, byte[] buffer, int startOffset, int count) {
		return 0;
	}
	
	/*
	 * writes to the file specified by DFileID from the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public  int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		return 0;
	}
	
	/* returns the size in bytes of the file indicated by DFileID. */
	public int sizeDFile(DFileID dFID) {
		return 0;
	}

	/* 
	 * List all the existing DFileIDs in the volume
	 */
	public List<DFileID> listAllDFiles() {
		return null;
	}

	/* Write back all dirty blocks to the volume, and wait for completion. */
	public void sync() {
	}
}
