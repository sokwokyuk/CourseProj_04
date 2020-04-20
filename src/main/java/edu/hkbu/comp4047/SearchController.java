package edu.hkbu.comp4047;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dataStorage.DataStore;
import dataStorage.Row;

@Controller
public class SearchController {
	@RequestMapping("/keyword")
	public String SearchKeyResult(@RequestParam String keywords, Model model) {
		List<Row> Rows = DataStore.SearchByKeywordResult(keywords);
		model.addAttribute("Rows", Rows);
		model.addAttribute("query", keywords);
		return "result";
	}

	@RequestMapping("/keyphrase")
	public String SearchPhraseResult(@RequestParam String keyphrase, Model model) {
		List<Row> Rows = DataStore.SearchByPhrasedResult(keyphrase);
		model.addAttribute("Rows", Rows);
		model.addAttribute("query", keyphrase);
		return "result";
	}

}
