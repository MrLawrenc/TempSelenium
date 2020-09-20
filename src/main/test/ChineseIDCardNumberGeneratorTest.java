import cn.binarywang.tools.generator.ChineseIDCardNumberGenerator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author : MrLawrenc
 * date  2020/9/17 21:25
 */
public class ChineseIDCardNumberGeneratorTest {

    @Test
    public void idCard() {
        String idCard = ChineseIDCardNumberGenerator.getInstance().generate();
        System.err.println(idCard);
        assertNotNull(idCard);
        if (idCard.charAt(idCard.length()-2)%2 == 0){
            System.err.println("女");
        } else {
            System.err.println("男");
        }

    }

    @Test
    public void testGenerateIssueOrg() {
        String issueOrg = ChineseIDCardNumberGenerator.generateIssueOrg();
        System.err.println(issueOrg);
        assertNotNull(issueOrg);
    }

    @Test
    public void testGenerateValidPeriod() {
        String result = ChineseIDCardNumberGenerator.generateValidPeriod();
        System.err.println(result);
        assertNotNull(result);
    }

}