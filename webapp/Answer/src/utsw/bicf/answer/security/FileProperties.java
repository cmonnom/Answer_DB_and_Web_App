package utsw.bicf.answer.security;

import java.io.File;

public class FileProperties {

	String mdaFilesPath;
	File mdaFilesDir;
	String excelFilesPath;
	File excelFilesDir;
	String bamLinksPath;
	File bamLinksDir;
	String bamFilesPath;
	File bamFilesDir;
	Boolean productionEnv;
	String genePanelSearchUrl;
	
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
	public String getExcelFilesPath() {
		return excelFilesPath;
	}
	public void setExcelFilesPath(String excelFilesPath) {
		this.excelFilesPath = excelFilesPath;
	}
	public File getExcelFilesDir() {
		if (excelFilesPath == null) {
			return null;
		}
		if (excelFilesDir == null) {
			excelFilesDir = new File(excelFilesPath);
		}
		return excelFilesDir;
	}
	public void setExcelFilesDir(File excelFilesDir) {
		this.excelFilesDir = excelFilesDir;
	}
	public String getBamLinksPath() {
		return bamLinksPath;
	}
	public void setBamLinksPath(String bamLinksPath) {
		this.bamLinksPath = bamLinksPath;
	}
	public File getBamLinksDir() {
		if (bamLinksPath == null) {
			return null;
		}
		if (bamLinksDir == null) {
			bamLinksDir = new File(bamLinksPath);
		}
		return bamLinksDir;
	}
	public void setBamLinksDir(File bamLinksDir) {
		this.bamLinksDir = bamLinksDir;
	}
	public String getBamFilesPath() {
		return bamFilesPath;
	}
	public void setBamFilesPath(String bamFilesPath) {
		this.bamFilesPath = bamFilesPath;
	}
	public File getBamFilesDir() {
		if (bamFilesPath == null) {
			return null;
		}
		if (bamFilesDir == null) {
			bamFilesDir = new File(bamFilesPath);
		}
		return bamFilesDir;
	}
	public void setBamFilesDir(File bamFilesDir) {
		this.bamFilesDir = bamFilesDir;
	}
	public Boolean getProductionEnv() {
		return productionEnv;
	}
	public void setProductionEnv(Boolean productionEnv) {
		this.productionEnv = productionEnv;
	}
	public String getGenePanelSearchUrl() {
		return genePanelSearchUrl;
	}
	public void setGenePanelSearchUrl(String genePanelSearchUrl) {
		this.genePanelSearchUrl = genePanelSearchUrl;
	}
	
	
}
