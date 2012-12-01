******************************************************************************
*                                                                            *
*        InfiniteGraph Sample: Patient Medications and Allergies             *
*                                                                            *
*                  ===============================                           *
*                  COPYRIGHT AND DISCLAIMER NOTICE                           *
*                  ===============================                           *
*                                                                            *
* The following copyright and disclaimer notice applies to all files         *
* included in this application.                                              *
*                                                                            *
* Objectivity, Inc. grants you a nonexclusive copyright license to use all   *
* programming code examples from which you can generate similar function     *
* tailored to your own specific needs.                                       *
*                                                                            *
* All sample code is provided by Objectivity, Inc. for illustrative          *
* purposes only. These examples have not been thoroughly tested under all    *
* conditions. Objectivity, Inc., therefore, cannot guarantee or imply        *
* reliability, serviceability, or function of these programs.                *
*                                                                            *
* All programs contained herein are provided to you "AS IS" without any      *
* warranties or indemnities of any kind. The implied warranties of           *
* non-infringement, merchantability and fitness for a particular purpose     *
* are expressly disclaimed.                                                  *
*                                                                            *
******************************************************************************

Overview
--------
This example ingests drug information from the FDA's National Drug Code (NDC)
directory and creates fictitious patient information to populate a graph.
Sample programs access the graph and run queries to determine whether a
patient with particular allergies can safely take a particular drug. The
example provides two approaches for running queries -- through the use of a
navigator plugin or through the use of iterators in code.

The example represents the vertices and edges with instances of derived
classes:

Subclasses of the InfiniteGraph class BaseVertex:

* Patient                    * DrugAllergy
* Encounter                  * Drug
* Observation                * GenericDrug
* NDCVertex                  * Ingredient
* Allergy

Subclasses of the InfiniteGraph class BaseEdge:

* AllergensEdge              * FoundInEdge
* AllergiesEdge              * GenericEdge
* BrandsEdge                 * IngredientsEdge
* NDCEdge                    * EncountersEdge
* ObservationsEdge

The example includes the following main class implementations:

* StagePatientDatabase
  Creates the graph database and various indexes.
* ImportPatient
  Randomly creates the vertices and edges for Patient instances. Uses text
  files obtained from the U.S. Census Bureau as input.
* ImportProduct
  Creates the vertices and edges for Drug, GenericDrug and Ingredient
  instances from the NDC data.
* GeneratePatientAllergies
  Creates the vertices and edges for Allergy instances and prints output to a
  file (GeneratePatientAllergies.txt).
* MedicatePatient
  Let's a prescriber check whether a Patient instance has prohibitive
  Allergies to a particular Drug instance. Uses iterators for the traversal.
* Navigate
  Let's a prescriber check whether a Patient instance has prohibitive
  Allergies to a particular Drug instance. Uses a navigator plugin for the
  traversal.
* PrintDrugs
  Prints Drugs and Ingredients to a file.

See the InfiniteGraph Developer Site for the latest API documentation and
other useful InfiniteGraph topics:

http://wiki.infinitegraph.com

Example Files
-------------
The example is structured to support the use of Eclipse or Apache Ant.

    NDCSample
    |
    |___bin
    |   |___[Compiled output]
    |
    |___build.xml [Ant build file]
    |
    |___config
    |   |___[NDCSample.properties]
    |
    |___data
    |   |___[InfiniteGraph database files]
    |   |
    |   |___ plugins [Directories to contain plugin JAR files]
    |        |___formatters
    |        |___lib
    |        |___navigators
    |
    |___datasource
    |   |___[Name lists from U.S. Census Bureau]
    |
    |___NDCdata
    |   |___[Directory to contain downloaded NDC data]
    |
    |___src
    |   |___[.java files]
    |
    |___ *.launch [Eclipse launch configuration files]


The example includes an Ant build file, a config directory containing the
graph database property file, a data directory for the InfiniteGraph
database files and for plugin JAR files, a datasource directory containing
name lists, an empty NDCdata directory in which to download NDC data, a
src directory with the .java files for the project, and Eclipse launch
configuration files. A bin directory will be created when you build the
project.

Setting Up
----------
You must have already installed and configured InfiniteGraph as described in 
the installation instructions on the InfiniteGraph Developer Site. In 
addition, perform the following:

1. Download the NDC Database File (zip file) from:

   http://www.fda.gov/Drugs/InformationOnDrugs/ucm142438.htm

2. Extract the files into the 'NDCdata' directory.

Compiling and Running
---------------------
To run this example in the Eclipse Java IDE:

1. Create a new Java project with the following settings:
   a. Name the project NDCSample.
   b. For the default project location, navigate to the NDCSample
      directory you extracted.
   c. For the Java build settings, add the
      <installDir>/lib/InfiniteGraph.jar and
      <installDir>/lib/slf4j-simple-1.6.1.jar to your
      libraries as external JARs.

2. Run the project as a Java application, choosing the following
   configurations in order:
   a. StagePatientDatabase
   b. ImportPatient
   c. ImportProduct
   d. GeneratePatientAllergies

3. Examine the GeneratePatientAllergies.txt output file that was created.
   Choose a patient mmi (ID number) and drug brand (not a GenericDrug or
   Ingredient) to use as input for a query.

   For example, the following entries indicate an mmi number and a drug brand:
     for patient mmi = 90364
     brands Sinus Relief

4. (Optional) Run a program that uses iterators to check whether a patient
   can safely take a medication.
   a. Choose 'Run > Run Configurations' and select 'MedicatePatient'.
   b. In the 'Arguments' tab, replace '6085 "Tree Mixture"' with the mmi number and
      drug name you chose. For a drug whose name contains whitespaces, use
      double quotes. For example:
        90364 "Sinus Relief"
   c. Click 'Run'.

5. Run a program that uses a navigator plugin to check whether a patient can
   safely take a medication and uses a formatter plugin to output results.
   a. Create a JAR file for the navigator plugin:
      - Right click on the 'ndc.plugins' package in the 'src' folder and
        choose 'Export'.
      - Select JAR file, then click 'Next'.
      - Expand the 'resources to export' to the 'ndc.plugins' level and select
        the following three files: DrugNameResultQualifier.java,
        EdgeTypePathQualifier.java, and SearchOrderGuide.java. Do not include
        PrintPathFormatResultHandler.java, which must be packaged in a
        separate JAR file.
      - Click 'Browse' to select the export destination, and save the JAR file
        as NDCNavigatorPlugin.jar in data/plugins/navigators. Click Finish.
   a. Create a JAR file for the formatter plugin:
      - Right click on the 'ndc.plugins' package in the 'src' folder and
        choose 'Export'.
      - Select JAR file, then click 'Next'.
      - Expand the 'resources to export' to the 'ndc.plugins' level and select
        only the following file: PrintPathFormatResultHandler.java.
      - Click 'Browse' to select the export destination, and save the JAR file
        as NDCFormatterPlugin.jar in data/plugins/formatters. Click Finish.
   c. Add the mmi number and the drug name for the query:
      - Choose 'Run > Run Configurations' and select 'Navigate'.
      - In the 'Arguments' tab, replace '6085 "Tree Mixture"' with the mmi number
        and drug name you chose. For a drug whose name contains whitespaces,
        use double quotes. For example:
          90364 "Sinus Relief"
      - Click 'Run'.
      
      Warnings about allergies display in the console. The formatter plugin
      creates a FormattedOutput.txt file that shows the paths that led to the
      allergy warnings.

To compile using Ant:

1. Create an IG_HOME environment variable that points your InfiniteGraph
   installation. For example:

   IG_HOME=C:\Program Files\InfiniteGraph\<version>

2. Compile using Ant:

   cd NDCSample
   ant

3. To run one of the implementations, use its target name. For example:

   ant runStagePatientDatabase
   ant runImportPatient
   ant runImportProduct
   ant runGeneratePatientAllergies

4. Examine the GeneratePatientAllergies.txt output file that was created.
   Choose a patient mmi (ID number) and drug brand (not a GenericDrug or
   Ingredient) to use as input for a query.

   For example, the following entries indicate an mmi number and a drug brand:
     for patient mmi = 90364
     brands Sinus Relief

5. (Optional) Run a program that uses iterators to check whether a patient
   can safely take a drug. For example:

   ant  -Dpatientmmi="90364" -Ddrugname="Sinus Relief" runMedicatePatient

6. Run a program that uses the navigator plugin to check whether a patient can
   safely take a drug. For example:

   ant  -Dpatientmmi="90364" -Ddrugname="'Sinus Relief'" runNavigate
   
   Warnings about allergies display in the console. The formatter plugin
   creates a FormattedOutput.txt file that shows the paths that led to the
   allergy warnings.

Viewing Results
---------------
The following instructions explain how to load a Person vertex, then use the
navigator plugin to check whether that person has an allergy to a particular
drug.

By default, the Visualizer looks for plugins in the default location inside
subdirectories of a 'plugins' directory located with the bootfile. The
JAR file for the navigator plugin should already be present in
data/plugins/navigators (as described in previous instructions), but you
might need to add a JAR file with the class types needed by the navigator to
data/plugins/lib. If you built using Ant, this JAR file was automatically
created for you.

Note: JARs that have been previously loaded by InfiniteGraph are renamed with
an '.isLoaded' numbered extension.

1. (If needed) Create a JAR file for the 'ndc.types' package and place it in
   the default location. For example, in Eclipse:
   a. Right click on the 'ndc.types' package and choose 'Export'.
   b. Select JAR file, then click 'Next'.
   c. Verify that all the class definitions are selected for inclusion.
   d. Click 'Browse' to select the export destination, and save the JAR file
      as NDCTypes.jar in data/plugins/lib.

2. Start IG Visualizer and connect to the graph database through its bootfile,
   NDCSample.boot.

3. Click the 'Indexes' tab, then double click the 'MMI-Lookup-Root' index.

4. Enter a patient mmi in the search field and click 'Query'. For example:
     90364

5. Double click on the returned '>> Patient' to display the vertex in the
   graph.

6. Choose 'Navigation > Run Configurations > Navigators' to open the
   'Navigators' tab.

7. Click in the 'Value' field for 'DrugNameResultQualifier.drugName'
   and type in the name of the drug brand. For example:
     Sinus Relief

8. Right click on the 'Patient' vertex and choose 'Navigate from Selected >
   NDCNavigatorPlugin'.

   Assuming you chose a drug brand to which the person is allergic, a path is
   presented indicating the allergy.

For information about using IG Visualizer, choose 'IGVisualizer > Help
Contents'.

























