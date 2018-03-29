package utsw.bicf.answer.controller.serialization.vuetify;
//package utsw.bicf.answer.controller.serialization.vuetify;
//
//import java.util.List;
//import java.util.Locale;
//import java.util.stream.Collectors;
//
//import utsw.bicf.answer.controller.serialization.SearchItemString;
//import utsw.bicf.answer.controller.serialization.SearchItems;
//
//public class ChromItems extends SearchItems {
//	
//	public ChromItems() {
//	}
//	
//	public ChromItems(List<String> chroms) {
//		super();
//		this.items = chroms.stream()
//				.map(chrom -> createSearchItem(chrom))
//				.sorted()
//				.collect(Collectors.toList());
//	}
//	
//	private SearchItemString createSearchItem(String chromName) {
//		String chromNbString = chromName.replace("chr", "");
//		if (chromNbString.length() == 1) { //could be a single digit
//			try {
//				Integer chrNb = Integer.parseInt(chromNbString);
//				String formattedChromName = "chr" + String.format(Locale.US, "%02d", chrNb);
//				return new SearchItemString(formattedChromName, chromName);
//			} catch (NumberFormatException e) { //not a number, keep as is.
//				return new SearchItemString(chromName, chromName);
//			}
//		}
//		return new SearchItemString(chromName, chromName);
//	}
//
//
//}
//
