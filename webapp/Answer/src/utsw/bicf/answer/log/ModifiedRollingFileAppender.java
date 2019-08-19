package utsw.bicf.answer.log;

import java.io.File;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class ModifiedRollingFileAppender extends RollingFileAppender {
	
	@Override 
    public void append(LoggingEvent event) {
        checkLogFileExist();
        super.append(event);
    }

    private void checkLogFileExist(){
        File logFile = new File(super.fileName);
        if (!logFile.exists()) {
            this.activateOptions();
        }
    }
}
