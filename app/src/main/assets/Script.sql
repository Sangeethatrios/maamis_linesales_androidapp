ALTER TABLE tbltempsalesitemdetails ADD COLUMN autonum INTEGER;

CREATE TABLE "tblsalesstockconversion" ( `transactionno` INTEGER NOT NULL DEFAULT NULL, `transactiondate` datetime DEFAULT NULL, `vancode` INTEGER DEFAULT NULL, `itemcode` INTEGER DEFAULT NULL, `inward` REAL DEFAULT NULL, `outward` REAL DEFAULT NULL, `type` TEXT DEFAULT NULL, `refno` INTEGER DEFAULT NULL, `createddate` datetime DEFAULT NULL, `flag` INTEGER, `companycode` INTEGER, `op` REAL, `financialyearcode` INTEGER )

ALTER TABLE tblsalesstockconversion ADD COLUMN 
autonum INTEGER;

ALTER TABLE tblstocktransaction ADD COLUMN autonum 
INTEGER;

