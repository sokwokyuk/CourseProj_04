package webCrawler;

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.lang.Character.UnicodeScript;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import dataStorage.DataStore;

public class HtmlParser implements Runnable {

	private URL ParsingURL;
	private String TitleText;
	private PriorityQueue<String> ProcessedURL = URLQueue.getProcessedURL();
	private PriorityQueue<String> UnprocessedURL = URLQueue.getUnprocessedURL();
	DataStore ds;
	private int MaxUnprocessedQueueSize;
	private final org.slf4j.Logger logger;

	public HtmlParser(URL ParsingURL, int MaxUnprocessedQueueSize) {
		this.ParsingURL = ParsingURL;
		this.MaxUnprocessedQueueSize = MaxUnprocessedQueueSize;
		this.logger = LoggerFactory.getLogger(HtmlParser.class);
		addToProcessedURL(this.ParsingURL.toString());
	}

	public void addToProcessedURL(String url) {
		URLQueue.PushProcessedURL(url);
	}

	public void addToUnProcessedURL(ArrayList<String> linksList) {
		for (String l : linksList) {
			if (UnprocessedURL.size() >= MaxUnprocessedQueueSize) {
				break;
			} else if (!URLQueue.ProcessedURLisContain(l) && !URLQueue.UnprocessedURLisContain(l))
				URLQueue.PushUnProcessedURL(l);
		}
	}

	@Override
	public void run() {
		Parser(ParsingURL);

	}

	public void Parser(URL url) {
		try {
			Document doc = Jsoup.parse(url, 50000);
			// System.out.println(doc);
			ds = new DataStore();

			// Get title
			Elements WebTitles = doc.select("title");
			for (Element t : WebTitles) {
				TitleText = t.text();
			}

			// Get words
			Elements aTagWordsString = doc.select("a");
			List<Element> Line = aTagWordsString.stream()
					.filter(line -> !line.text().replaceAll("[ \\t\\n\\x0B\\f\\r\\d|\\|]", "").equals(""))
					.collect(Collectors.toList());
			KeywordsSeparator(Line);

			// Get URLs
			Elements Links = doc.select("a");
			ArrayList<String> LinksList = Links.stream().filter(l -> !l.absUrl("href").isEmpty())
					.map(l -> l.absUrl("href")).collect(Collectors.toCollection(ArrayList::new));
			addToUnProcessedURL(LinksList);

			ds.Store(ParsingURL.toString(), LinksList);

		} catch (UnsupportedMimeTypeException ex) {
			logger.debug(ex.getLocalizedMessage() + "\t{}", ParsingURL.toString());
			System.err.println("Remove Errorn URL: " + ProcessedURL.remove(ParsingURL.toString()));
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(e.getLocalizedMessage() + "\t{}", ParsingURL.toString());
			System.err.println("Remove Errorn URL: " + ProcessedURL.remove(ParsingURL.toString()));
		}
	}

	public void StoreKeywordRow(String key, int WordPos) {
		ds.addRow(key, 1, WordPos, ParsingURL.toString(), TitleText);
	}

	// separate keywords
	public void KeywordsSeparator(List<Element> Lines) {
		AtomicInteger counter = new AtomicInteger(0);
		String regex = "[ \u00a0<>\\pP+0-9\"\"\\t\\x0B\\f\\r\\d|\\|\\s+]";
		Lines.forEach(line -> {
			// splite by space and special characters
			for (String s : line.text().replaceAll("&nbsp", "").split(regex)) {
				// if s is not space only
				if (!s.trim().replaceAll(regex, "").equals("") && !s.isEmpty()) {
					// If contain Chinese/other character, split dividual character
					if (ContainsTargetScript(s)) {
						Character lastChar = null;
						String NonChineseString = "";
						char[] charArray = s.toCharArray();
						for (int i = 0; i < charArray.length; i++) {
							// If contain not Chinese Character, store in notChinese
							if (!containsTargetScript(charArray[i])) {
								// if Last Character is chinese, clear list
								if (containsTargetScript(lastChar))
									NonChineseString = "";
								// Store notChinese char
								NonChineseString += charArray[i];
								// if(大@in{s}) this char != chinese && is last of the char array
								if (i == charArray.length - 1) {
									StoreKeywordRow(NonChineseString, counter.incrementAndGet());
								}
							} else {
								// if (大@ins{大}) this char == chinese && not Fist Char && last char != chinese
								if (lastChar != null && !containsTargetScript(lastChar)) {
									StoreKeywordRow(NonChineseString, counter.incrementAndGet());
								} else {
									// Is chinese, directly print out
									StoreKeywordRow(String.valueOf(charArray[i]), counter.incrementAndGet());
								}
							}
							lastChar = charArray[i];
						}
					} else {
						// Not Contain Chinese, print directly
						StoreKeywordRow(s, counter.incrementAndGet());
					}
				}
			}
		});
	}

	List<UnicodeScript> TargetScript = Arrays.asList(Character.UnicodeScript.HAN, Character.UnicodeScript.HIRAGANA);

	public boolean containsTargetScript(Character c) {
		return ContainsTargetScript(String.valueOf(c));
	}

	public boolean ContainsTargetScript(String s) {
		return s.codePoints().anyMatch(codepoint -> TargetScript.contains(Character.UnicodeScript.of(codepoint)));
	}

}