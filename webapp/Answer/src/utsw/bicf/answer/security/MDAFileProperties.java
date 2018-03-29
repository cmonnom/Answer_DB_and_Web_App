package utsw.bicf.answer.security;

import java.io.File;

public class MDAFileProperties {

	String mdaFilesPath;
	File mdaFilesDir;
	
	public String getMdaFilesPath() {
		return mdaFilesPath;
	}
	public void setMdaFilesPath(String mdaFilesPath) {
		this.mdaFilesPath = mdaFilesPath;
	}
	public File getMdaFilesDir() {
		if (mdaFilesPath == null) {
			return null;
		}
		if (mdaFilesDir == null) {
			mdaFilesDir = new File(mdaFilesPath);
		}
		return mdaFilesDir;
	}
	public void setMdaFilesDir(File mdaFilesDir) {
		this.mdaFilesDir = mdaFilesDir;
	}
	
	
}
