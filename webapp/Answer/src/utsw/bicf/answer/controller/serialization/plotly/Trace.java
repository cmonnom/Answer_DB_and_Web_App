package utsw.bicf.answer.controller.serialization.plotly;

import java.util.ArrayList;
import java.util.List;

public class Trace {
	
	List<Object> x = new ArrayList<Object>();
	List<Object> y = new ArrayList<Object>();
	List<Object> start = new ArrayList<Object>();
	List<Object> end = new ArrayList<Object>();
	List<String> labels = new ArrayList<String>();
	String name;
	
	public Trace(List<Object> x, List<Object> y, List<String> labels) {
		super();
		this.x = x;
		this.y = y;
		this.labels = labels;
	}

	public Trace() {
		super();
	}
	
	public void addX(Object o) {
		x.add(o);
	}
	
	public void addY(Object o) {
		y.add(o);
	}
	
	public void addStart(Object o) {
		start.add(o);
	}
	
	public void addEnd(Object o) {
		end.add(o);
	}
	
	public void addLabel(String l) {
		labels.add(l);
	}
	
	public void removeLastStart() {
		if (!start.isEmpty())
			start.remove(start.size() -1);
	}
	
	public void removeLastEnd() {
		if (!end.isEmpty())
			end.remove(end.size() -1);
	}

	public List<Object> getX() {
		return x;
	}

	public void setX(List<Object> x) {
		this.x = x;
	}

	public List<Object> getY() {
		return y;
	}

	public void setY(List<Object> y) {
		this.y = y;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<Object> getStart() {
		return start;
	}

	public void setStart(List<Object> start) {
		this.start = start;
	}

	public List<Object> getEnd() {
		return end;
	}

	public void setEnd(List<Object> end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
