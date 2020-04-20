package webCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import dataStorage.DataStore;

public class WebCrawler {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(WebCrawler.class);
	private final String initialURL = "http://www.hkbu.edu.hk/tch/main/index.jsp";
	private static int ProcessedSite = 0;
	private final int ProcessURLPoolSize = 100;
	private final int MaxUnprocessedQueueSize = 10;

	public void initialization() throws MalformedURLException {
		URLQueue.PushUnProcessedURL(initialURL);
	}

	List<Thread> ParserThreadList = new ArrayList<Thread>();

	public void run() {
		try {
			Thread ParserThread = null;
			while (URLQueue.getProcessedURLSize() < ProcessURLPoolSize) {
				while (URLQueue.getProcessedURLSize() < ProcessURLPoolSize) {
					if (URLQueue.getUnprocessedURLSize() > 0) {
						String pu = URLQueue.PollUnProcessedURL();
						System.out.println("Start parse URL: " + pu);
						ParserThread = new Thread(new HtmlParser(new URL(pu), MaxUnprocessedQueueSize));
						ParserThreadList.add(ParserThread);
						ParserThread.start();
						ProcessedSite++;
					}
				}
				try {
					for (Thread t : ParserThreadList)
						t.join();
				} catch (InterruptedException e) {
					logger.debug(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

			// DataStore.print();
			DataStore.output();
			
			
			
			logger.debug("Final visited url: " + ProcessedSite);
			logger.debug("[" + URLQueue.getProcessedURLSize() + "] ProcessedURL: " + URLQueue.getProcessedURL());
			logger.debug("[" + URLQueue.getUnprocessedURLSize() + "] UnprocessedURL: " + URLQueue.getUnprocessedURL());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		WebCrawler w = new WebCrawler();
		try {
			w.initialization();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		w.run();
	}

}