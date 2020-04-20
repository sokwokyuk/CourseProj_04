package webCrawler;

import java.util.PriorityQueue;

public class URLQueue {

	private static final PriorityQueue<String> ProcessedURL = new PriorityQueue<String>();
	private static final PriorityQueue<String> UnprocessedURL = new PriorityQueue<String>();

	public synchronized static PriorityQueue<String> getProcessedURL() {
		return ProcessedURL;
	}

	public synchronized static PriorityQueue<String> getUnprocessedURL() {
		return UnprocessedURL;
	}

	public synchronized static boolean ProcessedURLisContain(String url) {
		return ProcessedURL.contains(url);
	}
 
	public synchronized static boolean UnprocessedURLisContain(String url) {
		return UnprocessedURL.contains(url);
	}

	public synchronized static int getProcessedURLSize() {
		return ProcessedURL.size();
	}

	public synchronized static int getUnprocessedURLSize() {
		return UnprocessedURL.size();
	}

	public synchronized static void PushProcessedURL(String url) {
		ProcessedURL.add(url);
	}

	public synchronized static void PushUnProcessedURL(String url) {
		UnprocessedURL.add(url);
	}

	public synchronized void RemoveErrorURL(String url) {
		ProcessedURL.remove(url);
	}

	public synchronized static String PollProcessedURL() {

		return ProcessedURL.poll();
	}

	public synchronized static String PollUnProcessedURL() {

		return UnprocessedURL.poll();
	}

}
