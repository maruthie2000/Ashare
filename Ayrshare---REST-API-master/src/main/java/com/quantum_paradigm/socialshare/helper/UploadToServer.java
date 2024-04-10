package com.quantum_paradigm.socialshare.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.quantum_paradigm.socialshare.configuration.ConfigurationClass;

@Component
public class UploadToServer {

	@Autowired
	ConfigurationClass configurationClass;

	private String BUCKET_NAME = "planotech123";
	private String AWS_REGION = "ap-south-1";

	@PostMapping("/upload")
	public String uploadFile(MultipartFile file) {
		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(AWS_REGION).build();

//			String fileName = file.getName();
			String fileName = file.getOriginalFilename();
//			String contentType = Files.probeContentType(file.toPath());
			String contentType = file.getContentType();
			String key = "public/" + fileName + "." + contentType; // Specify the S3 key for the uploaded file
//			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, key, file);
			ObjectMetadata metadata = configurationClass.getMetaObject();
			metadata.setContentType(file.getContentType());
			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, key, file.getInputStream(), metadata);
//                    .withContentType(contentType);
			PutObjectResult result = s3Client.putObject(request);

			String publicUrl = s3Client.getUrl(BUCKET_NAME, key).toString();
			System.out.println("publicUrl : " + publicUrl);
			return publicUrl;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public File convertMultipartFileToFile(MultipartFile multipartFile) {
		File file = new File(multipartFile.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(multipartFile.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
}
