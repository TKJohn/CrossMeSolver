package calcMark;

public class Calculator implements Runnable {

	private static Board board;

	private final int id;

	private String path;

	public Calculator(Board board, int id, String path) {
		if (1 == id) {
			Calculator.board = board;
		}
		this.id = id;
		this.path = path;
	}

	public void run() {
		System.out.println("start");
		if (1 == id) {
			Point startPoint = new Point(0, 0);
			calc(board, startPoint);
		} else {
			while (!board.solved) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("----------------------" + path + "-----------------------");
				board.printBoard();

			}
		}
	}

	private int calc(Board board, Point point) {
		char originStatus = board.getPointStatus(point);
		Point nextPoint = board.getNextPoint(point);

		// System.out.print("this point: ");
		// point.print();
		//
		// //System.out.print("next point: ");
		// //nextPoint.print();

		// ��ǰ��Ϳɫ�������
		int checkThisPointSolve = 0;
		// ��ǰ����ϣ������
		int checkThisPointBlock = 0;

		// ��ǰ��Ϳɫ���ӵ������
		int childThisPointSolve = 0;
		// ��ǰ����ϣ��ӵ������
		int childThisPointBlock = 0;

		// board.printBoard();

		if (Board.Status.BLANK != originStatus && !point.equals(nextPoint)) {
			// �õ��Ѿ����ã��һ����µ㣬����
			return calc(board, nextPoint);
		}

		// ���Խ����ǰ��
		board.setPointStatus(point, Board.Status.SOLVE);
		checkThisPointSolve = board.check(point);

		board.setPointStatus(point, Board.Status.BLOCK);
		checkThisPointBlock = board.check(point);

		if (Board.Result.FAIL == checkThisPointSolve && Board.Result.FAIL == checkThisPointBlock) {
			// ��ǰ����ô��д�����ʧ�ܣ�����һ�㣬����ʧ��
			board.setPointStatus(point, originStatus);
			return Board.Result.FAIL;
		}

		// ��������
		if (point.equals(nextPoint)) {
			// ��ǰ�Ѿ������һ��
			if (Board.Result.SOLVED == checkThisPointSolve || Board.Result.SOLVED == checkThisPointBlock) {
				// ��ǰ������Ӧ����Ϳ��Խ��
				board.setPointStatus(point,
						Board.Result.SOLVED == checkThisPointSolve ? Board.Status.SOLVE : Board.Status.BLOCK);
				System.err.println("==========Solved!!========");
				board.solved = true;
				board.printBoard();
				board.save(path + "_result.txt");
				System.err.println("==========================");

				board.setPointStatus(point, Board.Status.BLANK);
				return Board.Result.SOLVED;
			} else {
				board.setPointStatus(point, Board.Status.BLANK);
				return Board.Result.FAIL;
			}
		}

		// ��ǰ�㲻�����㣬�����������һ��
		if (Board.Result.FAIL != checkThisPointSolve) {
			// ��ǰ��Ϳɫû�д���
			board.setPointStatus(point, Board.Status.SOLVE);

			// ��������
			childThisPointSolve = calc(board, nextPoint);
		}

		if (Board.Result.FAIL != checkThisPointBlock) {
			// ��ǰ�����û�д���
			board.setPointStatus(point, Board.Status.BLOCK);

			// ��������
			childThisPointBlock = calc(board, nextPoint);
		}

		// ������ɣ���ʼ��顢����
		board.setPointStatus(point, originStatus);

		if (Board.Result.FAIL == childThisPointBlock && Board.Result.FAIL == childThisPointSolve) {
			return Board.Result.FAIL;
		}
		return 0;
	}

}
