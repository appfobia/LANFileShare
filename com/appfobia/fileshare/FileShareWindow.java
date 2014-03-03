package fileshare;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class FileShareWindow extends JFrame implements ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new FileShareWindow();
            }
        });
        
        

	}
	
	public ReceiveRequestMsg RecvReqMsgObj=null;
	public SendRequestMsgToUser SendReqMsgObj=null;
	JFileChooser filechooser = null;
	DefaultListModel<String> UserlistModel,FilelistModel;
	JScrollPane UsernameListSP,FileListSP;
	JLabel lHostIp,lUserName,lSelecthostUserName,lSfile,lFilesSelected;
	JTextField tfHostIp,tfUserName;
	JButton bAddIp,bSelect,bConnect,bSendFile,bSelectfiles,bDeleteUsernames,bAbout,bHelp,bEULA,bRemoveFile,bCancel;
	JList<String> UserList;
	JList<String> FileList;
	Vector<UsernameProperty> UserListVector;
	Vector<FilesProperty> FileListVector;
	//JScrollPane spStatusMessage;
	String MetaDataFileName=".LFS";
	String MetaDataFileNamePath=null;
	JCheckBox startReceive ;
	
	String HostIp,UserName;
	Thread threadRun=null;
	Thread threadRunRecvReqMsg=null;
	
	FileShareWindow() {
		super("LAN File Share ");
		UserListVector=new Vector<UsernameProperty>(0);
		FileListVector=new Vector<FilesProperty>(0);
		//UserlistModel=new DefaultListModel<String>();
		//FilelistModel=new DefaultListModel<String>();
		//System.out.println("UserlistModel size ="+UserlistModel.getSize()+"capacity="+UserlistModel.capacity());
		
		System.out.println("Starting the setupUI() ...");
		setupUI();
		
		
	}
	
	

	void setupUI() {
		setLayout(null);
		setSize(880,700);
		UserlistModel=new DefaultListModel();
		FilelistModel=new DefaultListModel();
		JPanel panel00=new JPanel();
		JPanel panel01=new JPanel();
		JPanel panel02=new JPanel();
		JPanel toolbar=new JPanel();
		
		panel00.setLayout(null);
		panel01.setLayout(null);
		panel02.setLayout(null);
		panel02.setBounds(5,40,850,600);
		panel02.setBackground(Color.GRAY);
		//panel02.setBorder(BorderFactory.createLineBorder(Color.gray,10));
		
		toolbar.setLayout(null);
		toolbar.setBackground(Color.GRAY);
		toolbar.setBounds(5,5,850,28);
		
		bAbout=new JButton("About");
		bAbout.setBounds(425,5,70,25);
		bAbout.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   //System.out.println("Inside Server connect button.");
				   ; 
				   }
		});
		
		bHelp=new JButton("Help");
		bHelp.setBounds(350,5,70,25);
		bHelp.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   //System.out.println("Inside Server connect button.");
				   ; 
				   }
		});
		
		toolbar.add(bHelp);
		toolbar.add(bAbout);
		add(toolbar);
		
		lHostIp=new JLabel("Add Computer name/IP :") ;
		lHostIp.setBounds(10,45,200,25);
		
		
		tfHostIp=new JTextField(50);
		tfHostIp.setBounds(240,45,200,25);
		tfHostIp.setBackground(Color.GREEN);
		
		
		lUserName=new JLabel("Name                 :") ;
		lUserName.setBounds(10,10,200,25);
		
		
		tfUserName=new JTextField(50);
		tfUserName.setBounds(240,10,200,25);
		tfUserName.setBackground(Color.GREEN);
		
		
		
		bAddIp=new JButton("Add");
		bAddIp.setBounds(240,80,100, 25);
		bAddIp.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   System.out.println("Add button pressed.");
				   UserName=getUserName();
				   HostIp=getIP();
				   if(HostIp.length()!=0 && UserName.length()!=0)
				   {
					   if (UserlistModel.contains((String)UserName)){
						   tfHostIp.setBackground(Color.RED);
						   tfUserName.setBackground(Color.RED);
						   JOptionPane.showMessageDialog(null,"User already present.","Inane error",JOptionPane.ERROR_MESSAGE);
						   tfHostIp.setBackground(Color.GREEN);
					   		tfUserName.setBackground(Color.GREEN);
					   }
					   
					   else
					   {
						   threadRun=null;
						   threadRun=new Thread(ActionPropertybAdd());
						   System.out.println("Starting the ActionPropertybAdd() ...");
						   threadRun.start();
					   }
				   }
				   
				   
				   
				   else 
					   {
					   if(HostIp.length()==0)
						   tfHostIp.setBackground(Color.RED);
					   if(UserName.length()==0)
						   tfUserName.setBackground(Color.RED);
					   JOptionPane.showMessageDialog(null,"You need to fill both the username and computername fields.","Inane error",JOptionPane.ERROR_MESSAGE);
					   tfHostIp.setBackground(Color.GREEN);
				   		tfUserName.setBackground(Color.GREEN);
					   }
				   		
				   }
		});
		bAddIp.setToolTipText("Add this user.");
		
		
		panel00.add(lHostIp);
		panel00.add(tfHostIp);
		panel00.add(lUserName);
		panel00.add(tfUserName);
		panel00.add(bAddIp);
		
		panel00.setForeground(null);
		panel00.setBounds(180,20,480,110);
		
		panel02.add(panel00);
		
		lSelecthostUserName=new JLabel("Select usernames to send files :") ;
		lSelecthostUserName.setBounds(10,30,200,25);
		
		bSelectfiles=new JButton("Select file(s) to be sent");
		bSelectfiles.setBounds(430,30,200,25);
		bSelectfiles.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   //System.out.println("Inside Server connect button.");
				   FileListVector.clear();
				   FileListVector.trimToSize();
				   FilelistModel.clear();
				   FilelistModel.trimToSize();
				   filechooser=new JFileChooser();
				   filechooser.setMultiSelectionEnabled(true); 
				   int option = filechooser.showSaveDialog(getContentPane());
			        if (option == JFileChooser.APPROVE_OPTION) {
			          File[] sf = filechooser.getSelectedFiles();
			          for (int i = 0; i<sf.length; i++) {
			            FileListVector.addElement(new FilesProperty(sf[i].getName(),filesize(sf[i].length()),sf[i].length()));
			            FilelistModel.addElement(sf[i].getName()+" // "+filesize(sf[i].length()));
			          }
			          
			        }
			       
				   }
		});
		
		lFilesSelected=new JLabel("Selected files is/are :") ;
		lFilesSelected.setBounds(430,70,200,25);
		
		panel01.add(lSelecthostUserName);
		panel01.add(bSelectfiles);
		panel01.add(lFilesSelected);
		
		
		bConnect=new JButton("Connect");
		bConnect.setBounds(550,320,100, 25);
		bConnect.setEnabled(false);
		bConnect.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   threadRun=null;
				   System.out.println("Connect button pressed.");
				   if(SendReqMsgObj==null){
					   System.out.println("Theard "+this.toString() +"starting..");
					   SendReqMsgObj=new SendRequestMsgToUser(UserListVector.get(UserList.getSelectedIndex()),FileListVector);
				   }
				   threadRun=new Thread(SendReqMsgObj);
				   threadRun.start();
				   
				   }
		});
		bConnect.setToolTipText("Connect to the selected user computer(s).");
		
		bCancel=new JButton("Cancel");
		bCancel.setBounds(670,150,100, 25);
		//bCancel.setEnabled(false);
		bCancel.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   ;
				   
				   }
		});
		bCancel.setToolTipText("Cancel file sending.");
		
		bDeleteUsernames=new JButton("Delete");
		bDeleteUsernames.setBounds(10,300,100, 25);
		bDeleteUsernames.setEnabled(false);
		bDeleteUsernames.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   //System.out.println("Inside Server connect button.");
				   bDeleteAction(); 
				   }
		});
		bDeleteUsernames.setToolTipText("Delete selected user's record.");
		
		bSendFile=new JButton("Send");
		bSendFile.setBounds(670,100,100, 25);
		bSendFile.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   //System.out.println("Inside Server connect button.");
				   ; 
				   }
		});
		bSendFile.setToolTipText("Press this button to send file(s).");
		
		bRemoveFile=new JButton("Remove");
		bRemoveFile.setBounds(430,320,100, 25);
		bRemoveFile.setEnabled(false);
		bRemoveFile.addActionListener(new ActionListener(){
			   @Override
			public void actionPerformed(ActionEvent ae){
				   //System.out.println("Inside Server connect button.");
				   bRemoveAction(); 
				   }
		});
		bRemoveFile.setToolTipText("Remove this file.");
		
		panel01.add(bConnect);
		panel01.add(bCancel);
		panel01.add(bDeleteUsernames);
		panel01.add(bSendFile);
		panel01.add(bRemoveFile);
		
		UserList=getUserList(); // should be called after adding all the components
		UserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		UserList.setSelectedIndex(0);
		UserList.setBackground(Color.lightGray);
		//UserList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		UserList.addListSelectionListener(new UserListListerner("User"));
		UserList.addKeyListener(new UserListListerner("User"));
		
		UsernameListSP=new JScrollPane(UserList);
		UsernameListSP.setBounds(10,80,220,200);
		
		FileList=getFileList(); // should be called after adding all the components
		FileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		FileList.addListSelectionListener(new UserListListerner("File"));
		FileList.addKeyListener(new UserListListerner("File"));
		FileListSP=new JScrollPane(FileList);
		FileListSP.setBounds(430,100,220,200);
		
		panel01.add(UsernameListSP);
		panel01.add(FileListSP);
		
		panel01.setForeground(null);
		panel01.setBounds(35,150,790,400);
		panel02.add(panel01);
		
		startReceive = new JCheckBox("Check this to enable receiving files from other computers.",false);
		startReceive.addItemListener(this);
		startReceive.setBounds(45,560,400,25);
		add(startReceive);
		
		add(panel02);
	
		threadRun=null;
		threadRun=new Thread(PopulateUserNameListThread());//should always be called after declaring the JList
		threadRun.start();
		setLocationRelativeTo(null);
		this.setVisible(true);
		
		//this.setDefaultCloseOperation(closeJFrame());
		addWindowListener(   
			      new java.awt.event.WindowAdapter()   
			      {  
			        public void windowClosing( java.awt.event.WindowEvent e )   
			        {  
			          closeJFrame();  
			          dispose() ;  
			          System.exit( 0 );  
			        }  
			      }  
			    ); 
		
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
	
	private int closeJFrame() {
		showM("Close JFrame");
		// closing sockets
		if(RecvReqMsgObj!=null)
			try {
				if(RecvReqMsgObj!=null)
				{
					
					try {
		    			threadRunRecvReqMsg.interrupt();
		    			showM("Calling RecvReqMsgObj.closeServer()..");
		    			RecvReqMsgObj.closeServer();
		    			showM("Server closed");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						showM("Error in closing server at 1997.");
					}
				}
				
				if(SendReqMsgObj!=null)
				{
					SendReqMsgObj.closeClient();
					showM("Closeclientr");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				showM("Socket already closed.");
			}
		
		
		
		return (EXIT_ON_CLOSE);
	}
 
	public JList<String> getUserList() {
		JList<String> tempList=null;
		tempList=new JList<String>(UserlistModel);
		return tempList;
	}
	
	public JList<String> getFileList() {
		JList<String> tempList=null;
		tempList=new JList<String>(FilelistModel);
		return tempList;
		
	}
	
	private Runnable PopulateUserNameListThread()  {
		// TODO Auto-generated method stub
		FileReader fin=null;
		String username=null;
		String compName=null;
		File f= new File(System.getProperty("user.home"),".LFS");
		if(f.exists())
			{
			System.out.println("file exists");
			try {
				fin=new FileReader(f);
				BufferedReader br=new BufferedReader(fin);
					while(((username=br.readLine())!=null) && (compName=br.readLine())!=null) {
						//System.out.println(username+"  "+compName);
						UserListVector.addElement(new UsernameProperty(username,compName));
						showM("vectorlist object added from file; capacity:"+UserListVector.capacity());
						UserlistModel.addElement(username);
						showM(username+" :added added from file; UserlistModel capacity:"+UserlistModel.capacity()+"size ="+UserlistModel.getSize());
					
					}
				System.out.println("File reading compelete");
					//read two lines
					//save the user name and comp name in vector
					//jump two lines and see if it the end of file or not
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		
			}
		else
			System.out.println("No preset user names found! Add corresponding usernames and computernames.");
		
		System.out.println("Populating Username thread complete .");
		return null;
	}
	
	private Runnable PopulateFileListThread()  {
		// TODO Auto-generated method stub
		int c=FilelistModel.size();
		for(int i=1;i<=c;i++) {
			FilelistModel.addElement(FileListVector.get(i).getFileName()+" // "+FileListVector.get(i).getFileSize());
		}
		return null;
	}
	
	public Runnable ActionPropertybAdd() {
		
		File f=new File(System.getProperty("user.home"),".LFS") ;
		PrintWriter pw=null;
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
			pw.println(UserName);
			pw.flush();
			pw.println(HostIp);
			
			UserlistModel.addElement(UserName);
			System.out.println(UserName+" :added ; UserlistModel capacity:"+UserlistModel.capacity()+" ;size ="+UserlistModel.getSize());
			UserListVector.addElement(new UsernameProperty(UserName,HostIp));
			System.out.println("vectorlist object added ; capacity:"+UserListVector.capacity());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(pw!=null)
			pw.close();
		}

		   System.out.println("ActionPropertybAdd() thread  complete.");
	return null;
}
	
	public String getIP() {
		return tfHostIp.getText();
	}
	
	public String getUserName() {
		return tfUserName.getText();
	}
	
	public void showMessage(String str) {
		
	}
	public void bDeleteAction() {
		int i=-1;
		i=UserList.getSelectedIndex();
		System.out.println("Userlist capacity = "+UserlistModel.capacity());
		System.out.println("Userlist Removing element at "+i);
		UserlistModel.remove(i);
		UserlistModel.trimToSize();
		System.out.println("Userlist capacity = "+UserlistModel.capacity());
		System.out.println("UserlistVector capacity = "+UserListVector.capacity());
		System.out.println("UserlistVector Removing element at "+i);
		UserListVector.remove(i);
		UserListVector.trimToSize();
		System.out.println("UserlistVector capacity = "+UserlistModel.capacity());
		threadRun=null;
		threadRun=new Thread(deleteElementFromFile());
		threadRun.start();
	}
	
	public void bRemoveAction() {
		int i=-1;
		i=FileList.getSelectedIndex();
		System.out.println("FileList capacity = "+UserlistModel.capacity());
		System.out.println("FileList Removing element at "+i);
		FilelistModel.remove(i);
		FilelistModel.trimToSize();
		System.out.println("FileList capacity = "+FilelistModel.capacity());
		System.out.println("FileListVector capacity = "+FileListVector.capacity());
		System.out.println("FileListVector Removing element at "+i);
		FileListVector.remove(i);
		FileListVector.trimToSize();
		System.out.println("UserlistVector capacity = "+FilelistModel.capacity());
	}
	public class UserListListerner implements KeyListener , ListSelectionListener {

		String mode=null;
		JList<String> templ ;
		Vector<?> tempV;
		DefaultListModel<?> templistModel;
		UserListListerner(String _mode) {
			mode=_mode;
			showM("constructor");
			if(mode.equals("File"))
			{
				showM("mode=File");
				templ=FileList;
				tempV=FileListVector;
				templistModel=FilelistModel;
			}
			if(mode.equals("User"))
			{
				showM("mode=User");
				templ=UserList;
				tempV=UserListVector;
				templistModel=UserlistModel;
			}
		}
		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			showM("Del pressed");
			int i=-1;
			i=templ.getSelectedIndex();
			System.out.println("Userlist capacity = "+templistModel.capacity());
			System.out.println("Userlist Removing element at "+i);
			templistModel.remove(i);
			templistModel.trimToSize();
			System.out.println("Userlist capacity = "+templistModel.capacity());
			System.out.println("UserlistVector capacity = "+tempV.capacity());
			System.out.println("UserlistVector Removing element at "+i);
			tempV.remove(i);
			tempV.trimToSize();
			System.out.println("UserlistVector capacity = "+templistModel.capacity());
			if(mode.equals("User")) {
				threadRun=null;
				threadRun=new Thread(deleteElementFromFile());
				threadRun.start();
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			showM("Del releaseed");
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			showM("Del Typed");
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub
			//showM("ListAction Listener");
			showM("mode="+mode);
			if(!templ.isSelectionEmpty())
			{
				if(mode.equals("User")) {
					bConnect.setEnabled(true);
					bDeleteUsernames.setEnabled(true);
				}
				else
				{
					bRemoveFile.setEnabled(true);
					bSendFile.setEnabled(true);
				}
			}
			else
				{
				if(mode.equals("User")) {

					bConnect.setEnabled(false);
					bDeleteUsernames.setEnabled(false);
				}
				else
				{
					bRemoveFile.setEnabled(false);
					bSendFile.setEnabled(false);
				}
				
				}
			
		}


	}
	
	public Runnable deleteElementFromFile(){
		
		File f=new File(System.getProperty("user.home"),".LFS") ;
		PrintWriter pw=null;
		if(f.exists())
			{
			if(f.delete()) {
				showM("Successfully deleted");
				try {
					f.createNewFile();
					showM("New file created");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					showM("Error in deleting the file");
				}
				
						}
			
			try {
				
				pw=new PrintWriter(  new FileWriter(  f ,true));
				int s=UserListVector.size();
				showM("size of UserListVector"+s);
				UsernameProperty temp=null;
				for(int j=1;j<=s;j++) {
					temp=UserListVector.get(j-1);
					pw.println(temp.getUserName());
					pw.flush();
					pw.println(temp.getCompName());
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				if(pw!=null)
				pw.close();
			}
			
			
			}
		
		showM("Deleting Username from file thread complete .");
		return null;
	}
void showM(String str) {
	System.out.println(str);
}

public void itemStateChanged(ItemEvent e) {
    Object source = e.getItemSelectable();
showM("Item state :"+startReceive.isSelected());
    if (source == startReceive) {
    	if(startReceive.isSelected()) {
    		showM("Calling RecvReqMsgObj..");
    		RecvReqMsgObj=null;
    		 RecvReqMsgObj=new ReceiveRequestMsg();
    		threadRunRecvReqMsg=null;
    		 threadRunRecvReqMsg=new Thread(RecvReqMsgObj);
    		 threadRunRecvReqMsg.start();
    	}
    	else
    	{
    		if(threadRunRecvReqMsg!=null) {
    		try {
    			threadRunRecvReqMsg.interrupt();
    			showM("Calling RecvReqMsgObj.closeServer()..");
    			RecvReqMsgObj.closeServer();
    			
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				showM("Error in closing server at 1997.");
			}
    		}
    	}
    } 
}



}