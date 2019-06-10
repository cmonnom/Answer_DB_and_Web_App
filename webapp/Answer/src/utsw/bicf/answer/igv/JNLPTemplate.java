package utsw.bicf.answer.igv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import utsw.bicf.answer.security.FileProperties;

public class JNLPTemplate {
	
	String sessionURL;
	File templateFile;
	FileProperties fileProps;
	File templateOutput;


	public JNLPTemplate(String sessionURL, FileProperties fileProps, String igvUrlRoot) throws IOException {
		super();
		this.sessionURL = sessionURL;
		this.templateFile = new File(fileProps.getIgvTemplateFilesDir(), "template.jnlp");
		this.fileProps = fileProps;
		this.init(igvUrlRoot);
	}

	private void init(String igvUrlRoot) throws IOException {
		String content = FileUtils.readFileToString(templateFile, Charset.defaultCharset());
		content = content.replace("${sessionIGV}", sessionURL);
		this.templateOutput = createTemplateOutputFile();
		FileUtils.write(templateOutput, content, Charset.defaultCharset());
	}
	
	private File createTemplateOutputFile() throws IOException {
		String random = RandomStringUtils.random(25, true, true);
		String linkName = random + ".jnlp";
		File file = new File(fileProps.getIgvLinksDir(), linkName);
		return file;
	}
	
	public String getSessionURL() {
		return sessionURL;
	}

	public void setSessionURL(String sessionURL) {
		this.sessionURL = sessionURL;
	}

	public File getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public FileProperties getFileProps() {
		return fileProps;
	}

	public void setFileProps(FileProperties fileProps) {
		this.fileProps = fileProps;
	}

	public File getTemplateOutput() {
		return templateOutput;
	}

	public void setTemplateOutput(File templateOutput) {
		this.templateOutput = templateOutput;
	}
	
	

}
