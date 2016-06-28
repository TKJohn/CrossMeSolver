package net.tkfan.calcMark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;


/**
 * 一行或一列的解集数据结构
 *
 * @author John
 */
@SuppressWarnings("unused")
public class SolveOfLine {
    private static final Logger logger = LoggerFactory.getLogger(SolveOfLine.class);
    /**
     * 该行、列的长度
     */
    private int size;

    /**
     * 输入条件
     */
    private List<Integer> condition;

    /**
     * 该行所有的可能解
     */
    private Set<String> possibleSolves = new HashSet<String>();

    /**
     * 该行的宽松匹配条件
     */
    private Pattern solvingPattern;

    /**
     * 该行的严格匹配条件
     */
    private Pattern solvedPattern;

    /**
     * 按照给定条件，算出的所有条件下的公共解 <br>
     * 可变值为特殊字符<code>'@'</code>, 确定值写进去
     */
    private String stableSolve;

    /**
     * 从上次公共解算出后，条件是否有变化（部分解被过滤），需要重新计算公共解
     */
    private boolean isStableSolveModified = true;

    public SolveOfLine(List<Integer> condition, int size) throws ApplicationException {
        if (null == condition || condition.isEmpty() || size <= 0) {
            throw new ApplicationException("error input");
        }
        this.size = size;
        this.condition = condition;

        this.solvedPattern = Pattern.compile(PublicUtils.getSolvedMatchStr(condition));
        this.solvingPattern = Pattern.compile(PublicUtils.getSolvingMatchStr(condition));
        this.solveLine();
        this.calcStableSolve();
        logger.debug("size " + this.possibleSolves.size());
    }

    public static void main(String[] args) {
        List<Integer> condition = new ArrayList<Integer>();
        condition.add(3);
        condition.add(4);
        try {
            SolveOfLine sv = new SolveOfLine(condition, 50);
            sv.solveLine();
            String stable = sv.getStableSolve();
            System.out.println(stable);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 按照给定条件，算出全部解
     */
    private void solveLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size; i++)
            sb.append('E');
        solver(sb, 0);
    }

    /**
     * 递归函数，枚举出给定条件的全部可能解
     *
     * @param sb    解空间
     * @param index 当前需要处理的字符位
     */
    private void solver(StringBuilder sb, int index) {
        sb.setCharAt(index, 'S');
        String solveCondition = sb.toString();
        sb.setCharAt(index, 'B');
        String blockCondition = sb.toString();

        if (index == this.size - 1) {
            // 当前是最后一个字符，直接完全校验
            if (this.solvedPattern.matcher(solveCondition).matches()) {
                this.possibleSolves.add(solveCondition);
            }
            if (this.solvedPattern.matcher(blockCondition).matches()) {
                this.possibleSolves.add(blockCondition);
            }
        } else {
            // 不是最后一个字符，对可能情况进行递归
            if (this.solvingPattern.matcher(solveCondition).matches()) {
                sb.setCharAt(index, 'S');
                solver(sb, index + 1);
            }
            if (this.solvingPattern.matcher(blockCondition).matches()) {
                sb.setCharAt(index, 'B');
                solver(sb, index + 1);
            }
        }
        // 清空退栈
        sb.setCharAt(index, 'E');
        return;
    }

    /**
     * 获取公共解
     *
     * @return
     */
    public String getStableSolve() {
        if (!this.isStableSolveModified) {
            return this.stableSolve;
        }

        calcStableSolve();
        return this.stableSolve;
    }

    /**
     * 按照给定条件，算出的所有条件下的公共解 <br>
     * 可变值为特殊字符<code>'@'</code>, 确定值写进去
     */
    private void calcStableSolve() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.size; i++) {
            result.append(' ');
        }

        StringBuilder defaultResult = new StringBuilder();
        for (int i = 0; i < this.size; i++) {
            defaultResult.append('@');
        }

        for (String solve : this.possibleSolves) {
            for (int i = 0; i < this.size; i++) {
                switch (result.charAt(i)) {
                    case '@':
                        continue;
                    case ' ':
                        result.setCharAt(i, solve.charAt(i));
                        continue;
                    default:
                        if (result.charAt(i) != solve.charAt(i))
                            result.setCharAt(i, '@');
                        break;
                }
            }

            if (result.equals(defaultResult)) {
                result = defaultResult;
                break;
            }
        }
        this.stableSolve = result.toString();
        this.isStableSolveModified = false;
    }

    /**
     * 使用给定坐标给定字符，从所有可能解中删除不符合要求的解
     *
     * @param index
     * @param checkChar
     * @return 有解被删除
     */
    public boolean filterByCharacter(int index, char checkChar) {
        boolean removed = false;
        if ('@' == checkChar || 'E' == checkChar)
            return removed;

        Iterator<String> it = possibleSolves.iterator();
        while (it.hasNext()) {

            String solveToCheck = it.next();
            if (solveToCheck.charAt(index) != checkChar) {
                it.remove();
                removed = true;
                logger.debug("removed " + solveToCheck);

            }
        }
        if (removed)
            this.calcStableSolve();
        return removed;
    }
}