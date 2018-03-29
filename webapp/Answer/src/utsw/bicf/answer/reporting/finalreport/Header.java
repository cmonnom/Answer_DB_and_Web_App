package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;

import org.apache.pdfbox.pdmodel.font.PDFont;

import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.VerticalAlignment;

public class Header {

	String text;
//	PDFont font;
//	Color textColor;
//	Color backgroundColor;
//	float fontSize;
	float width;
	
	/**
	 * Width should be a pct of the row
	 * Eg. 3 rows could have 60, 25, 15 widths.
	 * which would occupy 60%, 25% and 15% of the table total width
	 * @param text
	 * @param width
	 */
	public Header(String text, float width) {
		super();
		this.text = text;
//		this.font = font;
//		this.textColor = textColor;
//		this.backgroundColor = backgroundColor;
//		this.fontSize = fontSize;
		this.width = width;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


//	public PDFont getFont() {
//		return font;
//	}
//
//	public void setFont(PDFont font) {
//		this.font = font;
//	}
//
//	public Color getTextColor() {
//		return textColor;
//	}
//
//	public void setTextColor(Color textColor) {
//		this.textColor = textColor;
//	}
//
//	public Color getBackgroundColor() {
//		return backgroundColor;
//	}
//
//	public void setBackgroundColor(Color backgroundColor) {
//		this.backgroundColor = backgroundColor;
//	}
//
//	public float getFontSize() {
//		return fontSize;
//	}
//
//	public void setFontSize(float fontSize) {
//		this.fontSize = fontSize;
//	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
}
