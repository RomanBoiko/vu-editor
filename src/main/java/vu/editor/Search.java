package vu.editor;

import static java.lang.String.format;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

class Search {
	private String searchText = "";
	private File searchRoot = new File(".");
	private List<SearchResult> searchResults = new LinkedList<SearchResult>();
	private int lastSearchViewPosition = 0;
	
	class SearchResult {
		public final File file;
		public final int rowNumber;
		public final String line;
		public final int positionInFile;
		SearchResult(File file, int rowNumber, String line, int positionInFile) {
			this.file = file;
			this.rowNumber = rowNumber;
			this.line = line;
			this.positionInFile = positionInFile;
		}
	}

	String searchText() {
		return searchText;
	}
	String searchRoot() {
		return searchRoot.getAbsolutePath();
	}

	void search(String newTextToSearch, File newRootFile) {
		if (!sameSearchAgain(newTextToSearch, newRootFile)) {
			this.lastSearchViewPosition = 0;
			this.searchText = newTextToSearch;
			this.searchRoot = newRootFile;
			this.searchResults.clear();
			startSearch(this.searchRoot);
		}
	}
	private boolean sameSearchAgain(String textToSearch, File rootFile) {
		return this.searchText.equals(textToSearch)
				&& this.searchRoot.getAbsolutePath().equals(rootFile.getAbsolutePath());
	}

	private void startSearch(File root) {
		if (root.isFile()) {
			searchInFile(root);
		} else {
			for (File child : root.listFiles()) {
				startSearch(child);
			}
		}
	}

	private void searchInFile(File file) {
		String originalFileText = Buffer.fileText(file);
		String fileText = originalFileText.toLowerCase();
		String toSearch = searchText.toLowerCase();
		int startSearchPosition = 0;
		while (true) {
			int position = fileText.indexOf(toSearch, startSearchPosition);
			if(position < 0) {
				return;
			} else {
				startSearchPosition = position + 1;
				String line = Texts.lineContainingPosition(originalFileText, position);
				int rowNumber = Texts.rowNumber(fileText, position);
				searchResults.add(new SearchResult(file, rowNumber, line, position));
			}
		}
	}

	SearchResult getSearchResult(int row) {
		return searchResults.get(row - 1);
	}
	String asString() {
		StringBuffer result = new StringBuffer();
		int rootFolderPathLength = searchRoot().length();
		for (SearchResult searchResult: searchResults) {
			result.append(
					format("| %s | %d | %s",
							searchResult.file.getAbsolutePath().substring(rootFolderPathLength),
							searchResult.rowNumber,
							searchResult.line))
			.append(Texts.LINE_SEPARATOR);
		}
		return result.toString().trim();
	}
	int lastSearchViewPosition() {
		return lastSearchViewPosition;
	}
	void updateLatSearchViewPosition(int position) {
		this.lastSearchViewPosition = position;
	}
}
