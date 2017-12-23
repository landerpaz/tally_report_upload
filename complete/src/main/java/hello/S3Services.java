package hello;

public interface S3Services {
	public void uploadFile(String keyName, String uploadFilePath, String bucketName) throws Exception;
}
