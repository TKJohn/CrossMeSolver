package net.tkfan.calcMark;

public interface PublicConstants {

    enum JudgeResult {
        FAIL, PENDING, SUCCESS;
    }

    enum LineRowType {
        LINE, ROW;
    }

    interface Status {
        char BLANK = 'E';
        char BLOCK = 'B';
        char SOLVE = 'S';
    }
}
