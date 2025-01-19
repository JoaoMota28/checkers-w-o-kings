import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;

public class View {
	Checkers2 model;
	Board board;
	Position[] piece = new Position[1];

	View(Checkers2 model) {
		this.model = model;
		board = new Board("" + (model.isWhiteTurn() ? "White":"Black") + "'s turn.", model.getSize(), model.getSize(), 50);
		board.setBackgroundProvider(this::background);
		board.setIconProvider(this::icon);
		board.addMouseListener(this::click);
		board.addAction("Random", this::random);
		board.addAction("New", this::newGame);
		board.addAction("Save", this::saveGame);
		board.addAction("Load", this::loadGame);
		board.addAction("Scoreboard", this::scoreboard);
	}
	
	Color background(int line, int col) {
		if (!model.gameOver() && piece[0] != null && piece[0].line() == line && piece[0].col() == col)
			return StandardColor.YELLOW;
		else if ((line + col) % 2 == 0)
			return StandardColor.WHITE;
		else
			return StandardColor.BLACK;
	}
	
	String icon(int line, int col) {
		if (model.isWhite(line, col))
			return "white.png";
		else if (model.isBlack(line, col))
			return "black.png";
		else
			return null;
	}
	
	void changeTitle() {
		if (model.gameOver()) {
			if (model.winner() > 0) {
				board.setTitle("White wins!");
				String name = board.promptText("Winning player name (3 letters):");
				while(name.length() != 3) {
					board.showMessage("The name has to contain exactly 3 letters.");
					name = board.promptText("Player name (3 letters):");
				}
				try {
					model.updateScoreboard(name);
				} catch (FileNotFoundException e) {
					board.showMessage("Scoreboard not found.");
				}
				board.showMessage("White wins!");
			}
			else if (model.winner() < 0) {
				board.setTitle("Black wins!");
				String name = board.promptText("Winning player name (3 letters):");
				while(name.length() != 3) {
					board.showMessage("The name has to contain exactly 3 letters.");
					name = board.promptText("Player name (3 letters):");
				}
				try {
					model.updateScoreboard(name);
				} catch (FileNotFoundException e) {
					board.showMessage("Scoreboard not found.");
				}
				board.showMessage("Black wins!");
			}
			else {
				board.setTitle("It's a tie!");
				board.showMessage("It's a tie!");
			}
		}
		else {
			board.setTitle("" + (model.isWhiteTurn() ? "White":"Black") + "'s turn.");
		}
	}
	
	void click(int line, int col) {
		Position selected = new Position(line, col);
		if (piece[0] == null && !model.gameOver() && (model.isWhiteTurn() && model.isWhite(line, col) || !model.isWhiteTurn() && model.isBlack(line, col)))
			piece[0] = selected;
		else if (piece[0] != null) {
			model.play(piece[0], selected);
			piece[0] = null;
			changeTitle();
		}
	}

	void random() {
		if(model.randomPlay())
			changeTitle();
	}

	void start() {
		board.open();
		changeTitle();
	}
	
	void newGame() {
		int choice = board.promptInt("Write 1 to create a new standard game, or 2 to edit the board size and number of pieces:");
		if (choice == 1) {
			Checkers2 newModel = new Checkers2();
			View gui = new View(newModel);
			gui.start();
		} else if (choice == 2) {
			int	size = board.promptInt("Choose the size of your board:");
			int pieces = board.promptInt("Choose the number of pieces per player:");
			if (size > 1 && size < 17 && pieces > 0 && pieces <= size * size / 4) {
				Checkers2 newModel = new Checkers2(size, pieces);
				View gui = new View(newModel);
				gui.start();
			}
			else
				board.showMessage("Invalid size / number of pieces. try again.");
		} else {
			board.showMessage("Invalid option. Try again.");
		}
	}
	
	void saveGame() {
		String fileName;
		if (model.getFile() != null) {
			int choice = board.promptInt("Write 1 to save the game to the file it was loaded from, or 2 to create a new save file:");
			if (choice == 1)
				fileName = model.getFile();
			else if (choice == 2)
				fileName = board.promptText("Choose the name of your new save file:");
			else {
				board.showMessage("Invalid option. Game not saved.");
				fileName = null;
			}
		}
		else
			fileName = board.promptText("Choose the name of your new save file:");
		try {
			model.saveFile(fileName);
		} catch (FileNotFoundException e) {
			board.showMessage("Error writing on file.");
		} catch (NullPointerException f) {
			return ;
		}
	}
	
	void loadGame() {
		String fileName = board.promptText("Choose the name of the file you want to load from:");
		try {
			Checkers2 newModel = new Checkers2(fileName);
			View gui = new View(newModel);
			gui.start();
		} catch (FileNotFoundException e) {
			board.showMessage("No save file exists.");
		} catch (NoSuchElementException g) {
			board.showMessage("Error reading from file.");
		} catch (NullPointerException f) {
			return ;
		}
	}
	
	void scoreboard() {
		try {
			board.showMessage(model.printScoreboard());
		} catch (FileNotFoundException e) {
			board.showMessage("Scoreboard not found.");
		}
	}

	public static void main(String[] args) {
		View gui = new View(new Checkers2());
		gui.start();
	}
}