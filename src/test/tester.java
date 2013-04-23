package test;

import common.DFileID;

import dfs.DFS;

public class tester {
	public static void main(String args[]) {
		// DFS myDFS = DFS.getInstance();
		// byte[] myBlock = new byte[1024];
		// boolean check = true;
		// for(int i = 0; i < myBlock.length; i++)
		// {
		// if(check)
		// {
		// myBlock[i] = 0;
		// check = false;
		// }
		// else{
		// myBlock[i] = 1;
		// check = true;
		// }
		//
		// }
		// DFileID DFID = new DFileID(700);
		// myDFS.write(dFID, buffer, startOffset, count)
		// myDFS.read(dFID, buffer, startOffset, count)
		
		DiskThread dt = new DiskThread();
		
		Thread t = new Thread(dt);

		DFS dfs = DFS.getInstance();
		System.out.println("initialized");
		DFileID dfid1 = dfs.createDFile();
		byte[] buffer = { 5, 10, 23, 40, 45 };
		
		dfs.write(dfid1, buffer, 0, 5);
		System.out.println(dfid1.getDFileID());
		
		DFileID dfid2 = dfs.createDFile();
		dfs.write(dfid2, buffer, 2, 3);
		System.out.println(dfid2.getDFileID());
		
		DFileID dfid3 = dfs.createDFile();
		dfs.write(dfid3, buffer, 1, 2);
		System.out.println(dfid3.getDFileID());
		
		for (DFileID d : dfs.listAllDFiles()) {

			int size = dfs.sizeDFile(d);

			System.out.println(d.getDFileID() + "Size:" + size);

			dfs.read(d, buffer, 0, size);

			for (int x = 0; x < size; x++) {

				System.out.print(buffer[x]);

			}

			System.out.println();

		}

		System.out.println("complete");

//		dfs.stop();
	}

}
