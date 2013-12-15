package vu.editor;

import com.google.common.base.Charsets;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

public class Launcher {
	public static void main(String[] args) {
		Terminal terminal = TerminalFacade.createTerminal(System.in, System.out, Charsets.UTF_8);
		terminal.enterPrivateMode();
		terminal.clearScreen();
		TerminalSize screenSize = terminal.getTerminalSize();
		Log.info("ScreenSize: " + screenSize.toString());
		terminal.moveCursor(screenSize.getColumns() - 1,
				screenSize.getRows() - 1);
		terminal.moveCursor(0, 0);
		int iteration = 0;
		while(iteration <=15) {
			Key key = terminal.readInput();
			if(key == null) {
				pause();
			} else {
				iteration++;
				if(!key.isAltPressed() && !key.isCtrlPressed()) {
					terminal.putCharacter(key.getCharacter());
				}
				Log.debug("Key pressed: " + key);
				if(key.getKind().equals(Kind.F5)) {
					break;
				}
			}
		}
		terminal.exitPrivateMode();
		terminal.flush();

	}

	private static void pause() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
