/**
 * (C) Copyright IBM Corp. 2016,2022. All Rights Reserved. US Government Users Restricted Rights - Use,
 * duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ibm.vie.mazerunner.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import com.ibm.vie.mazerunner.IPlayer;

/**
 * Class loader to load a student solution and dependent classes from a jar. Classes not in the jar
 * will be loaded from the parent class path
 * 
 * 
 * @author ntl
 *
 */
public class StudentClassLoader extends URLClassLoader {
  private final URL jar;


  public StudentClassLoader(final URL jar) throws MalformedURLException {
    super(new URL[] {jar});
    this.jar = jar;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    if (name.startsWith("com.ibm")) {
      return StudentClassLoader.class.getClassLoader().loadClass(name);
    }

    try {
      URL classUrl = new URL("jar:" + jar.toString() + "!/" + name.replace('.', '/') + ".class");
      URLConnection connection = classUrl.openConnection();

      try (InputStream input = connection.getInputStream()) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = input.read();

        while (data != -1) {
          buffer.write(data);
          data = input.read();
        }

        byte[] classData = buffer.toByteArray();

        CodeSource source = new CodeSource(classUrl, new Certificate[] {});
        PermissionCollection permissions = new Permissions();
        return defineClass(name, classData, 0, classData.length,
            new ProtectionDomain(source, permissions));

      }


    } catch (FileNotFoundException e) {
      return super.loadClass(name);
    } catch (Exception e) {

      throw new ClassNotFoundException("Unable to load class " + name + " from " + jar.toString(),
          e);
    }
  }


  @SuppressWarnings("unchecked")
  public static Class<? extends IPlayer> getMyPlayerFromJar(File jarFile, final String className)
      throws ClassNotFoundException {

    try (StudentClassLoader classLoader = new StudentClassLoader(jarFile.toURI().toURL())) {
      return (Class<? extends IPlayer>) classLoader.loadClass(className);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
