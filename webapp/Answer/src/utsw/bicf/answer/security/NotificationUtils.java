package utsw.bicf.answer.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

public class NotificationUtils {
	
	public static final String HEAD = "<head><style>html {font-family: Roboto,sans-serif;}</style></head>";
	
	/**
	 * Send an email by running sendmail on the command line
	 * @param from
	 * @param to
	 * @param subject
	 * @param message
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean sendEmail(String from, String to, String subject, String message) throws IOException, InterruptedException {
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
		Process process;
		if (!isWindows) {
			StringBuilder emailCommand = new StringBuilder("#!/bin/bash\nprintf 'From:").append(from).append("\n")
			.append("To:").append(to).append("\n")
			.append("Subject:").append(subject).append("\n")
			.append("Content-Type: text/html\n")
			.append("MIME-Version: 1.0\n")
			.append(message)
			.append("\n' | sendmail -t\n");
			System.out.println("Sending this email:");
			System.out.println(emailCommand.toString());
			File script = new File("/tmp/email.sh");
			if (script.exists()) {
				script.delete();
			}
			FileUtils.write(script, emailCommand.toString(), Charset.defaultCharset());
			process = Runtime.getRuntime().exec("sh " + script.getAbsolutePath());
			int exitCode = process.waitFor();
			
			return exitCode == 0;
		}
		return false;
	}
	
	public static String buildStandardMessage(String message, EmailProperties emailProps, String link) {
		StringBuilder fullMessage = new StringBuilder()
				.append("<html>")
				.append(NotificationUtils.HEAD)
				.append("<body>")
				.append("<img src='")
				.append(emailProps.getRootUrl());
		if (emailProps.getRootUrl().endsWith("/")) {
			fullMessage.append("resources/"); //to make sure there is no double //
		}
		else {
			fullMessage.append("/resources/");
		}
		fullMessage.append("images/answer-logo-small-alpha.png'")
				.append(" width='150px' />")
//				.append("<p>Dr. ").append(toName).append(",</p><br/>")
//				.append("<b>")
//				.append(initiatorName)
//				.append("</b>")
//				.append(" assigned you a new case. ")
//				.append("<b>")
//				.append("Case Id: ").append(caseId).append("<br/>")
//				.append("</b>")
				.append(message)
				.append("Follow this link to access it: ")
				.append("<a href='")
				.append(link)
				.append("'>")
				.append(link)
				.append("</a><br/><br/>")
				.append(emailProps.getSignature())
				.append("</body></html>");
		return fullMessage.toString();
	}
}
