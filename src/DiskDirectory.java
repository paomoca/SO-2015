import java.util.HashMap;

public class DiskDirectory extends HashMap<String, Integer> {
	
	

	public void newFile(String fileName, Integer inodeAddress){
		
		this.put(fileName, inodeAddress);
		
	}
	
	public int searchForFile(String fileName){
		
		if(this.containsKey(fileName)){
			return this.get(fileName).intValue();
		}
		
		return -1;
	}
	
	public void listDirectory(){
		
		for(java.util.Map.Entry<String, Integer> entry : this.entrySet()){
		    System.out.printf("Key : %s and Value: %s %n", entry.getKey(), entry.getValue());
		}
		
	}

}
