package utsw.bicf.answer.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import utsw.bicf.answer.model.User;

public class NotificationUtils {
	
	public static final String HEAD = "<head><style>html {font-family: Roboto,sans-serif;}</style></head>";
	public static final String SRC_BASE_64 = "data:image/png;base64,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";
	
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
			long time = System.currentTimeMillis() + Math.round(Math.random() * 100);
			File script = new File("/tmp/email_" + time + ".sh");
			if (script.exists()) {
				script.delete();
			}
			FileUtils.write(script, emailCommand.toString(), Charset.defaultCharset());
			process = Runtime.getRuntime().exec("sh " + script.getAbsolutePath());
			int exitCode = process.waitFor();
			if (script.exists()) {
				script.delete();
			}
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
//				.append(SRC_BASE_64)
//				.append("'")
				.append(emailProps.getRootUrl());
		if (emailProps.getRootUrl().endsWith("/")) {
			fullMessage.append("resources/"); //to make sure there is no double //
		}
		else {
			fullMessage.append("/resources/");
		}
		fullMessage.append("images/answer-logo-small.png'")
				.append(" width='150px' />")
				.append(message)
				.append("Follow this link to access it: ")
				.append("<a href='")
				.append(link)
				.append("'>")
				.append(link)
				.append("</a><br/><br/>");
		if (emailProps.getRemoteDesktopName() != null && !emailProps.getRemoteDesktopName().equals("")) {
			fullMessage.append("You may need to open ")
			.append(emailProps.getRemoteDesktopName())
			.append(" if the link does not work.<br/><br/>");
		}
		fullMessage.append(emailProps.getSignature())
				.append("</body></html>");
		return fullMessage.toString();
	}
	
	public static String buildStandardSelfNotificationMessage(String message, EmailProperties emailProps) {
		StringBuilder fullMessage = new StringBuilder()
				.append("<html>")
				.append(NotificationUtils.HEAD)
				.append("<body>")
				.append("<img src='")
//				.append(SRC_BASE_64)
//				.append("'")
				.append(emailProps.getRootUrl());
		if (emailProps.getRootUrl().endsWith("/")) {
			fullMessage.append("resources/"); //to make sure there is no double //
		}
		else {
			fullMessage.append("/resources/");
		}
		fullMessage.append("images/answer-logo-small.png'")
				.append(" width='150px' />")
				.append("<br/><br/>")
				.append(message)
				.append("<br/><br/>")
				.append(emailProps.getSignature())
				.append("</body></html>");
		return fullMessage.toString();
	}
	
	public static String buildStandardPasswordResetMessage(User user, boolean firstTime, String link, EmailProperties emailProps) {
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
		fullMessage.append("images/answer-logo-small.png'")
				.append(" width='150px' />")
				.append("<br/><br/>")
				.append("Dr. ").append(user.getLast()).append(",")
				.append("<br/><br/>");
		if (firstTime) {
			fullMessage.append("Welcome to Answer.");
		}
		fullMessage.append("Please click ").append("<a href='")
				.append(link)
				.append("'>")
				.append("this link</a> to ");
		if (firstTime) {
			fullMessage.append("set ");
		}
		else {
			fullMessage.append("reset ");
		}
		fullMessage.append("your password.<br/>The link will expire in 10 minutes.")
				.append("<br/><br/>")
				.append(emailProps.getSignature())
				.append("</body></html>");
		return fullMessage.toString();
	}
	
}
