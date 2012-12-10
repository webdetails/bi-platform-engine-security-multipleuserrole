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

Just run *ant* and you shuold be all set


### Deploy the jar in Pentaho

Copy the resulting file to pentaho's lib dir, (eg:
*/opt/pentaho/server/webapps/pentaho/WEB-INF/lib/*).


### Copy the muiltiple provider spring config files to *solution/system*

Copy the following files from *resources/* to *pentaho-solutions/system/*:

* applicationContext-pentaho-security-multiple.xml
* applicationContext-spring-security-multiple.xml


### Change *pentaho-spring-beans.xml* to load the new files

Instead of loading one of the individual files (defaults to hibernate authentication), 
tell pentaho to instead load the new configuration files. 

*pentaho-spring-beans.xml* should then look something like:

	<beans>
	  <import resource="pentahosystemconfig.xml" />
	  <import resource="adminplugins.xml" />
	  <import resource="systemlisteners.xml" />
	  <import resource="sessionstartupactions.xml" />
	  <import resource="applicationcontext-spring-security.xml" />
	  <import resource="applicationcontext-common-authorization.xml" />
	  <import resource="applicationcontext-spring-security-multiple.xml" />
	  <import resource="applicationcontext-pentaho-security-multiple.xml" />
	  <import resource="pentahoobjects.spring.xml" />
	</beans>


*Note: This snippet is taken from pentaho 4.8, different versions may have files
with different content*


### Change the list of providers in *applicationContext-spring-security.xml*

In *applicationContext-spring-security.xml*, look for a bean named
*authenticationManager*, and add the providers you want. If you're using the
sample file *applicationContext-spring-security-multiple.xml*, the 2 referenced
beans are called *daoAuthenticationProvider* and *daoAuthenticationProvider2*.
You're not limited to just 2 providers.


### Edit *applicationContext-spring-security-multiple.xml* and
*applicationContext-pentaho-security-multiple.xml* for your case

This is the part where you configure the types of authentication you want. Even
if it seems complicated at first, you'll notice that the 2 configuration files
for multiple authentication are simply a concatenations of the individual files
provided by pentaho, making sure the bean names don't colide. 

On this example case we have hibernate and memory authentication joined together.


### Launch the bi-server

Launch the BI server and *hopefully* you're all set.


### Troubleshooting

Of course this won't work out of the box. Pay close attention to the logs. One
of the most common errors is bean id collision, which is reported in the
pentaho logs.

One other option is setting the *spring* logs to debug in *log4j.xml*


### Questions / doubts

For any additional questions, feel free to get in touch: ctools _at_ webdetails.pt



