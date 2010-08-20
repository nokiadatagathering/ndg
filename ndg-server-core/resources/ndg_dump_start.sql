-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51a-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO,ANSI_QUOTES' */;


--
-- Create schema ndg
-- Running at command line: mysql -undg -hlocalhost -pndg2009 ndg < C:\Users\ndg\source\Trunk\ndg-server-core\resources\ndg_dump_start.sql 

CREATE DATABASE IF NOT EXISTS ndg;
USE ndg;

--
-- Definition of table "company"
--

DROP TABLE IF EXISTS "company";
CREATE TABLE "company" (
  "idCompany" int(11) NOT NULL auto_increment,
  "companyName" varchar(100) NOT NULL,
  "companyType" varchar(100) NOT NULL,
  "companyCountry" varchar(100) NOT NULL,
  "companyIndustry" varchar(100) NOT NULL,
  "companySize" varchar(255) NOT NULL,
  PRIMARY KEY  ("idCompany"),
  UNIQUE KEY "idCompany" ("idCompany"),
  UNIQUE KEY "companyName" ("companyName")
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Dumping data for table "company"
--

--
-- Definition of table "device"
--

DROP TABLE IF EXISTS "device";
CREATE TABLE "device" (
  "idDevice" int(11) NOT NULL auto_increment,
  "deviceModel" varchar(10) default NULL,
  PRIMARY KEY  ("idDevice"),
  UNIQUE KEY "idDevice" ("idDevice"),
  UNIQUE KEY "deviceModel" ("deviceModel")
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table "device"
--
INSERT INTO "device" ("idDevice","deviceModel") VALUES 
 (3,'E61'),
 (2,'E61i'),
 (1,'E71');

--
-- Definition of table "imei"
--

DROP TABLE IF EXISTS "imei";
CREATE TABLE "imei" (
  "imei" varchar(15) NOT NULL,
  "msisdn" varchar(25) NOT NULL,
  "qtdeResults" int(11) default NULL,
  "idUser" int(11) NOT NULL,
  "idDevice" int(11) NOT NULL,
  PRIMARY KEY  ("imei"),
  UNIQUE KEY "msisdn" ("msisdn"),
  KEY "FK_imei_idDevice" ("idDevice"),
  KEY "FK_imei_idUser" ("idUser"),
  CONSTRAINT "FK_imei_idDevice" FOREIGN KEY ("idDevice") REFERENCES "device" ("idDevice") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "FK_imei_idUser" FOREIGN KEY ("idUser") REFERENCES "user" ("idUser") ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table "imei"
--

--
-- Definition of table "results"
--

DROP TABLE IF EXISTS "results";
CREATE TABLE "results" (
  "idResult" varchar(8) NOT NULL,
  "resultXML" text,
  "idSurvey" varchar(10) NOT NULL,
  PRIMARY KEY  ("idResult"),
  UNIQUE KEY "idResult" ("idResult"),
  KEY "FK_results_idSurvey" ("idSurvey"),
  CONSTRAINT "FK_results_idSurvey" FOREIGN KEY ("idSurvey") REFERENCES "surveys" ("idSurvey") ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table "results"
--

--
-- Definition of table "role"
--

DROP TABLE IF EXISTS "role";
CREATE TABLE "role" (
  "idRole" int(11) NOT NULL auto_increment,
  "roleName" varchar(15) default NULL,
  PRIMARY KEY  ("idRole"),
  UNIQUE KEY "idRole" ("idRole"),
  UNIQUE KEY "roleName" ("roleName")
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table "role"
--
INSERT INTO "role" ("idRole","roleName") VALUES 
 (1,'Admin'),
 (3,'Field Worker'),
 (2,'Operator');

--
-- Definition of table "surveys"
--

DROP TABLE IF EXISTS "surveys";
CREATE TABLE "surveys" (
  "idSurvey" varchar(10) NOT NULL,
  "surveyXML" text,
  "idUser" int(11) NOT NULL,
  PRIMARY KEY  ("idSurvey"),
  UNIQUE KEY "idSurvey" ("idSurvey"),
  KEY "FK_surveys_idUser" ("idUser"),
  CONSTRAINT "FK_surveys_idUser" FOREIGN KEY ("idUser") REFERENCES "user" ("idUser")
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table "surveys"
--

--
-- Definition of table "transactionlog"
--

DROP TABLE IF EXISTS "transactionlog";
CREATE TABLE "transactionlog" (
  "idTransactionLog" int(11) NOT NULL auto_increment,
  "address" varchar(20) default NULL,
  "transactionType" varchar(2) default NULL,
  "transactionStatus" varchar(20) default NULL,
  "transmissionMode" varchar(5) default NULL,
  "transactionDate" date default NULL,
  "idSurvey" varchar(10) default NULL,
  "idResult" varchar(8) default NULL,
  "imei" varchar(15) default NULL,
  "idUser" int(11) NOT NULL,
  PRIMARY KEY  ("idTransactionLog"),
  UNIQUE KEY "idTransactionLog" ("idTransactionLog"),
  KEY "FK_transactionlog_idSurvey" ("idSurvey"),
  KEY "FK_transactionlog_imei" ("imei"),
  KEY "FK_transactionlog_idResult" ("idResult"),
  KEY "FK_transactionlog_idUser" ("idUser"),
  CONSTRAINT "FK_transactionlog_imei" FOREIGN KEY ("imei") REFERENCES "imei" ("imei") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "FK_transactionlog_idResult" FOREIGN KEY ("idResult") REFERENCES "results" ("idResult") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "FK_transactionlog_idSurvey" FOREIGN KEY ("idSurvey") REFERENCES "surveys" ("idSurvey") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "FK_transactionlog_idUser" FOREIGN KEY ("idUser") REFERENCES "user" ("idUser") ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table "transactionlog"
--

--
-- Definition of table "user"
--

DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
  "idUser" int(11) NOT NULL auto_increment,
  "password" varchar(255) NOT NULL,
  "username" varchar(50) NOT NULL,
  "email" varchar(255) NOT NULL,
  "firstName" varchar(50) NOT NULL,
  "lastName" varchar(50) NOT NULL,
  "countryCode" varchar(255) default NULL,
  "areaCode" varchar(255) default NULL,
  "phoneNumber" varchar(255) default NULL,
  "userAdmin" varchar(50) NOT NULL,
  "userValidated" char(1) NOT NULL,
  "whoUseIt" char(1) NOT NULL,
  "emailPreferences" char(1) NOT NULL,
  "howDoYouPlanUseNdg" varchar(255) default NULL,
  "firstTimeUse" char(1) NOT NULL,
  "validationKey" varchar(255) default NULL,
  "idCompany" int(11) NOT NULL,
  "idRole" int(11) NOT NULL,
  "hasFullPermissions" char(1) NOT NULL,
  PRIMARY KEY  ("idUser"),
  UNIQUE KEY "idUser" ("idUser"),
  UNIQUE KEY "email" ("email"),
  KEY "FK_user_idRole" ("idRole"),
  KEY "FK_user_idCompany" ("idCompany"),
  CONSTRAINT "FK_user_idCompany" FOREIGN KEY ("idCompany") REFERENCES "company" ("idCompany") ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT "FK_user_idRole" FOREIGN KEY ("idRole") REFERENCES "role" ("idRole") ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table "user"
--

--
-- Definition of table "userbalance"
--

DROP TABLE IF EXISTS "userbalance";
CREATE TABLE "userbalance" (
  "idUSerBalance" int(11) NOT NULL auto_increment,
  "users" int(11) default NULL,
  "imeis" int(11) default NULL,
  "sendAlerts" int(11) default NULL,
  "results" int(11) default NULL,
  "surveys" int(11) default NULL,
  "idUser" int(11) default NULL,
  PRIMARY KEY  ("idUSerBalance"),
  KEY "FK_userbalance_idUser" ("idUser"),
  CONSTRAINT "FK_userbalance_idUser" FOREIGN KEY ("idUser") REFERENCES "user" ("idUser") ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table "userbalance"
--



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
