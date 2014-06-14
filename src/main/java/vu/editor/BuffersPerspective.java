package vu.editor;

class BuffersPerspective extends SelectableReadOnlyPerspective {

	public BuffersPerspective(Driver driver) {
		super(driver);
	}

	@Override protected void actionOnSelected() {
		driver.loadBufferIntoEditor();
	}
	@Override protected String mainText() {
		return driver.buffersAsString();
	}

	@Override protected String title() {
		return "Buffers";
	}

	@Override protected String statusBarText() {
		return title();
	}
}
