package utsw.bicf.answer.clarity.api.utils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.LinkedList;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import utsw.bicf.answer.clarity.api.model.ClarityValue;

public class TypeUtils {
	
	public static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	public static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	public static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter shortMonthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
	public static final DateTimeFormatter shortDayMonthYearFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
	
	static final NumberFormat pctFormatter = NumberFormat.getPercentInstance();
	static final DateFormat sqlDateFormatter = DateFormat.getDateTimeInstance();
	
	static {
		pctFormatter.setMaximumFractionDigits(2);
	}
	
	public static LocalDate parseSQLDateToLocalDate(Date date) throws ParseException {
		if (date == null) {
			return null;
		}
		return date.toLocalDate();
	}
	
	public static LocalDateTime parseSQLDateToLocalDateTime(Date date) throws ParseException {
		if (date == null) {
			return null;
		}
		return LocalDateTime.parse(sqlDateFormatter.format(date));
	}
	
	public static LocalDateTime parseDateToLocalDateTime(LocalDate date) throws ParseException {
		if (date == null) {
			return null;
		}
		return date.atStartOfDay();
	}
	
	public static LocalDateTime parseDateToLocalDateTime(ClarityValue dateValue) throws ParseException {
		if (dateValue == null || dateValue.getValue() == null) {
			return null;
		}
		String dateString = dateValue.getValue() + " 00:00";
		return LocalDateTime.parse(dateString, localDateTimeFormatter);
	}
	
	public static LocalDateTime parseDateToLocalDateTime(String dateString) throws ParseException {
		if (dateString == null) {
			return null;
		}
		return LocalDateTime.parse(dateString + " 00:00", localDateTimeFormatter);
	}
	
	public static LocalDate parseMonthDateToLocalDate(String monthDate) {
		if (monthDate == null || monthDate.equals("")) {
			return null;
		}
		monthDate += "-01";
		return LocalDate.parse(monthDate, monthFormatter);
	}
	
	public static LocalDate getLastDayOfMonthLocalDate(LocalDate firstDayOfMonthDate) {
		if (firstDayOfMonthDate == null) {
			return null;
		}
		return firstDayOfMonthDate.plusMonths(1).minusDays(1);
	}
	
	public static Float doubleToFloat(Double value) {
		return value != null ? value.floatValue() : null;
	}
	
	public static Integer StringToInt(String value) {
		return value != null ? Integer.parseInt(value) : null;
	}
	
	public static String toPercentString(Float value) {
		return pctFormatter.format(value);
	}

	public static boolean notNullNotEmpty(String value) {
		return value != null && !value.equals("");
	}
	
	public static Boolean intToBoolean(Integer value) {
		if (value == null) {
			return null;
		}
		return value == 1 ? true : false;
	}
	
	/**
	 * From a chromosome number, format it so that it always
	 * returns CHR## (2digit number) or CHR<SOME LETTERS>
	 * @param chrom
	 * @return
	 */
	public static String formatChromosome(String chrom) {
		String formattedChrNb = null;
		if (chrom != null && (chrom.startsWith("chr") || chrom.startsWith("CHR"))) {
			String chrNb = chrom.substring(3, chrom.length());
			boolean isNumber = StringUtils.isNumeric(chrNb);
			if (isNumber) {
				formattedChrNb = "CHR" + String.format(Locale.US, "%02d", Integer.parseInt(chrNb));
			}
			else {
				formattedChrNb = chrom.toUpperCase();
			}
		}
		return formattedChrNb;
	}
	
	public static String dateSince(Temporal date) {
		Temporal now = OffsetDateTime.now(ZoneOffset.UTC);
		if (date == null) {
			date = OffsetDateTime.now(ZoneOffset.UTC);
		}
		if (date instanceof LocalDate) {
			date = LocalDateTime.of((LocalDate) date, LocalTime.now(ZoneOffset.UTC));
		}
		else{
			now = OffsetDateTime.now(ZoneOffset.UTC);
		}
		Duration since = Duration.between(date, now);
		
		if (since.toDays() > 365) {
			String ago = " year ago";
			if (since.toDays() / 365 > 1) {
				ago = " years ago";
			}
			return (since.toDays() / 365) + ago;
		}
		if (since.toDays() > 30) {
			String ago = " month ago";
			if (since.toDays() / 30 > 1) {
				ago = " months ago";
			}
			return (since.toDays() / 30) + ago;
		}
		else if (since.toDays() > 1) {
			return since.toDays() + " days ago";
		}
		else if (since.toHours() > 1) {
			return since.toHours() + " hours ago";
		}
		else if (since.toMinutes() > 1) {
			return since.toMinutes() + " minutes ago";
		}
		else if (since.toMillis() > 2000) {
			return since.toMillis() / 1000 + " seconds ago";
		}
		else {
			return "just now";
		}
	}
	
	//accept a string, like aCamelString
	//return a contatenated string containing in this case, [a, Camel, String] -> a Camel String
	public static String splitCamelCaseString(String s){
	    LinkedList<String> result = new LinkedList<String>();	
	    for (String w : s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
	    	result.add(w);
	    }    
	    return result.stream().collect(Collectors.joining(" "));
	}
	
	/**
	 * Since the column is "trusted" Vuetify will interpret the html tags
	 * This is kind of a hack but easier to manage than doing it as a special case in data-tables
	 * @param dateSince
	 * @param isReviewer 
	 * @return
	 */
	public static String buildDateSinceChip(String dateSince, Boolean isReviewer) {
		StringBuilder sb = new StringBuilder();
		String chipColor = (isReviewer != null && isReviewer) ? "green" : "grey";
		sb.append("<span tabindex='-1' class='chip chip--disabled chip--label ").append(chipColor).append(" chip--small white--text'><span class='chip__content'>")
        .append("<i aria-hidden='true' class='icon material-icons mdi mdi-checkbox-marked'></i>") 
        .append("<span class='pl-2'>").append(dateSince).append("</span></span></span>");
		return sb.toString();
	}
}
