CREATE TABLE userLogin (
	userid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(100) NOT NULL,
	password VARCHAR(100) NOT NULL,
	);
	
CREATE TABLE recipes (
	recipeId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	recipeName VARCHAR(100) NOT NULL,
	recipeType VARCHAR(100) NOT NULL,
	createdDate DATE NOT NULL,
	visibility BOOLEAN NOT NULL,
	ingredientList VARCHAR(100) NOT NULL,
	instructions VARCHAR(100) NOT NULL,
	additionalInfo VARCHAR(100),
	imageURL VARCHAR(30), 
	userid INT,
	FOREIGN KEY (userid) REFERENCES userLogin(userid)
);


--Like
--Login Table
--id Auto Inc, Prim key
--username VARCHAR(x) NOT NULL
--password VARCHAR(x) NOT NULL
--secId secondary key
--Recipe table
--id
--recipename
--recipetype
--tags
--createdDate
--visibility
--ingredients
--instrucitons
--additional info
--imageurl
--userId Secondary ke