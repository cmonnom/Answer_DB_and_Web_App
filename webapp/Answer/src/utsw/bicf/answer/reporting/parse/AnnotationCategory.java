package utsw.bicf.answer.reporting.parse;

public class AnnotationCategory {
	
	String title;
	String text;
	
	public static final String GENE_FUNCTION_TITLE = "Gene function";
	public static final String ALTERATION_FUNCTION_TITLE = "Alteration function";
	public static final String ALTERATIONS_FUNCTION_TITLE = "Alterations function";
	public static final String POTENTIAL_THERAPEUTIC_IMPLICATION_TITLE = "Potential therapeutic implication";
	public static final String POTENTIAL_THERAPEUTIC_IMPLICATIONS_TITLE = "Potential therapeutic implications";
	public static final String TUMOR_SPECIFIC_INFO_TITLE = "Tumor type specific information";
	
	public AnnotationCategory(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String toHTMLString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>").append("<b>").append(title).append("</b>").append(text).append("</p>");
		return sb.toString();
	}
	
	public String toPlainText() {
		return title + text;
	}
	

}
