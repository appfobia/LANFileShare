package fileshare;

import java.io.Serializable;

public class FilesProperty implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2504706057430471776L;
	public String name=null;
	public String size=null;
	public long absSize;


	FilesProperty(String _name, String _size,long _absSize) {
		// TODO Auto-generated constructor stub
		 name=_name;
		 size=_size;
		 absSize=_absSize;
	}
	public String getFileName(){
		return name;
	}
	public String getFileSize(){
		return size;
}
	public long getAbsFileSize(){
		return absSize;
}
}