package com.github.mrlawrenc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hz20035009-逍遥
 * date   2020/9/25 10:52
 * <p>
 * 脚本加载器
 */
@Slf4j
public class ScriptLoader extends ClassLoader {
    private Map<String, Class<?>> loaded = new HashMap<>();

    private final String prePath = new File("./").getAbsolutePath() + "script/";

    protected Class<?> findClass(String clzFullName) throws ClassNotFoundException {
        try {
            String cname = prePath + clzFullName.replace('.', '/') + ".class";
            byte[] classBytes = Files.readAllBytes(Paths.get(cname));
            Class<?> cl = defineClass(clzFullName, classBytes, 0, classBytes.length);
            if (cl == null) {
                throw new ClassNotFoundException(clzFullName);
            }
            return cl;
        } catch (IOException e) {
            throw new ClassNotFoundException(clzFullName, e);
        }
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }


    public void load(String clzFullName, byte[] clzByte) {
        Class<?> clz = defineClass(clzFullName, clzByte, 0, clzByte.length);
        loaded.put(clzFullName, clz);
    }


    public void loadJavaFile(String javaCode) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            writer.append(javaCode);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("write to writer fail", e);
            return;
        }

        loadJavaFile0(writer, null, javaCode);
    }

    public static void main(String[] args) throws Exception {
        //获取系统Java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //获取Java文件管理器
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        //定义要编译的源文件
        File file = new File("F:\\tmp\\Test.java");
        //通过源文件获取到要编译的Java类源码迭代器，包括所有内部类，其中每个类都是一个 JavaFileObject，也被称为一个汇编单元
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(file);
        //生成编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        //执行编译任务
        task.call();

        fileManager.close();
    }

    public void loadJavaFile(File file) {

        compileJava(file);

        String pkg = "";
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line = fr.readLine();
            while (StringUtils.isNotEmpty(line)) {
                if (line.startsWith("package ")) {
                    pkg = line.substring(7).replaceAll(" ", "");
                    break;
                }
                line = fr.readLine();
            }
        } catch (Exception e) {
            log.error("parse package name fail");
            return;
        }
        log.info("parse package name success, name={}", pkg);

        String absolutePath = file.getAbsolutePath();
        String classPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator) + 1) + "Test.class";
        Class<?> result = loadClassByPath(classPath, pkg);
        try {
            System.out.println("result:" + result.newInstance().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 废弃
     */
    @Deprecated
    public void loadJavaFile0(Writer writer, File javaFile, String javaCode) {
        try {
            // 编译
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);

            Iterable<? extends JavaFileObject> fileObject;
            if (StringUtils.isEmpty(javaCode)) {
                fileObject = fileManager.getJavaFileObjects(javaFile);
            } else {
                fileObject = fileManager.getJavaFileObjects(javaCode);
            }
            JavaCompiler.CompilationTask task = compiler.getTask(writer, fileManager, null, null, null, fileObject);
            task.call();
            fileManager.close();


            // 加载运行 ClassLoader只能加载bin目录下的class文件
            URL[] urls = new URL[]{new URL("file:/F:\\tmp\\")};
            URLClassLoader ucl = new URLClassLoader(urls);
            Class<?> clazz = ucl.loadClass("Test");

            log.info("load success class:{}", clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据class文件路径加载
     *
     * @param classPath   class文件全路径
     * @param packageName 包名
     * @return load之后的class文件
     */
    public Class<?> loadClassByPath(String classPath, String packageName) {
        String clzName = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
        String fullClzName = StringUtils.isEmpty(packageName) ? clzName : packageName + "." + clzName;
        try {
            URL[] urls = new URL[]{new URL(new File(classPath).getParentFile().toURL().toString())};
            URLClassLoader ucl = new URLClassLoader(urls);
            Class<?> r = ucl.loadClass(fullClzName);
            log.info("load success class:{}", fullClzName);
            return r;
        } catch (Exception e) {
            log.info("load fail class:{}", fullClzName);
            return null;
        }
    }

    /**
     * 编译java文件到当前file所处目录
     *
     * @param file java源文件
     */
    public void compileJava(File file) {
        compileJava(file, null);
    }

    /**
     * 编译java文件到writer
     *
     * @param file java源文件
     */
    public void compileJava(File file, Writer writer) {
        try {
            //获取系统Java编译器
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            //获取Java文件管理器
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            //定义要编译的源文件
            //File file = new File("F:\\tmp\\Test.java");

            //通过源文件获取到要编译的Java类源码迭代器，包括所有内部类，其中每个类都是一个 JavaFileObject，也被称为一个汇编单元
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(file);
            //生成编译任务 第一个参数是输出到哪个位置，为null会默认输出到java文件同级
            JavaCompiler.CompilationTask task = compiler.getTask(writer, fileManager, null, null, null, compilationUnits);

            //执行编译任务
            task.call();

            fileManager.close();

        } catch (Exception e) {
            log.error("compile java fail", e);
        }
    }
}