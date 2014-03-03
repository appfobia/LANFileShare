package fileshare;

import java.io.File;
import java.io.Serializable;

public class FilesProperty implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2504706057430471776L;
	public String name=null;
	public String size=null;
	public long absSize;
	public File f=null;

	FilesProperty(String _name, String _size,long _absSize,File _f) {
		// TODO Auto-generated constructor stub
		 name=_name;
		 size=_size;
		 absSize=_absSize;
		 f=_f;
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
	public File getFile(){
		return f;
}
	
}