package net.tkfan.calcMark;

import java.util.ArrayList;
import java.util.List;

public abstract class PublicUtils {

    // E-blank;B-block;S-solve;
    private static final String matchSolvingStater = "[EB]*?";
    private static final String matchSolvingEnder = "[EB]*?";
    private static final String matchSolvingSpliter = "[EB]+?";
    private static final String matchSolvingMarker = "[ES]";

    private static final String matchSolvedStater = "B*";
    private static final String matchSolvedEnder = "B*";
    private static final String matchSolvedSpliter = "B+";
    private static final String matchSolvedMarker = "S";

    /**
     * 将给定数字数组（一位整数）拼接为无分隔字符串
     *
     * @param marks 数字数组
     * @return 拼接无分隔字符串
     */
    static public String array2String(char[] marks) {
        return new String(marks);
    }

    /**
     * 根据给定的单行\列条件，得到对应的完成匹配正则表达式 </br>
     * 不符合不一定错误，符合保证正确
     *
     * @param conditions 条件List //5 2 3 4~~
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
     * 根据给定的单行\列条件，得到对应的部分匹配正则表达式 </br>
     * 不符合一定错误，符合不一定正确
     *
     * @param conditions 条件List //5 2 3 4~~
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

    /**
     * 根据给定条件，判断给定行是否满足 考虑到每个串都是从0开始顺序填写，e可以只考虑尾部
     *
     * @param lineStr   行数据
     * @param condition 条件
     * @return -1：不满足；0：行未填写完，暂时满足；1：完全ok
     */
    public static int check(String lineStr, List<Integer> condition) {
        boolean lineUnfinished = false;
        boolean requireUnfinished = false;

        String lineStrCopy = new String(lineStr);
        if (-1 != lineStr.indexOf("E")) {
            lineUnfinished = true;
        }

        // 将该行所有未填写转化为E，用E分割，得到填写情况数组，用于与条件比较
        lineStrCopy = lineStrCopy.replace("B", "E");
        String[] judgeArray = lineStrCopy.split("E+");

        // 再转成list，去掉空串(连续的E)
        List<String> judgeList = new ArrayList<String>();
        for (String string : judgeArray) {
            if (string.length() > 0)
                judgeList.add(string);
        }

        // 整串长度限制
        if (judgeList.size() > condition.size())
            return -1;

        if (judgeList.size() < condition.size())
            requireUnfinished = true;

        // 最后一格前，需要完全匹配
        int index = 0;
        while (index < judgeList.size() - 2) {
            if (judgeList.get(index).length() != condition.get(index))
                return -1;
            index++;
        }

        if (judgeList.size() > 0) {
            // 最后一格，单独判断
            int lastIndex = judgeList.size() - 1;
            if (judgeList.get(lastIndex).length() > condition.get(lastIndex))
                return -1;
            if (judgeList.get(lastIndex).length() < condition.get(lastIndex))
                requireUnfinished = true;
        } else
            requireUnfinished = true;
        if (requireUnfinished) {
            if (lineUnfinished)
                return 0;
            else
                return -1;
        }
        return 1;
    }
}