package file_operation;
//**********************************************************************************************
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//**********************************************************************************************
public class fileOperation {
	static public boolean is_holding=false;
	//****************************************************
	protected static String sub_file_name(String file_name){
		int i=file_name.length()-1;
		for(;i>=0&&file_name.charAt(i)!='.';i--);
		if(i<0)return file_name; else return file_name.substring(0,i);
	}
	protected static void rename_suffix(String path,String old_name,String new_name,
								String new_suffix) throws IOException{
		File F=new File(path+"\\"+old_name);
		if(!F.exists()||F.isDirectory())throw new IOException();
		if(!F.renameTo(new File(path+"\\"+sub_file_name(new_name)+"."+new_suffix)))
			throw new IOException("Rename failed.");
	}
	protected static boolean is_exists(String full_file_name){
		File F=new File(full_file_name); return F.exists();
	}
	protected static String random_file_name(){
		StringBuilder SB=new StringBuilder();
		for(int i=0;i<10;i++)SB.append("%"+(int)(Math.random()*10));
		return SB.toString();
	}
	public static int string_hash_code(String str){
		int code=0;for(int i=0;i<str.length();i++){
			code+=(int)str.charAt(i); code=(code>>27|code<<5);
		}
		return code;
	}
	//****************************************************
	synchronized public static void delete_file(String full_file_name){
		File F=new File(full_file_name); if(F.exists())F.delete();
	}
	//****************************************************
	synchronized public static boolean copy_file(String path_old,String path_new,String file_name){
		DataInputStream DIS=null; DataOutputStream DOS=null;
		try{try{
				if(!is_exists(path_old+"\\"+file_name))
					throw new IOException("Target not exist.");
				if(is_exists(path_new+"\\"+file_name)){
					throw new IOException("Target has exist."); }
				DIS=new DataInputStream(new BufferedInputStream
						(new FileInputStream(path_old+"\\"+file_name)));
				DOS=new DataOutputStream(new BufferedOutputStream
						(new FileOutputStream(path_new+"\\"+file_name)));
				while(true) DOS.writeByte(DIS.readUnsignedByte());
			} catch (IOException IOE) {
				String mes=IOE.getMessage();
				if(mes!=null&&(mes.equals("Target not exist.")||
								mes.equals("Target has exist."))){
					 System.out.println(mes); return false;}
				DIS.close(); DOS.close(); return true; }
		} catch(IOException e){ return false; }
	}
	//****************************************************
	synchronized public static long get_length(String path,String... file_name){
		long length=0; File this_file=null;
		for(int i=0;i<file_name.length;i++){
			this_file=new File(path+"\\"+file_name[i]);
			if(this_file.isDirectory())length+=get_length(path+"\\"+file_name[i],this_file.list());
			else length+=this_file.length();
		}
		return length;
	}
	public static String[] get_hot_path(String abs_path){
		String[] hot_path=new String[2];
		int i=abs_path.length()-1;
		for(;abs_path.charAt(i)!='\\'&&i>=0;i--);
		hot_path[1]=abs_path.substring(i+1,abs_path.length());
		hot_path[0]=abs_path.substring(0,i);
		return hot_path;
	}
	public static boolean is_zip(String file_name){
		if(file_name.length()<4||!file_name.substring
				(file_name.length()-4,file_name.length()).equals(".zip"))
			return false;
		return true;
	}
}
//**********************************************************************************************