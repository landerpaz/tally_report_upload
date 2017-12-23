package hello;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyReader {
	
	static Map<String, String> getConfigDetail() {
	
		Properties prop = new Properties();
		InputStream input = null;
		Map<String, String> propertyData = null;

		try {

			// load a properties file
			input = new FileInputStream(Constants.CONFIG_FILE); //report upload
			prop.load(input);
			
			propertyData = new HashMap<String, String>();
			propertyData.put(Constants.REPORT_LOCATION, prop.getProperty(Constants.REPORT_LOCATION));
			propertyData.put(Constants.REPORT_BK_LOCATION, prop.getProperty(Constants.REPORT_BK_LOCATION));
			propertyData.put(Constants.BUCKET, prop.getProperty(Constants.BUCKET));
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return propertyData;
	}
	
	public static void main(String a[]) {
		
		try {
			
			String fileLocation = "/Users/ashokarulsamy/tally/";
			String bkFileLocation = "/Users/ashokarulsamy/tally/bk/";
			
			List<String> filesList = new ArrayList<String>();
			File directory = new File(fileLocation);
	        
			//get all the files from a directory
	        File[] fList = directory.listFiles();
	        
	        for (File file : fList){
	            if (file.isFile()){
	                System.out.println(file.getName());
	                filesList.add(file.getName());
	            }
	        }
	        
	        if(null != filesList && filesList.size() > 0) {
		        
		        //move files to bk folder
	        	for(String fileName : filesList) {
	        		
	        		try {
				        Path temp = Files.move
			            (Paths.get(new StringBuilder(fileLocation).append(fileName).toString()), 
			            Paths.get(new StringBuilder(bkFileLocation).append(fileName).toString()));
			     
			            if(temp != null)
			            {
			                System.out.println("File moved successfully");
			            }
			            else
			            {
			                System.out.println("Failed to move the file");
			            }
	        		} catch (FileAlreadyExistsException e) {
	        			System.out.println("File already exists in the bk folder : " + fileName);
					}
		            
	        	}
	        
	        } else {
	        	System.out.println("No files found in folder!!!");
	        } 
	        
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
