package test;

import virtualdisk.VirtualDisk;
import common.Constants;
import common.DFileID;
import dblockcache.DBufferCache;
import dfs.DFS;

public class DiskThread implements Runnable {

    VirtualDisk myVD = VirtualDisk.getInstance();

    @Override
    public void run() {

	DFS dfs = DFS.getInstance();
	
	DFileID dfid = dfs.createDFile();
	
	int bufferSize = 1024;
	byte[] buffer = new byte[bufferSize];
	
	for (int i = 0; i < bufferSize; i++) {
	    buffer[i] = 0;
	}
	
	dfs.write(dfid, buffer, 0, bufferSize);
    }
    
    public static void main(String[] args) {
	
	DFS dfs = DFS.createDFS(Constants.vdiskName, false);
	
	(new DiskThread()).run();
	
    }

}
