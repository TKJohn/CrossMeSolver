package net.tkfan.calcMark;

import org.apache.commons.collections.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 缓冲区<br>棋盘填充方式-校验结果
 *
 * @auther John
 * Created by John on 2016/6/28.
 */
public class JudgeBuffer {
    private static final Logger logger = LoggerFactory.getLogger(JudgeBuffer.class);

    private static final Map<JudgeItem, JudgeResult> judgeBuffer = new LRUMap(PublicConstants.MAX_JUDGE_BUFFER_SIZE);

    public static JudgeResult getResult(JudgeItem judgeItem) {
        return judgeBuffer.get(judgeItem);
    }

    public static JudgeResult getResult(PublicConstants.LineRowType type, int position, String judgeItem) {
        return getResult(new JudgeItem(type, position, judgeItem));
    }

    public static void setResult(JudgeItem judgeItem, JudgeResult result) {
        judgeBuffer.put(judgeItem, result);
    }

    public static void setResult(PublicConstants.LineRowType type, int position, String judgeItem, JudgeResult result) {
        setResult(new JudgeItem(type, position, judgeItem), result);
    }

    public static void printStatus() {
        LRUMap map = (LRUMap) judgeBuffer;
        logger.debug("map status {}", map.isFull());
    }
}
