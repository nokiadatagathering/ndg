# MySQL-Front 5.1  (Build 1.5)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;


# Host: localhost    Database: ndg
# ------------------------------------------------------
# Server version 5.0.81-community-nt

CREATE DATABASE IF NOT EXISTS `ndg` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `ndg`;

#
# Source for table company
#

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `company` (
  `idCompany` int(11) NOT NULL auto_increment,
  `companyName` varchar(100) NOT NULL,
  `companyType` varchar(100) NOT NULL,
  `companyCountry` varchar(100) NOT NULL,
  `companyIndustry` varchar(100) NOT NULL,
  `companySize` varchar(255) NOT NULL,
  PRIMARY KEY  (`idCompany`),
  UNIQUE KEY `idCompany` (`idCompany`),
  UNIQUE KEY `companyName` (`companyName`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

#
# Dumping data for table company
#

INSERT IGNORE INTO `company` VALUES (1,'CompanyName','','CompanyCountry','CompanyIndustry','CompanySize');

#
# Source for table device
#

CREATE TABLE IF NOT EXISTS `device` (
  `idDevice` int(11) NOT NULL auto_increment,
  `deviceModel` varchar(10) default NULL,
  PRIMARY KEY  (`idDevice`),
  UNIQUE KEY `idDevice` (`idDevice`),
  UNIQUE KEY `deviceModel` (`deviceModel`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

#
# Dumping data for table device
#

INSERT IGNORE INTO `device` VALUES (1,'E71');
INSERT IGNORE INTO `device` VALUES (2,'E61');
INSERT IGNORE INTO `device` VALUES (3,'E63');

#
# Source for table imei
#

CREATE TABLE IF NOT EXISTS `imei` (
  `imei` varchar(15) NOT NULL,
  `msisdn` varchar(25) NOT NULL,
  `qtdeResults` int(11) default NULL,
  `idUser` int(11) NOT NULL,
  `idDevice` int(11) NOT NULL,
  `realImei` char(1) NOT NULL default 'N',
  PRIMARY KEY  (`imei`),
  UNIQUE KEY `msisdn` (`msisdn`),
  KEY `FK_imei_idDevice` (`idDevice`),
  KEY `FK_imei_idUser` (`idUser`),
  KEY `FK3160C8882A600B` (`idDevice`),
  KEY `FK3160C8F6E146B5` (`idUser`),
  CONSTRAINT `FK_imei_idDevice` FOREIGN KEY (`idDevice`) REFERENCES `device` (`idDevice`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `FK_imei_idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Dumping data for table imei
#


#
# Source for table results
#

CREATE TABLE IF NOT EXISTS `results` (
  `idResult` varchar(8) NOT NULL,
  `resultXML` mediumtext,
  `idSurvey` varchar(10) NOT NULL,
  `imei` varchar(15) default NULL,
  `latitude` varchar(25) default NULL,
  `longitude` varchar(25) default NULL,
  `title` varchar(150) default NULL,
  PRIMARY KEY  (`idResult`),
  UNIQUE KEY `idResult` (`idResult`),
  KEY `FK_results_idSurvey` (`idSurvey`),
  KEY `FK416B3BF6D99AD6BC` (`idSurvey`),
  KEY `FK_results_imei` (`imei`),
  CONSTRAINT `FK_results_idSurvey` FOREIGN KEY (`idSurvey`) REFERENCES `surveys` (`idSurvey`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_results_imei` FOREIGN KEY (`imei`) REFERENCES `imei` (`imei`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Dumping data for table results
#


#
# Source for table role
#

CREATE TABLE IF NOT EXISTS `role` (
  `idRole` int(11) NOT NULL auto_increment,
  `roleName` varchar(15) default NULL,
  PRIMARY KEY  (`idRole`),
  UNIQUE KEY `idRole` (`idRole`),
  UNIQUE KEY `roleName` (`roleName`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

#
# Dumping data for table role
#

INSERT IGNORE INTO `role` VALUES (1,'Admin');
INSERT IGNORE INTO `role` VALUES (2,'Operator');
INSERT IGNORE INTO `role` VALUES (3,'Field Worker');

#
# Source for table surveys
#

CREATE TABLE IF NOT EXISTS `surveys` (
  `idSurvey` varchar(10) NOT NULL,
  `surveyXML` text character set utf8,
  `idUser` int(11) NOT NULL,
  `isUploaded` char(1) default 'N',
  PRIMARY KEY  (`idSurvey`),
  UNIQUE KEY `idSurvey` (`idSurvey`),
  KEY `FK_surveys_idUser` (`idUser`),
  KEY `FK91914459F6E146B5` (`idUser`),
  CONSTRAINT `FK_surveys_idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Dumping data for table surveys
#

INSERT IGNORE INTO `surveys` VALUES ('1263929563','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<survey display=\"1-1\" id=\"1263929563\" title=\"Demo Survey\" deployed=\"false\" checksum=\"bf867bca60eed402754b531350658256\">\n  <category id=\"1\" name=\"General Questions\">\n    <question id=\"1\" type=\"_str\" field=\"\" direction=\"in\" editable=\"true\">\n      <description>What is your name?</description>\n      <length>80</length>\n    </question>\n    <question id=\"2\" type=\"_int\" field=\"\" direction=\"in\" editable=\"true\" min=\"0\" max=\"50\">\n      <description>On average, how often do you laugh per day?</description>\n      <length>10</length>\n    </question>\n    <question id=\"3\" type=\"_choice\" field=\"\" direction=\"in\" editable=\"true\">\n      <description>Are you a thinker, a talker or a doer?</description>\n      <select>exclusive</select>\n      <item otr=\"0\">Doer</item>\n      <item otr=\"0\">Thinker</item>\n      <item otr=\"0\">Talker</item>\n    </question>\n    <question id=\"4\" type=\"_choice\" field=\"\" direction=\"in\" editable=\"true\">\n      <description>Which continents have you visited?</description>\n      <select>multiple</select>\n      <item otr=\"0\">Asia</item>\n      <item otr=\"0\">Africa</item>\n      <item otr=\"0\">Australia</item>\n      <item otr=\"0\">Europe</item>\n      <item otr=\"0\">Antarctica</item>\n      <item otr=\"0\">North America</item>\n      <item otr=\"0\">Latin America</item>\n    </question>\n    <question id=\"5\" type=\"_str\" field=\"\" direction=\"in\" editable=\"true\">\n      <description>What do you like most about Finland?</description>\n      <length>50</length>\n    </question>\n    <question id=\"6\" type=\"_date\" field=\"\" direction=\"in\" editable=\"true\" min=\"12/10/2008\" max=\"\">\n      <description>Date</description>\n    </question>\n  </category>\n</survey>',1,'\0');

#
# Source for table transactionlog
#

CREATE TABLE IF NOT EXISTS `transactionlog` (
  `idTransactionLog` int(11) NOT NULL auto_increment,
  `address` varchar(20) default NULL,
  `transactionType` varchar(2) default NULL,
  `transactionStatus` varchar(20) default NULL,
  `transmissionMode` varchar(5) default NULL,
  `transactionDate` date default NULL,
  `idSurvey` varchar(10) default NULL,
  `idResult` varchar(8) default NULL,
  `imei` varchar(15) default NULL,
  `idUser` int(11) NOT NULL,
  PRIMARY KEY  (`idTransactionLog`),
  UNIQUE KEY `idTransactionLog` (`idTransactionLog`),
  KEY `FK_transactionlog_idSurvey` (`idSurvey`),
  KEY `FK_transactionlog_imei` (`imei`),
  KEY `FK_transactionlog_idResult` (`idResult`),
  KEY `FK_transactionlog_idUser` (`idUser`),
  KEY `FK3C3C7BA6D99AD6BC` (`idSurvey`),
  KEY `FK3C3C7BA65D5AA3FD` (`imei`),
  KEY `FK3C3C7BA6D46F1842` (`idResult`),
  KEY `FK3C3C7BA6F6E146B5` (`idUser`),
  CONSTRAINT `FK_transactionlog_idResult` FOREIGN KEY (`idResult`) REFERENCES `results` (`idResult`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_transactionlog_idSurvey` FOREIGN KEY (`idSurvey`) REFERENCES `surveys` (`idSurvey`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_transactionlog_idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `FK_transactionlog_imei` FOREIGN KEY (`imei`) REFERENCES `imei` (`imei`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

#
# Dumping data for table transactionlog
#

INSERT IGNORE INTO `transactionlog` VALUES (1,NULL,'RS','SUCCESS','HTTP','2010-01-19','1263929563',NULL,NULL,1);
INSERT IGNORE INTO `transactionlog` VALUES (2,NULL,'NU','SUCCESS','HTTP','2010-01-19','1263929563',NULL,NULL,1);

#
# Source for table user
#

CREATE TABLE IF NOT EXISTS `user` (
  `idUser` int(11) NOT NULL auto_increment,
  `password` varchar(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `countryCode` varchar(255) default NULL,
  `areaCode` varchar(255) default NULL,
  `phoneNumber` varchar(255) default NULL,
  `userAdmin` varchar(50) NOT NULL,
  `userValidated` char(1) NOT NULL,
  `whoUseIt` char(1) NOT NULL,
  `emailPreferences` char(1) NOT NULL,
  `howDoYouPlanUseNdg` varchar(255) default NULL,
  `firstTimeUse` char(1) NOT NULL,
  `validationKey` varchar(255) default NULL,
  `idCompany` int(11) NOT NULL,
  `idRole` int(11) NOT NULL,
  `hasFullPermissions` char(1) NOT NULL default 'n',
  `editorSettings` text,
  PRIMARY KEY  (`idUser`),
  UNIQUE KEY `idUser` (`idUser`),
  UNIQUE KEY `email` (`email`),
  KEY `FK_user_idRole` (`idRole`),
  KEY `FK_user_idCompany` (`idCompany`),
  KEY `FK36EBCBF6DE700B` (`idRole`),
  KEY `FK36EBCBA7EBF1F2` (`idCompany`),
  CONSTRAINT `FK_user_idCompany` FOREIGN KEY (`idCompany`) REFERENCES `company` (`idCompany`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `FK_user_idRole` FOREIGN KEY (`idRole`) REFERENCES `role` (`idRole`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

#
# Dumping data for table user
#

INSERT IGNORE INTO `user` VALUES (1, 'b235fd01d8130026cfcca86a1b206208','admin','admin@admin.com','FirstName','LastName.Text','CountryCode','AreaCode.Text','PhoneNumber','admin','Y','Y','Y','','N','10010000001001010011011001010001010010010011110001010110001',1,1,'Y',NULL);

#
# Source for table userbalance
#

CREATE TABLE IF NOT EXISTS `userbalance` (
  `idUSerBalance` int(11) NOT NULL auto_increment,
  `users` int(11) default NULL,
  `imeis` int(11) default NULL,
  `sendAlerts` int(11) default NULL,
  `results` int(11) default NULL,
  `surveys` int(11) default NULL,
  `idUser` int(11) default NULL,
  PRIMARY KEY  (`idUSerBalance`),
  KEY `FK_userbalance_idUser` (`idUser`),
  KEY `FK7691B8B1F6E146B5` (`idUser`),
  CONSTRAINT `FK_userbalance_idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

#
# Dumping data for table userbalance
#

INSERT IGNORE INTO `userbalance` VALUES (1,5,5,10,30,3,1);

#
# Source for table surveysopenrosa
#

CREATE TABLE IF NOT EXISTS `surveysopenrosa` (
  `idSurvey` varchar(10) NOT NULL,
  `idSurveyOriginal` varchar(100),
  `surveyXML` text,
  PRIMARY KEY (`idSurvey`) USING BTREE,
  UNIQUE KEY `idSurvey` (`idSurvey`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

SET autocommit = 0;

#
# Dumping data for table surveysopenrosa
#

INSERT IGNORE INTO `surveysopenrosa` VALUES
  ('1600763264','1600763264', '<h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\">  <h:head>    <h:title>Untitled Form</h:title>    <model>      <instance>        <data id=\"1600763264\">          <textPytanie>            JoeDoe          </textPytanie>          <numericPytanie>            123          </numericPytanie>          <datePytanie>            03/12/2011          </datePytanie>          <locationPytanie/>          <imagePytanie/>          <barcodePytanie/>          <chooseOne/>          <multiSelect/>          <mediaAudio/>          <mediaVideo/>        </data>      </instance>      <itext>        <translation lang=\"eng\">          <text id=\"/data/textPytanie:label\">            <value>Pytanie tekstowe</value>          </text>          <text id=\"/data/textPytanie:hint\">            <value>hint hint</value>          </text>          <text id=\"/data/numericPytanie:label\">            <value>Pytanie numerkowe</value>          </text>          <text id=\"/data/numericPytanie:hint\">            <value>hint</value>          </text>          <text id=\"/data/datePytanie:label\">            <value>Pytanie datowe</value>          </text>          <text id=\"/data/datePytanie:hint\">            <value>hint</value>          </text>          <text id=\"/data/locationPytanie:label\">            <value>pytanie lokacja</value>          </text>          <text id=\"/data/locationPytanie:hint\">            <value>hint</value>          </text>          <text id=\"/data/imagePytanie:label\">            <value>imagePytanie</value>          </text>          <text id=\"/data/imagePytanie:hint\">            <value></value>          </text>          <text id=\"/data/barcodePytanie:label\">            <value>barcodePytanie</value>          </text>          <text id=\"/data/barcodePytanie:hint\">            <value/>          </text>          <text id=\"/data/chooseOne:label\">            <value>singleSelect</value>          </text>          <text id=\"/data/chooseOne:hint\">            <value>asd</value>          </text>          <text id=\"/data/chooseOne:option0\">            <value>one</value>          </text>          <text id=\"/data/chooseOne:option1\">            <value>two</value>          </text>          <text id=\"/data/chooseOne:option2\">            <value>three</value>          </text>          <text id=\"/data/multiSelect:label\">            <value>multiSelect</value>          </text>          <text id=\"/data/multiSelect:hint\">            <value>multiSelect</value>          </text>          <text id=\"/data/multiSelect:option0\">            <value>one</value>          </text>          <text id=\"/data/multiSelect:option1\">            <value>two</value>          </text>          <text id=\"/data/multiSelect:option2\">            <value>three</value>          </text>          <text id=\"/data/multiSelect:option3\">            <value>four</value>          </text>          <text id=\"/data/mediaAudio:label\">            <value>mediaAudio</value>          </text>          <text id=\"/data/mediaAudio:hint\">            <value>mediaAudio</value>          </text>          <text id=\"/data/mediaVideo:label\">            <value>mediaVideo</value>          </text>          <text id=\"/data/mediaVideo:hint\">            <value>mediaVideo</value>          </text>        </translation>      </itext>      <bind nodeset=\"/data/textPytanie\" type=\"string\" constraint=\"(. &gt; 1 and . &lt; 10)\"/>      <bind nodeset=\"/data/numericPytanie\" type=\"int\" constraint=\"(. &gt; 20 and . &lt; 150)\"/>      <bind nodeset=\"/data/datePytanie\" type=\"date\" constraint=\"(. &gt; 03/09/2010 and . &lt; 03/12/2012)\"/>      <bind nodeset=\"/data/locationPytanie\" type=\"geopoint\"/>      <bind nodeset=\"/data/imagePytanie\" type=\"binary\"/>      <bind nodeset=\"/data/barcodePytanie\" type=\"barcod\"/>      <bind nodeset=\"/data/chooseOne\" type=\"select1\"/>      <bind nodeset=\"/data/multiSelect\" type=\"select\"/>      <bind nodeset=\"/data/mediaAudio\" type=\"binary\"/>      <bind nodeset=\"/data/mediaVideo\" type=\"binary\"/>    </model>  </h:head>  <h:body>    <input ref=\"/data/textPytanie\">      <label ref=\"jr:itext(\'/data/textPytanie:label\')\"/>      <hint ref=\"jr:itext(\'/data/textPytanie:hint\')\"/>    </input>    <input ref=\"/data/numericPytanie\">      <label ref=\"jr:itext(\'/data/numericPytanie:label\')\"/>      <hint ref=\"jr:itext(\'/data/numericPytanie:hint\')\"/>    </input>    <input ref=\"/data/datePytanie\">      <label ref=\"jr:itext(\'/data/datePytanie:label\')\"/>      <hint ref=\"jr:itext(\'/data/datePytanie:hint\')\"/>    </input>    <input ref=\"/data/locationPytanie\">      <label ref=\"jr:itext(\'/data/locationPytanie:label\')\"/>      <hint ref=\"jr:itext(\'/data/locationPytanie:hint\')\"/>    </input>    <upload ref=\"/data/imagePytanie\" mediatype=\"image/*\">      <label ref=\"jr:itext(\'/data/imagePytanie:label\')\"/>      <hint ref=\"jr:itext(\'/data/imagePytanie:hint\')\"/>    </upload>    <input ref=\"/data/barcodePytanie\">      <label ref=\"jr:itext(\'/data/barcodePytanie:label\')\"/>      <hint ref=\"jr:itext(\'/data/barcodePytanie:hint\')\"/>    </input>    <select1 ref=\"/data/chooseOne\">      <label ref=\"jr:itext(\'/data/chooseOne:label\')\"/>      <hint ref=\"jr:itext(\'/data/chooseOne:hint\')\"/>      <item>        <label ref=\"jr:itext(\'/data/chooseOne:option0\')\"/>        <value>one</value>      </item>      <item>        <label ref=\"jr:itext(\'/data/chooseOne:option1\')\"/>        <value>two</value>      </item>      <item>        <label ref=\"jr:itext(\'/data/chooseOne:option2\')\"/>        <value>three</value>      </item>    </select1>    <select ref=\"/data/multiSelect\">      <label ref=\"jr:itext(\'/data/multiSelect:label\')\"/>      <hint ref=\"jr:itext(\'/data/multiSelect:hint\')\"/>      <item>        <label ref=\"jr:itext(\'/data/multiSelect:option0\')\"/>        <value>one</value>      </item>      <item>        <label ref=\"jr:itext(\'/data/multiSelect:option1\')\"/>        <value>two</value>      </item>      <item>        <label ref=\"jr:itext(\'/data/multiSelect:option2\')\"/>        <value>three</value>      </item>      <item>        <label ref=\"jr:itext(\'/data/multiSelect:option3\')\"/>        <value>four</value>      </item>    </select>    <upload ref=\"/data/mediaAudio\" mediatype=\"audio/*\">      <label ref=\"jr:itext(\'/data/mediaAudio:label\')\"/>      <hint ref=\"jr:itext(\'/data/mediaAudio:hint\')\"/>    </upload>    <upload ref=\"/data/mediaVideo\" mediatype=\"video/*\">      <label ref=\"jr:itext(\'/data/mediaVideo:label\')\"/>      <hint ref=\"jr:itext(\'/data/mediaVideo:hint\')\"/>    </upload>  </h:body></h:html>');
COMMIT;

#
# Source for table resultsopenrosa
#

CREATE TABLE IF NOT EXISTS `resultsopenrosa` (
  `idResult` varchar(10) NOT NULL,
  `resultXML` text,
  `idSurvey` varchar(10) DEFAULT NULL,
  `idDevice` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`idResult`) USING BTREE,
  UNIQUE KEY `idResult` (`idResult`) USING BTREE,
  KEY `fk_resultsopenrosa_surveysopenrosa` (`idSurvey`),
  KEY `idDevice` (`idDevice`) USING BTREE,
  CONSTRAINT `fk_resultsopenrosa_surveysopenrosa` FOREIGN KEY (`idSurvey`) REFERENCES `surveysopenrosa` (`idSurvey`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Source for table surveysstatusopenrosa
#

CREATE TABLE IF NOT EXISTS `surveysstatusopenrosa` (
  `idSurvey` varchar(10) DEFAULT NULL,
  `idDevice` varchar(20) DEFAULT NULL,
  UNIQUE KEY `unique_entry` (`idSurvey`,`idDevice`) USING BTREE,
  KEY `fk_surveysstatusopenrosa_imei` (`idDevice`),
  CONSTRAINT `fk_surveysstatusopenrosa_imei` FOREIGN KEY (`idDevice`) REFERENCES `imei` (`imei`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_surveysstatusopenrosa_surveysopenrosa` FOREIGN KEY (`idSurvey`) REFERENCES `surveysopenrosa` (`idSurvey`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

grant usage on *.* to ndg@localhost identified by 'ndg';
grant all privileges on ndg.* to ndg@localhost;
