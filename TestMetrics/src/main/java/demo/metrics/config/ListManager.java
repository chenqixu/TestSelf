package demo.metrics.config;

import java.util.ArrayList;
import java.util.List;

import com.codahale.metrics.MetricRegistry;

public class ListManager {
	private List<Integer> list = new ArrayList<Integer>();
	private MetricRegistry metrics;
	
	public ListManager(MetricRegistry metrics) {
		this.metrics = metrics;
	}
	
	public List<Integer> getList() {
		return list;
	}
}
