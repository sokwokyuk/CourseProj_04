package dataStorage;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class RowList {
	private List<Row> list;

	public RowList() {
		list = new ArrayList<Row>();
	}

	public void add(Row r) {
		list.add(r);
	}

	public List<Row> getRowList() {
		return this.list;
	}


}
