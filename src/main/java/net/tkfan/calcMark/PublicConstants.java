package net.tkfan.calcMark;

public interface PublicConstants {

    int MAX_JUDGE_BUFFER_SIZE = 1000000;

    enum LineRowType {
        COLUMN, ROW
    }

    interface Status {
        char BLANK = 'E';
        char BLOCK = 'B';
        char SOLVE = 'S';
    }
}
