package utsw.bicf.answer.reporting.finalreport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * Helper class. Instead of writing a String
 * we can use writeString to get the position of words in the document
 * Build a list of the lookupWord positions to be later retrieve
 * and add annotations (links) to the document at the proper locations
 * @author Guillaume
 *
 */
public class HyperLinkReplacer extends PDFTextStripper {
	
	String lookupWord;
	List<LinkCoordinates> linkCoords = new ArrayList<LinkCoordinates>();
	Link currentLink;

	public HyperLinkReplacer() throws IOException {
		super();
	}
	
	public HyperLinkReplacer(Link link, String lookupWord) throws IOException {
		super();
		this.currentLink = link;
		this.lookupWord = lookupWord;
	}

	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
		int startIndex = text.indexOf(lookupWord);
		if (startIndex > -1) {
//			System.out.println("found");
			TextPosition startTextPos = textPositions.get(startIndex);
			TextPosition endTextPos = textPositions.get(startIndex + lookupWord.length() - 1);
			float lowerLeftX = startTextPos.getX();
			float lowerLeftY = endTextPos.getY() + 1; //leave some space under the text
			float upperRightX = endTextPos.getEndX();
			float upperRightY = startTextPos.getY() - startTextPos.getHeight(); 
			int currentPageNb = getCurrentPageNo() - 1;
			LinkCoordinates coords = new LinkCoordinates(lowerLeftX, lowerLeftY, upperRightX, upperRightY, currentPageNb);
			if (!linkCoords.contains(coords)) {
				linkCoords.add(coords);
			}
			
//			System.out.println(startTextPos + " " + startTextPos.getX() + " " + startTextPos.getY());
//			System.out.println(endTextPos + " " + endTextPos.getEndX()  + " " +  (startTextPos.getY() + endTextPos.getHeight()));
		}
	}

	public String getLookupWord() {
		return lookupWord;
	}

	public void setLookupWord(String lookupWord) {
		this.lookupWord = lookupWord;
	}

	public Link getCurrentLink() {
		return currentLink;
	}

	public void setCurrentLink(Link currentLink) {
		this.currentLink = currentLink;
	}

	public List<LinkCoordinates> getLinkCoords() {
		return linkCoords;
	}


	

}
