/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.edurt.tutorial.presto.scalar;

import com.facebook.presto.spi.Plugin;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p> FunctionLoadPlugin </p>
 * <p> Description : FunctionLoadPlugin </p>
 * <p> Author : qianmoQ </p>
 * <p> Version : 1.0 </p>
 * <p> Create Time : 2020-04-27 10:34 </p>
 * <p> Author Email: <a href="mailTo:shichengoooo@163.com">qianmoQ</a> </p>
 */
public class FunctionLoadPlugin implements Plugin {

    private List<Class<?>> getFunctionClasses() throws IOException {
        List<Class<?>> classes = Lists.newArrayList();
        String classResource = this.getClass().getName().replace(".", "/") + ".class";
        String jarURLFile = Thread.currentThread().getContextClassLoader().getResource(classResource).getFile();
        int jarEnd = jarURLFile.indexOf('!');
        String jarLocation = jarURLFile.substring(0, jarEnd); // This is in URL format, convert once more to get actual file location
        jarLocation = new URL(jarLocation).getFile();

        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarLocation));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                String className = entry.getName().replace("/", "."); // This still has .class at the end
                className = className.substring(0, className.length() - 6); // remvove .class from end
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                }
            }
        }
        return classes;
    }

    @Override
    public Set<Class<?>> getFunctions() {
        try {
            List<Class<?>> classes = getFunctionClasses();
            Set<Class<?>> set = Sets.newHashSet();
            for (Class<?> clazz : classes) {
                if (clazz.getName().startsWith("io.edurt.tutorial.presto.scalar.function")) {
                    set.add(clazz);
                }
            }
            return ImmutableSet.<Class<?>>builder().addAll(set).build();
        } catch (IOException e) {
            return ImmutableSet.of();
        }
    }

}
