package net.tkfan.calcMark;


import java.util.List;

/**
 * 一行或一列的解集数据结构
 * 
 * @author John
 *
 */
@SuppressWarnings("unused")
public class SolveOfLine {
	/**
	 * 该行、列的长度
	 */
	private int size;

	/**
	 * 所有可选字符（空白、填写、阻断、颜色等）
	 */
	private List<Character> possibleChars;

	/**
	 * 输入条件
	 */
	private List<Integer> condition;

	/**
	 * 该行所有的可能解
	 */
	private List<List<Integer>> possibleSolves;

	/**
	 * 该行的宽松匹配条件
	 */
	private String solvingPattern;

	/**
	 * 该行的严格匹配条件
	 */
	private String solvedPattern;

	/**
	 * 按照给定条件，算出的任何条件下都必须填写的值 <br>
	 * 可变值为null
	 */
	private Character[] stableSolve;

}
