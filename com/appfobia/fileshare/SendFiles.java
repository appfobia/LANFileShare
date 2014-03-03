package fileshare;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class SendFiles extends SendRequestMsgToUser {

	
	//ObjectInputStream in=null;
	//ObjectOutputStream out=null;
	int noOfFiles=-1;
	long totalSize=0;
	Thread t=null;
	JProgressBar progressBar=null;
	SendFiles() {
		System.out.println("SendFiles: Constructor.");
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		JPanel progressPanel=null;
		System.out.println("Theard Send file started.");
		try {
			
			while(!Thread.interrupted()) {
				
				if(readBoolean()) {
					//AddToLogFile("Receiver: boolean received");
					if(progressBar==null)
						progressPanel = createComponents();
					fileSendProtocol();
					break;
				}
				
				else
					continue;
				
			}
			
		}catch(java.net.SocketException e){
			e.printStackTrace();
			
			System.out.println("Send File Thread:The receiver could not be connected at port no. "+ port);
			}
		
		catch(IOException e){  
			System.out.println("Send File Thread: problem found in IO streams.");
	e.printStackTrace();
	
	}
		if(progressPanel!=null) {
			progressPanel.setVisible(false);
		}
		//InteruptThisThread();	
		System.out.println("Theard "+this.toString() +"ending..");
	}

	private void fileSendProtocol() {
		for(int i=1;i<=FilenameList.size();i++) {
			try {
				InputStream fin= new FileInputStream(new File(FilenameList.get(i-1).getFile(),FilenameList.get(i-1).getFileName()));
				pushBytesToReceiver(fin,ostrm);
			}catch(FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("SendFilesThread: File not found.");
			}
			catch(IOException e) {
				e.printStackTrace();
				System.out.println("SendFilesThread: IO Exception found while sending bytes to receiver.");
			}
			
		}
		
	}
	
	void pushBytesToReceiver(InputStream ins,OutputStream outs) throws IOException {
		byte[] buf = new byte[1024];
        int len = 0;
        while ((len = ins.read(buf)) != -1) {
            outs.write(buf, 0, len);
            outs.flush();
            t=new threadRun(len);							// updating the progress bar in a separate thread.
            t.start();
        }
	}
	class threadRun extends Thread {
		int l=0;
		threadRun(int _l){
			l=_l;
		}
		public void run() {
			updateProgressBar(l);
		}
	}
	public  boolean readBoolean() throws IOException {
		int i=in.available();
		//AddToLogFile("oob.available()="+i);
			if(i!=0)
		{
				boolean t=in.readBoolean();
				//AddToLogFile("oob.readBoolean()="+t);
				return t;
		}
		else
			return false;

		}
	
	public void InteruptThisThread() {
		 closeClient() ;
		 Thread.currentThread().interrupt();
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
		for(int i=1;i<=FilenameList.size();i++) 
			size+=FilenameList.get(i-1).getAbsFileSize();
		System.out.println("Send File: Total file sizes ="+size);
		totalSize=size;
		return size;
	}
	void updateProgressBar(int t) {
		progressBar.setValue((int) (t/totalSize*6000));
	}
}
