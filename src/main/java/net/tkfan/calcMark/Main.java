package net.tkfan.calcMark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start");

        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        scanner.close();

        Board board;
        try {
            board = readFileIntoBoard(path);
        } catch (ApplicationException e) {
            logger.error("Exist");
            return;
        }
        // board.solve();
        new Thread(new Calculator(board, 1, path)).start();
        new Thread(new Calculator(board, 2, path)).start();
    }

    private static Board readFileIntoBoard(String path) throws ApplicationException {
        Path file = Paths.get(path);
        Charset charset = StandardCharsets.UTF_8;
        Board board = null;
        BufferedReader reader = null;
        int checkSum = 0;

        try {
            if (Files.notExists(file)) {
                logger.error("File {} not exists. Exit", file);
                throw new ApplicationException("Input file not exists: " + file);
            }

            reader = Files.newBufferedReader(file, charset);
            // read size
            String line;
            String[] lineContent;

            line = reader.readLine();
            lineContent = line.split("[ ]+");

            int width = Integer.parseInt(lineContent[0]);
            int height = Integer.parseInt(lineContent[1]);
            board = new Board(width, height);

            List<List<Integer>> rowConditions = new ArrayList<>();
            List<List<Integer>> columnConditions = new ArrayList<>();

            for (int i = 0; i < width; i++) {
                line = reader.readLine();
                lineContent = line.split("[ ]+");
                List<Integer> mark = new ArrayList<>();
                columnConditions.add(mark);
                for (String string : lineContent) {
                    mark.add(Integer.parseInt(string));
                    checkSum += Integer.parseInt(string);
                }
            }
            logger.debug(String.valueOf(checkSum));
            for (int i = 0; i < height; i++) {
                line = reader.readLine();
                lineContent = line.split("[ ]+");
                List<Integer> mark = new ArrayList<>();
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
            logger.error("error", e);
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("error", e);
            }
        }
        return board;
    }

}