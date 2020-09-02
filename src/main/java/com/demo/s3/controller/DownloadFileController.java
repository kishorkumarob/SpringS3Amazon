package com.demo.s3.controller;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.demo.s3.services.S3Services;
  
@RestController
public class DownloadFileController {
 	
	@Autowired
	S3Services s3Services;
	
	@Autowired
	private AmazonS3 s3client;
 
	@Value("${s3.bucket}")
	private String bucketName;
	
	@Value("${lqbdocsdownload.rootpath}")
	private String rootPath;
	
	private static final String SUFFIX = "/";
	 
    /*
     * Download Files
     */
	@GetMapping("/api/file/download")
	public ResponseEntity<byte[]> downloadFile() throws IOException {
		
        File zipFile = null;
		zipFile = new File(rootPath +"/test.zip");
		
		if(!zipFile.exists()) {
			zipFile.createNewFile();
		}
		
		String folderName = "loans/pa";
		createFolder(bucketName, folderName);
		
		String keyName = folderName + SUFFIX + zipFile.getName();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(zipFile.length());
		
		s3client.putObject(bucketName, keyName, zipFile);
		
		ByteArrayOutputStream downloadInputStream = s3Services.downloadFile(keyName);
	
		return ResponseEntity.ok()
					.contentType(contentType(keyName))
					.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + keyName + "\"")
					.body(downloadInputStream.toByteArray());	
	}
	
	public void createFolder(String bucketName, String folderName) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
					folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		s3client.putObject(putObjectRequest);
	}

	
	private MediaType contentType(String keyname) {
		String[] arr = keyname.split("\\.");
		String type = arr[arr.length-1];
		switch(type) {
			case "txt": return MediaType.TEXT_PLAIN;
			case "png": return MediaType.IMAGE_PNG;
			case "jpg": return MediaType.IMAGE_JPEG;
			default: return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
}
