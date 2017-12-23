package hello;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;


@SpringBootApplication
public class Application {

	@Autowired
	S3Services s3Services;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		ApplicationContext context = SpringApplication.run(Application.class);
		System.exit(SpringApplication.exit(context));
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		
		return args -> {
			
			try {
			
				log.info("Reading config file started...");
				
				Map<String, String> configDetail = PropertyReader.getConfigDetail();
				
				if(null == configDetail || configDetail.size() < 1) {
					throw new Exception("Config detail not found!!!!!!!!!");
				}
				
				log.info("Reading config file completed.");
				
				String fileLocation = configDetail.get(Constants.REPORT_LOCATION);
				String bkFileLocation = configDetail.get(Constants.REPORT_BK_LOCATION);
				String bucketName = configDetail.get(Constants.BUCKET);
				
				log.info("File location : " + fileLocation);
				log.info("File bk location : " + bkFileLocation);
				log.info("Bucket Name : " + bucketName);
				
				List<String> filesList = new ArrayList<String>();
				File directory = new File(fileLocation);
		        
				log.info("Get file names from the directory : " + fileLocation);
				
				//get all the files from a directory
		        File[] fList = directory.listFiles();
		        
		        if(null == fList || fList.length < 1) throw new Exception("No files found to process in " + fileLocation);
		        
		        for (File file : fList){
		            if (file.isFile()){
		                filesList.add(file.getName());
		            }
		        }
		        
		        log.info("Number of files : " + filesList.size());
		        
		        if(null != filesList && filesList.size() > 0) {
			        
		        	//move files to bk folder
		        	for(String fileName : filesList) {
		        		
		        		try {
		        			
		        			log.info("Uploading file : " + fileName);
		        			
		        			//upload files into s3
		    		        s3Services.uploadFile(fileName, new StringBuilder(fileLocation).append(fileName).toString(), bucketName);
		        	    	
		    		        log.info("Uploaded file : " + fileName);
		    		        
		    		        log.info("Moving file to bk folder : " + fileName);
		    		        
		    		        //moving files to backup folder
					        Path temp = Files.move
				            (Paths.get(new StringBuilder(fileLocation).append(fileName).toString()), 
				            Paths.get(new StringBuilder(bkFileLocation).append(fileName).toString()));
				     
				            if(temp != null) {
				                log.info("File moved successfully : " + fileName);
				            } else {
				                log.info("Failed to move the file : " + fileName);
				            }
				            
		        		} catch (FileAlreadyExistsException e) {
		        			log.error("File already exists in the bk folder : " + fileName);
						} catch (Exception e) {
							log.error("Exception captured : " + fileName + " : " + e.getMessage());
						}
			            
		        	}
		        
		        } else {
		        	log.info("No files found in folder!!!");
		        }
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}    
		};
	}
	
}