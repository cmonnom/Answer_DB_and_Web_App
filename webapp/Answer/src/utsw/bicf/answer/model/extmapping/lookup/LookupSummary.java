package utsw.bicf.answer.model.extmapping.lookup;

public class LookupSummary {

	/**
	 * A manually set url to link back to NCBI
	 */
	String moreInfoUrl;
	String summary;
	String database; //name of the database in lookup-tool.js (RefSeq, OncoKB,...)
	
	public LookupSummary() {
		super();
	}
	
	public String getMoreInfoUrl() {
		return moreInfoUrl;
	}
	public void setMoreInfoUrl(String moreInfoUrl) {
		this.moreInfoUrl = moreInfoUrl;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
