package fileshare;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ReceiveRequestMsg extends Thread {

	Vector<FilesProperty> FilenameList=new Vector<FilesProperty>(0);;
	String filenames[];
	public Socket sock=null;
	final int port=1997;
	ServerSocket serversock=null;
	JFrame FileReceiveFrame=null;
	JScrollPane ReceivedFileTableSP=null;
	JButton bAccept,bCancelRev;
	DefaultListModel<String> ReceivedFileModel=null;
	//JList<String> ReceivedFileJList=null;
	public ObjectInputStream oob=null;
	public ObjectOutputStream out=null;
	public InputStream instrm=null;
	public OutputStream outstrm=null;
	DefaultTableModel myData=null;
	JTable table=null;
	String currentUserName=null;
	InetAddress add=null;
	JLabel frameHeading,lTotalFileSize;
	long totalFileSize=0;
	Thread runReceiveFile=null;
	ReceiveFiles ReceiveFilesObj=null;
	int FilePropertyListSize=-1;
	FileShareWindow JFrameObj=null;
	File f=null;
	PrintWriter pw=null;
	ReceiveRequestMsg(FileShareWindow _JFrameObj) {
		JFrameObj=_JFrameObj;

		
		
		f=new File(System.getProperty("user.home"),".lfsRecvLog") ;
		if(!f.exists())
			{
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("Error in creating the file");
			}
			}
		try {
			pw=new PrintWriter(  new FileWriter(  f ,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AddToLogFile("Log file initialized");
		 
	}
	ReceiveRequestMsg() {
		
		
	}
	
	@Override
	public void run()  {
		// TODO Auto-generated method stub
		//System.out.println("Thread "+this.toString() +"starting..");
		AddToLogFile("Thread "+this.toString() +"starting..");
		FileReceiveFrame=getFileReceiveFrame();
			try {
				AddToLogFile("Opening server socket");
				
				serversock = new ServerSocket(port);
				sock= serversock.accept();
				
				AddToLogFile("trying to read Objects sent from client");
				outstrm=sock.getOutputStream();
				out=new ObjectOutputStream(outstrm);
				instrm=sock.getInputStream();
				oob = new ObjectInputStream(instrm);
				
				add=sock.getInetAddress();
				AddToLogFile(""+sock.getLocalPort()+"    "+sock.getPort()+"\n");
				AddToLogFile("out stream"+out.toString());
				AddToLogFile("input stream"+instrm.toString());
				AddToLogFile("Object input stream"+oob.toString());
				AddToLogFile(""+sock.getLocalPort()+"    "+sock.getPort()+"\n");
				while(!Thread.interrupted()) {
					//try {
					
					if(readBoolean()) {
						//AddToLogFile("Receiver: boolean received");
						TransfrProtocol();
						//out.flush();
					}
					
					else
						continue;
					
				}
				AddToLogFile("while loop ended. ");
						
				InteruptThisThread();	
					
						AddToLogFile("Thread "+this.toString() +"stopping.. xxxxxxxx");
			}catch(java.net.SocketException e){
				
				InteruptThisThread();
				//AddToLogFile("Receiver:The sender could not be connected at port no. "+ port);
				}
			
			catch(IOException e){  
				//AddToLogFile("Receiver: probel found in IO streams.");
		e.printStackTrace();}
	
	catch(ClassNotFoundException e){
		//AddToLogFile("Receiver: Problem in reading FileProperty object from Sender Object stream.");
		e.printStackTrace();}
	}

 void InteruptThisThread() {
	 try {
			closeServer();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			//AddToLogFile("Receiver: Error in closing ObjectOutputStream & ObjectInputStream after receiving all the file names.");
			e1.printStackTrace();
		}
		Thread.currentThread().interrupt();
 }

public Vector<FilesProperty> getFilePropertyObj() {
	 return FilenameList;
 }
 
public void TransfrProtocol() throws IOException , ClassNotFoundException{
	
	int j;
	FilesProperty tempObj=null;
	AddToLogFile("Trying to read FilePropertyListSize from Sender..");
	FilePropertyListSize=ReadFromReceiverInt();
	//AddToLogFile("Int received from sender:"+FilePropertyListSize);
	AddToLogFile("Read FilePropertyListSize from Sender.");
		for(j=1;j<=FilePropertyListSize;j++) {
			
			tempObj=ReadFromReceiverObject();
			if(!FilenameList.contains(tempObj))
			{
				FilenameList.add(tempObj);// delete if not required
				myData.addRow(new Object[]{tempObj.getFileName(), tempObj.getFileSize()});
				//AddToLogFile(tempObj.getFileSize()+"  "+tempObj.getAbsFileSize());
				totalFileSize=totalFileSize+tempObj.getAbsFileSize();
				
				String myString = "<html><p>"+"The user "+add.getHostName()+" with address "+add.getHostAddress()+" wants to send the following"+"\n"+" files to your computer."+"</p></html>";  
				frameHeading.setText(myString);
				lTotalFileSize.setText(filesize(totalFileSize));
				FileReceiveFrame.setVisible(true);
			}
			
		}
		AddToLogFile("TrnsferProtocol finished.");
		}

 void closeServer() throws IOException {
	 AddToLogFile("Close server called.");
	 //push a msg to client to say that there will will be no further communication.
		try {
			if(oob!=null)
			oob.close();
			if(out!=null)
				out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			AddToLogFile("Error in closing ObjectOutputStream & ObjectInputStream after receiving all the file names.");
			e1.printStackTrace();
		}
		if(!serversock.isClosed())
			serversock.close();
		
			if(pw!=null)
			pw.close();
	}
 
 public JFrame getFileReceiveFrame() {
	 	final JFrame tempFrame =new JFrame("File list");
	 	tempFrame.setLayout(null);
	 	tempFrame.setSize(500,300);
		
	 	frameHeading =new JLabel();
	 	frameHeading.setBounds(5,5,500,80);
	 	tempFrame.add(frameHeading);
	 	
	 	myData= new DefaultTableModel();   
	 	myData.addColumn("File name"); 
	 	myData.addColumn("File size"); 
	 	
	 	table = new JTable(myData);  
	 	table.setSize(500,300);
	 	Dimension dim = table.getPreferredSize(); 
	 	int width = dim.width/8;
	 	table.getColumnModel().getColumn(0).setPreferredWidth(width*14); 
	 	table.getColumnModel().getColumn(1).setPreferredWidth(width);
	 	ReceivedFileTableSP=new JScrollPane(table);
	 	ReceivedFileTableSP.setBounds(10,75,400,100);
	 	tempFrame.add(ReceivedFileTableSP);
	 	
	 	JLabel total =new JLabel("Total -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
	 	total.setBounds(12,175,300,25);
	 	tempFrame.add(total);
	 	
	 	lTotalFileSize =new JLabel();
	 	lTotalFileSize.setBounds(80+width*14,175,100,25);
	 	tempFrame.add(lTotalFileSize);
	 	
	 	bAccept=new JButton("Accept");
		bAccept.setBounds(40,200,100, 25);
		//bCancel.setEnabled(false);
		bAccept.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   
				   if(instrm!=null )
				{
					   runReceiveFile=null;
					   System.out.println("Accept button pressed.");
					   if(ReceiveFilesObj==null){
						   
						   AddToLogFile(""+sock.getLocalPort()+"    "+sock.getPort()+"\n");
							AddToLogFile("out stream"+out.toString());
							AddToLogFile("input stream"+instrm.toString());
							AddToLogFile("Object input stream"+oob.toString());
						   ReceiveFilesObj=new ReceiveFiles(this);
						   ReceiveFilesObj.start();
					   }
					   //runReceiveFile=new Thread(ReceiveFilesObj);
					   //runReceiveFile.start();
					   
					   
					  // out.writeBoolean(true);
					   //out.close();
				}
				   
				   
				   myData=null;
				   totalFileSize=0;
				   tempFrame.dispose();
				   //Thread.currentThread().interrupt();
			   }
		});
		bAccept.setToolTipText("Accept file receive.");
		
		
		bCancelRev=new JButton("Cancel");
		bCancelRev.setBounds(280,200,100, 25);
		//bCancel.setEnabled(false);
		bCancelRev.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   tempFrame.dispose();
				   FilenameList.clear();// delete if not required
					myData.setRowCount(0);
					totalFileSize=0;
				   // delete all the lists saved from the previous instance.
				   }
		});
		bCancelRev.setToolTipText("Cancel file receive.");
		
		tempFrame.add(bAccept);
		tempFrame.add(bCancelRev);
		tempFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		
		
	 return tempFrame;
 }
 
 private String filesize(Long _L) {
		String s=null;
		//showM(_L+"");   
		int exp = (int) (Math.log(_L) / Math.log(1000));
		if(exp==1) 
			s=String.format("%.2f KB",(float)_L/1000);
		else if(exp==2)
			s=String.format("%.2f MB",(float)_L/1000000);
		else if(exp==3)
			s=String.format("%.2f GB",(float)_L/1000000000);
		else
			s=_L+ " Bytes";
		
		return s;
	}
 public  void PushMsgToSender(String sendOnlyInfoMsg) throws IOException {
		
		if(sendOnlyInfoMsg!=null)
			{
			//in.writeObject(q);
			sendOnlyInfoMsg=null;
			}
}



public  FilesProperty ReadFromReceiverObject() throws ClassNotFoundException, IOException  {
	
	return (FilesProperty) oob.readObject();
}

public  int ReadFromReceiverInt() throws IOException {

return oob.readInt();

}
public  boolean readBoolean() throws IOException {
int i=oob.available();
//AddToLogFile("oob.available()="+i);
	if(i!=0)
{
		boolean t=oob.readBoolean();
		//AddToLogFile("oob.readBoolean()="+t);
		return t;
}
else
	return false;

}


 public void AddToLogFile(String add) {
	pw.println(add);
	pw.flush();
} 

}