package utsw.bicf.answer.model.extmapping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CosmicRawData {
	
	private static final Pattern enstPattern = Pattern.compile("(ENST[0-9]+)");
	private static final Pattern genePattern = Pattern.compile("[(]([A-Z]+[0-9]*)[)]");
	
	String fusionId;
	String translocationName;
	String fiveChr;
	String fiveStart;
	String fiveEnd;
	String threeChr;
	String threeStart;
	String threeEnd;
	
	String fiveENST;
	String threeENST;
	String fiveGene;
	String threeGene;
	

	public CosmicRawData() {
		super();
	}


	public String getFusionId() {
		return fusionId;
	}


	public void setFusionId(String fusionId) {
		this.fusionId = fusionId;
	}


	public String getTranslocationName() {
		return translocationName;
	}


	public void setTranslocationName(String translocationName) {
		this.translocationName = translocationName;
	}


	public String getFiveChr() {
		return fiveChr;
	}


	public void setFiveChr(String fiveChr) {
		this.fiveChr = fiveChr;
	}


	public String getFiveStart() {
		return fiveStart;
	}


	public void setFiveStart(String fiveStart) {
		this.fiveStart = fiveStart;
	}


	public String getFiveEnd() {
		return fiveEnd;
	}


	public void setFiveEnd(String fiveEnd) {
		this.fiveEnd = fiveEnd;
	}


	public String getThreeChr() {
		return threeChr;
	}


	public void setThreeChr(String threeChr) {
		this.threeChr = threeChr;
	}


	public String getThreeStart() {
		return threeStart;
	}


	public void setThreeStart(String threeStart) {
		this.threeStart = threeStart;
	}


	public String getThreeEnd() {
		return threeEnd;
	}


	public void setThreeEnd(String threeEnd) {
		this.threeEnd = threeEnd;
	}


	public String getFiveENST() {
		return fiveENST;
	}


	public void setFiveENST(String fiveENST) {
		this.fiveENST = fiveENST;
	}


	public String getThreeENST() {
		return threeENST;
	}


	public void setThreeENST(String threeENST) {
		this.threeENST = threeENST;
	}
	
	public boolean isValid() {
		return fusionId != null && fusionId.length() > 0
				&& translocationName != null && translocationName.length() > 0
				&& fiveChr != null && fiveChr.length() > 0
				&& fiveStart != null && fiveStart.length() > 0
				&& fiveEnd != null && fiveEnd.length() > 0
				&& threeChr != null && threeChr.length() > 0
				&& threeStart != null && threeStart.length() > 0
				&& threeEnd != null && threeEnd.length() > 0;
	}
	
	public void extractENSTsGenes() {
		Matcher matcher = enstPattern.matcher(translocationName);
		int counter = 0;
		while(matcher.find()) {
			counter++;
			if (counter == 1) {
				fiveENST = matcher.group(1);
			}
			else if (counter == 2) {
				threeENST = matcher.group(1);
			}
			else {
				fiveENST = null;
				threeENST = null;
			}
		}
		matcher = genePattern.matcher(translocationName);
		counter = 0;
		while(matcher.find()) {
			counter++;
			if (counter == 1) {
				fiveGene = matcher.group(1);
			}
			else if (counter == 2) {
				threeGene = matcher.group(1);
			}
			else {
				fiveGene = null;
				threeGene = null;
			}
		}
	}


	public String getFiveGene() {
		return fiveGene;
	}


	public void setFiveGene(String fiveGene) {
		this.fiveGene = fiveGene;
	}


	public String getThreeGene() {
		return threeGene;
	}


	public void setThreeGene(String threeGene) {
		this.threeGene = threeGene;
	}

	public String createBashLineFive(boolean firstPass) {
		StringBuilder sb = new StringBuilder();
		sb.append("'chr").append(this.getFiveChr()).append(":g.")
		.append(this.getFiveStart()).append("_").append(this.getFiveEnd())
		.append("' | grep -P '");
		if (firstPass) {
			sb.append("[\\t][,]*");
		}
		sb.append(this.getFiveENST()).append("'");
		createAwkPart(sb, "five");
		return sb.toString();
	}
	
	public String createBashLineThree(boolean firstPass) {
		StringBuilder sb = new StringBuilder();
		sb.append("'chr").append(this.getThreeChr()).append(":g.")
		.append(this.getThreeStart()).append("_").append(this.getThreeEnd())
		.append("' | grep -P '");
		if (firstPass) {
			sb.append("[\\t][,]*");
		}
		sb.append(this.getThreeENST()).append("'");
		createAwkPart(sb, "three");
		return sb.toString();
	}
	
	private void createAwkPart(StringBuilder sb, String fiveOrThree) {
		sb.append(" | awk '{print $7 $8 \"\\t").append(fusionId).append("\" \"\\t").append(fiveOrThree).append("\"}'\n"); 
		
	}
	
}
