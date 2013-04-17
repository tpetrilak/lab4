package test;

import virtualdisk.MyVirtualDisk;
import common.Constants;
import dblockcache.MyDBuffer;
import dblockcache.MyDBufferCache;

public class DiskThread implements Runnable {

    MyVirtualDisk myVD = new MyVirtualDisk();
    MyDBufferCache myBufferCache = new MyDBufferCache(Constants.NUM_OF_CACHE_BLOCKS * Constants.BLOCK_SIZE);

    @Override
    public void run() {

    }

}
