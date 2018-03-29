package utsw.bicf.answer.controller.serialization;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.VueTableArgs;
import utsw.bicf.answer.controller.serialization.VueTablePagination;

/**
 * This utility class can generate the correct data format
 * for the vuetable-2 javascript module.
 * It wraps the given object with the appropriate pagination
 * information.
 * @author Guillaume
 * @param <T>
 *
 */
public class VueTableData<T> {
	
	List<T> data; //the actual data to display with vuetable-2
	VueTablePagination<T> pagination;; //pagination information

	public VueTableData(List<T> data, VueTableArgs args, Long total) {
		this.data = data;
		pagination = new VueTablePagination<T>(args, total);
	}
	
	public String createVueTableDataJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public VueTablePagination<T> getPagination() {
		return pagination;
	}

	public void setPagination(VueTablePagination<T> pagination) {
		this.pagination = pagination;
	}

}
