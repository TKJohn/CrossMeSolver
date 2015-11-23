package net.tkfan.calcMark;


public abstract class PublicConstants {

	public interface Result {
		public static final int FAIL = -1;
		public static final int PENDING = 0;
		public static final int SUCCESS = 1;
	}

	public interface Status {
		public static final char BLANK = 'E';
		public static final char BLOCK = 'B';
		public static final char SOLVE = 'S';
	}
}
