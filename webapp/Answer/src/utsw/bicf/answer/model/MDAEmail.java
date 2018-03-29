package utsw.bicf.answer.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mda_email")
public class MDAEmail {
	
	public MDAEmail() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mda_email_id")
	Integer mdaEmailId;
	
	@Column(name="filename")
	String filename;
	
	@Column(name="date_imported")
	LocalDateTime dateImported;
	
	@Column(name="raw_html")
	String rawHTML;

	public Integer getMdaEmailId() {
		return mdaEmailId;
	}

	public void setMdaEmailId(Integer mdaEmailId) {
		this.mdaEmailId = mdaEmailId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public LocalDateTime getDateImported() {
		return dateImported;
	}

	public void setDateImported(LocalDateTime dateImported) {
		this.dateImported = dateImported;
	}

	public String getRawHTML() {
		return rawHTML;
	}

	public void setRawHTML(String rawHTML) {
		this.rawHTML = rawHTML;
	}
	

	
	
}
