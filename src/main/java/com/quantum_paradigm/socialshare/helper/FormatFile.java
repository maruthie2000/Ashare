package com.quantum_paradigm.socialshare.helper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FormatFile {

	public File formatType(MultipartFile mediaFile) {
		try {
			String originalFilename = mediaFile.getOriginalFilename();
			if (mediaFile.getContentType().startsWith("image")) {
				return convertToJPEG(mediaFile);
			} else if (mediaFile.getContentType().startsWith("video")) {
				return convertToMP4(mediaFile.getBytes());
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private File convertToJPEG(MultipartFile mediaFile) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(mediaFile.getBytes()));
		File outputFile = new File(mediaFile.getOriginalFilename()); 
		ImageIO.write(bufferedImage, "jpg", outputFile);
		return outputFile;
	}

	private File convertToMP4(byte[] fileBytes) throws IOException {
		Path tempFile = Files.createTempFile("temp-video", ".mp4");
		Files.write(tempFile, fileBytes);
		ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", tempFile.toString(), "-c:v", "libx264",
				"-c:a", "aac", "-strict", "experimental", "-movflags", "faststart", "-vf",
				"scale=trunc(iw/2)*2:trunc(ih/2)*2", tempFile.toString() + ".mp4");
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		try {
			process.waitFor();
		} catch (InterruptedException e) {

		}
		File convertedFile = new File(tempFile.toString() + ".mp4");
		Files.delete(tempFile);
		return convertedFile;
	}

}
