package edu.hkbu.comp4047;

import java.net.MalformedURLException;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dataStorage.DataStore;
import webCrawler.WebCrawler;

@SpringBootApplication
public class SearchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineApplication.class, args);
		Scanner sc = new Scanner(System.in);
		boolean start = false;
		while (!start) {
			System.err.println("Press 1 to Run WebCrawler, 2 to Input Data from File");
			int i = sc.nextInt();
			start = true;
			if (i == 1) {
				WebCrawler w = new WebCrawler();
				try {
					w.initialization();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				w.run();
			} else if (i == 2) {
				DataStore ds = new DataStore();
				ds.input();
			} else {
				start = false;
			}
		}
	}
}
