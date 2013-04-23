package dfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.Constants;
import common.DFileID;
import dblockcache.DBuffer;
import dblockcache.DBufferCache;

public class DFS {

	private boolean _format;
	private String _volName;
	private int numINodeBlocks;
	// private ArrayList<INode> iNodes;
	// private int[] DFileIds;//index represents ID, 1 represents used, 0
	// represents free
//	private Map<DFileID, INode> myINodes; // Integer will be a DFID, all INODES
	private INode[]  myINodes;
	// will be mapped to a specific DFID
	// whether or not they are in use
	private int[] availableBlocks;
	private static DFS instance = null;
	private DBufferCache myBufferCache;

	/*
	 * @volName: Explicitly overwrite volume name
	 * 
	 * @format: If format is true, the system should earse the underlying disk
	 * contents and reinialize the volume.
	 */

	private DFS(String volName, boolean format) {
		System.out.println("DFS constructor called");
		_volName = volName;
		_format = format;
		numINodeBlocks = 26214;
		myBufferCache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS, format);
		System.out.println("After bufferCache");
		// DFileIds = new int[numINodes];
		// for(int i = 0; i< numINodes; i++)
		// {
		// DFileIds[i] = 0;
		// }
		// iNodes = new ArrayList<INode>();
		availableBlocks = new int[Constants.NUM_OF_BLOCKS
				- (numINodeBlocks + 1)];// this should be the number of blocks
		// int he data region
//		myINodes = new HashMap<DFileID, INode>();
		myINodes = new INode[Constants.MAX_DFILES];
		System.out.println("just before init");
	
		init();// reads all inodes form disk into iNodes map
	}

	public DFS(boolean format) {
		this(Constants.vdiskName, format);
	}

	private DFS() {
		this(Constants.vdiskName, false);
	}

	public static DFS getInstance() {
		if (instance == null) {
			return new DFS();
		}
		return instance;
	}

	public static DFS createDFS(String name, boolean format) {
		DFS thing = new DFS(name, format);
		return thing;
	}

	/*
	 * Initialize all the necessary structures with sizes as specified in the
	 * common/Constants.java
	 */
	public void init() {
		try {
			File disk = new File(_volName);
			FileInputStream in = new FileInputStream(disk);
			byte[] blockRead = new byte[1024];
			in.read(blockRead);// block 0 is empty/ has metadata

			// reading in iNode blocks
			for (int i = 0; i < (Constants.MAX_DFILES*Constants.INODE_SIZE)/Constants.BLOCK_SIZE; i++) {
				in.read(blockRead);// read in first block with 32 iNodes

				for (int j = 0; j < 32; j++) {
					byte[] node = Arrays.copyOfRange(blockRead, j * 32,
							(j + 1) * 32);
					int fileID = byteArrayToInt(node, 0);
					int[] blockMap = new int[7];
					int fileSize = 0;
					DFileID DFID;
					if (fileID > 0)// if this inode holds a reference to a DFID
					{
						for (int k = 0; k < 7; k++) {
							int blockLocation = byteArrayToInt(node,
									4 + (k * 4));
							if (blockLocation >= 0) {
								availableBlocks[blockLocation] = 1;
								fileSize+=Constants.BLOCK_SIZE;
							}
							blockMap[k] = blockLocation;
						}
						DFID = new DFileID(fileID);
					}
					else{
						DFID = new DFileID(-1);
					}
					
					
					INode currentNode = new INode(DFID, blockMap);
					currentNode.setSize(fileSize);
					myINodes[i*32 + j] = currentNode;
					// iNodes.add(currentNode);
					// DFileIds[fileID] = 1;
//					myINodes.put(new DFileID(i*1024+j*32), currentNode);
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int byteArrayToInt(byte[] b, int startingIndex) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[startingIndex + i] & 0x000000FF) << shift;
		}
		return value;
	}

	/*
	 * creates a new DFile and returns the DFileID, which is useful to uniquely
	 * identify the DFile
	 */
	public DFileID createDFile() {
		INode available = getFreeINode();
		if (available != null) {
			return available.getId();
		}
		return null;
	}

	/* destroys the file specified by the DFileID */
	public void destroyDFile(DFileID dFID) {// i have no idea what im doing
		INode toDestroy = myINodes[dFID.getDFileID()];
		int[] bMap = toDestroy.getBlockMap();
		for (int i = 0; i < bMap.length; i++) {
			availableBlocks[bMap[i]] = 0;
		}
		toDestroy.setId(-1);// tommy can change this later
	}

	/*
	 * reads the file dfile named by DFileID into the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int read(DFileID dFID, byte[] buffer, int startOffset, int count) {

		INode fileNode = myINodes[dFID.getDFileID()];
		int[] blockMap = fileNode.getBlockMap();
		for (int i = 0; i < blockMap.length; i++) {

			if (blockMap[i] > 0 && count >= 0) {
				DBuffer db = myBufferCache.getBlock(blockMap[i]);

				if (count > Constants.BLOCK_SIZE) {
					db.read(buffer, startOffset + 1024 * i,
							Constants.BLOCK_SIZE);
					count -= Constants.BLOCK_SIZE;
				} else {
					db.read(buffer, startOffset + 1024 * i, count);
					count -= count;
				}

			}

		}

		int blockNum = startOffset / Constants.BLOCK_SIZE;

		return 0;
	}

	/*
	 * writes to the file specified by DFileID from the buffer starting from the
	 * buffer offset startOffset; at most count bytes are transferred
	 */
	public int write(DFileID dFID, byte[] buffer, int startOffset, int count) {
		INode fileNode = myINodes[dFID.getDFileID()];
		System.out.println("INode present in write "+ fileNode);
		int[] blockMap = fileNode.getBlockMap();
		for (int i = 0; i < blockMap.length; i++) {

			if (count >= 0) {
				DBuffer db = myBufferCache.getBlock(blockMap[i]);

				if (count > Constants.BLOCK_SIZE) {
					db.write(buffer, startOffset + 1024 * i,
							Constants.BLOCK_SIZE);
					count -= Constants.BLOCK_SIZE;
				} else {
					db.write(buffer, startOffset + 1024 * i, count);
					count -= count;
				}
				blockMap[i] = 1;

			}

		}
		return 0;
	}

	/* returns the size in bytes of the file indicated by DFileID. */
	public int sizeDFile(DFileID dFID) {
		INode in = myINodes[dFID.getDFileID()];
		return in.getFileSize();
	}

	/*
	 * List all the existing DFileIDs in the volume
	 */
	public List<DFileID> listAllDFiles() {
		List<DFileID> existingDFIDs = new ArrayList<DFileID>();
		for(int i = 0; i< myINodes.length; i++)
		{
			if(myINodes[i].getId().getDFileID()>=0)//gets the INodes with this DFID and checks that it exists(DFID is not -1)
			{
				existingDFIDs.add(myINodes[i].getId());
			}
		}
		return existingDFIDs;
	}

	/* Write back all dirty blocks to the volume, and wait for completion. */
	public void sync() {
		myBufferCache.sync();
		// TODO also have to sync back all of the inodes
	}

	public INode getFreeINode() {
		for (int i = 0; i< myINodes.length; i++) {//needs to start from first DFID of something in data space
			INode node = myINodes[i];
//			System.out.println("going through inodes " + i);
			if (node.getId().getDFileID() >= 0) {
				node.setId(i);
				System.out.println("node return from getFreeINode's DFID is"+ node.getId());
				return node;
			}
		}
		System.out.println("no free inodes");
		return null;
	}
}