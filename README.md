# cassandra4j
Various tools, libs and helpers for working and testing cassandra based applications

## pro.cassandra4j.testing
Fancy way of writing integration tests for your cassandra data layer. Table-oriented groovy DSL for easy
visual table assertions. Integrates with JUnit. Works just fine with java/scala/groovy projects, see details below.

### Status
Not yet published to maven central.

### How to use
1. Clone project
2. `mvn clean install` to make it installed locally
3. Add reference in your project's `pom.xml`:
    ```xml
    <dependency>
        <groupId>pro.cassandra4j</groupId>
        <artifactId>test</artifactId>
        <version>1.0.0</version>
        <scope>test</scope>
    </dependency>
    ```

4. Normally you'd like to run and seed cassandra db during your IT execution phase, can be done with maven configuration for instance
(or any other way too, make sure cassandra accessible on `127.0.0.1`):
    ```xml
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>cassandra-maven-plugin</artifactId>
        <version>3.5</version>
        <configuration>
            <script>${basedir}/src/main/cassandra/cql/01-init-db.cql</script> <!-- seed schema and data -->
            <startNativeTransport>true</startNativeTransport>
            <clusterSize>3</clusterSize>
            <skip>${skipITs}</skip>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>start</goal>
                    <goal>stop</goal>
                </goals>
            </execution>
        </executions>
        <dependencies>
            <dependency>
                <groupId>org.apache.cassandra</groupId>
                <artifactId>cassandra-all</artifactId>
                <version>3.10</version>
            </dependency>
        </dependencies>
    </plugin>
    ```

5. Hook up groovy compiler:
    ```xml
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <compilerId>groovy-eclipse-compiler</compilerId>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>org.codehaus.groovy</groupId>
                        <artifactId>groovy-eclipse-compiler</artifactId>
                        <version>2.9.2-01</version>
                    </dependency>
                    <dependency>
                        <groupId>org.codehaus.groovy</groupId>
                        <artifactId>groovy-eclipse-batch</artifactId>
                        <version>2.4.3-01</version>
                    </dependency>
                </dependencies>
            </plugin>
            <plugin>
                <groupId>org.codehaus.groovy</groupId>
                <artifactId>groovy-eclipse-compiler</artifactId>
                <version>2.9.2-01</version>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
    ```

6. write your JUnit IT tests:

    ```java
    import pro.cassandra4j.tests.rules.CassandraRule.

    public class ITCassandraDao {

        // let junit connect to cassandra and take care of table cleanup:
        // 'users' and 'posts' tables under 'blog' keyspace
        @Rule
        public CassandraRule cassandra = new CassandraRule(tables("blog", "users", "posts"));

        @Test
        void addUser()
        {
            given(table("blog", "users"))
            {
                nickname    | created_at                        | subscription
                "john"      | "2017-09-25 09:32:03.496+0000"    | "TRIAL"
                "mary"      | "2017-09-25 09:32:07.547+0000"    | "PREMIUM"
                "fred"      | "2017-09-24 09:32:07.547+0000"    | "TRIAL"
            }

            // when:
            // use DAO or call API to insert new record into 'users'
            usersStorage.add(new User("smith", oct26, "STANDARD"));

            // then: assert table state after
            assertThat(table("blog", "users")).matchesTo
            {
                nickname    | created_at                        | subscription
                "john"      | "2017-09-25 09:32:03.496+0000"    | "TRIAL"
                "mary"      | "2017-09-25 09:32:07.547+0000"    | "PREMIUM"
                "fred"      | "2017-09-24 09:32:07.547+0000"    | "TRIAL"
                "smith"     | "2017-10-26 16:17:17.190+0000"    | "STANDARD"
            }
        }
    }
    ```
7. keep going till you have strong coverage for you storage layer ;)



