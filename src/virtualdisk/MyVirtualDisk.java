package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;
import dfs.MyDFS;

public class MyVirtualDisk extends VirtualDisk {

    private static MyVirtualDisk instance = null;
    private Queue<DBuffer> dBufferQ;
    private Queue<DiskOperationType> operationQ;

    public MyVirtualDisk() throws IOException, FileNotFoundException {
	super();
	dBufferQ = new LinkedList<DBuffer>();
	operationQ = new LinkedList<DiskOperationType>();
    }

    public static MyVirtualDisk getInstance() throws FileNotFoundException, IOException {
	if (instance == null) {
	    return new MyVirtualDisk();
	}
	return instance;
    }

    @Override
    public void startRequest(DBuffer buf, DiskOperationType operation)
	    throws IllegalArgumentException, IOException {
	dBufferQ.add(buf);
	operationQ.add(operation);

    }

    public DBuffer getBuffer() {
	return dBufferQ.poll();
    }

    public DiskOperationType getOperation() {
	return operationQ.poll();
    }

}
