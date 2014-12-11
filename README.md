# Viking Framework		
		
Viking is an Open Source Portlet development framework for [Liferay](http://www.liferay.com).  It takes advantage of the groovy language and introduces convention over configuration to the Liferay ecosystem.		
Our goal is to provide portlet developers with a modern and agile true MVC framework that speeds up the development process. 	

Some of the key features are:		
* Productive		
* MVC Architecture		
* Easy to learn		
* Fast development cycle		
		
Main technologies used:		
* Groovy		
* Gradle		
* Angular		
* Freemarker

# Table of contents
* [Getting started](#getting-started)
 * [Prerequisites](#prerequisites)
 * [Installation](#installation)
 * [Hello world](#hello-world)
 * [Viking shell](#viking-shell)
* [Essential documentation](#essential-documentation)
 * [Folder structure](#folder-structure)
 * [Configuration](#configuration)
 * [Routes and Redirects](#routes-and-redirects)
 * [Return values](#return-values)
 * [Data binding](#data-binding)
 * [Validations](#validations)
 * [Models](#models)
 * [Liferay helpers](#liferay-helpers)
 * [Jobs](#jobs)
 * [Site builder](#site-builder)


# Getting started
## Prerequisites
* JDK >= 7 

### Mac OS
* brew ([http://brew.sh/](http://brew.sh/))

### Ubuntu/debian
* apt-get

### Other operating systems

If you use Mac OS, Ubuntu or Debian: Viking shell will automatically download the libreries it requires (using brew and apt-get respectively). If you use a different operating system (i.e. Windows), you must manually install:

* Gradle 2.1+ ([http://www.gradle.org/](http://www.gradle.org/))
* MySQL 5.1+ ([http://www.mysql.com/](http://www.mysql.com/))
* Git 1.8+ ([http://git-scm.com/](http://git-scm.com/))
* Maven 3.2+ ([http://maven.apache.org/](http://maven.apache.org/))


### Recommended (commonly mandatory on viking projects)
* Coffeescript 1.8+ ([http://coffeescript.org/](http://coffeescript.org/))
* MongoDB 2.4+ ([http://www.mongodb.org/](http://www.mongodb.org/))

#### Additional notes
##### MySQL
If you are running Mac OS, and you installed mysql via viking-shell (brew), you may want to start mysql server by executing:

```
launchctl load ~/Library/LaunchAgents/homebrew.mxcl.mysql.plist
```

To start mysql server at login:

```
ln -sfv /usr/local/opt/mysql/*.plist ~/Library/LaunchAgents
```

## Installation

Install viking-shell, it's all you need to setup your viking projects.

Go to:
[https://github.com/digitalgeko/viking-shell/releases](https://github.com/digitalgeko/viking-shell/releases) and download the latest release.

Uncompress the zip, install it somewhere in your file system and add viking-shell/bin to your **$PATH.**

Make sure **viking-shell/bin/viking-shell** has execution permissions.

You should also add this to your ~/.profile (or any other profile file you have) to make gradle build faster:
`export GRADLE_OPTS="-Xmx768m -Dorg.gradle.daemon=true"`

## Hello world
Let’s execute the `new-project` command, this will prompt you asking for the project name, let’s type "MyProject" for example. Then it will ask you for the liferay version you want to use, we’ll choose Liferay 6.2.1 GA2 in this example. This will create the project structure you need, including:
* Liferay Tomcat bundle
* Viking portlets project
* Theme project (maven) project.conf, basic configuration of your viking project
* SQL folder, will contain a backup file to fully restore your liferay’s database whenever you want (using `restore-database` command).

Additionally, this will create a schema named “MyProject” on your database, and it will be populated with the SQL script file located in sql/LR621GA2.sql, the script is named that way because it’s the backup for a Liferay 6.2 GA2 specifically.

Moreover, a **file listener** is now active, it will automatically reload your changes as you modify your source files located in the portlets and theme projects.

A big output should appear, containing the information about deploying portlets and theme projects.

Notice that the prompt has changed for “MyProject>” now, this is will help you to quickly see which project is active.

Now, let’s start our liferay: Execute the `start` command, this will run Liferay’s startup.sh script file and listen for a process listening on port 8080 (or the one defined in *liferay/tomcat/conf/server.xml*), you can follow the logs with `tail-log` command in another viking-shell session.

A deploy of your portlets and theme has already been done for you after creating the project, but if you need to manually deploy your portlets for some reason, you can do it by executing the `deploy` command.

And “deploy --target theme” to deploy the theme project. 

Alternatively, you can run `full-deploy` command, which will deploy: portlets project, theme project, and all dependencies specified in project.conf; in fact, this is the command executed when creating a project.

Now, let’s open http://localhost:8080 in our browser, login as un:“test@liferay.com” pw:”test” (these are the default credentials in our backup sql script), and try to add a portlet named “My project portlet” right in the “My project” category, drop the portlet in a page.

Finally, we can edit our source files as the listener is ready to reload our changes; open ~/viking-projects/MyProject-env/MyProject in your favorite editor, edit the view file located in viking/views/MyProjectPortlet/view.ftl, change it’s contents for “Hello world!”, refresh your browser and you should see “Hello world!” message!.

## Viking shell
Viking shell is a great companion to the framework that does a lot of things:

* Setup isolated environments, for new or exisiting projects
* Helps you to dramatically decrease the number of deploys for both portlets and themes projects while developing
* ... And much more! Check the commands below!

### Commands
#### add-portlet
Adds a portlet to the active project

#### build-site
Runs site builder. 

The configuration is at *~/viking-projects/MyProject-env/MyProject/conf/sitebuilder.conf*

The sites.groovy and assets are at *~/viking-projects/MyProject-env/MyProject/sitebuilder*

See [site builder]() section for more information.

#### dependencies-deploy
Deploy the project's dependencies.

#### deploy
Build and deploy the active project

To build the theme project:
```
deploy --target theme
```


#### full-deploy
Deploy the project and its dependencies.

#### help
List all commands usage

#### list-projects

Lists all the projects that are defined in the ‘projectsDir’.  The ‘projectsDir’ location is defined in the init.conf (*~/.viking-shell/conf*) file. By default the ‘projectsDir’ is viking-projects and is located in the user home directory.  The path specified in this file must be relative to the user home directory.
This is the default definition:
var set --name projectsDir --value "viking-projects"

#### new-project

Creates a new Viking project.  When the command is issued the shell requires a project name and the Liferay version to be used.

For every Viking project the following structure is created:
* MyProject-env
 * MyProject (Actual location of the Viking project)
 * liferay (Complete liferay installation for the project with a database in mysql.)
 * theme
 * project.conf (Configuration file for your project)

#### restore-database
Restore database. This will erase your project's database and create it from the backup stored in *~/MyProject-env/MyProject/sql*.

#### setup-project
Sets up a new or existing project. Is intended to be used when a new project is just downloaded from your favorite version control system.

#### start
Starts Liferay for the active project.

#### status
Liferay status.

#### stop
Stops Liferay for the active project.

#### tail-log
Follows Liferay's tomcat/logs/catalina.out log.

#### use-project
Set the active project.

#### add-portlet
Adds a portlet to the active project

Example:
```
add-portlet --name Name
```

#### clean
Cleans build folders, and tomcat webapps, temp and work if specified

Examples:

To clean *tomcat/webapps/MyPortlet* folder:
```
clean --portletsWebapp
```

To clean portlet's and theme's build folder:
```
clean --portletsBuild --themeBuild
```

To clean everything:
```
clean --portletsBuild --portletsWebapp --themeBuild --themeWebapp --tomcatTempAndWork
```

There's a shorthand for this last one:
```
clean --everything
```

#### init-dev-conf
Initializes dev.conf file of your portlets project.

#### install-project
Installs a project from a git repository.

Example:
```
install-project --gitRepository git@github.com:digitalgeko/viking-booking-tutorial.git
```

#### install-shell
Installs the version specified of viking-shell

Example:
```
install-shell --version latest
```

To install a specific version:
```
install-shell --version 0.1.8
```

#### prod-war
Builds a WAR file without the dev.conf file, ready to deploy to production.

#### pwd
Shows project paths.

#### test
Runs project tests.

Examples:
```
test --regex *IntegrationTests
```

To clean before running tests:
```
test --clean
```


# Essential documentation

## Folder structure

|**File path**|**Description**|
|---------------------------|-------------|--------------|
|conf/log4j.properties			|log4j configuration file|
|conf/portlet.conf				|Viking configuration file|
|conf/dev.conf					|Overrides project.conf variables. This file **SHOULD NOT** be deployed to production.|
|conf/sitebuilder.conf			|Sitebuilder configuration file|
|i18n/								|Folder that will contain Language.properties files|
|public/coffee					|Here you can drop your coffee files, they will be compiled and be available in the WAR js folder when deploying|
|public/css						|CSS folder|
|public/icon.png					|Icon that will be shown in your portlets|
|public/images					|Images folder|
|public/js						|JavaScript files |
|resources/						|Project resources |
|sitebuilder/sites.groovy		|Sitebuilder script file |
|test/functional					|Folder that will contain functional tests|
|test/integration				|Folder that will contain integration tests|
|test/resources					|Tests resources folder|
|viking/controllers				|Here you will have your controllers|
|viking/models					|Here you will have your models|
|viking/views						|Here you will have your **freemarker** templates|
|viking/views/viking_macros |In this folder you find useful Viking macros that are included on all templates, you can change them if you want :) |
|.templates                 |If you can't accomplish something with viking configuration, this folder has viking templates for generating source code, you can customize any xml you want in order to meet you specific needs.|


## Configuration
Configuration file is a groovy property file

First you will have a simple configuration file like:
```
mongo.db.host="127.0.0.1"
mongo.db.name="MyProject"
// mongo.db.username=
// mongo.db.password=

MyPortletName {
	instanceable = false
}
```
Notice that there are many ways to declare properties, take a look at this link for more information: [http://groovy.codehaus.org/ConfigSlurper](http://groovy.codehaus.org/ConfigSlurper)

You can also define a *conf/dev.conf* configuration file to define configuration variables that are specific for the development environment; for example the database connection in your development and production environments might not be the same. conf/dev.conf file **SHOULD NOT BE ADDED TO YOUR VERSION CONTROL SYSTEM!**.

If for some reason you need to create a WAR file excluding dev.conf, you can run in your terminal:

```
gradle war -Penv=prod
```

Or execute the `prod-war` command in your viking-shell.

The WAR file will be located in *~/MyProject-env/MyProject/build/libs* as usual in any gradle project.

## Routes and Redirects

In viking routes are declared as follows:

ControllerName.method

For example, if we have the next controller:

```
class MyPortlet extends Controller {

    @Render
    def view() {
        "Hello World!"
    }

}
```
### Use in html
```
<a href="${route('MyPortlet.view')}">Go to My Portlet view!</a>
```

or sending parameters:

```
<a href="${route('MyPortlet.view', {"param1":"myValue"} )}">Go to My Portlet view!</a>
```
### Use in javascript
```
var viewAction = ${jsRoute('MyPortlet.view')}
```

The most common use case is using it to send parameters:

```
var viewAction = ${jsRoute('MyPortlet.view', ['param1'] )}
```

This will create a javascript function with placeholders for each parameter, you can call it this way:

```
viewAction({ param1:"myValue" });
```

// will generate a string for the action's url//

### Use in controllers

Redirects can easily be done this way:

```
    @Render
    def goToView() {
        redirect("MyPortlet.view")
    }
```

Or with parameters:

```
    @Render
    def goToView() {
        redirect("MyPortlet.view", [param1: "myValue"] )
    }
```
## Return values

In viking you can return a set of supported values, for example consider the next action that returns a string:

```
class MyPortlet extends Controller {

    @Render
    def view() {
        "Hello World!"
    }

}
```

It will display "Hello World" in your portlet, but if we change it to:

```
class MyPortlet extends Controller {

    @Render
    def view() {
        
    }

}
```

or:

```
class MyPortlet extends Controller {

    @Render
    def view() {
        [renderVariable:"myValue"]
    }

}
```

it will look for a template in views/MyPortlet/view.ftl, process it, and display the resulting HTML.

To return a JSON, just create an object and return it as follows:

```
class MyPortlet extends Controller {

    @Render
    def view() {
        def objects = [one:"1", two:"2"]
        renderJSON(objects)
    }

}
```

## Data binding

Let's say you have the next model:

```
class Person {

	String name
	String email
	Int age

}
```

In you HTML form you can just name your inputs:

```
<form action="...">
	Name: <input type="text" name="person.name">
	
	Email: <input type="text" name="person.email">
	
	Age: <input type="text" name="person.age">	
	
	<input type="submit" value="Save">
</form>
```

And bind it in your controller:

```
    @Action
    def savePerson() {
        def person = bind("person", Person.class)
        // use person object...
    }
```

## Validations

Consider the last example, maybe you want to have a valid email address and a required name, you can do this with validation annotations:

```
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.NotNull

class Person {
	
	@NotNull @NotEmpty
	String name

	@Email
	String email

	Int age

}
```

And run a validation in the controller:

```
	@Action
	def savePerson() {
	    def person = bind("person", Person.class)
	    validator.validate("person", person)

	    if (validator.hasErrors()) {
	    	// display the form again
	    	return render("MyPortlet/view.ftl", person)
	    }

	    // use a VALID person object...
	}
```

If errors are present, you can retrieve them from the errors macro that is available in every template:

```
<form action="...">
	Name: <input type="text" name="person.name">
	<@errors "person.name" />

	Email: <input type="text" name="person.email">
	<@errors "person.email" />

	Age: <input type="text" name="person.age">	
	
	<input type="submit" value="Save">
</form>
```

You can learn more about Hibernate validations here:

[http://docs.jboss.org/hibernate/validator/5.0/reference/en-US/html_single/](http://docs.jboss.org/hibernate/validator/5.0/reference/en-US/html_single/)

## Models

### Morphia

*"Morphia is a lightweight type-safe library for mapping Java objects to/from MongoDB. Morphia provides a typesafe, and fluent Query API support with (runtime) validation. Morphia uses annotations so there are no XML files to manage or update. Morphia should feel very comfortable for any developer with JPA experience."*


Read more at [https://github.com/mongodb/morphia](https://github.com/mongodb/morphia)

You should use morphia if your project allows to, because the **angular + groovy + mongo** json combination works like a charm, you can build awesome things with really simple models.

First you need to configure your mongo connection in *conf/portlet.conf*:
```
mongo.db.host="127.0.0.1"
mongo.db.name="MyProject"
// mongo.db.username=
// mongo.db.password=
```

#### Definition
Let's create a morphia model:
```
package models

import org.mongodb.morphia.annotations.Entity
import nl.viking.model.morphia.Model

@Entity
class Person extends Model {

    String name
    String email
    Int age

}
```
Please note that the imports come from the **morphia** package; that's very important, because is a common mistake to import from the hibernate package when you need the morphia one, or the other way around.

#### CRUD Operations

##### Create
To **save** a model:
```
def person = new Person(name:"My name", ...)
person.save()
```

##### Read
To **read** (or **query**) an object or a list of objects, there are many ways:

Using `findById(id)`:
```
def person = Person.findById(personId)
```
Where `personId` is a String containing a mongo id.

Using `find(fields, values)`
```
// single record
def person = Person.find("name,age", "My name", 25).get()

// list of records
def somePeople = Person.find("age", 21).asList()

def allPeople = Person.find().asList()

// another way to retrieve all records
def allPeople = Person.findAll()
```

In fact, the `find()` method returns a Morphia Query, you should really check how to use morphia queries here: [https://github.com/mongodb/morphia/wiki/Query](https://github.com/mongodb/morphia/wiki/Query)

You can also have access to the Morphia's DataStore by calling `Person.ds()`

##### Update
To **update** a model, is almost the same, but you retrieve a record from the database instead of creating a new instance:
```
def person = Person.findById(personId)
person.save()
```

##### Delete
```
def person = Person.findById(personId)
person.delete()
```


### Hibernate
If you have a Liferay already configured to use a database via portal-ext.properties, you already have configured viking's hibernate connection as well.

If you need to use a different connection, or don't have a connection available, you can configure hibernate using any hibernate property using the following format:

```
hibernate.property = value
```

Here's an example:
```
hibernate {
	dialect = "org.hibernate.dialect.MySQLDialect"
	hbm2ddl.auto = "update"
	connection {
		driver_class = "com.mysql.jdbc.Driver"
		url = "jdbc:mysql://localhost:3306/mydb?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useFastDateParsing=false"
		username = "myuser"
		password = "mysecret"

	}
}
```

Please note that we didn't use `hibernate.property = value` format, but is still a valid ConfigSlurper format (see [Configuration section](#configuration))


#### Definition
Let's create a hibernate model:
```
package models

import nl.viking.model.hibernate.Model
import javax.persistence.Entity

@Entity
class Person extends Model {

    String name
    String email
    Int age

}
```

Again, please note the **hibernate** package imports.

#### CRUD Operations

##### Create
```
def person = new Person(name:"My name", ...)
person.save()
```

##### Read
To **query** records in hibernate:

Using `findById(id)`
```
def person = Person.findById(personId)
```
Where `personId` is a long.

Using `find(whereString, valueMap)`

This method will return a **javax.persistence.Query**, so you can do something like:
```
// list of records
def somePeople = Person.find("age = :age", [age: 21]).resultList

def allPeople = Person.findAll()

// Complex queries
def youngPeopleAge = Person.query("SELECT p.age FROM Person p WHERE age < :maxAge", [maxAge: 18]).resultList

// Single record
def mike = = Person.find("name = :name", [age: "Mike"]).singleResult

```

##### Update
To **update** a model, is almost the same, but you retrieve a record from the database instead of creating a new instance:
```
def person = Person.findById(personId)
person.name = "Bob"
person.save()
```

##### Delete
```
def person = Person.findById(personId)
person.delete()
```

## Liferay helpers

There's a very useful helper variable named "**h**" that can be used to have quick access to very common variables you use everyday in Liferay, for example: To retrieve request's themeDisplay, you can simply write: 

```
    @Render
    def view() {
        h.themeDisplay // use it...  
    }  
```

Instead of the boring way:

```
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
```

(Seriously, have you ever done this in a different way?)

**h** is available in templates as well.

### Variables

Variables available in **h** are:

|**Variable name**				|**Class**|
|-----------------				|---------|
|h.themeDisplay					|com.liferay.portal.theme.ThemeDisplay|
|h.servletRequest				|javax.servlet.http.HttpServletRequest|
|h.servletResponse				|javax.servlet.http.HttpServletResponse|
|h.user							|com.liferay.portal.model.User|
|h.portletId						|java.lang.String|
|h.session						|javax.portlet.PortletSession|
|h.contextPath					|java.lang.String|
|h.portletConfig					|javax.portlet.PortletConfig|
|h.serviceContext				|com.liferay.portal.service.ServiceContext|
|h.messages						|nl.viking.i18n.Messages|

Every variable is instantiated the first time you request it, so there are no performance implications.

### Methods

#### - boolean hasPermission(resourceName, actionId, groupId = null, primKey = null)

This method checks if the current user has permissions to apply the action `actionId` to the resource `resourceName`, with an optional `groupId` and `primKey`.

## Jobs

Viking can schedule jobs using Liferay's built-in Quartz Scheduler. First, you need a job class, for example:

```
package jobs

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;

class MyJob implements MessageListener {

  @Override
  void receive(Message message) throws MessageListenerException {
    println "Message triggered!"
  }
}
```

Then you need to add the job to a portlet, so in a portlet's configuration you should have something like:

```
MyPortlet {
	jobs = [
		[class:'jobs.MyJob', unit:'minute', value:60]
	]
}
```

Values can be one of the following


|**unit**|**value**|**type**|
|--------|---------|--------|
|cron|Cron trigger value|String|
|second, minute, hour, day, month or year|Simple trigger value|Integer|

You can have as many jobs you want, just add it to the "jobs" array in the configuration, for example:

```
MyPortlet {
	jobs = [
		[class:'jobs.MyJob', unit:'second', value:5],
		[class:'jobs.MyJob2', unit:'hour', value:12],
		[class:'jobs.MyJob2', unit:'cron', value:'0 0 12 * * ?']
	]
}
```

## Tests

We run our tests using **Spock** ([http://spockframework.org](http://spockframework.org)) and **Arquillian** ([http://arquillian.org](http://arquillian.org)), please refer to their docs for further information.

Every viking project has 3 tests directories: `test/integration`, `test/functional` to code your integration and functional tests respectively and `test/resources` to add test resources for both directories.

Every test file should have the following:

 * Must be annotated with **@RunWith(ArquillianSputnik)**
 * Must extend from **spock.lang.Specification**
 * Must have a **static** method that returns a **WebArchive**, and must be annotated with **@Deployment**, there's a handy way to do this:

 ```
 @Deployment
	def static WebArchive "create deployment"() {
		ShrinkWrap.createFromZipFile(WebArchive.class, VikingTestDeploymentHelper.warFile)
	}
 ```
 * And of course, must have at least one method with your test

### Examples

#### Integration

The following example validates that there's at least one company in a liferay installation.

```
import com.liferay.portal.service.CompanyLocalServiceUtil
import nl.viking.arquillian.deployment.VikingTestDeploymentHelper
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.spock.ArquillianSputnik
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.junit.runner.RunWith
import spock.lang.Specification
import spock.lang.Unroll

@RunWith(ArquillianSputnik)
class DefaultIntegrationTest extends Specification {

	@Deployment
	def static WebArchive "create deployment"() {
		ShrinkWrap.createFromZipFile(WebArchive.class, VikingTestDeploymentHelper.warFile)
	}

	def "liferay should have at least one company" () {
		when:
		def companyCount = CompanyLocalServiceUtil.companiesCount

		then:
		companyCount > 0
	}

}

```

#### Functional

The following example requests the liferay login page, and validates that after logging in, the user should be at the user's personal site:

```
import nl.viking.arquillian.deployment.VikingTestDeploymentHelper
import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.container.test.api.RunAsClient
import org.jboss.arquillian.drone.api.annotation.Drone
import org.jboss.arquillian.graphene.Graphene
import org.jboss.arquillian.spock.ArquillianSputnik
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.junit.runner.RunWith
import org.openqa.selenium.WebDriver
import pageobject.LoginPage
import spock.lang.Specification

@RunWith(ArquillianSputnik)
class DefaultFunctionalTest extends Specification {

	@Deployment
	def static WebArchive "create deployment"() {
		ShrinkWrap.createFromZipFile(WebArchive.class, VikingTestDeploymentHelper.warFile)
	}

	@Drone
	WebDriver driver;

	@RunAsClient
	def "liferay is running" () {

		expect:
		def loginPage = Graphene.goTo(LoginPage)
		loginPage.login(email, pass)

		driver.currentUrl.toURI().path == "/user/$username/home"

		where:
		email 				| pass 		| username
		"test@liferay.com"	| "test"	| "test"

	}

}

```

The WebDriver, as you see, is injected. By default, we use **firefox**, but you can change by modifying the file `test/resources/arquillian.xml`. For example, to use phantomjs, your arquillian.xml should look like:

```
<?xml version="1.0"?>
<arquillian
		xmlns="http://jboss.org/schema/arquillian"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://jboss.org/schema/arquillian http://jboss.org/schema/arquillian/arquillian_1_0.xsd"
		>
	<container qualifier="tomcat" default="true">
		<configuration>
			<property name="jmxPort">8099</property>
			<property name="pass">tomcat</property>
			<property name="user">tomcat</property>
		</configuration>
	</container>

	<extension qualifier="webdriver">
		<property name="browser">phantomjs</property>
	</extension>
</arquillian>
```

The important line here is `<property name="browser">phantomjs</property>`

Also, there's a pageobject.LoginPage class, this class is located inside `test/functional/pageobject` and looks like this:

```
package pageobject

import org.jboss.arquillian.graphene.Graphene
import org.jboss.arquillian.graphene.findby.FindByJQuery
import org.jboss.arquillian.graphene.page.Location
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

@Location("/c/portal/login")
class LoginPage {

	@FindBy(id = "_58_login")
	private WebElement userName;

	@FindBy(id = "_58_password")
	private WebElement password;

	@FindByJQuery(".button-holder > .btn.btn-primary")
	private WebElement loginButton;

	void login(String u, String p) {
		this.userName.sendKeys(u)
		this.password.sendKeys(p)
		Graphene.guardHttp(loginButton).click()
	}
}
```

The class **pageobject.LoginPage** uses Graphene's pageobject approach.

To read more about Graphene, Drone and integration with Arquillian, follow this URL: [http://arquillian.org/guides/functional_testing_using_graphene](http://arquillian.org/guides/functional_testing_using_graphene)

### Running tests
To run the tests just execute the `test` viking shell command:
```
MyProject> test
```


## Site builder
TODO
### Configuration

### Import sites

### Export sites
