# Viking Framework

Viking is an Open Source Portlet development framework for [Liferay](http://www.liferay.com).  It takes advantage of the groovy language and introduces convention over configuration to the Liferay ecosystem.
Our goal is to provide portlet developers with a modern and agile true MVC framework that speeds up the development process. 

Some of the key features are:
* Productive
* MVC Architecture
* Easy to learn
* Fast development cycle

Technology used:
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
 * [Persistence](#persistence)
 * [Liferay helpers](#liferay-helpers)
 * [Jobs](#jobs)
 * [Site builder](#site-builder)


# Getting started
## Prerequisites
* JDK >= 7 
* brew ([http://brew.sh/](http://brew.sh/))
* Gradle ([http://www.gradle.org/](http://www.gradle.org/))
* Git ([http://git-scm.com/](http://git-scm.com/))
* Coffeescript ([http://coffeescript.org/](http://coffeescript.org/))
* MySQL ([http://www.mysql.com/](http://www.mysql.com/))
* MongoDB ([http://www.mongodb.org/](http://www.mongodb.org/))
* Liferay ([http://liferay.com/](http://liferay.com/))

## Installation

Install viking-shell, it's all you need to setup your viking projects.

Go to:
[https://github.com/digitalgeko/viking-shell/releases](https://github.com/digitalgeko/viking-shell/releases) and download the latest release.

Uncompress the zip, install it somewhere in your file system and add viking-shell/bin to your **$PATH.**

You should also add this to your ~/.profile (or any other profile file you have) to make gradle build faster:
```
export GRADLE_OPTS="-Xmx768m -Dorg.gradle.daemon=true"
```

## Hello world
Let’s execute the “new-project” command, this will prompt you asking for the project name, let’s type “MyProject” for example. Then it will ask you for the liferay version you want to use, we’ll choose Liferay 6.2.1 GA2 in this example. This will create the project structure you need including:
* Liferay Tomcat bundle
* Viking portlets project
* Theme project (maven) project.conf, basic configuration of your viking project
* SQL folder, will contain a backup file to fully restore your liferay’s database whenever you want (using “restore-database” command).

Additionally, this will create a schema named “MyProject” on your database, and it will be populated with the SQL script file located in sql/LR621GA2.sql, the script is named that way because it’s the backup for a Liferay 6.2 GA2 specifically.

Moreover, a **file listener** is now active, it will automatically reload your changes as you modify your source files located in the portlets and theme projects.

A big output should appear, containing the information about deploying portlets and theme projects.

Notice that the prompt has changed for “MyProject>” now, this is will help you to quickly see which project is active.

Now, let’s start our liferay: Execute the “start” command, this will run Liferay’s startup.sh script file and listen for a process listening on port 8080 (or the one defined in *liferay/tomcat/conf/server.xml*), you can follow the logs with “tail-log” command in another viking-shell session.

A deploy of your portlets and theme has already been done for you after creating the project, but if you need to manually deploy your portlets for some reason, you can do it by executing the “deploy” command.

And “deploy --target theme” to deploy the theme project. 

Alternatively, you can run “full-deploy” command, which will deploy: portlets project, theme project, and all dependencies specified in project.conf; in fact, this is the command executed when creating a project.

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

#### update
Updates templates located in *~/.viking-shell/templates*.

#### use-project
Set the active project.

# Essential documentation

## Folder structure

|**File path**|**Description**|
|---------------------------|-------------|--------------|
|conf/log4j.properties      |log4j configuration file|
|conf/portlet.conf          |Viking configuration file|
|public/coffee              |Here you can drop your coffee files, they will be compiled and be available in the WAR js folder when deploying|
|public/css                 |CSS folder|
|public/icon.png            |Icon that will be shown in your portlets|
|public/images              |Images folder|
|public/js                  |JavaScript files |
|viking/controllers         |Here you will have your controllers|
|viking/models              |Here you will have your models|
|viking/views               |Here you will have your **freemarker** templates|
|viking/views/viking_macros |In this folder you find useful Viking macros that are included on all templates, you can change them if you want :) |
|.templates                 |If you can't accomplish something with viking configuration, this folder has viking templates for generating source code, you can customize any xml you want in order to meet you specific needs.|


## Configuration
Configuration file is a groovy property file

First you will have a simple configuration file like:
```
mongo.db.host="127.0.0.1"
mongo.db.name="${projectName}"
// mongo.db.username=
// mongo.db.password=

MyPortletName {
	instanceable = false
}
```
Notice that there are many ways to declare properties, take a look at this link for more information: [http://groovy.codehaus.org/ConfigSlurper](http://groovy.codehaus.org/ConfigSlurper)

### Available variables in portlet.conf
TODO

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

There are many return types you can send to viking, please check them below:

TODO

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

[TODO] Target objects...

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

## Persistence
TODO

## Liferay helpers

There's a very useful helper variable named "**h**" that can be used to have quick access to very common variables you use everyday in Liferay, for example:To retrieve request's themeDisplay, you can simply write: 

```
    @Render
    def view() {
        h.themeDisplay // use it...  
    }  
```

Instead of Liferay's (boring) way:

```
ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
```

(Seriously, have you ever done this in a different way?)

Variables available in **h** are:

|**Variable name**|**Class**|
|-----------------|---------|
|h.themeDisplay|com.liferay.portal.theme.ThemeDisplay|
|h.servletRequest|javax.servlet.http.HttpServletRequest|
|h.locale|java.util.Locale|
|h.user|com.liferay.portal.model.User|
|h.portletId|java.lang.String|
|h.session|javax.portlet.PortletSession|

**h** is available in templates as well.

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



## Site builder
