package net.tkfan.calcMark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class Calc {
	private static final Logger logger = Logger.getLogger(Calc.class);

	public static void main(String[] args) {
		InputStream is = null;
		Properties prop = System.getProperties();
		try {
			is = Calc.class.getClassLoader().getResourceAsStream("config.properties");
			prop.load(is);
		} catch (IOException e) {
			logger.error(e);
			return;
		} finally {
			try {
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				logger.error("Close file error", e);
			}
		}

		logger.info("OK");
		logger.info(Calc.class.getClassLoader().getResource("").getPath());

		Scanner scanner = new Scanner(System.in);
		String path = scanner.nextLine();
		scanner.close();

		Board board;
		board = readFileIntoBoard(path);

		new Thread(new Calculator(board, 1, path)).start();
		new Thread(new Calculator(board, 2, path)).start();
	}

	private static Board readFileIntoBoard(String path) {
		Path file = Paths.get(path);
		Charset charset = Charset.forName("UTF-8");
		Board board = null;
		BufferedReader reader = null;
		int checkSum = 0;

		try {
			reader = Files.newBufferedReader(file, charset);

			// read size
			String line = null;
			String[] lineContent = null;

			line = reader.readLine();
			lineContent = line.split("[ ]+");

			int width = Integer.parseInt(lineContent[0]);
			int height = Integer.parseInt(lineContent[1]);
			board = new Board(width, height);

			List<List<Integer>> rowConditions = new ArrayList<List<Integer>>();
			List<List<Integer>> columnConditions = new ArrayList<List<Integer>>();

			for (int i = 0; i < width; i++) {
				line = reader.readLine();
				lineContent = line.split("[ ]+");
				List<Integer> mark = new ArrayList<Integer>();
				columnConditions.add(mark);
				for (String string : lineContent) {
					mark.add(Integer.parseInt(string));
					checkSum += Integer.parseInt(string);
				}
			}
			logger.debug(checkSum);
			for (int i = 0; i < height; i++) {
				line = reader.readLine();
				lineContent = line.split("[ ]+");
				List<Integer> mark = new ArrayList<Integer>();
				rowConditions.add(mark);
				for (String string : lineContent) {
					mark.add(Integer.parseInt(string));
					checkSum -= Integer.parseInt(string);
				}
			}
			if (0 != checkSum) {
				logger.error("error:checkSum=" + checkSum);
				return board;
			}
			board.setColumnConditions(columnConditions);
			board.setRowConditions(rowConditions);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return board;
	};
}