package test;

import virtualdisk.VirtualDisk;
import common.Constants;
import dblockcache.DBufferCache;

public class DiskThread implements Runnable {

    VirtualDisk myVD = VirtualDisk.getInstance();
    DBufferCache myBufferCache = new DBufferCache(Constants.NUM_OF_CACHE_BLOCKS
	    * Constants.BLOCK_SIZE);

    @Override
    public void run() {

    }

}
