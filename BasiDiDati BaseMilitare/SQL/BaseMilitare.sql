DROP database IF EXISTS basemilitare;
CREATE database basemilitare;
USE basemilitare;
  
DROP TABLE IF EXISTS Caserma;

CREATE TABLE Caserma (
	nome varchar(20),
	annoCostruzione int(4) not null DEFAULT 0000,
	nOggettiInDeposito int not null DEFAULT 0,
    codiceAlfanumerico char(8) PRIMARY KEY
);
  
DROP TABLE IF EXISTS Addestramento;

CREATE TABLE Addestramento(
	genere VARCHAR(20),
	anno int(4) not null DEFAULT 0000,
    codiceAlfanumerico varchar(20),
    primary key(codiceAlfanumerico, genere),
	FOREIGN KEY (codiceAlfanumerico) REFERENCES Caserma(codiceAlfanumerico)
    ON DELETE CASCADE  ON UPDATE CASCADE
);

DROP TABLE IF EXISTS Deposito;

CREATE TABLE Deposito(
	capacitaMaxArmi int DEFAULT 0,
	capacitaMaxVeicoli int DEFAULT 0,
	codiceAlfanumerico varchar(20),
    nDeposito int(2),
	primary key(codiceAlfanumerico, nDeposito),
	FOREIGN KEY (codiceAlfanumerico) REFERENCES Caserma(codiceAlfanumerico)
    ON DELETE CASCADE  ON UPDATE CASCADE
);

DROP TABLE IF EXISTS Veicolo;

CREATE TABLE Veicolo(
	numeroposti int(2) not null DEFAULT 0,
	annoImmatricolazione int(4) not null,
	matricola varchar(20) PRIMARY KEY,
    codiceAlfanumericoDeposito varchar(20),
    nDeposito int(2),
    foreign key (codiceAlfanumericoDeposito, nDeposito) references deposito(codiceAlfanumerico, nDeposito)
    on delete cascade 
    on update cascade
); 

DROP TABLE IF EXISTS Caccia;

 CREATE TABLE Caccia(
	numeromissili int not null DEFAULT 0,
	litribenzina double not null DEFAULT 0,
	matricola varchar(20),
	FOREIGN KEY (matricola) REFERENCES Veicolo(matricola)
    ON UPDATE CASCADE  ON DELETE CASCADE
);

DROP TABLE IF EXISTS CarroArmato; 

CREATE TABLE CarroArmato(
	numeroproiettili int not null,
	calibrocannone double not null,
	matricola varchar(20),
	FOREIGN KEY (matricola) REFERENCES Veicolo(matricola)
    ON UPDATE CASCADE  ON DELETE CASCADE
);

DROP TABLE IF EXISTS Sottomarino; 

CREATE TABLE Sottomarino(
	numerosiluri int(1) not null DEFAULT 0,
	dataimmersione date not null DEFAULT 00000000,
	matricola varchar(20),
	FOREIGN KEY (matricola) REFERENCES Veicolo(matricola)
    ON UPDATE CASCADE  ON DELETE CASCADE
);    

DROP TABLE IF EXISTS Battaglia;

CREATE TABLE Battaglia(
	nome varchar(15),
	annoInizio int(4),
	tipo varchar(10) not null,
    PRIMARY KEY (nome, annoInizio)
);

DROP TABLE IF EXISTS Militare;

CREATE TABLE Militare (
	matricola int(8) PRIMARY KEY ,
	nome varchar(15) not null,
	cognome varchar(15) not null,
	eta int DEFAULT 18,
    codiceAlfanumerico char(8) not null,
    foreign key (codiceAlfanumerico) references Caserma(codiceAlfanumerico)
    on update cascade
    on delete cascade
);

DROP TABLE IF EXISTS Pilota;

CREATE TABLE Pilota (
    qualificaTipoVeicolo varchar(15) not null,
	matricola int(8),
    FOREIGN KEY (matricola) REFERENCES Militare(matricola)
    ON UPDATE CASCADE  ON DELETE CASCADE
);

DROP TABLE IF EXISTS Soldato;

CREATE TABLE Soldato (
    ruolo varchar(15) not null,
	matricola int(8),
    FOREIGN KEY (matricola) REFERENCES Militare(matricola)
    ON UPDATE CASCADE  ON DELETE CASCADE
);

DROP TABLE IF EXISTS Ufficiale;

CREATE TABLE Ufficiale (
    grado int not null DEFAULT 0,
    nSubordinati int DEFAULT 0,
	matricola int(8),
    FOREIGN KEY(matricola) REFERENCES Militare(matricola)
    ON UPDATE CASCADE  ON DELETE CASCADE
);

DROP TABLE IF EXISTS Guarnigione;

CREATE TABLE Guarnigione (
	codice int(8) PRIMARY KEY,
    stemma varchar(20) not null,
    matricolaUfficiale int(8),
    nomeBattaglia varchar(15),
    annoBattaglia int(4),
    foreign key (matricolaUfficiale) references Ufficiale(matricola)
    on update cascade
    on delete set null,
    foreign key (nomeBattaglia, annoBattaglia) references Battaglia(nome, annoInizio)
    on update cascade
    on delete set null
);

DROP TABLE IF EXISTS Arma; 

CREATE TABLE Arma(
	numeroserie int PRIMARY KEY,
	capacitaMaxProettili int not null DEFAULT 0,
	calibro double not null DEFAULT 0,
    codiceAlfanumericoDeposito varchar(20),
    nDeposito int (2),
    foreign key (codiceAlfanumericoDeposito, nDeposito) references deposito(codiceAlfanumerico, nDeposito)
    on delete cascade 
    on update cascade
);

DROP TABLE IF EXISTS EQUIPAGGIA;

CREATE TABLE EQUIPAGGIA(
    dataAssegnamentoArma date,
    matricolaSoldato int(8),
	numeroserieArma int,
	PRIMARY KEY (matricolaSoldato, numeroserieArma),
	FOREIGN KEY ( matricolaSoldato) REFERENCES Soldato(matricola)
		ON UPDATE CASCADE	ON DELETE CASCADE,
	FOREIGN KEY (numeroserieArma) REFERENCES Arma (numeroserie)
		ON UPDATE CASCADE	ON DELETE CASCADE
);

DROP TABLE IF EXISTS GUIDA;

CREATE TABLE GUIDA(
	matricolaPilota int(8),
    matricolaVeicolo varchar(20),
    primary key(matricolaPilota, matricolaVeicolo),
    foreign key (matricolaPilota) references Pilota(matricola)
    on update cascade
    on delete cascade,
    foreign key (matricolaVeicolo) references Veicolo(matricola)
	on update cascade
    on delete cascade
);

DROP TABLE IF EXISTS PARTECIPA;

CREATE TABLE PARTECIPA(
    matricolaMilitare int(8),
    codiceGuarnigione int(8),
    PRIMARY KEY (matricolaMilitare,codiceGuarnigione),
    foreign key (matricolaMilitare) references Militare(matricola)
	on update cascade
    on delete cascade,
    foreign key (codiceGuarnigione) references Guarnigione(codice)
	on update cascade
    on delete cascade
);

DELIMITER %%
CREATE TRIGGER addToDeposito AFTER INSERT ON Arma
	FOR EACH ROW
    BEGIN
	UPDATE Caserma SET nOggettiInDeposito = nOggettiInDeposito + 1
    WHERE Caserma.codiceAlfanumerico = NEW.codiceAlfanumericoDeposito;
	END;%%

CREATE TRIGGER addToDepositoVeicolo AFTER INSERT ON Veicolo
	FOR EACH ROW
    BEGIN
	UPDATE Caserma SET nOggettiInDeposito = nOggettiInDeposito + 1
    WHERE Caserma.codiceAlfanumerico = NEW.codiceAlfanumericoDeposito;
	END;%%

CREATE TRIGGER addToMilitare AFTER INSERT ON Partecipa
	FOR EACH ROW
    BEGIN
	UPDATE Ufficiale, guarnigione SET nSubordinati = nSubordinati + 1
    WHERE NEW.codiceGuarnigione = Guarnigione.codice AND Guarnigione.matricolaUfficiale = ufficiale.matricola;
	END;%%
DELIMITER ;

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Caserma.sql'
into table Caserma(annocostruzione,nome,codiceAlfanumerico,nOggettiInDeposito);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Deposito.sql'
into table Deposito(capacitaMaxArmi,capacitaMaxVeicoli,codiceAlfanumerico, nDeposito);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Addestramento.sql'
into table Addestramento(genere,anno,codiceAlfanumerico);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Veicolo.sql'
into table Veicolo(codiceAlfanumericoDeposito, matricola,numeroposti,annoImmatricolazione, nDeposito);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\CarroArmato.sql'
into table CarroArmato(numeroproiettili,calibrocannone,matricola);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Sottomarino.sql'
into table Sottomarino (numerosiluri,dataimmersione,matricola);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Caccia.sql'
into table Caccia(numeromissili,litribenzina,matricola);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Militare.sql'
into table militare (matricola, nome, cognome, eta, codiceAlfanumerico);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Ufficiale.sql'
into table ufficiale (grado, nSubordinati, matricola);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Soldato.sql'
into table soldato (ruolo, matricola);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Pilota.sql'
into table pilota (qualificaTipoVeicolo, matricola);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Battaglia.sql'
into table battaglia (nome, annoInizio, tipo);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Arma.sql'
into table Arma(codiceAlfanumericoDeposito,numeroserie,capacitaMaxProettili,calibro, nDeposito);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Guarnigione.sql'
into table guarnigione (codice, stemma, matricolaUfficiale, nomeBattaglia, annoBattaglia);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Guida.sql'
into table GUIDA (matricolaPilota, matricolaVeicolo);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Partecipa.sql'
into table PARTECIPA (matricolaMilitare, codiceGuarnigione);

load data local infile 'C:\\Users\\User\\Desktop\\BaseDiDati\\SQL\\Equipaggia.sql'
into table EQUIPAGGIA (dataAssegnamentoArma, matricolaSoldato, numeroserieArma);