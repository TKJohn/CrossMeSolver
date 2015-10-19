package calcMark;

import java.util.List;

public abstract class PublicUtils {

	// E-blank;B-block;S-solve;
	private static final String matchSolvingStater = "[EB]*";
	private static final String matchSolvingEnder = "[EB]*";
	private static final String matchSolvingSpliter = "[EB]+";
	private static final String matchSolvingMarker = "[ES]";

	private static final String matchSolvedStater = "B*";
	private static final String matchSolvedEnder = "B*";
	private static final String matchSolvedSpliter = "B+";
	private static final String matchSolvedMarker = "S";

	/**
	 * 将给定数字数组（一位整数）拼接为无分隔字符串
	 * 
	 * @param marks
	 *            数字数组
	 * @return 拼接无分隔字符串
	 */
	static public String array2String(char[] marks) {
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
	static public String getSolvedMatchStr(List<Integer> conditions) {
		if (null == conditions || 0 == conditions.size()) {
			return matchSolvedStater;
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
	static public String getSolvingMatchStr(List<Integer> conditions) {
		if (null == conditions || 0 == conditions.size()) {
			return matchSolvingStater;
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
}