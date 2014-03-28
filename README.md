Multiple Authentication Providers in Pentaho
============================================

Objective
-------

The goal of this project is to add support for multiple authentication in
[Pentaho](http://www.pentaho.com). Currently, there are several providers that
can be used, like LDAP, hibernate and jdbc, but it's not possible to use
several at the same time.

This project allows for that by implementing a bean that will cycle through all
the desired providers


How to use
----------

Following this steps should get you going:

### Compile the project

Just run *ant* and you should be all set


### Deploy the jar in Pentaho

Copy the resulting file to pentaho's lib dir, (eg:
*/pentaho/server/webapps/pentaho/WEB-INF/lib/*).


### Copy the muiltiple provider spring config files to *solution/system*

Copy the following files from *resources/* to *pentaho-solutions/system/*:

* applicationContext-spring-security-multiple.xml
* applicationContext-pentaho-security-multiple.xml

### Change *pentaho-spring-beans.xml* to load the new files

Leave all the existing files there, add these 2 new config files

*pentaho-spring-beans.xml* should then look something like:

	<beans>
	  (...)
	  <import resource="applicationContext-spring-security.xml" />
  
	  <import resource="applicationContext-spring-security-superuser.xml" />
	  <import resource="applicationContext-pentaho-security-superuser.xml" />
	  
	  <import resource="applicationContext-common-authorization.xml" />

	  <import resource="applicationContext-spring-security-memory.xml" />
	  <import resource="applicationContext-pentaho-security-memory.xml" />

	  <import resource="applicationContext-spring-security-ldap.xml" />
	  <import resource="applicationContext-pentaho-security-ldap.xml" />

	  <import resource="applicationContext-spring-security-jackrabbit.xml" />
	  <import resource="applicationContext-pentaho-security-jackrabbit.xml" />
	  
	  <import resource="applicationContext-pentaho-security-jdbc.xml" />
	  <import resource="applicationContext-spring-security-jdbc.xml" />

	  <import resource="applicationContext-spring-security-multiple.xml" />
	  <import resource="applicationContext-pentaho-security-multiple.xml" />
	  
	  <import resource="pentahoObjects.spring.xml" />
	  (...)
	</beans>


### Edit *applicationContext-spring-security-multiple.xml* 

In the *PROVIDERS INFO* block, you'll notice that each provider info xml block references
bean id's declared in the other config files. Ensure all bean ids are correct. 


### Enabling multiple authentication

edit pentaho-solutions/system/security.properties and set security.provider=multiple 

Note: at any time you can switch providers simply by declaring which one you intend to use on this security.properties file

Accepted provider keys are: ldap, jdbc, memory, jackrabbit, multiple


### Launch the bi-server

Launch the BI server and *hopefully* you're all set.


### Troubleshooting

Of course this won't work out of the box. Pay close attention to the logs. One
of the most common errors is bean id collision, which is reported in the
pentaho logs.

One other option is setting the *spring* logs to debug in *log4j.xml*


### Questions / doubts

For any additional questions, feel free to get in touch: ctools _at_ webdetails.pt



