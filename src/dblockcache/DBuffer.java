package dblockcache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import common.Constants;
import common.Constants.DiskOperationType;

import virtualdisk.VirtualDisk;

public class DBuffer {
	private int blockId;
	private boolean isValid;
	private boolean isClean;
	private boolean isBusy;
	// private ByteBuffer buffer;
	private byte[] myBuffer;
	private VirtualDisk myDisk;

	public DBuffer(int id, VirtualDisk disk) {
		blockId = id;
		myDisk = disk;
		// buffer = ByteBuffer.allocate(Constants.BLOCK_SIZE);
		myBuffer = new byte[Constants.BLOCK_SIZE];
		isClean = true;
		isBusy = false;
		isValid = true;
	}
	
	public DBuffer(VirtualDisk disk) {
	    	blockId = -1;
		myDisk = disk;
		// buffer = ByteBuffer.allocate(Constants.BLOCK_SIZE);
		myBuffer = new byte[Constants.BLOCK_SIZE];
		isClean = true;
		isBusy = false;
		isValid = true;
	}

	public DBuffer() {
		blockId = 0;
	}

	/* Start an asynchronous fetch of associated block from the volume */
	public void startFetch() {
		isValid = false;
		try {
			myDisk.startRequest(this, DiskOperationType.READ);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		waitValid();
	}

	/* Start an asynchronous write of buffer contents to block on volume */
	public void startPush() {
		isValid = false;
		try {
			myDisk.startRequest(this, DiskOperationType.WRITE);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//waitValid();
	}

	/* Check whether the buffer has valid data */
	public boolean checkValid() {
		return isValid;
	}

	/* Wait until the buffer has valid data, i.e., wait for fetch to complete */
	public synchronized boolean waitValid() {
		while (!isValid) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		return true;
	}

	public void setValid() {
		isValid = true;
//		notifyAll();
	}

	/*
	 * Check whether the buffer is dirty, i.e., has modified data written back
	 * to disk?
	 */
	public boolean checkClean() {
		return isClean;
	}

	/*
	 * Wait until the buffer is clean, i.e., wait until a push operation
	 * completes
	 */
	public synchronized boolean waitClean() {
		while (!isClean) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/*
	 * Check if buffer is evictable: not evictable if I/O in progress, or buffer
	 * is held
	 */
	public boolean isBusy() {
		return isBusy;
	}

	public void holdBuffer() {
		isBusy = true;
	}

	public void releaseBuffer() {
		isBusy = false;
	}

	/*
	 * reads into the buffer[] array from the contents of the DBuffer. Check
	 * first that the DBuffer has a valid copy of the data! startOffset and
	 * count are for the buffer array, not the DBuffer. Upon an error, it should
	 * return -1, otherwise return number of bytes read.
	 */
	public int read(byte[] buffer, int startOffset, int count) {
		isBusy = true;
		if (!isValid) {
			try {
				startFetch();
			} catch (Exception e) {
				return -1;
			}
		}

		myBuffer = buffer;

		return count;
	}

	/*
	 * writes into the DBuffer from the contents of buffer[] array. startOffset
	 * and count are for the buffer array, not the DBuffer. Mark buffer dirty!
	 * Upon an error, it should return -1, otherwise return number of bytes
	 * written.
	 */
	public int write(byte[] buffer, int startOffset, int count) {
		isBusy = true;
		myBuffer = buffer;
		try {
			startPush();
		} catch (Exception e) {
		    e.printStackTrace();
		    return -1;
		}
		isClean = false;
		return count;
	}

	/*
	 * An upcall from VirtualDisk layer to inform the completion of an IO
	 * operation
	 */
	public void ioComplete() {
		isClean = true;
		isBusy = false;
		isValid = true;
	}

	/*
	 * An upcall from VirtualDisk layer to fetch the blockID associated with a
	 * startRequest operation
	 */
	public int getBlockID() {
		return blockId;
	}

	public void setBlockID(int id) {
		blockId = id;
	}

	/*
	 * An upcall from VirtualDisk layer to fetch the buffer associated with
	 * DBuffer object
	 */
	public byte[] getBuffer() {
		return myBuffer;
	}
}
