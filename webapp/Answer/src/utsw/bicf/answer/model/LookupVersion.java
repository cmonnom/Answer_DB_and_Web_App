package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="lookup_version")
public class LookupVersion {
	
	public LookupVersion() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="lookup_version_id")
	Integer lookupVersionId;
	
	@Column(name="database_name")
	String databaseName;

	@Column(name="version")
	String version;

	public Integer getLookupVersionId() {
		return lookupVersionId;
	}

	public void setLookupVersionId(Integer lookupVersionId) {
		this.lookupVersionId = lookupVersionId;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	
	
}
