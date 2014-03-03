package fileshare;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;



public class SendRequestMsgToUser implements Runnable {

	Vector<FilesProperty> FilenameList=null;
	String filenames[];
	Socket sock=null;
	final int port=1997;
	String localhost=null;
	boolean sendStatus=false;
	UsernameProperty compNameProperty=null;
	OutputStream ostrm=null;
	ObjectOutputStream oob=null;
	ObjectInputStream in=null;
	SendFiles sendFileObj=null;
	Thread threadRunSendFiles=null;
	boolean clientsocket =false;
	
	SendRequestMsgToUser(UsernameProperty _compNameProperty,Vector<FilesProperty> list) {
		System.out.println("Sender: Type 1 constructor called.");
		FilenameList=list;
		compNameProperty=_compNameProperty;
		startServerSockIO();
			
	}
	
	SendRequestMsgToUser() {
		//System.out.println("Sender: Type 2 constructor called.");
	}
	public void startServerSockIO() {
		try {
			sock= new Socket(compNameProperty.getCompName(),port);
			sendStatus=true;
			clientsocket=true;
			ostrm=sock.getOutputStream();
			oob = new ObjectOutputStream(ostrm);
			in= new ObjectInputStream(sock.getInputStream());
			
		}catch(Exception e){
			//System.out.println("The server "+compNameProperty.getUserName()+" could not be connected at port no. "+ port);
			
			JOptionPane.showMessageDialog(null,"The user "+ compNameProperty.getUserName()+"is not ready to receive files.","Error sending request",JOptionPane.ERROR_MESSAGE);
			sendStatus=false;
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Sender Run() method started.");
		System.out.println("Sender: SendStatus ="+sendStatus);
				try {
					
					if(sendStatus==true) {
						TransfrProtocol();
					}
					//in = new ObjectInputStream(sock.getInputStream());
					if(ostrm!=null && in!=null) {
						if(threadRunSendFiles==null )
						{
							sendFileObj=new SendFiles();
							threadRunSendFiles=new Thread(sendFileObj);
						}
						//threadRunSendFiles.start();
					}
						
						
				} catch(java.net.SocketException e){
					System.out.println("Sender:The receiver could not be connected at port no. "+ port);
					if(!sock.isClosed())
						try {
							sock.close();
							System.out.println("Sender socket closed.");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					clientsocket=false;
					//e.printStackTrace();
					System.out.println("Therefore closing socket.");
					sendStatus=false;
					}
				
				catch(IOException e){  
			System.out.println("Sender: problem found in IO streams.");
			if(!sock.isClosed())
				try {
					sock.close();
					System.out.println("Sender socket closed.");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			clientsocket=false;
			//e.printStackTrace();
			System.out.println("Therefore closing socket.");
			sendStatus=false;
			//e.printStackTrace();
			} 

				
				
				if(sendStatus==true)
					{
					JOptionPane.showMessageDialog(null,"Request to send the files sent.","Information",JOptionPane.PLAIN_MESSAGE);
					//closeClient() ;
					}
				else
				{
					JOptionPane.showMessageDialog(null,"The user "+ compNameProperty.getUserName()+"is not ready to receive files.","Error sending request",JOptionPane.ERROR_MESSAGE);
				}
			
			System.out.println("Theard "+this.toString() +"stopping..");
			sendStatus=true;
	}
	
	void closeClient()  {
		System.out.println("Closeclient() called.");
		try {
			if(oob!=null)
			oob.close();
			if(in!=null)
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error in closing ObjectOutputStream & ObjectInputStream after receiving all the file names.");
			e1.printStackTrace();
		}
		if(!sock.isClosed())
			try {
				sock.close();
				System.out.println("Sender socket closed.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		clientsocket=false;
	}
	
	public  void PushToReceiverObject(Object q) throws IOException {
		
			if(q!=null)
				{
				oob.writeObject(q);
				System.out.println("Wrote Obect.");
				q=null;
				}
	}
	
	public  void PushToReceiverInt(int q) throws IOException {
		
		oob.writeInt(q);
		System.out.println("Wrote Int=."+q);
	}
	
	public  boolean ReadMsgFromRecvBoolean() throws IOException {

		return true;

		}
	
	public  void PushToRecevBoolean(boolean t) throws IOException {

		oob.writeBoolean(t);
		System.out.println("Wrote Boolean=."+t);
		}
	
	
	public void TransfrProtocol() throws IOException {
		int s=FilenameList.size();
		PushToRecevBoolean(true);
		System.out.println("Sender: boolean sent");
		PushToReceiverInt(s) ;
		//oob.flush();
		for(int j=1;j<=s;j++) {
			System.out.println(	"Sending file name "+FilenameList.get(j-1).getFileName()+"...");
			PushToReceiverObject(FilenameList.get(j-1));
		oob.flush();
		}
	}
	void InterruptSendFile() {
		if(threadRunSendFiles!=null) {
    		try {
    			threadRunSendFiles.interrupt();
    			System.out.println("Interrupting SendFiles()...");
    			closeClient();
    			
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("Error in closing client socket.");
			}
	}
	
}
}
