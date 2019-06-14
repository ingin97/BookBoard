CREATE DATABASE BookBoardDB;

 CREATE TABLE Bruker (
   BrukerNavn	  	VARCHAR(30) NOT NULL,
   Navn				VARCHAR(30),
   Passord			VARCHAR(30) NOT NULL,
   Rolle			VARCHAR(30),
   CONSTRAINT Bruker_PK PRIMARY KEY (BrukerNavn)); 

CREATE TABLE Emne (
   EmneID    		VARCHAR(10) NOT NULL,
   Navn 			VARCHAR(30),
   Faglærer			VARCHAR(30),
   CONSTRAINT Emne_PK PRIMARY KEY (EmneID),
   CONSTRAINT Faglærer_FK FOREIGN KEY (Faglærer) REFERENCES Bruker(BrukerNavn)
													ON UPDATE CASCADE
                                                    ON DELETE CASCADE);

CREATE TABLE Saltid (
   Dato    			INTEGER NOT NULL,
   Tidspunkt		INTEGER NOT NULL,
   EmneID			VARCHAR(10) NOT NULL,
   Varighet 		INTEGER,
   Faglærer			VARCHAR(30),
   CONSTRAINT Saltid_PK PRIMARY KEY (Dato, Tidspunkt, EmneID),
   CONSTRAINT FaglærerSaltid_FK FOREIGN KEY (Faglærer) REFERENCES Bruker(BrukerNavn)
													ON UPDATE CASCADE
                                                    ON DELETE CASCADE,
	CONSTRAINT EmneID_FK FOREIGN KEY (EmneID) REFERENCES Emne(EmneID)
													ON UPDATE CASCADE
                                                    ON DELETE CASCADE);
   

CREATE TABLE StudassPåSal (
   Dato    			VARCHAR(10) NOT NULL,
   Tidspunkt		VARCHAR(5) NOT NULL,
   Studass			VARCHAR(30) NOT NULL,
   Varighet 		INTEGER,
   CONSTRAINT StudassPåSal_PK PRIMARY KEY (Dato, Tidspunkt, Studass),
   CONSTRAINT Studass_FK FOREIGN KEY (Studass) REFERENCES Bruker(BrukerNavn)
													ON UPDATE CASCADE
                                                    ON DELETE CASCADE);

CREATE TABLE Booking (
   BookingID		           INTEGER NOT NULL,
   Student			           VARCHAR(30) NOT NULL,
   StudassPåSalDato    		 VARCHAR(10) NOT NULL,
   StudassPåSalTidspunkt	 VARCHAR(5) NOT NULL,
   StudassPåSalStudass		 VARCHAR(30) NOT NULL,
   EmneID			             VARCHAR(10) NOT NULL,
   PRIMARY KEY (BookingID),
   CONSTRAINT Student_FK FOREIGN KEY (Student) REFERENCES Bruker(BrukerNavn)
      ON UPDATE CASCADE
      ON DELETE CASCADE,
   CONSTRAINT StudassPåSalD_FK FOREIGN KEY (StudassPåSalDato, StudassPåSalTidspunkt, StudassPåSalStudass)
     REFERENCES StudassPåSal(Dato, Tidspunkt, Studass)
      ON UPDATE CASCADE
      ON DELETE CASCADE,
   CONSTRAINT EmneID2_FK FOREIGN KEY (EmneID) REFERENCES Emne(EmneID)
      ON UPDATE CASCADE
      ON DELETE CASCADE);




SET time_zone='+01:00';

CREATE TABLE Melding (
   MeldingID	INTEGER NOT NULL AUTO_INCREMENT,
   Sender			VARCHAR(30) NOT NULL,
   Mottaker		VARCHAR(30) NOT NULL,
   Beskjed    VARCHAR(200),
   Ulest      BOOLEAN DEFAULT TRUE,
   Tid        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT Melding_PK PRIMARY KEY (MeldingID),
   CONSTRAINT Sender_FK FOREIGN KEY (Sender) REFERENCES Bruker(BrukerNavn)
													                        ON UPDATE CASCADE
                                                  ON DELETE CASCADE,
   CONSTRAINT Mottaker_FK FOREIGN KEY (Mottaker) REFERENCES Bruker(BrukerNavn)
												                          	ON UPDATE CASCADE
                                                    ON DELETE CASCADE);

 CREATE TABLE Oving (
    OvingID	INTEGER NOT NULL AUTO_INCREMENT,
    EmneID		VARCHAR(30) NOT NULL,
    Tittel VARCHAR(30) NOT NULL,
    Beskrivelse    VARCHAR(200),
    Frist        TIMESTAMP NOT NULL,
    CONSTRAINT Oving_PK PRIMARY KEY (OvingID),
    CONSTRAINT EmneID3_FK FOREIGN KEY (EmneID) REFERENCES Emne(EmneID)
       ON UPDATE CASCADE
       ON DELETE CASCADE);

 CREATE TABLE Innlevering (
     InnleveringID	INTEGER NOT NULL AUTO_INCREMENT,
     OvingID		INTEGER NOT NULL,
     Student   VARCHAR(30) NOT NULL,
     Levert        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     Beskrivelse VARCHAR(200),
     Fil        LONGBLOB,
     filtype    VARCHAR(5),
     CONSTRAINT Innlevering_PK PRIMARY KEY (InnleveringID),
        CONSTRAINT OvingID_FK FOREIGN KEY (OvingID) REFERENCES Oving(OvingID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
     CONSTRAINT Student11_FK FOREIGN KEY (Student) REFERENCES Bruker(BrukerNavn)
        ON UPDATE CASCADE
        ON DELETE CASCADE);

 CREATE TABLE Retting (
      RettingID INTEGER NOT NULL AUTO_INCREMENT,
     InnleveringID	INTEGER NOT NULL,
     Studass   VARCHAR(30) NOT NULL,
     Godkjent  BOOLEAN NOT NULL,
     Kommentar VARCHAR(200),
     Tid        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT Retting_PK PRIMARY KEY (RettingID),
     CONSTRAINT InnleveringID_FK FOREIGN KEY (InnleveringID) REFERENCES Innlevering(InnleveringID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
     CONSTRAINT Studass2_FK FOREIGN KEY (Studass) REFERENCES Bruker(BrukerNavn)
        ON UPDATE CASCADE
        ON DELETE CASCADE);

