package utsw.bicf.answer.model.hybrid;

public class JSFile {
	
	String name;
	boolean neededByCurrentPage;
	
	public JSFile() {
		
	}

	public JSFile(String name, boolean neededByCurrentPage) {
		super();
		this.name = name;
		this.neededByCurrentPage = neededByCurrentPage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeededByCurrentPage() {
		return neededByCurrentPage;
	}

	public void setNeededByCurrentPage(boolean neededByCurrentPage) {
		this.neededByCurrentPage = neededByCurrentPage;
	}
	



}
