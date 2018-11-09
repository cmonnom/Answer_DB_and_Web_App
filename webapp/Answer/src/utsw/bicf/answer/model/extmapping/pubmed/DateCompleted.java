package utsw.bicf.answer.model.extmapping.pubmed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateCompleted {
	
	public final SimpleDateFormat IN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public final SimpleDateFormat OUT_DATE_FORMAT = new SimpleDateFormat("yyyy MMM dd");
	
	@JsonProperty("Year")
	Integer year;
	@JsonProperty("Month")
	Integer month;
	@JsonProperty("Day")
	Integer day;
	

	public DateCompleted() {
	}


	public Integer getYear() {
		return year;
	}


	public void setYear(Integer year) {
		this.year = year;
	}


	public Integer getMonth() {
		return month;
	}


	public void setMonth(Integer month) {
		this.month = month;
	}


	public Integer getDay() {
		return day;
	}


	public void setDay(Integer day) {
		this.day = day;
	}

	public String getPrettyPrint() throws ParseException {
		Date date = IN_DATE_FORMAT.parse(year + " " + month + " " + day);
		return OUT_DATE_FORMAT.format(date);
	}



	
}
