package utsw.bicf.answer.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import utsw.bicf.answer.dao.ModelDAO;

@Controller
@EnableScheduling
public class CronController {

	@Autowired
	FileProperties fileProps;
	@Autowired
	ModelDAO modelDAO;
	

	@Scheduled(fixedDelay = 600000) //run every 10min after the last task ended (10 * 60 * 1000 milliseconds)
	public void cleanBamPdfLinks() {
//		System.out.println("cron started :" + LocalDateTime.now().toLocalTime());
		File linkDir = fileProps.getBamLinksDir();
		deleteLinks(linkDir);
		linkDir = fileProps.getPdfLinksDir();
		deleteLinks(linkDir);
		File fileDir = fileProps.getPdfFilesDir();
		deleteLinks(fileDir);
		File igvLinkDir = fileProps.getIgvLinksDir();
		deleteLinks(igvLinkDir);
		File vcfLinkDir = fileProps.getVcfLinksDir();
		deleteLinks(vcfLinkDir);
		File finalizedPDFDir = fileProps.getPdfFinalizedFilesDir();
		File finalizedPDFBackupDir = fileProps.getPdfFinalizedFilesBackupDir();
		copyToBackup(finalizedPDFDir, finalizedPDFBackupDir);
	}
	
	@Scheduled(fixedDelay = 123000) //run every 2min after the last task ended (2 * 60 * 1000 milliseconds)
	public void clearResetTokens() {
		modelDAO.clearResetTokens();
	}
	
	private void deleteLinks(File linkDir) {
		long now = System.currentTimeMillis();
		if (linkDir.exists()) {
			Stream<Path> stream = null;
			try {
				stream = Files.list(linkDir.toPath());
				List<Path> links = stream.filter(p -> p.toFile().isFile()).collect(Collectors.toList());
				for (Path link : links) {
					if (now - Files.getLastModifiedTime(link, LinkOption.NOFOLLOW_LINKS).toMillis() > 60 * 60 * 1000) { //modified more than 1h ago
						link.toFile().delete();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				stream.close();
			}
		}
	}
	
	private void copyToBackup(File finalizedPDFDir, File finalizedPDFBackupDir) {
		if (finalizedPDFDir != null && finalizedPDFDir.exists()
				&& finalizedPDFBackupDir != null && finalizedPDFBackupDir.exists()) {
			File[] pdfFiles = finalizedPDFDir.listFiles();
			for (File pdfFile : pdfFiles) {
				//check if destination file already exists
				File backupFile = new File(finalizedPDFBackupDir, pdfFile.getName());
				if (!backupFile.exists()) {
					//backup the pdf
					try {
						FileUtils.copyFile(pdfFile, backupFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
