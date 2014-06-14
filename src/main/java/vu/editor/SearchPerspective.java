package vu.editor;


class SearchPerspective extends SelectableReadOnlyPerspective {
	private final Search search;

	public SearchPerspective(Driver driver, Search search) {
		super(driver);
		this.search = search;
	}

	@Override protected void actionOnSelected() {
		driver.loadSearchResultIntoEditor();
	}
	@Override protected String mainText() {
		return search.asString();
	}

	@Override protected String title() {
		return "Search results";
	}

	@Override protected String statusBarText() {
		return String.format("Search results in %s for '%s'", search.searchRoot(), search.searchText());
	}

	@Override protected int cursorPositionOnLoad() {
		return driver.lastSearchViewPosition();
	}

	@Override void actionOnExitFromPerspective() {
		driver.updateLatSearchViewPosition(driver.selectionStart());
	}
}
