package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class MyVirtualDisk extends VirtualDisk {

    private Queue<DBuffer> dBufferQ;
    private Queue<DiskOperationType> operationQ;

    public MyVirtualDisk(String volName, boolean format) throws IOException, FileNotFoundException {
	super(volName, format);
	dBufferQ = new LinkedList<DBuffer>();
	operationQ = new LinkedList<DiskOperationType>();
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
