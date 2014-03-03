package fileshare;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReceiveFiles implements Runnable {

	ObjectInputStream in=null;
	ObjectOutputStream out=null;
	
	Thread t;
	ReceiveFiles(ObjectInputStream in2, ObjectOutputStream oob) {
		// TODO Auto-generated constructor stub
		in=	in2; out=oob;
		
	t=new Thread();	//thread to be suspended
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
