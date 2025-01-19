import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

record Position(int line, int col) {
	
}

class Checkers2 {
	private static final int EMPTY = 0;
	private static final int WHITE = 1;
	private static final int BLACK = 2;
	private int[][] grid;
	private boolean whiteTurn;
	private int pieces;
	private String file;
	
	Checkers2() {
		this.grid = new int[8][8];
		this.pieces = 12;
		this.whiteTurn = true;
		this.file = null;
		populateBoard();
	}
	
	Checkers2(int size, int pieces) {
		assert size > 1;
		assert (pieces > 0 && pieces <= ((size * size) / 4));
		this.grid = new int[size][size];
		this.pieces = pieces;
		this.whiteTurn = true;
		this.file = null;
		populateBoard();
	}
	
	Checkers2(String fileName) throws FileNotFoundException, NoSuchElementException {
		Scanner scanner = new Scanner(new File(fileName));
		this.whiteTurn = scanner.nextBoolean();
		int size = scanner.nextInt();
		this.grid = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.grid[i][j] = scanner.nextInt();
			}
		}
		this.file = fileName;
		scanner.close();
	}
	
	void populateBoard() {
		int black = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if ((i + j) % 2 != 0) {
					grid[i][j] = BLACK;
					black++;
					if (black == pieces)
						break;
				}
			}
			if (black == pieces)
				break;
		}
		
		int white = 0;
		for (int i = grid.length - 1; i >= 0; i--) {
			for (int j = grid.length - 1; j >= 0; j--) {
				if ((i + j) % 2 != 0) {
					grid[i][j] = WHITE;
					white++;
					if (white == pieces)
						break;
				}
			}
			if (white == pieces)
				break;
		}
	}
	
	void saveFile(String fileName) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new File(fileName));
		writer.println("" + this.whiteTurn);
		writer.println("" + this.getSize());
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				writer.println("" + grid[i][j]);
			}
		}
		this.file = fileName;
		writer.close();
	}
	
	void updateScoreboard(String name) throws FileNotFoundException{
		int counter = 0;
		try {
			boolean winner_write = true;
			Scanner scanner_count = new Scanner(new File("Scoreboard.txt"));
			while (scanner_count.hasNextLine()) {
				counter++;
				scanner_count.nextLine();
			}
			scanner_count.close();
			String[] scores = new String[counter];
			Scanner scanner = new Scanner(new File("Scoreboard.txt"));
			for (int i = 0; i < scores.length; i++)
				scores[i] = scanner.nextLine();
			scanner.close();
			PrintWriter writer = new PrintWriter(new File("Scoreboard.txt"));
			int j = 0;
			for (int i = 0; i < scores.length; i++) {
				if (i < 2) {
					writer.println(scores[i]);
					j++;
				}
				else if (scores[j].length() > 1 && (((scores[j].charAt(13) - '0') * 10 + (scores[j].charAt(14) - '0')) > Math.abs(winner()) || !winner_write) && j < 11) {
					writer.println(scores[j]);
					j++;
				}
				else if (j < 11 && winner_write) {
					String new_line = "  " + name + "   |   " + (winner() > 9 ? "0" + Math.abs(winner()) : "00" + Math.abs(winner())) + "   ";
					writer.println(new_line);
					winner_write = false;
				}
			}
			if (j < 11 && winner_write) {
				String new_line = "  " + name + "   |   " + (winner() > 9 ? "0" + Math.abs(winner()) : "00" + Math.abs(winner())) + "   ";
				writer.println(new_line);
			} 
			else if (j < 11)
				writer.println(scores[j]);
			writer.close();
		} catch (FileNotFoundException e) {
			PrintWriter writer = new PrintWriter(new File("Scoreboard.txt"));
			writer.println("  NAME  |  SCORE  ");
			writer.println("------------------");
			String new_line = "  " + name + "   |   " + (winner() > 9 ? "0" + Math.abs(winner()) : "00" + Math.abs(winner())) + "   ";
			writer.println(new_line);
			writer.close();
		}

	}
	
	String printScoreboard() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("Scoreboard.txt"));
		String scoreboard = "";
		while (scanner.hasNextLine()) {
			scoreboard += scanner.nextLine();
			scoreboard += "\n";
		}
		scanner.close();
		return scoreboard;
	}
	
	int getSize() {
		return this.grid.length;
	}
	
	String getFile() {
		return this.file;
	}
	
	boolean isWhiteTurn() {
		return whiteTurn;
	}
	
	boolean isValid(int line, int col) {
		return line >= 0 && line < this.grid.length && col >= 0 && col < this.grid.length;
	}
	
	boolean isWhite(int line, int col) {
		assert isValid(line, col);
		return grid[line][col] == WHITE;
	}
	
	boolean isBlack(int line, int col) {
		assert isValid(line, col);
		return grid[line][col] == BLACK;
	}
	
	boolean isEmpty(int line, int col) {
		assert isValid(line, col);
		return grid[line][col] == EMPTY;
	}
	
	boolean isOppositeColour(int line, int col) {
		assert isValid(line, col);
		if (whiteTurn)
			return isBlack(line, col);
		else
			return isWhite(line, col);
	}
	
	boolean isValidPlay(Position piece, Position next) {
		int  i = whiteTurn ? -1 : 1;
		if (isValid(next.line(), next.col()) && isEmpty(next.line(), next.col()))
			return (next.line() == piece.line() + i && (next.col() == piece.col() - 1 || next.col() == piece.col() + 1));
		return false;
	}
	
	boolean canMove(Position piece) {
		int i = whiteTurn ? -1 : 1;
		if (isValid(piece.line() + i, piece.col() - 1) && isEmpty(piece.line() + i, piece.col() - 1))
			return true;
		else if (isValid(piece.line() + i, piece.col() + 1) && isEmpty(piece.line() + i, piece.col() + 1))
			return true;
		return false;
	}
	
	boolean canCapture(Position piece) {
		int i = whiteTurn ? -1 : 1;
		int j = whiteTurn ? -2 : 2;
		if (isValid(piece.line() + i, piece.col() - 1) && isOppositeColour(piece.line() + i, piece.col() - 1)) {
			if (isValid(piece.line() + j, piece.col() - 2) && isEmpty(piece.line() + j, piece.col() - 2))
				return true;
		}
		if (isValid(piece.line() + i, piece.col() + 1) && isOppositeColour(piece.line() + i, piece.col() + 1)) {
			if (isValid(piece.line() + j, piece.col() + 2) && isEmpty(piece.line() + j, piece.col() + 2))
				return true;
		}
		return false;
	}
	
	boolean mustCapture() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (whiteTurn ? isWhite(i, j) : isBlack(i, j)) {
					if (canCapture(new Position(i, j)))
						return true;
				}
			}
		}
		return false;
	}
	
	boolean isValidCapture(Position piece, Position next) {
		int captureColour = whiteTurn ? BLACK : WHITE;
		int j = whiteTurn ? -2 : 2;
		if (grid[(piece.line() + next.line()) / 2][(piece.col() + next.col()) / 2] == captureColour && isEmpty(next.line(), next.col()))
			return next.line() == piece.line() + j && (next.col() == piece.col() - 2 || next.col() == piece.col() + 2);
		return false;
	}
	
	Position[] piecesThatCanCapture() {
		int count = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if ((whiteTurn ? isWhite(i, j) : isBlack(i, j)) && canCapture(new Position(i, j))) {
					count++;
				}
			}
		}
		Position[] piecesThatCanCapture = new Position[count];
		int index = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if ((whiteTurn ? isWhite(i, j) : isBlack(i, j)) && canCapture(new Position(i, j))) {
					piecesThatCanCapture[index] = new Position(i, j);
					index++;
				}
			}
		}
		return piecesThatCanCapture;
	}
	
	Position[] piecesThatCanMove() {
		int count = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if ((whiteTurn ? isWhite(i, j) : isBlack(i, j)) && canMove(new Position(i, j))) {
					count++;
				}
			}
		}
		Position[] piecesThatCanMove = new Position[count];
		int index = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if ((whiteTurn ? isWhite(i, j) : isBlack(i, j)) && canMove(new Position(i, j))) {
					piecesThatCanMove[index] = new Position(i, j);
					index++;
				}
			}
		}
		return piecesThatCanMove;
	}

	
	Position[] validMoves(Position piece) {
		int count = 0;
		int i = whiteTurn ? -1 : 1;
		int j = whiteTurn ? -2 : 2;
		if (mustCapture()) {
			if (isValid(piece.line() + j, piece.col() - 2) && isValidCapture(piece, new Position(piece.line() + j, piece.col() - 2)))
				count++;
			if (isValid(piece.line() + j, piece.col() + 2) && isValidCapture(piece, new Position(piece.line() + j, piece.col() + 2)))
				count++;
		}
		else {
			if(isValid(piece.line() + i, piece.col() - 1) && isValidPlay(piece, new Position(piece.line() + i, piece.col() - 1)))
				count++;
			if(isValid(piece.line() + i, piece.col() + 1) && isValidPlay(piece, new Position(piece.line() + i, piece.col() + 1)))
				count++;
		}
		Position[] validMoves = new Position[count];
		int index = 0;
		if (mustCapture()) {
			if (isValid(piece.line() + j, piece.col() - 2) && isValidCapture(piece, new Position(piece.line() + j, piece.col() - 2))) {
				validMoves[index] = new Position(piece.line() + j, piece.col() - 2);
				index++;
			}
			if (isValid(piece.line() + j, piece.col() + 2) && isValidCapture(piece, new Position(piece.line() + j, piece.col() + 2))) {
				validMoves[index] = new Position(piece.line() + j, piece.col() + 2);
				index++;
			}
		}
		else {
			if(isValid(piece.line() + i, piece.col() - 1) && isValidPlay(piece, new Position(piece.line() + i, piece.col() - 1))) {
				validMoves[index] = new Position(piece.line() + i, piece.col() - 1);
				index++;
			}
			if(isValid(piece.line() + i, piece.col() + 1) && isValidPlay(piece, new Position(piece.line() + i, piece.col() + 1))) {
				validMoves[index] = new Position(piece.line() + i, piece.col() + 1);
				index++;
			}
		}
		return validMoves;
	}

	
	void capture(Position piece, Position next) {
		grid[next.line()][next.col()] = isWhiteTurn() ? WHITE : BLACK;
		grid[(piece.line() + next.line()) / 2][(piece.col() + next.col()) / 2] = EMPTY;
		grid[piece.line()][piece.col()] = EMPTY;
		whiteTurn = !whiteTurn;
	}
	
	void move(Position piece, Position next) {
		grid[next.line()][next.col()] = isWhiteTurn() ? WHITE : BLACK;
		grid[piece.line()][piece.col()] = EMPTY;
		whiteTurn = !whiteTurn;
	}
	
	boolean gameOver() {
		if (piecesThatCanMove().length > 0 || piecesThatCanCapture().length > 0)
			return false;
		return true;
	}
	
	int winner() {
		int whiteCount = 0;
		int blackCount = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (isWhite(i, j))
					whiteCount++;
				else if (isBlack(i, j))
					blackCount++;
			}
		}
		return (whiteCount - blackCount);
	}
	
	void play(Position piece, Position next) {
		if (!gameOver()) {
			if (mustCapture()) {
				if (canCapture(piece) && isValidCapture(piece, next))
					capture(piece, next);
			}
			else {
				if (isValidPlay(piece, next))
					move(piece, next);
			}
		}
	}
	
	boolean randomPlay() {
		if (!gameOver()) {
			Position piece = null;
			if (mustCapture()) {
				Position[] piecesThatCanCapture = piecesThatCanCapture();
				piece = piecesThatCanCapture[(int)(Math.random() * piecesThatCanCapture.length)];
			}
			else {
				Position[] piecesThatCanMove = piecesThatCanMove();
				piece = piecesThatCanMove[(int)(Math.random() * piecesThatCanMove.length)];
			}
			Position[] validMoves = validMoves(piece);
			Position next = validMoves[(int)(Math.random() * validMoves.length)];
			play(piece, next);
			return true;
		}
		return false;
	}
	
}