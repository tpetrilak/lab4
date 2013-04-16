package dblockcache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import virtualdisk.MyVirtualDisk;
import virtualdisk.VirtualDisk;

public class MyDBufferCache extends DBufferCache{

	private VirtualDisk myDisk;
	private ArrayList<DBuffer> myDBuffers;
	
	public MyDBufferCache(int cacheSize) {
		super(cacheSize);
		myDBuffers = new ArrayList<DBuffer>();//create a list of MyDBuffers of size cacheSize
		for(int i = 0; i< cacheSize; i++)
		{
			myDBuffers.add(new MyDBuffer());
		}
		try {
			myDisk = new MyVirtualDisk();
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public DBuffer getBlock(int blockID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseBlock(DBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sync() {
		// TODO Auto-generated method stub
		
	}

}
