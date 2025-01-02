package com.example.coworking.classLoader;

import java.io.FileInputStream;
import java.io.IOException;

public class CustomClassLoader extends ClassLoader {
    private String classPath;

    public CustomClassLoader(String classPath) {

        this.classPath = classPath;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String fileName = classPath + ".class";
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] classBytes = inputStream.readAllBytes();
            inputStream.close();
            return defineClass(name, classBytes, 0, classBytes.length);
        }   catch (IOException e) {
            throw new ClassNotFoundException("Could not load class " + name, e);
        }
    }
}
