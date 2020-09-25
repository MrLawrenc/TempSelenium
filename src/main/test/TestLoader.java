import com.github.mrlawrenc.utils.ScriptLoader;
import org.junit.Test;

import java.io.File;

/**
 * @author hz20035009-逍遥
 * date   2020/9/25 13:50
 */
public class TestLoader {

    @Test
    public void loadJava() {
        String path="E:\\temp\\test\\";
        new ScriptLoader().loadJavaFile(new File(path+"/Test.java"));
    }

}