package virtualdisk;

import java.io.FileNotFoundException;
import java.io.IOException;

import common.Constants.DiskOperationType;

import dblockcache.DBuffer;

public class MyVirtualDisk extends VirtualDisk{

	public MyVirtualDisk() throws FileNotFoundException, IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startRequest(DBuffer buf, DiskOperationType operation)
			throws IllegalArgumentException, IOException {
		// TODO Auto-generated method stub
		
	}

}
