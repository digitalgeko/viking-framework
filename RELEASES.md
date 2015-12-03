# Release notes

## 0.4.3

Morphia upgrade to 1.0.1
Default liferay version set to 6.2.3

## 0.4.2

Bug fixes

## 0.4.1

Bug fixes

## 0.4.0

### Caching
To cache the response of your portlet for N seconds, just add a "caching" section on your portlet's configuration, example for 300 seconds (5 minutes):

```
MyPortlet {
	// ...
	caching {
		enabled = true
		timeToLive = 300 // secs
	}
}
``` 


## 0.3.1

* Added Bootstrap listener to use with web.xml (in order to init viking when running arquillian tests)
* Controllers are now abstract classes
* Added `bindFiles` controller function.
* Package refactoring.

### Migration guide
* Change your portlets project build.gradle line to use viking-framework 0.3.1:
```
compile 'org.vikingportlets:viking-framework:0.3.1'
```

* Edit `.templates/web.xml`, add inside the tag `<web-app>`:
```
<listener>
		<listener-class>nl.viking.listeners.BootstrapListener</listener-class>
	</listener>
```
* Edit `.templates/conf/liferay-portlet.xml`, change the package of `VikingActivityInterpreter` to **nl.viking.model.liferay.socialactivity.VikingActivityInterpreter**. It will look like this:
```
<social-activity-interpreter-class>
                nl.viking.model.liferay.socialactivity.VikingActivityInterpreter
</social-activity-interpreter-class>
```


## 0.3.0

### Hibernate
The usage of hibernate models is completely different in this release. We're using EntityManager instead of the Session API. The way we query the database is different, find() returns a **javax.persistence.Query**. `query()` does not longer receives a closure, no usage of criteria anymore. Please check the docs for more  detailed information [https://github.com/digitalgeko/viking-framework#hibernate](https://github.com/digitalgeko/viking-framework#hibernate).

### Morphia
We've updated morphia to use org.mongodb morphia, instead of the old com.google.code morphia. This means that if you have morphia models you'll need to change the package **com.google.code** to **org.mongodb**. That's it.

### Arquillian
We've introduced this awesome testing framework to execute the tests and you can now use it, check the docs for more information [https://github.com/digitalgeko/viking-framework#tests](https://github.com/digitalgeko/viking-framework#tests).

### Additional notes
* Updated jackson-jaxrs -> 1.9.13
* Updated groovy-all -> 2.3.7
* Updated gmongo -> 1.3
* Updated commons-configuration -> 1.10
* Updated reflections -> 0.9.9
* Updated freemarker ->2.3.21
* Updated gson -> 2.2
* Updated hibernate-validator -> 5.1.3.Final
* Updated log4j -> 1.2.17
* Updated morphia -> 0.108
* Updated hibernate -> 4.3.7.Final
* Added HikariCP
* Removed bonecp

