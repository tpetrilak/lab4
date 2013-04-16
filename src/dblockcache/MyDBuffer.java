package dblockcache;

import java.io.FileNotFoundException;
import java.io.IOException;

import common.Constants.DiskOperationType;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;

public class MyDBuffer extends DBuffer {

    private int blockId;
    private boolean isValid;
    private boolean isClean;
    private boolean isBusy;
    private byte[] buffer;

    public MyDBuffer(int id) {
	blockId = id;
    }

    public MyDBuffer() {
	blockId = 0;
    }

    @Override
    public void startFetch() {
	isValid = false;
	MyVirtualDisk.getInstance().startRequest(this, DiskOperationType.READ);
    }

    @Override
    public void startPush() {
	MyVirtualDisk.getInstance().startRequest(this, DiskOperationType.WRITE);

    }

    @Override
    public boolean checkValid() {
	return isValid;
    }

    @Override
    public boolean waitValid() {
	while (!isValid) { 
	    try {
		wait();
	    } catch(InterruptedException ie) {
		ie.printStackTrace() ;
	    }
	}
	return true;
    }

    @Override
    public boolean checkClean() {
	return isClean;
    }

    @Override
    public boolean waitClean() {
	while (!isClean) {
	    try {
		wait();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	return true;
    }

    @Override
    public boolean isBusy() {
	return isBusy;
    }

    @Override
    public int read(byte[] buffer, int startOffset, int count) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int write(byte[] buffer, int startOffset, int count) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public void ioComplete() {
	// TODO Auto-generated method stub

    }

    @Override
    public int getBlockID() {
	return blockId;
    }

    @Override
    public byte[] getBuffer() {
	return buffer;
    }

}
