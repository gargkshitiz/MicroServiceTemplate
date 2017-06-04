USE [Personalization]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

--DROP OLD TABLES
-----------------------
IF OBJECT_ID('dbo.Account', 'U') IS NOT NULL DROP TABLE dbo.Account; 

CREATE TABLE [dbo].[Account](
	[AccountId] BIGINT IDENTITY(1,1) PRIMARY KEY NOT NULL,
	[AccountNumber] VARCHAR(450) NOT NULL,
	[Owner] VARCHAR(450) NOT NULL,
	[Balance] NUMERIC(19,2) NOT NULL)

GO

insert into ACCOUNT ([AccountNumber], [Owner], [Balance]) values ('1', 'Kshitiz', '42.22');