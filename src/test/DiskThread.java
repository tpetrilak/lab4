package test;

import virtualdisk.MyVirtualDisk;
import common.Constants;

public class DiskThread implements Runnable {

    MyVirtualDisk myVD= new MyVirtualDisk(Constants.vdiskName, true);
    
    @Override
    public void run() {
	
    }
    

}
