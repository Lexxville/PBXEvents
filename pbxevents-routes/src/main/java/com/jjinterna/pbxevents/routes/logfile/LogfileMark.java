package com.jjinterna.pbxevents.routes.logfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogfileMark {

	private Path path;
	
	public LogfileMark(String uri) {
		this.path = Paths.get(uri);
	}
	
	public String getMark() {
		try {
			return new String(Files.readAllBytes(path));
		}
		catch (Exception e) {
			return null;
		}
	}

	public void setMark(String s) throws IOException {
		Files.write(path, s.getBytes());
	}
}
