package com.swust;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Dimension;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * @author : MrLawrenc
 * date  2020/9/12 14:47
 * <p>
 * selenium试用
 * 还不错
 */
public class SeleniumTest {
    static WebDriver driver = new ChromeDriver();

    public static void main(String[] args) throws Exception {

        h5();
   /*     for (int i = 0; i < 3; i++) {
            hzCheck(driver);
        }
*/

        //test();
    }


    public static void test() throws Exception {
        // 1.创建webdriver驱动
        WebDriver driver = new ChromeDriver();
        // 2.打开百度首页
        driver.get("https://www.baidu.com");

        // 3.获取输入框，输入selenium
        driver.findElement(By.id("kw")).sendKeys("selenium");
        // 4.获取“百度一下”按钮，进行搜索
        driver.findElement(By.id("su")).click();


        //不能更改的输入框可以使用js赋值
        // 获取js执行器
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // 对时间输入框进入赋值
        //String script = "document.getElementByName('cardPeriod_10').value='2020-03-30';";
        String script = "alert(\"打开新标签页\");";
        // 执行
        //js.executeScript(script);

        //执行chrome快捷键打开新标签页
        Actions actions = new Actions(driver);
        Thread.sleep(1000);
        //ctr+t 快捷方式新打开一个标签页
        actions.keyDown(Keys.CONTROL).sendKeys("t").keyUp(Keys.CONTROL).build().perform();


        driver.get("https://www.baidu.com");

        //driver.navigate().to("");

        for (String s : driver.getWindowHandles()) {
            System.out.println(s);
        }


        // 5.退出浏览器
        // driver.quit();
    }

    public static void h5() {
        Map<String, String> mobileEmulation = new HashMap<String, String>();
        //设置设备,例如:Google Nexus 7/Apple iPhone 6
        //mobileEmulation.put("deviceName", "Google Nexus 7");
        //这里是要使用的模拟器名称，就是浏览器中模拟器中的顶部型号
        mobileEmulation.put("deviceName", "iPad");
        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        try {
            System.out.println("开始启动driver~~~");
            ChromeDriver driver = new ChromeDriver(capabilities);
            System.out.println("启动driver成功~~~");
            driver.get("https://www.baidu.com");
            //可以
           driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.CONTROL, "a"));
          /*   driver.findElement(By.tagName("html")).sendKeys(Keys.F12);
            driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "T"));*/

            //new Actions(driver).keyDown(Keys.CONTROL).keyDown(Keys.ALT).keyDown("A").keyUp(Keys.CONTROL).keyUp(Keys.ALT).keyUp("A").perform();

            //new Actions(driver).keyDown(Keys.LEFT_CONTROL).sendKeys("t").keyUp(Keys.LEFT_CONTROL).perform();
            /*Robot robot0 = new Robot();
            robot0.keyPress(KeyEvent.VK_F12);
            robot0.keyRelease(KeyEvent.VK_F12);

            //可以 推荐
            robot0.keyPress(KeyEvent.VK_CONTROL);
            robot0.keyPress(KeyEvent.VK_T);*/

           /*  robot0.keyRelease(KeyEvent.VK_CONTROL);
            robot0.keyRelease(KeyEvent.VK_T);*/


            String s="window.open(\"https://www.baidu.com\",\"_blank\");";
            JavascriptExecutor js = driver;
            js.executeScript(s);
        } catch (Exception e) {
            System.out.println("启动driver失败~~~");
            System.out.println(e.getMessage());
        }

    }

    public static void hzCheck(WebDriver driver) throws Exception {
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String url = "https://www.huize.com/apps/cps/index/product/insure?prodId=101832&planId=104245&cuid=d7a4f903-89e5-4dd5-a486-312d64a6d4b8&aid=&encryptInsureNum=";

        driver.get(url);
        WebDriver.Window window = driver.manage().window();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        window.setSize(new org.openqa.selenium.Dimension((int) (screen.getWidth() / 10 * 7), (int) (screen.getHeight() / 10 * 8)));
        window.setPosition(new org.openqa.selenium.Point(200, 200));

        WebDriverWait wait0 = new WebDriverWait(driver, 1);
        try {
            wait0.until(ExpectedConditions.presenceOfElementLocated(By.id("layui-layer1")));
            WebElement element2 = driver.findElement(By.id("layui-layer1"));
            for (WebElement element : element2.findElements(By.className("layui-layer-btn1"))) {
                if ("取消".equals(element.getText())) {
                    element.click();
                }
            }
        } catch (Exception ignore) {
        }


        WebDriverWait wait = new WebDriverWait(driver, 5);
        // 查找目标元素是否加载出来了（已经在页面DOM中存在）
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("cName_10")));
        driver.findElement(By.name("cName_10")).sendKeys("假名字");
        driver.findElement(By.name("cardNumber_10")).sendKeys("513822199912016898");
        driver.findElement(By.name("yearlyIncome_10")).sendKeys("100000");
        driver.findElement(By.name("contactAddress_10")).sendKeys("高新区新港国际花园2栋3单元5楼");
        driver.findElement(By.name("moblie_10")).sendKeys("17745846213");
        driver.findElement(By.name("email_10")).sendKeys("1774584test@huize.com");


        //被保人和投保人关系
        WebElement element = driver.findElement(By.name("relationInsureInsurant_20_default_1"));
        //弹出选择框
        element.findElement(By.tagName("b")).click();
        //随机选择一个选项
        List<WebElement> relationList = element.findElements(By.tagName("li"));
        relationList.get(new Random().nextInt(relationList.size() - 1) + 1).click();

        //投保人证件有效期
        driver.findElement(By.name("cardPeriod_10")).click();
        List<WebElement> tdList = driver.findElements(By.tagName("td"));
        tdList.stream().filter(t -> format.equals(t.getAttribute("title"))).findAny().ifPresent(WebElement::click);

        //被保人证件有效期
        driver.findElement(By.name("cardPeriod_20_default_1")).click();
        List<WebElement> tdList1 = driver.findElements(By.tagName("td"));
        tdList1.stream().filter(t -> format.equals(t.getAttribute("title"))).findAny().ifPresent(WebElement::click);

/*        // 获取js执行器
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //通过js来移除readonly属性
        //获取元素，清理之后再赋值
        WebElement endDate = driver.findElement(By.name("cardPeriodEnd_20_default_1"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].removeAttribute('readonly','readonly')",endDate);
        endDate.clear();
        endDate.sendKeys("2030-03-30");*/


        //被保人证件结束日期
        driver.findElement(By.name("cardPeriodEnd_20_default_1")).click();
        //计算身份证有效期 fix(假设10年)
        int a = 10;
        String end = Integer.valueOf(format.substring(0, 4)) + a + "";
        System.out.println("目标年:" + end);
        //先选中 年
        success:
        while (true) {
            List<WebElement> yearTdList = driver.findElement(By.className("calendar-year-body")).findElements(By.tagName("td"));
            for (WebElement webElement : yearTdList) {
                if (end.equals(webElement.getText())) {
                    webElement.click();
                    break success;
                }
            }
            //翻页
            WebElement element1 = driver.findElement(By.className("calendar-year-next"));
            element1.findElement(By.tagName("span")).click();
        }
        //选中月
        String month = format.substring(5, 7);
        if (month.startsWith("0")) {
            month = month.substring(1);
        }
        month += "月";
        List<WebElement> td = driver.findElement(By.className("calendar-month-table-box")).findElements(By.tagName("td"));
        for (WebElement webElement : td) {
            if (webElement.getText().equals(month)) {
                webElement.click();
                break;
            }
        }
        //选中 日
        String targetDate = Integer.valueOf(format.substring(0, 4)) + a + format.substring(4);
        List<WebElement> tdList2 = driver.findElements(By.tagName("td"));
        tdList2.stream().filter(t -> targetDate.equals(t.getAttribute("title"))).findAny().ifPresent(WebElement::click);


        //被保人姓名和证件号码
        driver.findElement(By.name("cName_20_default_1")).sendKeys("我是被保人");
        driver.findElement(By.name("cardNumber_20_default_1")).sendKeys("513822199911116898");


        //投保人省市下拉框
        List<WebElement> areaList = driver.findElements(By.name("provCityText_10"));
        areaList.get(0).findElement(By.tagName("b")).click();
        areaList.get(0).findElements(By.tagName("li")).get(2).click();

        //上面选择之后，会追加子标签，待完成之后才能继续添加，这里显示等待，也可以隐式等待
        Thread.sleep(1000);
        areaList.get(1).findElement(By.tagName("b")).click();

        List<WebElement> cityLiList = areaList.get(1).findElements(By.tagName("li"));
        cityLiList.get(new Random().nextInt(cityLiList.size() - 1) + 1).click();

        Thread.sleep(2000);
        areaList.get(2).findElement(By.tagName("b")).click();
        List<WebElement> areaListNode = areaList.get(2).findElements(By.tagName("li"));
        areaListNode.get(new Random().nextInt(areaListNode.size() - 1) + 1).click();

        //submit
        // driver.findElement(By.id("submit")).findElement(By.tagName("span")).click();
    }
}