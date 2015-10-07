package calcMark;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class Board {

	public boolean solved = false;

	public interface Status {
		public static char BLANK = 'E';
		public static char BLOCK = 'B';
		public static char SOLVE = 'S';
	}

	public interface Result {
		public static int FAIL = -1;
		public static int PENDING = 0;
		public static int SOLVED = 1;
	}

	// E-blank;B-block;S-solve;
	private static final String matchSolvingStater = "[EB]*?";
	private static final String matchSolvingEnder = "[EB]*?";
	private static final String matchSolvingSpliter = "[EB]+?";
	private static final String matchSolvingMarker = "[ES]";

	private static final String matchSolvedStater = "B*";
	private static final String matchSolvedEnder = "B*";
	private static final String matchSolvedSpliter = "B+";
	private static final String matchSolvedMarker = "S";

	private char[][] boardData;// 10*5

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

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
	}

	/**
	 * 设置某点的状态
	 * 
	 * @param x
	 *            行坐标
	 * @param y
	 *            列坐标
	 * @param mark
	 *            'E'-未定；'B'-隔断；'S'-涂色
	 * 
	 */
	public void setPointStatus(Point point, char mark) {

		if (null == boardData) {
			return;
		}
		boardData[point.getX()][point.getY()] = mark;
	}

	public char getPointStatus(Point point) {
		// System.out.println(point.getX() + " " + point.getY() + " " +
		// this.boardData[point.getX()][point.getY()]);
		return this.boardData[point.getX()][point.getY()];
	}

	/**
	 * 按照传入的点坐标，和棋盘形状，顺序返回下一个棋盘点。先增加行坐标，再增加列坐标
	 * 
	 * @param point
	 *            传入点
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

	/**
	 * 根据给定行号，获取该行的棋盘所填状态数据
	 * 
	 * @param rowNo
	 *            给定行号
	 * @return 该行填写状态
	 */
	public char[] getRow(int rowNo) {
		char[] result = new char[this.width];
		for (int i = 0; i < this.width; i++) {
			result[i] = this.boardData[i][rowNo];
		}
		return result;
	}

	/**
	 * 根据给定列号，获取该列的棋盘所填状态数据
	 * 
	 * @param columnNo
	 *            给定列号
	 * @return 该列填写状态
	 */
	public char[] getColumn(int columnNo) {
		return this.boardData[columnNo];
	}

	/**
	 * 打印当前棋盘状态。未定、隔断、涂色
	 */
	public void printBoard() {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				switch (boardData[i][j]) {
				case Status.BLANK:
					System.out.print("□");
					break;
				case Status.BLOCK:
					System.out.print("x");
					break;
				case Status.SOLVE:
					System.out.print("■");
					break;
				default:
					break;
				}// end of switch
			} // end of row;
			System.out.println();
		}
	}

	/**
	 * 根据给定坐标，检查该点所在行、列是否匹配给定条件
	 * 
	 * @param point
	 *            给定点
	 * @return -1：检查出错误；0-未检查出错误，但还未完成；1-完全ok
	 */
	public int check(Point point) {
		int x = point.getX();
		int y = point.getY();

		int rowResult = Result.PENDING;
		int columnResult = Result.PENDING;

		char[] row = getRow(y);
		char[] column = getColumn(x);

		String rowStr = array2String(row);
		String columnStr = array2String(column);

		if (!solvingPatternOfEachRow[y].matcher(rowStr).matches()) {
			return Result.FAIL;
		} else if (solvedPatternOfEachRow[y].matcher(rowStr).matches()) {
			rowResult = Result.SOLVED;
		}

		if (!solvingPatternOfEachColumn[x].matcher(columnStr).matches()) {
			return Result.FAIL;
		} else if (solvedPatternOfEachColumn[x].matcher(columnStr).matches()) {
			columnResult = Result.SOLVED;
		}

		if (Result.SOLVED == rowResult && Result.SOLVED == columnResult) {
			return Result.SOLVED;
		}
		return Result.PENDING;
	};

	/**
	 * 将给定数字数组（一位整数）拼接为无分隔字符串
	 * 
	 * @param marks
	 *            数字数组
	 * @return 拼接无分隔字符串
	 */
	private String array2String(char[] marks) {
		return new String(marks);
	}

	/**
	 * 根据给定的单行\列条件，得到对应的完成匹配正则表达式
	 * <p>
	 * 不符合不一定错误，符合保证正确
	 * 
	 * @param conditions
	 *            条件List //5 2 3 4~~
	 * @return 完成匹配正则表达式. 对于 3 2 的输入：B*S{3}B+S{2}B*
	 */
	private String getSolvedMatchStr(List<Integer> conditions) {
		if (null == conditions || 0 == conditions.size()) {
			return "B*";
		}

		StringBuilder match = new StringBuilder();

		match.append(matchSolvedStater);
		for (Integer condition : conditions) {
			match.append(matchSolvedMarker).append("{").append(String.valueOf(condition)).append("}");
			match.append(matchSolvedSpliter);
		}
		String m = match.substring(0, match.length() - matchSolvedSpliter.length());

		m = m + matchSolvedEnder;

		return m;
	}

	/**
	 * 根据给定的单行\列条件，得到对应的部分匹配正则表达式
	 * <p>
	 * 不符合一定错误，符合不一定正确
	 * 
	 * @param conditions
	 *            条件List //5 2 3 4~~
	 * @return 部分匹配正则表达式 for 3 2，得到 [EB]*[ES]{3}[EB]+[ES]{2}[EB]*
	 */
	private String getSolvingMatchStr(List<Integer> conditions) {
		if (null == conditions || 0 == conditions.size()) {
			return "[EB]*";
		}

		StringBuilder match = new StringBuilder();

		match.append(matchSolvingStater);
		for (Integer condition : conditions) {
			match.append(matchSolvingMarker).append("{").append(String.valueOf(condition)).append("}");
			match.append(matchSolvingSpliter);
		}
		String m = match.substring(0, match.length() - matchSolvingSpliter.length());

		m = m + matchSolvingEnder;

		return m;
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

			this.solvingStrOfEachRow[i] = getSolvingMatchStr(thisRowCondition);
			this.solvedStrOfEachRow[i] = getSolvedMatchStr(thisRowCondition);

			this.solvingPatternOfEachRow[i] = Pattern.compile(this.solvingStrOfEachRow[i]);
			this.solvedPatternOfEachRow[i] = Pattern.compile(this.solvedStrOfEachRow[i]);
		}
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

			this.solvingStrOfEachColumn[i] = getSolvingMatchStr(thisColumnCondition);
			this.solvedStrOfEachColumn[i] = getSolvedMatchStr(thisColumnCondition);

			this.solvingPatternOfEachColumn[i] = Pattern.compile(getSolvingMatchStr(thisColumnCondition));
			this.solvedPatternOfEachColumn[i] = Pattern.compile(getSolvedMatchStr(thisColumnCondition));
		}
	}

	public List<List<Integer>> getRowConditions() {
		return rowConditions;
	}

	public List<List<Integer>> getColumnConditions() {
		return columnConditions;
	}

	public void save(String path) {
		Path file = Paths.get(path);
		Charset charset = Charset.forName("UTF-8");
		BufferedWriter writer = null;

		try {
			writer = Files.newBufferedWriter(file, charset);

			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					switch (boardData[i][j]) {
					case Status.BLANK:
						writer.write("E");
						break;
					case Status.BLOCK:
						writer.write("X");
						break;
					case Status.SOLVE:
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
}