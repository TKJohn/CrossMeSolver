package net.tkfan.calcMark;

/**
 * 保存给定行列位置上，某一填充方式<br>
 * 用于缓冲正则校验结果，作为Key
 *
 * @author John
 */
public class JudgeItem {
    /**
     * 该对象为行还是列
     */
    private PublicConstants.LineRowType type;

    /**
     * 该行/列在棋盘的位置
     */
    private int position;

    /**
     * 本次需要判断的填充字符串
     */
    private String judgeItem;

    /**
     * 本行/列对应棋盘大小
     */
    private int judgeSize;

    public JudgeItem(PublicConstants.LineRowType type, int position, String judgeItem){
        this.type=type;
        this.position=position;
        this.judgeItem=judgeItem;
    }

    public PublicConstants.LineRowType getType() {
        return type;
    }

    public void setType(PublicConstants.LineRowType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getJudgeItem() {
        return judgeItem;
    }

    public void setJudgeItem(String judgeItem) {
        this.judgeItem = judgeItem;
    }

    public int getJudgeSize() {
        return judgeSize;
    }

    public void setJudgeSize(int judgeSize) {
        this.judgeSize = judgeSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JudgeItem)) return false;

        JudgeItem judgeItem1 = (JudgeItem) o;

        if (!getJudgeItem().equals(judgeItem1.getJudgeItem())) return false;
        if (getPosition() != judgeItem1.getPosition()) return false;
        if (getType() != judgeItem1.getType()) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + getPosition();
        result = 31 * result + getJudgeItem().hashCode();
        return result;
    }
}
