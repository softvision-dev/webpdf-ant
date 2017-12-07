<h1>webPDF Ant task</h1>
This repository defines an Ant task, which can be used to call the webPDF SOAP webservices directly from an Ant build file. 

![webPDF Logo](images/logo.png "webPDF")

[webPDF](https://www.webpdf.de/) is a commercial multi-platform server solution for creating and processing PDF documents. To use the webPDF Ant tasks, a running webPDF installation is required. A demo version as Windows installation, Linux package or as a virtual machine can be downloaded from the [product page](https://www.webpdf.de/en/download-web-pdf.html).

    Note: Unless otherwise marked, the following is based on webPDF version 7 or newer.

<h2>Installation</h2>
You can clone this repository with 'Git' to your local system. Another way to use these Ant tasks without using 'Git' is to download the zip file containing the current version (using the following link or by clicking the "Download ZIP" button on the repo page).

[Download the samples as ZIP](https://github.com/softvision-dev/webpdf-ant/archive/master.zip)

This library is build with the IntelliJ IDEA IDE. You can directly open and run the project from inside the IDE. For other IDEs you have to adapt the project accordingly.

<h2>Usage</h2>
To use this library, import the produced JAR to a project of your choice and add it to your ant classpath.
   ```xml
    <taskdef resource="net/webpdf/ant/antlib.xml"
             classpath="${basedir}/../webPDF-task-1.0.jar"/>
   ```
<h2>Dependencies</h2>
This library requires the inclusion of the following libraries:                        
   - [ant-1.9.5.jar](https://mvnrepository.com/artifact/org.apache.ant/ant/1.9.5)
   - [ant-contrib-1.0b3.jar](https://mvnrepository.com/artifact/ant-contrib/ant-contrib/1.0b3) 
   - [commons-io-2.5.jar](https://mvnrepository.com/artifact/commons-io/commons-io/2.5)

<h2>Tasks</h2>
After including the library, the following tasks can be used from an Ant build file.
<h3>webPDF task</h3>
The webPDF task is the top-level container of a webPDF Ant webservice call, it configures the server and allows to set common options for all contained operation groups.
   
A webPDF task may also contain any standard Ant Task. You may call delete, copy, echo and similar tasks from the webPDF task as you see fit. It will call those tasks in sequence with the contained group tasks.
A webPDF task however, may never contain webPDF operation tasks directly. You should call all your operations in bundled sequences using group tasks.
   
   ```xml
   <webpdf serverurl="http://localhost:8080"
           tempdir="C:\someTempDir" targetdir="C:\someTargetDir"
           failonerror="false">
      ...        
   </webpdf>
   ```
   
   - __serverUrl__ Shall contain the webPDF server address. It shall only contain the authority part of the server's URL. (ie.: __"http://localhost:8080"__)
   - __tempDir__ (optional) When a valid path is given here, then a specific temp folder shall be used, instead of using the system's default temp path.
   - __targetDir__ All produced files shall be placed in this directory.                       
   - __failOnError__ (optional) When set to true the webservices calls will fail and halt after the first occurring problem. When set to false a failing operation will be ignored and skipped.       

<h4>Authentication</h4>
If the webPDF server is requiring user authentication, you can use a credentials subtask to provide all necessary information.

   ```xml
   <usercredentials username="username" password="password"/>
   ```

   - __username__ the name of the user.
   - __password__ the password of the user.
 
<h4>Resources</h4>
A webPDF task is processing a source file and is creating a target file by executing a number of group tasks. Each group task shall create it's own intermediate result, which shall be used as the source of the following group task. The last group is creating the target document. Be aware, that operation- and group tasks are creating temporary files and that the webPDF task will indiscriminately purge all those temporary files once it has created it's final target file.

A webPDF task makes use of filesets and other resource definitions to locate source files. For example the following resource definition will apply the operations to all *.jpg files, which can be found in the source directory.
 
   ```xml
   <fileset dir="${sourceDir}">
      <include name="**/*.jpg"/>
   </fileset>
   <globmapper from="*.jpg" to="*.pdf"/>
   ```
   
Because of the name mapper a pdf file of the same name shall be created for each *.jpg file.
The webPDF task defines the initial source files, that shall be the base of all following operations and maps them to a set of target files, that shall be created when all operations of a webPDF task have been completed.
When using multiple source files via resource collections, the webPDF task shall be called repeatedly for each individual source document.
<h4>Input and output variables</h4>
Using variables the default resource handling can be overridden. The input variable allows to manually set the source document of a webPDF task from a file path or a property containing a file path. The output variable allows to store the created target document as a referable variable, so that you may use it for later operations.
   ```xml
   <webpdf serverurl="${webPDFUrl}" user="${usr}" password="${pwd}"
      serverprotected="true" useauthorization="true"
      tempdir="${tempDir}" mtom="true" targetdir="${targetDir}" failonerror="true">      
      <!-- store target file in variable "out" -->
      <var name="out" role="OUTPUT"/>
      <!-- execute some operations. The Hereby created target file will be referable by the name "out". --> 
   </webpdf>
   <!-- do something entirely different and come back to the created output later via ie.: -->
   <webpdf ...>
      <var name "in" role="INPUT" value="${out}"/>
      <!-- do something using the file stored to out as the source document -->
   </webpdf>
   ```

   - __var role="INPUT"__ (optional) Allows to use a file as the source for the first operation of the first group. Provide either a path to a file, or a property containing a filepath via the value parameter. This variable is a sheer alternative to using a single file resource.
   - __var role="OUTPUT"__ (optional) Allows to manually set the target file path directly. Simply provide a name for the variable and you will be able to refer to the created file via that name.

<h3>Group task</h3>
The group task is bundling multiple operations (calls to webservices) to a common bundled task. It allows to evaluate and collect operations in a fixed sequence. An operation must always be called from a group task.  
Operations will pass on their result document as the source document for the next operation (iterative file processing). The group task allows to override and customize this default behaviour, to realize even more complex sequences.    
    
   ```xml
   <group>
      <var name="A" role="OUTPUT"/>
      <var name="B" role="INPUT" value="${C}"/>
      <operation xmlns="http://schema.webpdf.de/1.0/operation">...</operation>
      <operation xmlns="http://schema.webpdf.de/1.0/operation">...</operation>
   </group>
   ```
   
   - __var role="INPUT"__ (optional) Allows to use a file stored in an Ant property as the source for the first operation of this group, instead of the previous result or initial source file. Provide either a path to a file, or a property containing a filepath via the value parameter. 
   - __var role="OUTPUT"__ (optional) The results of operations are stored in temporary files - this parameter allows to prevent the deletion of the terminal temporary file of an operation group and assigns it's absolute filepath to a property of the set name. This allows to express more complex operations, based on multiple interim results. Simply provide a name for the variable and you will be able to refer to the created file via that name.
   
Be aware, that input and output variables completely remove a temporary file from the iterative file processing logic. An unparameterized group would continue with the last file, that has not been customized using input or output variables.
Example usage:

   ```xml
    <group>
        <!-- An unparameterized group, that creates some temporary file as a result. -->    
    </group>
    <group>
        <var name="Out1" role="OUTPUT"/>
        <!-- This group will use the file produced by the unparameterized group as it's input, but will not continue the
         iteration by replacing it with it's own output.
         Execute a sequence of operations, that produce "Out1" in the end. -->
    </group>
    <group>
        <var name="Out2" role="OUTPUT"/>
        <!-- This group will use the file produced by the unparameterized group as it's input, but will not continue the
         iteration by replacing it with it's own output.
         Execute a sequence of operations, that produce "Out2" in the end. -->
    </group>
    <group>
        <!-- This group will use the file produced by the previous unparameterized group.
         This group is unparameterized and will continue the iteration, by providing it's result as the source for the
         next group.
         There are 3 files known in the current context - The current iteration step and the stored files "Out1" and "Out2".
         This group could - for example - merge the 3 files. -->
    </group>
    <group>
        <var name="In1" role="INPUT" value="${Out1}"/>
        <!-- This group will use Out1 as it's source instead of the source file produced by the last group
         This group has not been given a varOutput, meaning it will replace the current source file with it's own output
         A group defined this way is not continuing, but rather starting a new iteration. -->
    </group>
   ```

<h3>Operation task</h3>
The operation task directly represents a call to a webPDF webservice. It is recommended to provide the webPDF operation.xsd as it's namespace, as all substructures of this task are treated according to said schema.
Operation tasks are equal to the parameter structure used by a webservice, you can find detailed information about webservice parameters in the [webPDF user manual](https://www.webpdf.de/fileadmin/user_upload/softvision.de/files/products/webpdf/help/enu/webservice_parameter.htm).
   
   ```xml
   <operation xmlns="http://schema.webpdf.de/1.0/operation">
      <annotation>
         <add>
            <text name="textName" creator="${usr}" zoomable="false"/>
         </add>
      </annotation>
   </operation>
   ```

<h2>Examples</h2>
You can find usage examples as ant build files within the project.

   - __convertAndAnnotateExample__ A simple usage example - a single file is converted and annotated.
   - __attachFileExample__ A simple usage example - a single file is converted and a file attachment is added.
   - __variablesExample__ An advanced usage example using multiple webpdf tasks, input and output variables.
   - __complexExample__ A complex usage example using group input and output variables.
   
The used input files, generated output files and the build scripts themselves, can be found in the "tests" subdirectory of the project's base path.
To run the examples, you should use the provided run configurations and you will have to adapt the connection information to your webPDF server's settings.

<h2>Contributing</h2>
Contributions are welcome. We are always looking for improvements or further programs for other programming languages or environments. We accept contributions via 'Pull Requests' on [GitHub](https://github.com/softvision-dev/webpdf-ant/pulls).

<h2>License</h2>
Please, see the [LICENSE](LICENSE) file for more information.

<h2>More help</h2>
[webPDF Documentation](https://www.webpdf.de/en/documentation)

