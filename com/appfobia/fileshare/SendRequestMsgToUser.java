package fileshare;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	ObjectOutputStream oob=null;
	ObjectInputStream in=null;
	Thread threadRunReceiveFiles=null;
	SendRequestMsgToUser(UsernameProperty _compNameProperty,Vector<FilesProperty> list) {
		FilenameList=list;
		compNameProperty=_compNameProperty;
			try {
				sock= new Socket(compNameProperty.getCompName(),port);
				sendStatus=true;
			}catch(Exception e){
				//System.out.println("The server "+compNameProperty.getUserName()+" could not be connected at port no. "+ port);
				
				JOptionPane.showMessageDialog(null,"The user "+ compNameProperty.getUserName()+"is not ready to receive files.","Error sending request",JOptionPane.ERROR_MESSAGE);
				sendStatus=false;
			}
			if(sendStatus==true) {
			try {
				
				oob = new ObjectOutputStream(sock.getOutputStream());
				
				in= new ObjectInputStream(sock.getInputStream());
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("wuigefiawrhvhvt");
				e1.printStackTrace();
			}
			}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Sender Run() method started.");
		int s=FilenameList.size();
		System.out.println("Sender: SendStatus ="+sendStatus);
		
				try {
					
					if(sendStatus==true) {
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
					
					//in = new ObjectInputStream(sock.getInputStream());
					//if(in.readBoolean())
					//{
						
					//}
					//threadRunReceiveFiles=new Thread(new ReceiveFiles(in,oob));
					
					
				} catch(java.net.SocketException e){
					System.out.println("Sender:The receiver could not be connected at port no. "+ port);
					e.printStackTrace();
					System.out.println("Therefore closing socket.");
					sendStatus=false;
					}
				
				catch(IOException e){  
			System.out.println("Sender: problem found in IO streams.");
			e.printStackTrace();}
		


				
				
				if(sendStatus==true)
					{
					JOptionPane.showMessageDialog(null,"Request to send the files sent.","Information",JOptionPane.PLAIN_MESSAGE);
					//closeClient() ;
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
	
}
