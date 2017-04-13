
-- CREATE ADMIN & DATAVIEWER USERS 

/* password is "admin" */
INSERT [Users] (Username, Password, PasswordSalt, AuthToken, PermissionLevel, TargetCaloriesPerDay, LastLogin, DateCreated, DateModified) 
VALUES ('Admin', 'BKRpvl/lmc1C1fOoswCPUJSeFNeDHfJGq3p8BqButsc=', 'b86a254d-7240-4722-aa72-6e892d9da56a', '', 2, 0, getdate(), getdate(), getdate())
GO 

/* password is "dataviewer" */
INSERT [Users] (Username, Password, PasswordSalt, AuthToken, PermissionLevel, TargetCaloriesPerDay, LastLogin, DateCreated, DateModified) 
VALUES ('DataViewer', 'k1r/IuoV1FWfMzzf5Kd/U23pdOuRLDPicGsrRpHIf5w=', '1064fa67-78e9-42ff-88cd-35ebc17b7127', '', 1, 0, getdate(), getdate(), getdate())



-- CREATE USER WITH MODERATE NUMBER OF MEALS 
declare @userId int 
declare @counter int
declare @date datetime

-- password is 11111
INSERT [Users] (Username, Password, PasswordSalt, AuthToken, PermissionLevel, TargetCaloriesPerDay, LastLogin, DateCreated, DateModified) 
VALUES ('test1', 'b3KEyGPr2cgytQvdlbV/++xU7gZ30NzA/SPG58wBkFM=', '01fea71f-bfe0-44d4-8265-993c55b0cb3e', '', 0, 3000, getdate(), getdate(), getdate())


set @userId = @@identity
set @date = CONVERT(datetime, '2016-01-16 09:00:00', 120)

set @counter = 0 


insert meals (userid, description, datetime, calories, datecreated, datemodified)
values (@userId, 'breakfast ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

set @date = DATEADD(hour, 4, @date)
insert meals (userid, description, datetime, calories, datecreated, datemodified)
values (@userId, 'lunch ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

set @date = DATEADD(hour, 4, @date)
insert meals (userid, description, datetime, calories, datecreated, datemodified)
values (@userId, 'dinner ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

-------------------------------------------------------------

while (@counter < 9)
begin
	set @date = DATEADD(hour, 16, @date)
	insert meals (userid, description, datetime, calories, datecreated, datemodified)
	values (@userId, 'breakfast ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

	set @date = DATEADD(hour, 4, @date)
	insert meals (userid, description, datetime, calories, datecreated, datemodified)
	values (@userId, 'lunch ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

	set @date = DATEADD(hour, 4, @date)
	insert meals (userid, description, datetime, calories, datecreated, datemodified)
	values (@userId, 'dinner ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

-------------------------------------------------------------
	set @counter = @counter + 1
end



-- CREATE LARGE NUMBER OF MEALS 
declare @userId int 
declare @counter int
declare @date datetime

-- password is 11111
INSERT [Users] (Username, Password, PasswordSalt, AuthToken, PermissionLevel, TargetCaloriesPerDay, LastLogin, DateCreated, DateModified) 
VALUES ('test2', 'b3KEyGPr2cgytQvdlbV/++xU7gZ30NzA/SPG58wBkFM=', '01fea71f-bfe0-44d4-8265-993c55b0cb3e', '', 0, 3000, getdate(), getdate(), getdate())

set @userId = @@identity
set @date = CONVERT(datetime, '2016-01-16 09:00:00', 120)

set @counter = 0 


insert meals (userid, description, datetime, calories, datecreated, datemodified)
values (@userId, 'breakfast ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

set @date = DATEADD(hour, 4, @date)
insert meals (userid, description, datetime, calories, datecreated, datemodified)
values (@userId, 'lunch ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

set @date = DATEADD(hour, 4, @date)
insert meals (userid, description, datetime, calories, datecreated, datemodified)
values (@userId, 'dinner ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

-------------------------------------------------------------

while (@counter < 999)
begin
	set @date = DATEADD(hour, 16, @date)
	insert meals (userid, description, datetime, calories, datecreated, datemodified)
	values (@userId, 'breakfast ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

	set @date = DATEADD(hour, 4, @date)
	insert meals (userid, description, datetime, calories, datecreated, datemodified)
	values (@userId, 'lunch ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

	set @date = DATEADD(hour, 4, @date)
	insert meals (userid, description, datetime, calories, datecreated, datemodified)
	values (@userId, 'dinner ' + CONVERT(varchar, @date), @date, 1000, getdate(), getdate())

-------------------------------------------------------------
	set @counter = @counter + 1
end



-- CREATE LARGE NUMBER OF USERS 
declare @userId int 
declare @counter int
declare @date datetime

set @counter = 0 
declare @username nvarchar(100)
set @username = 'xkcd'

while (@counter < 10000)
begin
	insert users(username, password, passwordsalt, authtoken, permissionlevel, TargetCaloriesPerDay, lastlogin, datecreated, datemodified)
	values (@username + convert(varchar, @counter), 'aEla5bgsodw5iLV0HOEFaebhfv33zLGczcIh0UqvZAY=', '9da1b799-0f52-474c-8230-f9c58e3819b9', '', 1, 0, getdate(), getdate(), getdate())


-------------------------------------------------------------
	set @counter = @counter + 1
end





/*
select * from users 
delete from Meals
delete from users 
*/