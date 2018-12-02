Simple servlet application that outputs sample db-table in html-table with filter fields and sorting ability.

To build application use [Maven](https://maven.apache.org/). Maven will build it as standard [war-package](https://en.wikipedia.org/wiki/WAR_(file_format)).

Db-connection should be declared as jndi-resource `jdbc/db` (for Tomcat, `conf/context.xml` is the right place), for example, db-connection with sqlite db:
```xml
    <Resource
        name="jdbc/db" type="javax.sql.DataSource"
        url="jdbc:sqlite:parts.db"
        driverClassName="org.sqlite.JDBC"
    />
```  