import cn.binarywang.tools.generator.ChineseAddressGenerator;
import cn.binarywang.tools.generator.ChineseMobileNumberGenerator;
import cn.binarywang.tools.generator.ChineseNameGenerator;
import cn.binarywang.tools.generator.EmailAddressGenerator;
import cn.binarywang.tools.generator.bank.BankCardNumberGenerator;
import cn.binarywang.tools.generator.bank.BankCardTypeEnum;
import cn.binarywang.tools.generator.bank.BankNameEnum;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author : MrLawrenc
 * date  2020/9/17 21:22
 */

public class GeneratorTest {
    @Test
    public void testGenerate() {
        String generatedMobileNum = ChineseMobileNumberGenerator.getInstance()
                .generate();
        assertNotNull(generatedMobileNum);
        System.err.println(generatedMobileNum);
    }
    @Test
    public void testGgenerateFake() {
        String generatedMobileNum = ChineseMobileNumberGenerator.getInstance()
                .generateFake();
        assertNotNull(generatedMobileNum);
        System.err.println(generatedMobileNum);
    }

    @Test
    public void generatedAddress() {
        String generatedAddress = ChineseAddressGenerator.getInstance()
                .generate();
        System.err.println(generatedAddress);
        assertNotNull(generatedAddress);
    }

    @Test
    public void generatedName() {
        String generatedName = ChineseNameGenerator.getInstance().generate();
        assertNotNull(generatedName);
        System.err.println(generatedName);
    }

    @Test
    public void generatedNameOdd() {
        String generatedName = ChineseNameGenerator.getInstance().generateOdd();
        assertNotNull(generatedName);
        System.err.println(generatedName);
    }

    @Test
    public void generatedEmail() {
        String generatedEmail = EmailAddressGenerator.getInstance().generate();
        System.err.println(generatedEmail);
        assertNotNull(generatedEmail);
    }


    @Test
    public void testGenerate_by_bankName() {
        String bankCardNo = BankCardNumberGenerator.generate(BankNameEnum.CR, null);
        System.err.println(bankCardNo);
        assertNotNull(bankCardNo);

        bankCardNo = BankCardNumberGenerator.generate(BankNameEnum.ICBC, BankCardTypeEnum.CREDIT);
        System.err.println(bankCardNo);
        assertNotNull(bankCardNo);

        bankCardNo = BankCardNumberGenerator.generate(BankNameEnum.ICBC, BankCardTypeEnum.DEBIT);
        System.err.println(bankCardNo);
        assertNotNull(bankCardNo);
    }

    @Test
    public void testGenerateByPrefix() {
        String bankCardNo = BankCardNumberGenerator.generateByPrefix(436742);
        System.err.println(bankCardNo);
        assertNotNull(bankCardNo);
    }

    @Test
    public void bankCardNo() {
        String bankCardNo = BankCardNumberGenerator.getInstance().generate();
        System.err.println(bankCardNo);
        assertNotNull(bankCardNo);
    }
}