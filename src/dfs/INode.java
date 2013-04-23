package dfs;

import common.Constants;
import common.DFileID;

public class INode {

    private DFileID fileId;
    private int blockId;
    private int fileSize;
    private int[] blockMap;

    public INode(DFileID dFileId, int id, int dFileSize, int[] map) {
	fileId = dFileId;
	blockId = id;
	fileSize = dFileSize;
	blockMap = map;
    }

    public INode(DFileID dFileId) {
	fileId = dFileId;
	blockId = 0;
	fileSize = 0;
	blockMap = new int[Constants.NUM_OF_BLOCKS];
    }

    public INode(DFileID dFileId, int[] map) {
	fileId = dFileId;
	blockMap = map;
    }

    public DFileID getId() {
	return fileId;
    }

    public void setId(int id) {
	fileId = new DFileID(id);
    }

    public int[] getBlockMap() {
	return blockMap;
    }

}
