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
	 * ����ĳ���״̬
	 * 
	 * @param x
	 *            ������
	 * @param y
	 *            ������
	 * @param mark
	 *            'E'-δ����'B'-���ϣ�'S'-Ϳɫ
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
	 * ���մ���ĵ����꣬��������״��˳�򷵻���һ�����̵㡣�����������꣬������������
	 * 
	 * @param point
	 *            �����
	 * @return ������.null-��������.�����-�����Ѿ������һ��
	 */
	public Point getNextPoint(Point point) {
		int x = point.getX();
		int y = point.getY();

		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return new Point(0, 0);
		}

		// ��ǰ�����һ��
		if (this.width - 1 == x) {
			// �������һ��
			if (this.height - 1 != y) {
				return new Point(0, y + 1);
			} else {
				// ���һ��
				return point;
			}
		}

		// ���������һ�㣬ֱ�����Ӻ�����
		return new Point(x + 1, y);
	}

	/**
	 * ���ݸ����кţ���ȡ���е���������״̬����
	 * 
	 * @param rowNo
	 *            �����к�
	 * @return ������д״̬
	 */
	public char[] getRow(int rowNo) {
		char[] result = new char[this.width];
		for (int i = 0; i < this.width; i++) {
			result[i] = this.boardData[i][rowNo];
		}
		return result;
	}

	/**
	 * ���ݸ����кţ���ȡ���е���������״̬����
	 * 
	 * @param columnNo
	 *            �����к�
	 * @return ������д״̬
	 */
	public char[] getColumn(int columnNo) {
		return this.boardData[columnNo];
	}

	/**
	 * ��ӡ��ǰ����״̬��δ�������ϡ�Ϳɫ
	 */
	public void printBoard() {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				switch (boardData[i][j]) {
				case Status.BLANK:
					System.out.print("��");
					break;
				case Status.BLOCK:
					System.out.print("x");
					break;
				case Status.SOLVE:
					System.out.print("��");
					break;
				default:
					break;
				}// end of switch
			} // end of row;
			System.out.println();
		}
	}

	/**
	 * ���ݸ������꣬���õ������С����Ƿ�ƥ���������
	 * 
	 * @param point
	 *            ������
	 * @return -1����������0-δ�������󣬵���δ��ɣ�1-��ȫok
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
	 * �������������飨һλ������ƴ��Ϊ�޷ָ��ַ���
	 * 
	 * @param marks
	 *            ��������
	 * @return ƴ���޷ָ��ַ���
	 */
	private String array2String(char[] marks) {
		return new String(marks);
	}

	/**
	 * ���ݸ����ĵ���\���������õ���Ӧ�����ƥ��������ʽ
	 * <p>
	 * �����ϲ�һ�����󣬷��ϱ�֤��ȷ
	 * 
	 * @param conditions
	 *            ����List //5 2 3 4~~
	 * @return ���ƥ��������ʽ. ���� 3 2 �����룺B*S{3}B+S{2}B*
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
	 * ���ݸ����ĵ���\���������õ���Ӧ�Ĳ���ƥ��������ʽ
	 * <p>
	 * ������һ�����󣬷��ϲ�һ����ȷ
	 * 
	 * @param conditions
	 *            ����List //5 2 3 4~~
	 * @return ����ƥ��������ʽ for 3 2���õ� [EB]*[ES]{3}[EB]+[ES]{2}[EB]*
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
	 * ��ȡ�����ĸ���ԭʼ���������沢������е�ƥ����������ʽ
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
	 * ��ȡ�����ĸ���ԭʼ���������沢������е�ƥ����������ʽ
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