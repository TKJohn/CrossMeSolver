import net.tkfan.calcMark.PublicUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class TestPublicUtils {

    @Test
    public void testCheck() {
        String input1 = "BBSSSBSSEEE";
        String input2 = "BBSSSBSSBEE";
        String input3 = "BBSSSBSSSSEE";
        String input4 = "BBSSSSSSSSEE";
        String input5 = "SSEEEEEEE";
        String input6 = "EEEEEEE";
        List<Integer> condition = new ArrayList<>();
        condition.add(3);
        condition.add(4);
        assertEquals(0, PublicUtils.check(input1, condition));
        // assertEquals(-1, PublicUtils.check(input2, condition));
        assertEquals(1, PublicUtils.check(input3, condition));
        assertEquals(-1, PublicUtils.check(input4, condition));
        assertEquals(0, PublicUtils.check(input5, condition));
        assertEquals(0, PublicUtils.check(input6, condition));
    }

    @Test
    public void testCheckPerformance() {
        String input1 = "SSBBSEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE";
        // [EB]*?[ES]{2}[EB]+?[ES]{6}[EB]+?[ES]{2}[EB]+?[ES]{8}[EB]+?[ES]{1}[EB]*?
        List<Integer> condition = new ArrayList<>();
        condition.add(2);
        condition.add(6);
        condition.add(2);
        condition.add(8);
        condition.add(1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++)
            assertEquals(0, PublicUtils.check(input1, condition));
        long end = System.currentTimeMillis();
        long costtime = end - start;
        System.out.println(costtime);
    }

    @Test
    public void testRegPerformance() {
        String input1 = "SSBBSEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE";
        // [EB]*?[ES]{2}[EB]+?[ES]{6}[EB]+?[ES]{2}[EB]+?[ES]{8}[EB]+?[ES]{1}[EB]*?
        List<Integer> condition = new ArrayList<>();
        condition.add(2);
        condition.add(6);
        condition.add(2);
        condition.add(8);
        condition.add(1);
        String solvingStr = PublicUtils.getSolvingMatchStr(condition);
        Pattern solvingPattern = Pattern.compile(solvingStr);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++)
            assertEquals(true, solvingPattern.matcher(input1).matches());
        long end = System.currentTimeMillis();
        long costtime = end - start;
        System.out.println(costtime);
    }
}
