package net.tkfan.calcMark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 棋盘数据对象
 *
 * @author John
 */
public class Board {

    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    public boolean solved = false;
    long slowest = 0;
    String slowestColumnStr;
    String slowestRowStr;
    Point slowestPoint;
    private char[][] boardData;// 10*5
    private int width;// 10 columns 0-9
    private int height;// 5 rows 0-4
    private String[] solvingStrOfEachRow;// 5
    private String[] solvedStrOfEachRow;// 5
    private String[] solvingStrOfEachColumn;// 10
    private String[] solvedStrOfEachColumn;// 10
    private Pattern[] solvingPatternOfEachRow;// 5
    private Pattern[] solvedPatternOfEachRow;// 5
    private Pattern[] solvingPatternOfEachColumn;// 10
    private Pattern[] solvedPatternOfEachColumn;// 10
    //    private SolveOfLine[] solveOfLineOfEachColumn; // 10
//    private SolveOfLine[] solveOfLineOfEachRow; // 5
    private List<List<Integer>> rowConditions;// 5 rows;
    private List<List<Integer>> columnConditions;// 10 columns;

    public Board(int width, int height) {
        if (width < 0 || height < 0) {
            return;
        }

        this.boardData = new char[width][height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                this.boardData[i][j] = 'E';
            }
        }

        this.width = width;
        this.height = height;

        this.solvedStrOfEachColumn = new String[width];
        this.solvedStrOfEachRow = new String[height];

        this.solvingStrOfEachColumn = new String[width];
        this.solvingStrOfEachRow = new String[height];

        this.solvedPatternOfEachColumn = new Pattern[width];
        this.solvedPatternOfEachRow = new Pattern[height];

        this.solvingPatternOfEachColumn = new Pattern[width];
        this.solvingPatternOfEachRow = new Pattern[height];

//        this.solveOfLineOfEachColumn = new SolveOfLine[width];
//        this.solveOfLineOfEachRow = new SolveOfLine[height];
    }

    /**
     * 根据给定坐标，检查该点所在行、列是否匹配给定条件
     *
     * @param point 给定点
     * @return FAIL-检查出错误；PENDING-未检查出错误，但还未完成；SUCCESS-完全ok
     */
    public JudgeResult check(Point point) {
        int columnPosition = point.getX();
        int rowPosition = point.getY();

        char[] row = getRow(rowPosition);
        char[] column = getColumn(columnPosition);

        String rowStr = PublicUtils.array2String(row);
        String columnStr = PublicUtils.array2String(column);

        JudgeResult rowResult = JudgeBuffer.getResult(PublicConstants.LineRowType.ROW, rowPosition, rowStr);
        if (rowResult == null) {
            rowResult = JudgeResult.PENDING;
            if (!solvingPatternOfEachRow[rowPosition].matcher(rowStr).matches()) {
                rowResult = JudgeResult.FAIL;
            } else if (solvedPatternOfEachRow[rowPosition].matcher(rowStr).matches()) {
                rowResult = JudgeResult.SUCCESS;
            }
            JudgeBuffer.setResult(PublicConstants.LineRowType.ROW, rowPosition, rowStr, rowResult);
        }

        JudgeResult columnResult = JudgeBuffer.getResult(PublicConstants.LineRowType.COLUMN, columnPosition, columnStr);
        if (columnResult == null) {
            columnResult = JudgeResult.PENDING;
            if (!solvingPatternOfEachColumn[columnPosition].matcher(columnStr).matches()) {
                columnResult = JudgeResult.FAIL;
            } else if (solvedPatternOfEachColumn[columnPosition].matcher(columnStr).matches()) {
                columnResult = JudgeResult.SUCCESS;
            }
            JudgeBuffer.setResult(PublicConstants.LineRowType.COLUMN, columnPosition, columnStr, columnResult);
        }
        if (JudgeResult.FAIL.equals(rowResult) || JudgeResult.FAIL.equals(columnResult)) {
            return JudgeResult.FAIL;
        }
        if (JudgeResult.SUCCESS.equals(rowResult) && JudgeResult.SUCCESS.equals(columnResult)) {
            return JudgeResult.SUCCESS;
        }
        return JudgeResult.PENDING;
    }

    /**
     * 根据给定列号，获取该列的棋盘所填状态数据
     *
     * @param columnNo 给定列号
     * @return 该列填写状态
     */
    public char[] getColumn(int columnNo) {
        return this.boardData[columnNo];
    }

    public List<List<Integer>> getColumnConditions() {
        return columnConditions;
    }

    /**
     * 获取给定的各列原始条件，保存并算出各列的匹配用正则表达式
     *
     * @param columnConditions
     */
    public void setColumnConditions(List<List<Integer>> columnConditions) {
        if (this.width != columnConditions.size()) {
            return;
        }
        this.columnConditions = columnConditions;

        for (int i = 0; i < this.width; i++) {
            List<Integer> thisColumnCondition = columnConditions.get(i);

            this.solvingStrOfEachColumn[i] = PublicUtils.getSolvingMatchStr(thisColumnCondition);
            this.solvedStrOfEachColumn[i] = PublicUtils.getSolvedMatchStr(thisColumnCondition);

            this.solvingPatternOfEachColumn[i] = Pattern.compile(PublicUtils.getSolvingMatchStr(thisColumnCondition));
            this.solvedPatternOfEachColumn[i] = Pattern.compile(PublicUtils.getSolvedMatchStr(thisColumnCondition));

        }
    }

    public int getHeight() {
        return height;
    }

    /**
     * 按照传入的点坐标，和棋盘形状，顺序返回下一个棋盘点。先增加行坐标，再增加列坐标
     *
     * @param point 传入点
     * @return 传出点.null-参数错误.传入点-传入已经是最后一点
     */
    public Point getNextPoint(Point point) {
        int x = point.getX();
        int y = point.getY();

        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            return new Point(0, 0);
        }

        // 当前行最后一点
        if (this.width - 1 == x) {
            // 不是最后一行
            if (this.height - 1 != y) {
                return new Point(0, y + 1);
            } else {
                // 最后一行
                return point;
            }
        }

        // 不是行最后一点，直接增加横坐标
        return new Point(x + 1, y);
    }

    public char getPointStatus(Point point) {
        // System.out.println(point.getX() + " " + point.getY() + " " +
        // this.boardData[point.getX()][point.getY()]);
        return this.boardData[point.getX()][point.getY()];
    }

    /**
     * 根据给定行号，获取该行的棋盘所填状态数据
     *
     * @param rowNo 给定行号
     * @return 该行填写状态
     */
    public char[] getRow(int rowNo) {
        char[] result = new char[this.width];
        for (int i = 0; i < this.width; i++) {
            result[i] = this.boardData[i][rowNo];
        }
        return result;
    }

    public List<List<Integer>> getRowConditions() {
        return rowConditions;
    }

    /**
     * 获取给定的各行原始条件，保存并算出各行的匹配用正则表达式
     *
     * @param rowConditions
     */
    public void setRowConditions(List<List<Integer>> rowConditions) {
        if (this.height != rowConditions.size()) {
            return;
        }
        this.rowConditions = rowConditions;

        for (int i = 0; i < this.height; i++) {
            List<Integer> thisRowCondition = rowConditions.get(i);

            this.solvingStrOfEachRow[i] = PublicUtils.getSolvingMatchStr(thisRowCondition);
            this.solvedStrOfEachRow[i] = PublicUtils.getSolvedMatchStr(thisRowCondition);

            this.solvingPatternOfEachRow[i] = Pattern.compile(this.solvingStrOfEachRow[i]);
            this.solvedPatternOfEachRow[i] = Pattern.compile(this.solvedStrOfEachRow[i]);

        }
    }

    public int getWidth() {
        return width;
    }

    /**
     * 打印当前棋盘状态。未定、隔断
     */
    public void printBoard() {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                switch (boardData[i][j]) {
                    case PublicConstants.Status.BLANK:
                        System.out.print("□");
                        break;
                    case PublicConstants.Status.BLOCK:
                        System.out.print("x");
                        break;
                    case PublicConstants.Status.SOLVE:
                        System.out.print("■");
                        break;
                    default:
                        break;
                }// end of switch
            } // end of row;
            System.out.println();
        }
        JudgeBuffer.printStatus();
    }
//
//    public void printSlow() {
//        logger.debug("----Slow----");
//        logger.debug("time: " + this.slowest);
//        logger.debug("Point: " + this.slowestPoint);
//        logger.debug("rowStr: " + this.slowestRowStr);
//        logger.debug("rowCondition: " + this.solvingPatternOfEachRow[this.slowestPoint.getY()]);
//
//        logger.debug("columnStr: " + this.slowestColumnStr);
//        logger.debug("columnCondition: " + this.solvingPatternOfEachColumn[this.slowestPoint.getX()]);
//
//        logger.debug("----Slow----");
//    }

    public void save(String path) {
        Path file = Paths.get(path);
        Charset charset = Charset.forName("UTF-8");
        BufferedWriter writer = null;

        try {
            writer = Files.newBufferedWriter(file, charset);

            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {
                    switch (boardData[i][j]) {
                        case PublicConstants.Status.BLANK:
                            writer.write("E");
                            break;
                        case PublicConstants.Status.BLOCK:
                            writer.write("X");
                            break;
                        case PublicConstants.Status.SOLVE:
                            writer.write("O");
                            break;
                        default:
                            break;
                    }// end of switch
                } // end of row;
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置某点的状态
     *
     * @param point 行坐标
     * @param mark  列坐标
     * @param mark  'E'-未定；'B'-隔断；'S'-涂色
     */
    public void setPointStatus(Point point, char mark) {

        if (null == boardData) {
            return;
        }
        boardData[point.getX()][point.getY()] = mark;
    }

//    /**
//     * 得到最终解
//     */
//    public void solve() {
//        while (filterByStable()) {
//        }
//
//    }

//    /**
//     * 使用每行每列的不变项，不断互相过滤，减少搜索范围
//     *
//     * @return 本次有消减(可以继续尝试过滤)
//     */
//    private boolean filterByStable() {
//        boolean filtered = false;
//
//        // 使用每一列的稳定解(的每一个稳定字符)，过滤处理每一行
//        for (int x = 0; x < this.width; x++) {
//            String subStableSolve = this.solveOfLineOfEachColumn[x].getStableSolve();
//            for (int y = 0; y < subStableSolve.length(); x++) {
//                char checkChar = subStableSolve.charAt(y);
//                if ('@' == checkChar || 'E' == checkChar || ' ' == checkChar)
//                    continue;
//                boolean subFiltered = this.solveOfLineOfEachRow[y].filterByCharacter(x, checkChar);
//                if (subFiltered)
//                    filtered = true;
//            }
//        }
//
//        // 使用每一行的稳定解(的每一个稳定字符)，过滤处理每一列
//        for (int y = 0; y < this.width; y++) {
//            String subStableSolve = this.solveOfLineOfEachRow[y].getStableSolve();
//            for (int x = 0; x < subStableSolve.length(); y++) {
//                char checkChar = subStableSolve.charAt(x);
//                if ('@' == checkChar || 'E' == checkChar || ' ' == checkChar)
//                    continue;
//                boolean subFiltered = this.solveOfLineOfEachColumn[x].filterByCharacter(y, checkChar);
//                if (subFiltered)
//                    filtered = true;
//            }
//        }
//        return filtered;
//    }
}