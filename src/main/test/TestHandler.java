import com.swust.handler.IdCardGenerate;
import org.junit.Test;

/**
 * @author hz20035009-逍遥
 * date   2020/9/14 20:30
 */
public class TestHandler {
    @Test
    public void testIdCard() {
        for (int i = 0; i < 200; i++) {
            System.out.println(new IdCardGenerate().generateV());
            System.out.println("-------------------");
        }
    }
}