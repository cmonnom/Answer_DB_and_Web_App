package utsw.bicf.answer.controller.serialization;

import java.util.Map;
import java.util.TreeMap;

/**
 * All arguments that need to be filtered and rebuild for each
 * Ajax call of a vuetable-2 grid
 * @author Guillaume
 *
 */
public class VueTableArgs {
	
	int page;
	int perPage;
	String sortArg;
	// Map could hold multiple sorting parameters
	//keep them in argument's order
	Map<String, Boolean> sortingOptions = new TreeMap<String, Boolean>();

	public VueTableArgs(int page, int per_page, String sortArg) {
		this.page = page;
		this.perPage = per_page;
		this.sortArg = sortArg;
		//sortArg is of format: sort=<columnName1>|<direction1>,<columnName2>|<direction2>
		//so we need to parse first by comma to separate each tuple then by "|"
		String[] sortParams = sortArg.split(",");
		for (int i = 0; i < sortParams.length; i++) {
			String[] sortParam = sortParams[i].split("\\|"); //this param is formatted with sortingColumn|sortingDirection
			sortingOptions.put(sortParam[0], "asc".equals(sortParam[1]));
		}
		
	}

	public int getPage() {
		return page;
	}

	public String getSortArg() {
		return sortArg;
	}

	public int getPerPage() {
		return perPage;
	}

	public Map<String, Boolean> getSortingOptions() {
		return sortingOptions;
	}
	
	

}
