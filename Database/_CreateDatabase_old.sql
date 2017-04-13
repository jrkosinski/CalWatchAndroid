USE [master]
GO


/****** Object:  Database [CalWatch2]    Script Date: 26/1/2559 23:05:36 ******/
IF EXISTS(SELECT name FROM sys.databases
	WHERE name = 'CalWatch2')
	DROP DATABASE [CalWatch2]
GO



/****** Object:  Database [CalWatch2]    Script Date: 26/1/2559 23:05:01 ******/
CREATE DATABASE [CalWatch2] ON  PRIMARY 
( NAME = N'CalWatch2', FILENAME = N'D:\SqlServerData\CalWatch2.mdf' , SIZE = 2048KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'CalWatch2_log', FILENAME = N'D:\SqlServerData\CalWatch2_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [CalWatch2] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [CalWatch2].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [CalWatch2] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [CalWatch2] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [CalWatch2] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [CalWatch2] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [CalWatch2] SET ARITHABORT OFF 
GO
ALTER DATABASE [CalWatch2] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [CalWatch2] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [CalWatch2] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [CalWatch2] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [CalWatch2] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [CalWatch2] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [CalWatch2] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [CalWatch2] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [CalWatch2] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [CalWatch2] SET  DISABLE_BROKER 
GO
ALTER DATABASE [CalWatch2] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [CalWatch2] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [CalWatch2] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [CalWatch2] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [CalWatch2] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [CalWatch2] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [CalWatch2] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [CalWatch2] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [CalWatch2] SET  MULTI_USER 
GO
ALTER DATABASE [CalWatch2] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [CalWatch2] SET DB_CHAINING OFF 
GO
USE [CalWatch2]
GO
/****** Object:  Table [dbo].[Meals]    Script Date: 26/1/2559 23:05:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Meals](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[UserId] [int] NOT NULL,
	[Description] [nvarchar](200) NOT NULL,
	[DateTime] [datetime2](7) NOT NULL,
	[Calories] [int] NOT NULL,
	[DateCreated] [datetime2](7) NOT NULL,
	[DateModified] [datetime2](7) NOT NULL,
 CONSTRAINT [PK_Meal] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Users]    Script Date: 26/1/2559 23:05:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Username] [nvarchar](100) NOT NULL,
	[Password] [nvarchar](100) NOT NULL,
	[PasswordSalt] [nvarchar](50) NOT NULL,
	[AuthToken] [nvarchar](100) NOT NULL,
	[PermissionLevel] [tinyint] NOT NULL,
	[TargetCaloriesPerDay] [int] NOT NULL,
	[LastLogin] [datetime2](7) NOT NULL,
	[DateCreated] [datetime2](7) NOT NULL,
	[DateModified] [datetime2](7) NOT NULL,
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Index [IX_Meals]    Script Date: 26/1/2559 23:05:01 ******/
CREATE NONCLUSTERED INDEX [IX_Meals] ON [dbo].[Meals]
(
	[UserId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_Users]    Script Date: 26/1/2559 23:05:01 ******/
CREATE NONCLUSTERED INDEX [IX_Users] ON [dbo].[Users]
(
	[AuthToken] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_Users_1]    Script Date: 26/1/2559 23:05:01 ******/
CREATE UNIQUE NONCLUSTERED INDEX [IX_Users_12] ON [dbo].[Users]
(
	[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Meals]  WITH CHECK ADD  CONSTRAINT [FK_Meals_Users] FOREIGN KEY([UserId])
REFERENCES [dbo].[Users] ([Id])
GO
ALTER TABLE [dbo].[Meals] CHECK CONSTRAINT [FK_Meals_Users]
GO
USE [master]
GO
ALTER DATABASE [CalWatch2] SET  READ_WRITE 
GO


/* password is "admin" */
INSERT [Users] (Username, Password, PasswordSalt, AuthToken, PermissionLevel, TargetCaloriesPerDay, LastLogin, DateCreated, DateModified) 
VALUES ('Admin', 'BKRpvl/lmc1C1fOoswCPUJSeFNeDHfJGq3p8BqButsc=', 'b86a254d-7240-4722-aa72-6e892d9da56a', '', 2, 0, getdate(), getdate(), getdate())
GO 

/* password is "dataviewer" */
INSERT [Users] (Username, Password, PasswordSalt, AuthToken, PermissionLevel, TargetCaloriesPerDay, LastLogin, DateCreated, DateModified) 
VALUES ('DataViewer', 'k1r/IuoV1FWfMzzf5Kd/U23pdOuRLDPicGsrRpHIf5w=', '1064fa67-78e9-42ff-88cd-35ebc17b7127', '', 1, 0, getdate(), getdate(), getdate())


