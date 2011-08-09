USE `ndg`;

# if user exists next line will fail - ignore it
# it is important that the user has the same password as hardcoded in server code
CREATE USER 'ndg'@'%' IDENTIFIED BY'ndg';
GRANT ALL PRIVILEGES ON ndg.* to 'ndg'@'%';

CREATE  TABLE IF NOT EXISTS `ndg`.`languages` (
  `language_id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `localeString` VARCHAR(45) NOT NULL ,
  `translationFilePath` VARCHAR(256) NOT NULL ,
  `fontFilePath` VARCHAR(256) NULL ,
  PRIMARY KEY (`language_id`) )
ENGINE = InnoDB;

INSERT INTO `ndg`.`languages`
(`name`, `localeString`, `translationFilePath`)
VALUES
( 'Spanish', 'es-ES', 'messages_es.properties' );

INSERT INTO `ndg`.`languages`
(`name`, `localeString`, `translationFilePath`)
VALUES
( 'Portuguese', 'pt-PT', 'messages_pt.properties' );

INSERT INTO `ndg`.`languages`
(`name`, `localeString`, `translationFilePath`,`fontFilePath`)
VALUES
( 'Finish', 'fi-FI', 'messages_fi.properties', 'fonts_fi.res');

INSERT INTO `ndg`.`languages`
(`name`, `localeString`, `translationFilePath`,`fontFilePath`)
VALUES
('Polish', 'pl-PL', 'messages_pl.properties','fonts_pl.res');


CREATE OR REPLACE VIEW V_user_role AS
SELECT username, roleName
FROM user 
INNER JOIN role 
USING (idRole);