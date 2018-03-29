package utsw.bicf.answer.reporting.finalreport;

public class TableDetails {
	
	Float offset;
	Float yPos;
	Float tableHeight;
	
	public TableDetails(Float offset, Float yPos, Float tableHeight) {
		super();
		this.offset = offset;
		this.yPos = yPos;
		this.tableHeight = tableHeight;
	}
	public Float getyPos() {
		return yPos;
	}
	public void setyPos(Float yPos) {
		this.yPos = yPos;
	}
	public Float getTableHeight() {
		return tableHeight;
	}
	public void setTableHeight(Float tableHeight) {
		this.tableHeight = tableHeight;
	}
	public Float getOffset() {
		return offset;
	}
	public void setOffset(Float offset) {
		this.offset = offset;
	}
	
	

}
