package fileshare;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import fileshare.SendFiles.threadRun;

public class ReceiveFiles extends Thread {

	//InputStream in=null;
	//OutputStream out=null;
	//ObjectOutputStream objOutstrm=null;
	//Socket sock=null;
	//int noOfFiles=-1;
	ReceiveRequestMsg obj=null;
	long totalSize=0;
	Thread t=null;
	JProgressBar progressBar=null;
	ReceiveFiles() {
		// TODO Auto-generated constructor stub

	//t=new Thread();	//thread to be suspended
		
	}
	
	public ReceiveFiles(ReceiveRequestMsg o) {
		// TODO Auto-generated constructor stub
		o=obj;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(""+obj.sock.getLocalPort()+"    "+obj.sock.getPort()+"\n");
				System.out.println("out stream"+obj.outstrm.toString());
		System.out.println("input stream"+obj.instrm.toString());
		System.out.println("Object output stream"+obj.out.toString());
		
		receiveFilesProtocol();
	}
	

public  void PushBooleanToSender(boolean _b) throws IOException {
		
	obj.out.writeBoolean(_b);
	System.out.println("Wrote Boolean=."+t);
	
}

private void receiveFilesProtocol() {
	File tempFilePath=null;
	File f=null;
	try {
		PushBooleanToSender(true);
		for(int i=1;i<=obj.FilenameList.size();i++) {
			//tempFileName=FilenameList.get(i-1).getFileName();
			//tempFileName=tempFileName.replace(' ',File.separatorChar);
			if(obj.JFrameObj.pathWhereFilesToBeSave==null) {
				f=new File(obj.JFrameObj.pathWhereFilesToBeSave,"ReceivedFiles");
				f.mkdir();
				tempFilePath=new File(obj.JFrameObj.pathWhereFilesToBeSave+File.separator+"ReceivedFiles");
			}
			else
				tempFilePath=obj.JFrameObj.pathWhereFilesToBeSave;
			
			f=new File(tempFilePath,obj.FilenameList.get(i-1).getFileName());
			f.createNewFile();
			//File f1=createTempFile(String prefix, String suffix,JFrameObj.pathWhereFilesToBeSave) 
			OutputStream fout= new FileOutputStream(f);
			pushBytesToReceiver(obj.instrm,obj.outstrm,(int)obj.FilenameList.get(i-1).getAbsFileSize());
		}
		
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

private void pushBytesToReceiver(InputStream ins, OutputStream outs,int fsize) throws IOException{
	// TODO Auto-generated method stub
	byte[] buf = new byte[1024];
    int len = 0;
    int tempsize=0;
    while ((len = ins.read(buf)) != -1) {
    	if( (fsize-tempsize) >= len )
    		outs.write(buf, 0, len);
    	else if((fsize-tempsize)<len && (fsize-tempsize)>0) 
    		outs.write(buf, 0,(fsize-tempsize));
        tempsize+=len;
       // t=new threadRun(len);							// updating the progress bar in a separate thread.
       // t.start();
    }
}

public JPanel createComponents() {
    JPanel top = new JPanel();
    top.setBackground(Color.WHITE);
    top.setLayout(new BorderLayout(20, 20));
 
    String lblText =
        "<html><font color=green size=+2" +">Sending file(s)</font><br/> </html>";
    JLabel lbl = new JLabel(lblText);
    top.add(lbl, BorderLayout.NORTH);
    progressBar = new JProgressBar(0,6000);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    top.add(progressBar, BorderLayout.SOUTH);

    return top;
}

public long  getTotalFileSize() {
	long size=0;
	for(int i=1;i<=obj.FilenameList.size();i++) 
		size+=obj.FilenameList.get(i-1).getAbsFileSize();
	System.out.println("Send File: Total file sizes ="+size);
	totalSize=size;
	return size;
}
void updateProgressBar(int t) {
	progressBar.setValue((int) (t/totalSize*6000));
}

}
