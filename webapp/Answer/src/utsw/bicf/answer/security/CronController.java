package utsw.bicf.answer.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@EnableScheduling
public class CronController {

	@Autowired
	FileProperties fileProps;
	

	@Scheduled(cron = "*/10 * * * * *") //run every 10min, every day
	public void cleanBamLinks() {
		long now = System.currentTimeMillis();
		File linkDir = fileProps.getBamLinksDir();
		if (linkDir.exists()) {
			Stream<Path> stream = null;
			try {
				stream = Files.list(linkDir.toPath());
				List<Path> links = stream.collect(Collectors.toList());
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
	
}
