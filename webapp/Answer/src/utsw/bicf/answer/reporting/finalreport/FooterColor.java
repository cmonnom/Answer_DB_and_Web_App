package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import be.quodlibet.boxable.line.LineStyle;

public class FooterColor {
	
	Color color;
	LineStyle lineStyle;
	
	
	public FooterColor(Color color, LineStyle lineStyle) {
		super();
		this.color = color;
		this.lineStyle = lineStyle;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public LineStyle getLineStyle() {
		return lineStyle;
	}
	public void setLineStyle(LineStyle lineStyle) {
		this.lineStyle = lineStyle;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   FooterColor rhs = (FooterColor) obj;
		   return new EqualsBuilder()
		                 .append(color, rhs.color)
		                 .append(lineStyle, rhs.lineStyle)
		                 .isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).
			       append(color).
			       append(lineStyle).
			       toHashCode();
	}
}
