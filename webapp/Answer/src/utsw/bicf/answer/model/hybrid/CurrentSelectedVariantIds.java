package utsw.bicf.answer.model.hybrid;

import java.util.Set;

public class CurrentSelectedVariantIds {
	
	Set<String> snpIdsAll;
	Set<String> snpIdsReviewer;
	Set<String> cnvIdsAll;
	Set<String> cnvIdsReviewer;
	Set<String> ftlIdsAll;
	Set<String> ftlIdsReviewer;
	
	public CurrentSelectedVariantIds() {
		super();
	}

	public Set<String> getSnpIdsAll() {
		return snpIdsAll;
	}

	public void setSnpIdsAll(Set<String> snpIdsAll) {
		this.snpIdsAll = snpIdsAll;
	}

	public Set<String> getSnpIdsReviewer() {
		return snpIdsReviewer;
	}

	public void setSnpIdsReviewer(Set<String> snpIdsReviewer) {
		this.snpIdsReviewer = snpIdsReviewer;
	}

	public Set<String> getCnvIdsAll() {
		return cnvIdsAll;
	}

	public void setCnvIdsAll(Set<String> cnvIdsAll) {
		this.cnvIdsAll = cnvIdsAll;
	}

	public Set<String> getCnvIdsReviewer() {
		return cnvIdsReviewer;
	}

	public void setCnvIdsReviewer(Set<String> cnvIdsReviewer) {
		this.cnvIdsReviewer = cnvIdsReviewer;
	}

	public Set<String> getFtlIdsAll() {
		return ftlIdsAll;
	}

	public void setFtlIdsAll(Set<String> ftlIdsAll) {
		this.ftlIdsAll = ftlIdsAll;
	}

	public Set<String> getFtlIdsReviewer() {
		return ftlIdsReviewer;
	}

	public void setFtlIdsReviewer(Set<String> ftlIdsReviewer) {
		this.ftlIdsReviewer = ftlIdsReviewer;
	}

	




}
