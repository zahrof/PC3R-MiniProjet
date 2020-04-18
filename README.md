# SimpleJavaWebApp
This  project is created as an example, demonstrating the principles of Java Servlet technology. It is used as a quideline for practical exercises on the subject Application Architectures of Software Systems at the Faculty of Informatics and Information Technologies in Bratislava. Feel free to use these materials for your needs.

## Configuration

### Downloads
* [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download) - 30 Day trial is enough for this demonstration. If you want, you can get free license from JetBrains if you provide your ISIC card information.
* [Apache Tomcat 9 Binary](http://tux.rainside.sk/apache/tomcat/tomcat-9/v9.0.0.M19/bin/apache-tomcat-9.0.0.M19.zip)
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Select version specific for your OS

### Installation

#### IntelliJ IDEA

Execute the installer and leave all settings as default. 

#### Tomcat

You have downloaded a zip file with the binary distribution of Tomcat. Unzip the archive and rename the main folder to **Tomcat**. Place it to some more convenient location like `C:\Tomcat` for Windows or `/Library/Tomcat` for Mac and Linux

#### Java

**Mac**
1. Execute the .pkg file
2. Double click on the Java installer to start the installation wizard
3. Leave all setings as default
4. DONE

**Windows**
1. Execute the .exe file 
2. Leave all settings as default
3. Now you need to add Java to your path
4. Open System Properties -> Advanced -> Environment Variables... -> (System variables) New...
5. Variable name: **JAVA_HOME**
6. Variable value: Folder where you have installed the JDK. Default is `C:\Program Files\Java\jdk1.8.9_121`
7. Click OK
8. DONE


### Create new project

1. Open IntelliJ IDEA Ultimate
2. Select "Create New Project"
3. Select "Java Enterprise" from the left panel
4. Add "Project SDK" from the JDK installation folder
5. From the "Additional Libraries and Frameworks" panel, select "Web Application"
6. Click "Next"
7. Enter project name. f.e.: "SimpleJavaWebApp"
8. Select "Finish"

### Build the project
First we need to select all artifacts that will be used to create the .war file that will be deployed to the Tomcat server.

1. Within the IntelliJ IDE, right click on the root of the project in the "Project" tool window on the left side of the editor
2. Select "Open Module Settings"
3. Select "Artifacts" from the left panel
4. Select the "+" on top of the center panel
5. Select the "Web Application: Archive" and select the option for our project
6. Make sure that the "Include in project build" checkbox for our archive is selected
7. Click "Apply" and "Done"

### Deploy the WAR file to Tomcat
1. Build the project from IntelliJ
2. Navigate to the directory `SimpleJavaWebApp_war/out/artifacts/SimpleJavaWebApp_war`
3. Copy the war file to the `Tomcat/webapps` directory

### Start the Tomcat server
1. Navigate to `Tomcat/bin`
2. Execute the `startup.sh` script for Mac or Linux or `startup.bat` script for Windows

The application can be accesed at http://localhost:8080/SimpleJavaWebApp_war/

### Create new servlet

#### Create servlet class file
1. Navigate to the `src` folder in the "Project" tool window
2. Right click on the `src` folder 
3. Select "New" -> "Servlet"

#### Add the servlet definition and mapping to the web.xml
1. Navigate to `web.xml`
2. Insert the servlet definition and mapping inside the `web-app` tags

**Example:**
```xml
    <servlet>
        <servlet-name>ServletName</servlet-name>
        <servlet-class>ServletName</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>ServletName</servlet-name>
        <url-pattern>/ServletName</url-pattern>
    </servlet-mapping>
```

### Exercises

[Exercise #1](https://github.com/marekbruchaty/SimpleJavaWebApp/commit/642c1876d9fb20d35465e3f248a0e35ded795ed0)

[Exercise #2](https://github.com/marekbruchaty/SimpleJavaWebApp/commit/18b15260c0d095414511352e1d6ae767b980f637)

[Exercise #3](https://github.com/marekbruchaty/SimpleJavaWebApp/commit/675235d24de288d169f955b89cacabc1de557c63)

[Exercise #4](https://github.com/marekbruchaty/SimpleJavaWebApp/commit/26a273a614e51cc20a6682936ba4739c6357d99c)
