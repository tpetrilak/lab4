package virtualdisk;

/*
 * VirtualDisk.java
 *
 * A virtual asynchronous disk.
 *
 */

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import common.Constants;
import common.Constants.DiskOperationType;
import dblockcache.DBuffer;
import dfs.DFS;

public class VirtualDisk implements IVirtualDisk, Runnable {

    private String _volName;
    private RandomAccessFile _file;
    private int _maxVolSize;

    private static VirtualDisk instance = null;
    private Queue<DBuffer> dBufferQ;
    private Queue<DiskOperationType> operationQ;

    /*
     * VirtualDisk Constructors
     */
    private VirtualDisk(String volName, boolean format)
	    throws FileNotFoundException, IOException {
	System.out.println("VirtualDisk constructor called");
	_volName = volName;
	_maxVolSize = Constants.BLOCK_SIZE * Constants.NUM_OF_BLOCKS;

	/*
	 * mode: rws => Open for reading and writing, as with "rw", and also
	 * require that every update to the file's content or metadata be
	 * written synchronously to the underlying storage device.
	 */
	_file = new RandomAccessFile(_volName, "rws");
	System.out.println("File created");
	/*
	 * Set the length of the file to be NUM_OF_BLOCKS with each block of
	 * size BLOCK_SIZE. setLength internally invokes ftruncate(2) syscall to
	 * set the length.
	 */
	_file.setLength(Constants.BLOCK_SIZE * Constants.NUM_OF_BLOCKS);
	if (format) {
	    formatStore();
	}
	dBufferQ = new LinkedList<DBuffer>();
	operationQ = new LinkedList<DiskOperationType>();
	/* Other methods as required */
    }

    private VirtualDisk(boolean format) throws FileNotFoundException,
	    IOException {
	this(Constants.vdiskName, format);
    }

    private VirtualDisk() throws FileNotFoundException, IOException {
	this(Constants.vdiskName, false);
    }

    public static VirtualDisk getInstance(boolean format) {
	if (format) {
	    try {
		instance = new VirtualDisk(format);
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return instance;
	}
	if (instance == null) {
	    try {
		instance = new VirtualDisk();
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	return instance;
    }

    /*
     * Start an asynchronous request to the underlying device/disk/volume. --
     * buf is an DBuffer object that needs to be read/write from/to the volume.
     * -- operation is either READ or WRITE
     */
    public synchronized void startRequest(DBuffer buf,
	    DiskOperationType operation) throws IllegalArgumentException,
	    IOException {
	dBufferQ.add(buf);
	operationQ.add(operation);

	// System.out.print("[");
	// for (int i = 0; i < buf.getBuffer().length - 1; i++) {
	// System.out.print(buf.getBuffer()[i] + ", ");
	// }
	// System.out.println(buf.getBuffer()[buf.getBuffer().length - 1] +
	// "]");
    }

    /*
     * Clear the contents of the disk by writing 0s to it
     */
    private void formatStore() {
	byte b[] = new byte[Constants.BLOCK_SIZE];
	setBuffer((byte) 0, b, Constants.BLOCK_SIZE);
	for (int i = 0; i < Constants.NUM_OF_BLOCKS; i++) {
	    try {
		int seekLen = i * Constants.BLOCK_SIZE;
		_file.seek(seekLen);
		_file.write(b, 0, Constants.BLOCK_SIZE);
	    } catch (Exception e) {
		System.out
			.println("Error in format: WRITE operation failed at the device block "
				+ i);
	    }
	}
    }

    /*
     * helper function: setBuffer
     */
    private static void setBuffer(byte value, byte b[], int bufSize) {
	for (int i = 0; i < bufSize; i++) {
	    b[i] = value;
	}
    }

    public DBuffer getBuffer() {
	return dBufferQ.poll();
    }

    public DiskOperationType getOperation() {
	return operationQ.poll();
    }

    /*
     * Reads the buffer associated with DBuffer from the underlying
     * device/disk/volume
     */
    private int readBlock(DBuffer buf) throws IOException {
	int seekLen = buf.getBlockID() * Constants.BLOCK_SIZE;
	/* Boundary check */
	if (_maxVolSize < seekLen + Constants.BLOCK_SIZE) {
	    return -1;
	}
	_file.seek(seekLen);
	return _file.read(buf.getBuffer(), 0, Constants.BLOCK_SIZE);
    }

    /*
     * Writes the buffer associated with DBuffer to the underlying
     * device/disk/volume
     */
    private void writeBlock(DBuffer buf) throws IOException {
	int seekLen = buf.getBlockID() * Constants.BLOCK_SIZE;
	System.out.println(buf.getBlockID());
	System.out.println("*** WRITING TO VIRTUAL DISK ***");
	int bufSize = buf.getBuffer().length;
	_file.seek(seekLen);
	_file.write(buf.getBuffer(), 0, bufSize);

    }

    public synchronized void run() {

	while (true)// each time through the VDF should take one buffer and one
	// operation to run
	{
	    DBuffer DB = getBuffer();

	    DiskOperationType DOT = getOperation();

	    if (DOT != null) {
		if (DOT.equals(DiskOperationType.READ)) {
		    try {
			readBlock(DB);
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		} else if (DOT.equals(DiskOperationType.WRITE)) {
		    try {
			writeBlock(DB);
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}

		DB.setValid();
	    }
	}

    }
}
