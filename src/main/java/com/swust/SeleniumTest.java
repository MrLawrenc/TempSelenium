package com.swust;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * @author : MrLawrenc
 * date  2020/9/12 14:47
 * <p>
 * selenium试用
 * 还不错
 */
@Data
public class SeleniumTest {


    private WebDriver driver;

    public WebDriver initDriver() {
        if (null == driver) {
            this.driver = new ChromeDriver();
        }
        return driver;
    }

    public static void main(String[] args) throws Exception {
        SeleniumTest seleniumTest = new SeleniumTest();
        seleniumTest.initDriver();

        // h5();

        //慧择核保
        //外网
        String url = "https://www.huize.com/apps/cps/index/product/insure?prodId=101832&planId=104245&cuid=d7a4f903-89e5-4dd5-a486-312d64a6d4b8&aid=&encryptInsureNum=";
        //p版
        //String url = "https://cps.qixin18.com/apps/cps/lxr1000014/product/insure?prodId=121482&planId=122830&cuid=213567b7-4c1c-48b9-9e85-4eda6e0448d2&aid=&encryptInsureNum=aKCN_w73h-TEOqKSb6dBCQ&notifyAnswerId=3526018&isHealthSuccess=true";
        seleniumTest.openBrowser(url);
        for (int i = 0; i < 1; i++) {
            seleniumTest.fullCheckInfo();
        }

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

    /**
     * chrome启动参数设置和快捷键试用
     */
    public static void h5() {
        ChromeOptions chromeoptions = new ChromeOptions();
        //无头浏览
        //chromeoptions.addArguments("--headless");
        //禁用图片
        //chromeoptions.addArguments("blink-settings=imagesEnabled=false");


        //模拟h5可以
        Map<String, String> mobileEmulation = new HashMap<String, String>();
        //设置设备,例如:Google Nexus 7/Apple iPhone 6
        //mobileEmulation.put("deviceName", "Google Nexus 7");
        //这里是要使用的模拟器名称，就是浏览器中模拟器中的顶部型号
        mobileEmulation.put("deviceName", "iPhone X");
        //chromeoptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        try {
            System.out.println("开始启动driver~~~");
            ChromeDriver driver = new ChromeDriver(chromeoptions);
            System.out.println("启动driver成功~~~");
            driver.get("https://www.baidu.com");
            //可以
            // driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.CONTROL, "a"));

            driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL, "t");


            //new Actions(driver).keyDown(Keys.LEFT_CONTROL).sendKeys("t").keyUp(Keys.LEFT_CONTROL).perform();
            /*Robot robot0 = new Robot();
            robot0.keyPress(KeyEvent.VK_F12);
            robot0.keyRelease(KeyEvent.VK_F12);

            //可以 推荐
            robot0.keyPress(KeyEvent.VK_CONTROL);
            robot0.keyPress(KeyEvent.VK_T);*/

           /*  robot0.keyRelease(KeyEvent.VK_CONTROL);
            robot0.keyRelease(KeyEvent.VK_T);*/

            //打开新标签页或者打开指定网页 可以的
          /*
　　　         // 打开一个空的新标签
　　　         js.executeScript( "window.open('about:blank');" );

           String s="window.open(\"https://www.google.com\",\"_blank\");";
            JavascriptExecutor js = driver;
            js.executeScript(s);*/
        } catch (Exception e) {
            System.out.println("启动driver失败~~~");
            System.out.println(e.getMessage());
        }

    }

    public void openBrowser(String url) {
        if (StrUtil.isEmpty(url)) {
            //外网
            // url = "https://www.huize.com/apps/cps/index/product/insure?prodId=101832&planId=104245&cuid=d7a4f903-89e5-4dd5-a486-312d64a6d4b8&aid=&encryptInsureNum=";
            //p版
            url = "https://cps.qixin18.com/apps/cps/lxr1000014/product/insure?prodId=121482&planId=122830&cuid=213567b7-4c1c-48b9-9e85-4eda6e0448d2&aid=&encryptInsureNum=aKCN_w73h-TEOqKSb6dBCQ&notifyAnswerId=3526018&isHealthSuccess=true";
        }
        driver.get(url);
    }

    public void quitBrowser() {
        driver.quit();
    }

    /**
     * 如果存在就点击取消按钮(回退核保会有弹窗"是否继续投保")
     */
    public void ifCancelExistClick(Duration duration) {
        WebDriverWait wait0 = new WebDriverWait(driver, duration);
        By cancelParent = By.id("layui-layer1");
        try {
            wait0.until(ExpectedConditions.presenceOfElementLocated(cancelParent));
        } catch (Exception ignore) {
            return;
        }
        WebElement element2 = driver.findElement(cancelParent);
        for (WebElement element : element2.findElements(By.className("layui-layer-btn1"))) {
            if ("取消".equals(element.getText())) {
                element.click();
            }
        }
    }

    public void fullCheckInfo() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

/*        WebDriver.Window window = driver.manage().window();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        window.setSize(new org.openqa.selenium.Dimension((int) (screen.getWidth() / 10 * 7), (int) (screen.getHeight() / 10 * 8)));
        window.setPosition(new org.openqa.selenium.Point(200, 200));*/


        ifCancelExistClick(Duration.ofSeconds(1));


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        // 查找目标元素是否加载出来了（已经在页面DOM中存在）
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("cName_10")));

        //投保人名字和证件
        driver.findElement(By.name("cName_10")).sendKeys("假名字");
        driver.findElement(By.name("cardNumber_10")).sendKeys("513822199912016898");

        //投保人地址
        driver.findElement(By.name("contactAddress_10")).sendKeys("高新区新港国际花园2栋3单元5楼");
        //投保人电话
        driver.findElement(By.name("moblie_10")).sendKeys("17745846213");
        //投保人邮箱
        driver.findElement(By.name("email_10")).sendKeys("1774584test@huize.com");

        //投保人年收入
        if (exist(driver, By.name("yearlyIncome_10"))) {
            driver.findElement(By.name("yearlyIncome_10")).sendKeys("100000");
        }

        //投保人职业 可以优化查找
        By jobBy = By.xpath("//*[@id=\"insure-pannel\"]/dl[3]/dd[8]/div/div[1]/div[1]/b");
        if (exist(driver, jobBy)) {
            By liBy = By.tagName("li");
            driver.findElement(jobBy).click();
            WebElement firstUl = driver.findElement(By.xpath("//*[@id=\"insure-pannel\"]/dl[3]/dd[8]/div/div[1]/div[2]/ul"));
            List<WebElement> jobList = firstUl.findElements(liBy);
            WebElement firstJob = jobList.get(new Random().nextInt(jobList.size() - 1) + 1);
            firstJob.click();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.findElement(By.xpath("//*[@id=\"insure-pannel\"]/dl[3]/dd[8]/div/div[2]/div[1]/b")).click();
            WebElement secondUl = driver.findElement(By.xpath("//*[@id=\"insure-pannel\"]/dl[3]/dd[8]/div/div[2]/div[2]/ul"));
            List<WebElement> secondJobList = secondUl.findElements(liBy);
            WebElement secondJob = secondJobList.get(new Random().nextInt(secondJobList.size() - 1) + 1);
            secondJob.click();
        }


        //投保人证件有效期
        driver.findElement(By.name("cardPeriod_10")).click();
        List<WebElement> tdList = driver.findElements(By.tagName("td"));
        tdList.stream().filter(t -> date.equals(t.getAttribute("title"))).findAny().ifPresent(WebElement::click);


        //投保人省市下拉框
        List<WebElement> areaList = driver.findElements(By.name("provCityText_10"));
        areaList.get(0).findElement(By.tagName("b")).click();
        areaList.get(0).findElements(By.tagName("li")).get(2).click();
        //上面选择之后，会追加子标签，待完成之后才能继续添加，这里显示等待，也可以隐式等待
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        areaList.get(1).findElement(By.tagName("b")).click();
        List<WebElement> cityLiList = areaList.get(1).findElements(By.tagName("li"));
        cityLiList.get(new Random().nextInt(cityLiList.size() - 1) + 1).click();
        try {
            Thread.sleep(2000);
            areaList.get(2).findElement(By.tagName("b")).click();
            List<WebElement> areaListNode = areaList.get(2).findElements(By.tagName("li"));
            areaListNode.get(new Random().nextInt(areaListNode.size() - 1) + 1).click();
        } catch (Exception ignore) {
            System.out.println("没有选三级地区");
        }


        //非本人投保再填充被保人
        if (!"本人".equals(driver.findElement(By.xpath("//*[@id=\"insurantType\"]/div[1]/span")).getText())) {
            System.out.println("存在被保人");

            if (exist(driver, By.name("relationInsureInsurant_20_default_1"))) {
                //被保人和投保人关系
                WebElement element = driver.findElement(By.name("relationInsureInsurant_20_default_1"));
                //弹出选择框
                element.findElement(By.tagName("b")).click();
                //随机选择一个选项
                List<WebElement> relationList = element.findElements(By.tagName("li"));
                relationList.get(new Random().nextInt(relationList.size() - 1) + 1).click();
            }

            //被保人和投保人关系
            WebElement element = driver.findElement(By.name("relationInsureInsurant_20_default_1"));
            //弹出选择框
            element.findElement(By.tagName("b")).click();
            //随机选择一个选项
            List<WebElement> relationList = element.findElements(By.tagName("li"));
            relationList.get(new Random().nextInt(relationList.size() - 1) + 1).click();

            //被保人证件有效期
            driver.findElement(By.name("cardPeriod_20_default_1")).click();
            List<WebElement> tdList1 = driver.findElements(By.tagName("td"));
            tdList1.stream().filter(t -> date.equals(t.getAttribute("title"))).findAny().ifPresent(WebElement::click);

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
            String end = Integer.valueOf(date.substring(0, 4)) + a + "";
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
            String month = date.substring(5, 7);
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
            String targetDate = Integer.valueOf(date.substring(0, 4)) + a + date.substring(4);
            List<WebElement> tdList2 = driver.findElements(By.tagName("td"));
            tdList2.stream().filter(t -> targetDate.equals(t.getAttribute("title"))).findAny().ifPresent(WebElement::click);


            //被保人姓名和证件号码
            driver.findElement(By.name("cName_20_default_1")).sendKeys("我是被保人");
            driver.findElement(By.name("cardNumber_20_default_1")).sendKeys("513822199911116898");
        }

        //续期银行信息
        By syr = By.xpath("//*[@id=\"insure-pannel\"]/dl[7]/div/dd[3]/input");
        if (exist(driver, syr)) {
            driver.findElement(syr).sendKeys("1152121223235513");
        }

        //submit
        //driver.findElement(By.id("submit")).findElement(By.tagName("span")).click();
    }

    /**
     * 检查元素是否存在
     */
    public static boolean exist(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}