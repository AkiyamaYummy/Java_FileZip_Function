package file_operation;
//**********************************************************************************************
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JProgressBar;
//**********************************************************************************************
public class fileZip extends fileOperation{
	synchronized public static boolean add_zip(JProgressBar JB,String path_old,String path_new,
											String...file_name){
		fileZip.is_holding=true;
		try{
			if(is_exists(path_new+"\\"+sub_file_name(file_name[0])+".zip"))
				throw new IOException("Target has exist.");
			if(JB!=null){ int con=0;
				for(int i=0;i<file_name.length;i++)
					con+=get_length(path_old,file_name[i]);
				JB.setMaximum(con); JB.setValue(0);
			}
			String random_name;
			ZipOutputStream out=new ZipOutputStream
									(new BufferedOutputStream
									(new FileOutputStream
									(path_new+"\\"+(random_name=random_file_name()))));
			add_zip_son(out,JB,"",path_old,file_name);
			out.close(); rename_suffix(path_new,random_name,file_name[0],"zip"); 
			fileZip.is_holding=false; return true;
		}catch(IOException IOE){
			System.out.println(IOE.getMessage()); fileZip.is_holding=false; return false;
		}
	}
	private static void add_zip_son(ZipOutputStream ZO,JProgressBar JB,String path_ex,
								String path_old,String...file_name) throws IOException{
		for(int i=0;i<file_name.length;i++){
			File this_file=new File(path_old+"\\"+file_name[i]);
			if(this_file.isDirectory()){
				ZO.putNextEntry(new ZipEntry(path_ex+file_name[i]+'/'));
				add_zip_son(ZO,JB,path_ex+file_name[i]+"\\",path_old+"\\"+file_name[i],this_file.list());
			}
			else {
				ZO.putNextEntry(new ZipEntry(path_ex+file_name[i]));
				BufferedInputStream ZI=new BufferedInputStream
									(new FileInputStream(path_old+"\\"+file_name[i]));
				int value;while((value=ZI.read())!=-1){
					ZO.write(value); if(JB!=null)JB.setValue(JB.getValue()+1);}
				ZI.close();
			}
		}
	}
	//****************************************************
	synchronized public static boolean release_zip(JProgressBar JB,String path_old,String path_new,
											String file_name){
		try{
			fileZip.is_holding=true;
			if(!is_exists(path_old+"\\"+file_name)||!is_zip(path_old+"\\"+file_name))
				throw new IOException("Target not exist.");
			if(JB!=null){JB.setMaximum((int) get_length(path_old,file_name));
				JB.setValue(0);}
			ZipInputStream in=new ZipInputStream
							(new BufferedInputStream
							(new FileInputStream(path_old+"\\"+file_name)));
			for(ZipEntry enter=in.getNextEntry();enter!=null;enter=in.getNextEntry()){
				String this_file=path_new+"\\"+enter.getName();
				if(is_exists(this_file)){
					in.close(); throw new IOException("Target has exist.");
				}
				if(enter.isDirectory())new File(this_file).mkdirs();
				else {BufferedOutputStream out=new BufferedOutputStream
								(new FileOutputStream(this_file));
				for(int i=in.read();i!=-1;i=in.read()){
					if(JB!=null)JB.setValue(JB.getValue()+1); out.write(i);
				} out.close(); }
			}	in.close(); fileZip.is_holding=false; return true; 
		} catch(IOException IOE){
			System.out.println(IOE.getMessage()); fileZip.is_holding=false; return false; }
	}
}
//**********************************************************************************************