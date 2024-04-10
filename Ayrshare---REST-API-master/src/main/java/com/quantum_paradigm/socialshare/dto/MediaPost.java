package com.quantum_paradigm.socialshare.dto;

import lombok.Data;

@Data
public class MediaPost {
	private String[] mediaPlatform;
	private String caption;
	private String title;
	private String subreddit;
	private String tumbNail;
}
