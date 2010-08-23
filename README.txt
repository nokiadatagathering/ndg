The NDG project is made up of various modules which combine to make the whole application.

The server modules are:

  ndg-commons-core
  ndg-server-core
  ndg-server-servlets
  ndg-web-server
  
The web user interface module is:

  ndg-web-ui
    
The release includes the source code to create the .jar files on which NDG depends. 
  
Inside the lib directory there are Java .jar files needed for compilation. They provide core functionality which the NDG software uses. For some of these files and which module uses them use we have specified the .jar name, give a short description and the software license: 

1. ndg-commons-core
  (Required) commons-logging.jar - A modular bridging API with support for most well known logging system - Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
  
2. ndg-server-core/lib
  (Required) indt-smslib.jar - (explained above) HTTP Classes and methods written by INdT which interact with the mobile broker Clickatell (http://www.clickatell.com/) - source code included
  (Required) mail.jar - provides a platform-independent and protocol-independent API framework to build mail and messaging applications - CDDL license and the GPLv2 with Classpath Exception
  (Required) smslib-3.3.0.jar - SMSLib is a programmer's library for sending and receiving SMS messages via a GSM modem or mobile phone - Apache License, Version 2.0 
  
3. ndg-web-server/WebContent/WEB-INF/lib contains various .jar files which are part of the flex toolkit. These are all released by Adobe under the Mozilla Public License (http://www.mozilla.org/MPL/). The .jars are all (Required) to be able to compile the server UI

 cfgatewayadapter.jar
 xalan.jar
 commons-logging.jar
 flex-messaging-core.jar
 flex-messaging-opt.jar
 concurrent.jar
 commons-codec-1.3.jar
 flex-messaging-remoting.jar
 flex-messaging-common.jar
 backport-util-concurrent.jar
 flex-messaging-proxy.jar
 commons-httpclient-3.0.1.jar

4. ndg-server-servlets/WebContent/client/ndg.jar
  (Optional) the downloadable mobile client which is offered as a download after user registration. This .jar is created by the ndg-mobile-client module

--------------------------------------------------------------------------------------------------------------------------------------------------------

NDG depends on openjdk-6-jre, mysql-server-5.1 and Flash.
Run
    $ sudo apt-get install openjdk-6-jre mysql-server-5.1 flashplugin-nonfree

Add the following environment variables to your ~/.bashrc file:

    export JAVA_HOME=/usr/lib/jvm/java-6-openjdk
    export PATH=$JAVA_HOME/bin:$PATH

Download the database_script.sql file on https://projects.forum.nokia.com/ndg/attachment/wiki/DevEnv/database_script.sql and run:

    $ mysql -h localhost -u root -p < PATH_TO/database_script.sql
    $ mysql -u root -p
    mysql> grant usage on *.* to ndg@localhost identified by 'ndg';
    mysql> grant all privileges on ndg.* to ndg@localhost;

Now you can login using "ndg" as username and password.

To compile, run the following ant build files:

    $ ant -f ndg-server-core/packaging-build.xml
    $ ant -f ndg-commons-core/packaging-build.xml
    $ ant -f ndg-server-servlets/packaging-build.xml
    $ ant -f ndg-web-server/deploy/build.xml

You need JBoss-4.2.2.GA to run the server, download and extract it.
You can get it from http://sourceforge.net/projects/jboss/files/JBoss/JBoss-4.2.2.GA/jboss-4.2.2.GA.zip/download?use_mirror=softlayer
After the compilation process is over, copy or symbolic links the following files to the following destinations:

ndg-commons-core/build/ndg-commons.jar          $JBOSS_HOME/server/default/lib
ndg-server-core/build/msmjms.jar                $JBOSS_HOME/server/default/lib
ndg-server-core/build/ndg-ejb-client.jar        $JBOSS_HOME/server/default/lib
ndg-server-core/build/ndg-core.ear              $JBOSS_HOME/server/default/deploy
ndg-server-servlets/build/ndg-servlets.war      $JBOSS_HOME/server/default/deploy
ndg-web-server/deploy/ndgFlex.war               $JBOSS_HOME/server/default/deploy

conf/email_verification_link.vm                 $JBOSS_HOME/server/default/conf
conf/mobisus.properties                         $JBOSS_HOME/server/default/conf
conf/msm-core.properties                        $JBOSS_HOME/server/default/conf
conf/msm-settings.properties                    $JBOSS_HOME/server/default/conf
conf/ndg.jad                                    $JBOSS_HOME/server/default/conf
conf/properties                                 $JBOSS_HOME/server/default/conf
conf/survey.xml                                 $JBOSS_HOME/server/default/conf
conf/survey_protocol.properties                 $JBOSS_HOME/server/default/conf
conf/version.properties                         $JBOSS_HOME/server/default/conf

deploy/ndg-ds.xml                               $JBOSS_HOME/server/default/deploy
deploy/ndg-ota.war                              $JBOSS_HOME/server/default/deploy

lib/indt-smslib.jar                             $JBOSS_HOME/server/default/lib
lib/jboss-common-client.jar                     $JBOSS_HOME/server/default/lib
lib/mysql-driver.jar                            $JBOSS_HOME/server/default/lib
lib/poi-3.1-FINAL-20080629.jar                  $JBOSS_HOME/server/default/lib
lib/smslib-3.3.0.jar                            $JBOSS_HOME/server/default/lib
lib/velocity-1.6.1-dep.jar                      $JBOSS_HOME/server/default/lib

Next, you need to edit msm-settings.properties and msm-core.properties configuration files.

After that, you need to start JBoss.
Move into $JBOSS_HOME/bin and run 

    $ sh run.sh

Once the server is up, go to your web browser and type

    http://localhost:8080/ndgFlex/swf/main.html

Username: admin	
Password: ndg

