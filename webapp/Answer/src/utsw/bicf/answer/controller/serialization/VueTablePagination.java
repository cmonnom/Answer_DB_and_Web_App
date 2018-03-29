package utsw.bicf.answer.controller.serialization;

import utsw.bicf.answer.controller.serialization.VueTableArgs;

public class VueTablePagination<T> {
	
	private static final String apiUrl = "./getSubjectsAjax?page=";
	long total;
	int per_page;
	int current_page;
	int last_page;
	String next_page_url;
	String prev_page_url;
	int from;
	int to;
	
	public VueTablePagination(VueTableArgs args, Long total) {
		this.total = total;
		this.per_page = args.getPerPage();
		this.from = (args.getPage() - 1) * args.getPerPage() + 1;
		this.to = this.from + args.getPerPage() - 1;
		current_page = args.getPage();
		last_page = (int) Math.ceil((double) total / (double) per_page);
		StringBuilder next_url = new StringBuilder(apiUrl);
		StringBuilder prev_url = new StringBuilder(apiUrl);
		StringBuilder commonArgs = new StringBuilder("&per_page=").append(args.getPerPage())
				.append("&sort=").append(args.getSortArg());
		next_url.append(current_page + 1).append(commonArgs);
		prev_url.append(current_page - 1).append(commonArgs);
		
		next_page_url = next_url.toString();
		prev_page_url = prev_url.toString();
		
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public int getPer_page() {
		return per_page;
	}
	public void setPer_page(int per_page) {
		this.per_page = per_page;
	}
	public int getCurrent_page() {
		return current_page;
	}
	public void setCurrent_page(int current_page) {
		this.current_page = current_page;
	}
	public int getLast_page() {
		return last_page;
	}
	public void setLast_page(int last_page) {
		this.last_page = last_page;
	}
	public String getNext_page_url() {
		return next_page_url;
	}
	public void setNext_page_url(String next_page_url) {
		this.next_page_url = next_page_url;
	}
	public String getPrev_page_url() {
		return prev_page_url;
	}
	public void setPrev_page_url(String prev_page_url) {
		this.prev_page_url = prev_page_url;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
}
