package so.filesystem.disk;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DiskDirectory  {
	
	 private HashMap<String, Integer> directory;
	 
	 public DiskDirectory() {
		 try {
				FileInputStream fos = new FileInputStream("directory.dir");
				ObjectInputStream oos = new ObjectInputStream(fos);
				try {
					this.directory = (HashMap<String, Integer>) oos.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.directory = new HashMap<String, Integer>();
				// e.printStackTrace();
			}
	 }

	public void newFile(String fileName, Integer inodeAddress){
		this.directory.put(fileName, inodeAddress);
	}
	
	public int searchForFile(String fileName){
		if(this.directory.containsKey(fileName)){
			return this.directory.get(fileName);
		}
		
		return -1;
	}
	
	
	public boolean isFileInDirectory(String filename) {
		return this.directory.containsKey(filename);
	}
	
	public ArrayList<String> listDirectory(){
//		for(java.util.Map.Entry<String, Integer> entry : this.entrySet()){
//		    System.out.printf("Key : %s and Value: %s %n", entry.getKey(), entry.getValue());
//		}
		ArrayList<String> ls = new ArrayList<String>();
		
		for (String key : this.directory.keySet()) {
			ls.add(key);
		}
		
		return ls;
	}
	
	public void deleteFile(String filename) {
		this.directory.remove(filename);
	}
	
	public void saveDirectory() {
		try {
			FileOutputStream fos = new FileOutputStream("directory.dir");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.directory);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resetDirectory() {
		this.directory =  new HashMap<String, Integer>();
		this.saveDirectory();
	}
}
