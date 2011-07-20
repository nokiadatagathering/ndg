To set a development environment for NDG on Linux the canonical URL is https://projects.forum.nokia.com/ndg/wiki/DevEnv

The NDG project is made up of various modules which combine to make the whole application. 

** MODULES **

The server modules are:

  ndg-commons-core
  ndg-server-core
  ndg-server-servlets
  ndg-web-server
  
The web user interface module is:

  ndg-web-ui
    

** SHORT INSTRUCTIONS **

If you are lazy:

Download JBoss-4.2.2.GA and then checkout the code. Compile it using ant and copy the files into the correct locations in the server. Start the server listening on all interfaces. 

** LONGER INSTRUCTIONS **

NDG depends on openjdk-6-jre, mysql-server-5.1 and Flash.
Run
    $ sudo apt-get install openjdk-6-jre mysql-server-5.1 flashplugin-nonfree

Add the following environment variables to your ~/.bashrc file:

    export JAVA_HOME=/usr/lib/jvm/java-6-openjdk
    export PATH=$JAVA_HOME/bin:$PATH

Create the database:

    $ mysql -u root -p < ndg/schema/database_script_openrosa.sql

Now you can login using "ndg" as username and password.

To compile, you have the following ant build files:

* ndg-server-core/packaging-build.xml
* ndg-commons-core/packaging-build.xml
* ndg-server-servlets/packaging-build.xml
* ndg-web-ui/build.xml
* ndg-web-server/deploy/build.xml

The easiest way to do it is to edit the ndg-web-ui/build.xml file and edit the right location of your flex sdk.
Currently Nokia Data Gathering supports en_US, es_ES, pt_BR and fi_FI and you also need to add these specific locales to your flex sdk.
Run the following commands to create them:

    $ ./<flex_sdk_home>/bin/copylocale en_US es_ES
    $ ./<flex_sdk_home>/bin/copylocale en_US pt_BR
    $ ./<flex_sdk_home>/bin/copylocale en_US fi_FI

Next, just run:

    $ ant -f build_all.xml

It will compile everything and put the necessary files you need to deploy inside the jboss-4.2.2.GA folder.

You need JBoss-4.2.2.GA to run the server, download and extract it.
You can get it from http://sourceforge.net/projects/jboss/files/JBoss/JBoss-4.2.2.GA/jboss-4.2.2.GA.zip/download?use_mirror=softlayer
After the compilation process is over, copy or symbolic links the following files to the following destinations:

jboss-4.2.2.GA/server/default/lib/ndg-commons.jar           $JBOSS_HOME/server/default/lib
jboss-4.2.2.GA/server/default/lib/msmjms.jar                $JBOSS_HOME/server/default/lib
jboss-4.2.2.GA/server/default/lib/ndg-ejb-client.jar        $JBOSS_HOME/server/default/lib
jboss-4.2.2.GA/server/default/deploy/ndg-core.ear           $JBOSS_HOME/server/default/deploy
jboss-4.2.2.GA/server/default/deploy/ndg-servlets.war       $JBOSS_HOME/server/default/deploy
jboss-4.2.2.GA/server/default/deploy/ndgFlex.war            $JBOSS_HOME/server/default/deploy

conf/msm-core.properties                                    $JBOSS_HOME/server/default/conf
conf/msm-settings.properties                                $JBOSS_HOME/server/default/conf
conf/ndg.jad                                                $JBOSS_HOME/server/default/conf
conf/survey_protocol.properties                             $JBOSS_HOME/server/default/conf
conf/version.properties                                     $JBOSS_HOME/server/default/conf

deploy/ndg-ds.xml                                           $JBOSS_HOME/server/default/deploy
deploy/ndg-ota.war                                          $JBOSS_HOME/server/default/deploy

lib/indt-smslib.jar                                         $JBOSS_HOME/server/default/lib
lib/jboss-common-client.jar                                 $JBOSS_HOME/server/default/lib
lib/mysql-driver.jar                                        $JBOSS_HOME/server/default/lib
lib/smslib-3.3.0.jar                                        $JBOSS_HOME/server/default/lib

Next, you need to edit msm-settings.properties and msm-core.properties configuration files.

After that, you need to start JBoss.
Move into $JBOSS_HOME/bin and run 

    $ sh run.sh

Once the server is up, go to your web browser and type

    http://localhost:8080/ndgFlex/swf/main.html

Username: admin	
Password: ndg
