package dataStorage;

import com.thoughtworks.xstream.annotations.XStreamAlias;


public class Row {

	private int RowId;
	private String Keyword;
	// private int Rank;
	private int WordNo;
	private String FromURL;
	private String Title;
	// private ArrayList<Object> RowContent;

	public Row(int RowId, String Keyword, int WordNo, String FromURL, String Title) {
		// RowContent = new ArrayList<Object>();
		this.RowId = RowId;
		this.Keyword = Keyword;
		// this.Rank = Rank;
		this.WordNo = WordNo;
		this.FromURL = FromURL;
		this.Title = Title;
	}

	public void setRowId(int RowId) {
		this.RowId = RowId;
	}

	public int getRowId() {
		return this.RowId;
	}

	public void setKeyword(String Keyword) {
		this.Keyword = Keyword;
	}

	public String getKeyword() {
		return this.Keyword;
	}

	public void setWordNo(int WordNo) {
		this.WordNo = WordNo;
	}

	public int getWordNo() {
		return this.WordNo;
	}

	public void setFromURL(String FromURL) {
		this.FromURL = FromURL;
	};

	public String getFromURL() {
		return this.FromURL;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}

	public String getTitle() {
		return this.Title;
	}
}
