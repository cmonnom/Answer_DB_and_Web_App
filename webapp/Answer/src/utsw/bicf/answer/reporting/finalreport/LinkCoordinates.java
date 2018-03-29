package utsw.bicf.answer.reporting.finalreport;

public class LinkCoordinates {
	float lowerLeftX;
	float lowerLeftY;
	float upperRightX;
	float upperRightY;
	int currentPageNb;
	
	public LinkCoordinates(float lowerLeftX, float lowerLeftY, float upperRightX, float upperRightY,
			int currentPageNb) {
		super();
		this.lowerLeftX = lowerLeftX;
		this.lowerLeftY = lowerLeftY;
		this.upperRightX = upperRightX;
		this.upperRightY = upperRightY;
		this.currentPageNb = currentPageNb;
	}
	
	public float getLowerLeftX() {
		return lowerLeftX;
	}
	public void setLowerLeftX(float lowerLeftX) {
		this.lowerLeftX = lowerLeftX;
	}
	public float getLowerLeftY() {
		return lowerLeftY;
	}
	public void setLowerLeftY(float lowerLeftY) {
		this.lowerLeftY = lowerLeftY;
	}
	public float getUpperRightX() {
		return upperRightX;
	}
	public void setUpperRightX(float upperRightX) {
		this.upperRightX = upperRightX;
	}
	public float getUpperRightY() {
		return upperRightY;
	}
	public void setUpperRightY(float upperRightY) {
		this.upperRightY = upperRightY;
	}
	public int getCurrentPageNb() {
		return currentPageNb;
	}
	public void setCurrentPageNb(int currentPageNb) {
		this.currentPageNb = currentPageNb;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LinkCoordinates) {
			LinkCoordinates objCoord = (LinkCoordinates) obj;
			return 	objCoord.currentPageNb == currentPageNb
					&& objCoord.lowerLeftX == lowerLeftX
					&& objCoord.lowerLeftY == lowerLeftY; //need more?
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return currentPageNb + " " + lowerLeftX + " " + lowerLeftY + " " + upperRightX + " " + upperRightY;
	}
}
