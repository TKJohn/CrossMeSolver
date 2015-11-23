package net.tkfan.calcMark;

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

	private int calc(Board board, Point point) {
		char originStatus = board.getPointStatus(point);
		Point nextPoint = board.getNextPoint(point);

		// System.out.print("this point: ");
		// point.print();
		//
		// //System.out.print("next point: ");
		// //nextPoint.print();

		// 当前点涂色，检查结果
		int checkThisPointSolve = 0;
		// 当前点隔断，检查结果
		int checkThisPointBlock = 0;

		// 当前点涂色，子迭代结果
		int childThisPointSolve = 0;
		// 当前点隔断，子迭代结果
		int childThisPointBlock = 0;

		// board.printBoard();

		if (PublicConstants.Status.BLANK != originStatus && !point.equals(nextPoint)) {
			// 该点已经设置，且还有下点，跳过
			return calc(board, nextPoint);
		}

		// 尝试解决当前点
		board.setPointStatus(point, PublicConstants.Status.SOLVE);
		checkThisPointSolve = board.check(point);

		board.setPointStatus(point, PublicConstants.Status.BLOCK);
		checkThisPointBlock = board.check(point);

		if (PublicConstants.Result.FAIL == checkThisPointSolve && PublicConstants.Result.FAIL == checkThisPointBlock) {
			// 当前点怎么填写都检测失败，上移一点，返回失败
			board.setPointStatus(point, originStatus);
			return PublicConstants.Result.FAIL;
		}

		// 结束条件
		if (point.equals(nextPoint)) {
			// 当前已经是最后一点
			if (PublicConstants.Result.SUCCESS == checkThisPointSolve
					|| PublicConstants.Result.SUCCESS == checkThisPointBlock) {
				// 当前点做相应处理就可以解决
				board.setPointStatus(point, PublicConstants.Result.SUCCESS == checkThisPointSolve
						? PublicConstants.Status.SOLVE : PublicConstants.Status.BLOCK);
				System.err.println("==========Solved!!========");
				board.solved = true;
				board.printBoard();
				board.save(path + "_result.txt");
				System.err.println("==========================");

				board.setPointStatus(point, PublicConstants.Status.BLANK);
				return PublicConstants.Result.SUCCESS;
			} else {
				board.setPointStatus(point, PublicConstants.Status.BLANK);
				return PublicConstants.Result.FAIL;
			}
		}

		// 当前点不是最后点，分情况迭代下一点
		if (PublicConstants.Result.FAIL != checkThisPointSolve) {
			// 当前点涂色没有错误
			board.setPointStatus(point, PublicConstants.Status.SOLVE);

			// 迭代处理
			childThisPointSolve = calc(board, nextPoint);
		}

		if (PublicConstants.Result.FAIL != checkThisPointBlock) {
			// 当前点隔断没有错误
			board.setPointStatus(point, PublicConstants.Status.BLOCK);

			// 迭代处理
			childThisPointBlock = calc(board, nextPoint);
		}

		// 迭代完成，开始检查、回退
		board.setPointStatus(point, originStatus);

		if (PublicConstants.Result.FAIL == childThisPointBlock && PublicConstants.Result.FAIL == childThisPointSolve) {
			return PublicConstants.Result.FAIL;
		}
		return 0;
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
}