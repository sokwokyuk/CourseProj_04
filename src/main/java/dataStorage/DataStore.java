package dataStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.Character.UnicodeScript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.thoughtworks.xstream.XStream;

public class DataStore {

	static KeywordCol keycol = new KeywordCol();
	// static RankCol rankcol = new RankCol();
	static WordNoCol wordnocol = new WordNoCol();
	static Map<Integer, Row> RowMap = new HashMap<Integer, Row>();
	Map<Integer, Row> TempRowMap;
	int ExistedRowSize;
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DataStore.class);
	static Map<String, ArrayList<String>> ContainedURLMap = new HashMap<String, ArrayList<String>>();;

	public DataStore() {
		ExistedRowSize = RowMap.size();
		TempRowMap = new HashMap<Integer, Row>();
	}

	public synchronized void Store(String ParsingURL, ArrayList<String> ContainedURLList) {
		TempRowMap.forEach((k, v) -> {
			int rowid = RowMap.size() + 1;
			v.setRowId(rowid);
			RowMap.put(rowid, v);
			StoreToCol(v);
		});
		ContainedURLMap.put(ParsingURL, ContainedURLList);
	}

	public static void print() {
		RowMap.forEach((k, v) -> {
			System.out.printf("Rid(k)-%s: {keyword:\"%s\", URL:\"%s\"}\n", k, v.getKeyword(), v.getFromURL());
			logger.debug("Rowid - {}: {keyword:{}, WordPos:{}, URL:{}}", k, v.getKeyword(), v.getWordNo(),
					v.getFromURL());
		});

		ContainedURLMap.forEach((k, v) -> {
			logger.debug("ContainedURLMap: \"{}\" => Contained({}): {}", k, v.size(), v);
		});
	}

	public static void output() {
		try {
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
			RowList list = new RowList();
			RowMap.forEach((k, v) -> {
				list.add(v);
			});
			OutputStream outputStream = null;
			outputStream = new FileOutputStream(new File(".\\output.json"));
			BufferedWriter bufferedWriter;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

			gson.toJson(list, RowList.class, bufferedWriter);
			bufferedWriter.close();

		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// try {
		// XStream xstream = new XStream();
		// xstream.alias("dataStorage.Row", RowList.class);
		// xstream.alias("dataStorage.RowList", RowList.class);
		// xstream.addImplicitCollection(RowList.class, "list");
		// RowList list = new RowList();
		// RowMap.forEach((k, v) -> {
		// list.add(v);
		// });
		// xstream.toXML(list, new FileWriter(".\\output.xml"));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	public static void input() {
		Gson gson = new Gson();
		try {
			JsonReader reader = new JsonReader(new FileReader(".\\output.json"));
			reader.setLenient(true);
			RowList rList = gson.fromJson(reader, RowList.class);
			System.out.println("Start Reading");
			rList.getRowList().forEach(r -> {
				int rowid = RowMap.size() + 1;
				r.setRowId(rowid);
				RowMap.put(rowid, r);
				StoreToCol(r);
				System.out.println("store: "+rowid);
			});
			System.out.println("Reading Finished.");
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//
		//
		// XStream xstream = new XStream();
		// Class<?>[] classes = new Class[] { dataStorage.Row.class,
		// dataStorage.RowList.class };
		// xstream.allowTypes(classes);
		// File xml = new File(".\\output.xml");
		// System.err.println("try");
		// RowList rList = (RowList) xstream.fromXML(xml);
		// System.err.println(xml);
		// rList.getRowList().forEach(r -> {
		// int rowid = RowMap.size() + 1;
		// r.setRowId(rowid);
		// RowMap.put(rowid, r);
		// StoreToCol(r);
		// });

	}

	public synchronized void addRow(String Keyword, int Rank, int WordNo, String FromURL, String Title) {
		TempRowMap.put(TempRowMap.size() + 1, new Row(TempRowMap.size() + 1, Keyword, WordNo, FromURL, Title));
	}

	public static void StoreToCol(Row r) {
		keycol.addColObj(r.getRowId(), r.getKeyword());
		// rankcol.addColObj(r.getRowId(), r.Rank);
		wordnocol.addColObj(r.getRowId(), r.getWordNo());
	}

	public static synchronized Map<Integer, Row> getRowMap() {
		return RowMap;
	}

	public static synchronized Col getkeycol() {
		return keycol;
	}

	// public static synchronized Col getrankcol() {
	// return rankcol;
	// }

	public static synchronized Col getwordnocol() {
		return wordnocol;
	}

	public static List<Row> SearchByKeywordResult(String keyword) {
		List<Row> result = new ArrayList<Row>();
		SearchByKeyword(keyword).stream().forEach(row -> {
			if (result.stream().noneMatch(r -> Objects.equals(r.getFromURL(), row.getFromURL())))
				result.add(row);
		});
		return result;
	}

	public static List<Row> SearchByPhrasedResult(String keyword) {
		List<Row> result = new ArrayList<Row>();
		SearchByPhrase(keyword).stream().forEach(row -> {
			if (result.stream().noneMatch(r -> Objects.equals(r.getFromURL(), row.getFromURL())))
				result.add(row);
		});
		return result;
	}

	public static List<Row> SearchByKeyword(String keyword) {
		Map<Integer, String> map = keycol.ColObj;
		return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), keyword))
				.map(entry -> RowMap.get(entry.getKey())).collect(Collectors.toList());
	}

	static List<UnicodeScript> TargetScript = Arrays.asList(Character.UnicodeScript.HAN,
			Character.UnicodeScript.HIRAGANA);

	public static boolean containsTargetScript(Character c) {
		return ContainsTargetScript(String.valueOf(c));
	}

	public static boolean ContainsTargetScript(String s) {
		return s.codePoints().anyMatch(codepoint -> TargetScript.contains(Character.UnicodeScript.of(codepoint)));
	}

	public static List<String> PhraseSplitor(String phrase) {
		List<String> keywords = new ArrayList<String>();
		String regex = "[ \u00a0<>\\pP+0-9\"\"\\t\\x0B\\f\\r\\d|\\|\\s+]";
		for (String s : phrase.replaceAll("&nbsp", "").split("\\s+")) {
			if (!s.trim().replaceAll(regex, "").equals("") && !s.contains(regex) && !s.isEmpty()) {
				if (ContainsTargetScript(s)) {
					Character lastChar = null;
					String NonChineseString = "";
					char[] charArray = s.toCharArray();
					for (int i = 0; i < charArray.length; i++) {
						if (!containsTargetScript(charArray[i])) {
							if (containsTargetScript(lastChar))
								NonChineseString = "";
							NonChineseString += charArray[i];
							if (i == charArray.length - 1) {
								keywords.add(NonChineseString);
							}
						} else {
							if (lastChar != null && !containsTargetScript(lastChar)) {
								keywords.add(NonChineseString);
							} else {
								keywords.add(String.valueOf(charArray[i]));
							}
						}
						lastChar = charArray[i];
					}
				} else {
					keywords.add(s);
				}
			}
		}
		return keywords;

	}

	public static List<Row> SearchByPhrase(String phrase) {
		List<Row> Last = new ArrayList<Row>();
		List<Row> Common = new ArrayList<Row>();
		for (String s : PhraseSplitor(phrase)) {
			List<Row> Rows = SearchByKeyword(s);
			if (!Last.isEmpty()) {
				for (Row r : Rows) {
					if (Last.stream()
							.anyMatch(lastrow -> Objects.equals(lastrow.getFromURL(), r.getFromURL())
									&& Objects.equals(lastrow.getFromURL(), r.getFromURL())
									&& Objects.equals(lastrow.getWordNo(), r.getWordNo() - 1))
							&& !Common.stream()
									.anyMatch(commonrow -> Objects.equals(commonrow.getFromURL(), r.getFromURL()))) {
						Common.add(r);
					}
				}
				Rows = Common;
			}
			Last = Rows;
		}
		return Common;
	}
}

class KeywordCol extends Col {
	public KeywordCol() {
		ColObj = new HashMap<Integer, String>();
	}
}

// class RankCol extends Col {
// public RankCol() {
// ColObj = new HashMap<Integer, Integer>();
// }
// }

class WordNoCol extends Col {
	public WordNoCol() {
		ColObj = new HashMap<Integer, Integer>();
	}
}

class Col {
	public String ColName;
	public HashMap ColObj;

	public void addColObj(int rowId, Object value) {
		ColObj.put(rowId, value);
	}

	public HashMap getColObjMap() {
		return ColObj;
	}

	public Object getColObj(int rowId) {
		return ColObj.get(rowId);
	}
}
