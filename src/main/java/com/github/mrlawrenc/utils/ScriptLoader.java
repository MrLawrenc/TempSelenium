package com.github.mrlawrenc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Field;
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

    public void loadJavaFile(File file) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            log.error("write to writer fail", e);
            return;
        }

        loadJavaFile0(fileWriter, file, null);
       /* compileJavaCode(fileWriter, file, null);

        String pkg = "";
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line = fr.readLine();
            while (StringUtils.isNotEmpty(line)) {
                if (line.startsWith("package ")) {
                    pkg = line.substring(7).replaceAll(" ", "");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("parse package name fail");
            return;
        }
        log.info("parse package name success, name={}", pkg);


        loadClassByPath("E:\\temp\\test\\Test.class", pkg);*/
    }


    public void compileJavaCode(Writer writer, File javaFile, String javaCode) {
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

        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void loadClassByPath(String classPath, String pkgPreName) {
        String clzName = classPath.substring(classPath.lastIndexOf(File.separator) + 1, classPath.lastIndexOf("."));
        String fullClzName = StringUtils.isEmpty(pkgPreName) ? clzName : pkgPreName + "." + clzName;

        Class<?> clazz = null;
        try {
            URL[] urls = new URL[]{new URL(new File(classPath).getParentFile().toURL().toString())};
            URLClassLoader ucl = new URLClassLoader(urls);
            clazz = ucl.loadClass(fullClzName);
        } catch (Exception e) {
            log.error("load class({}) fail", clzName, e);
            return;
        }

        log.info("load success class:{}", clazz);
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(field.getType() + "  " + field.getName());
        }
    }

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
            URL[] urls = new URL[]{new URL("file:/E:\\temp\\test")};
            URLClassLoader ucl = new URLClassLoader(urls);
            Class<?> clazz = ucl.loadClass("Test");

            log.info("load success class:{}", clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}