package pro.cassandra4j.tests.rules

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.querybuilder.QueryBuilder
import org.junit.rules.ExternalResource
import pro.cassandra4j.tests.Cassandra

class CassandraRule extends ExternalResource {

    private Cassandra cassandra

    CassandraRule(Cassandra cassandra)
    {
        this.cassandra = cassandra
    }

    @Override
    protected void before() throws Throwable
    {
        cassandra.connect()
    }

    @Override
    protected void after()
    {
        cassandra.tables.each {
            cassandra.session.execute(QueryBuilder.truncate(it))
        }

        cassandra.close()
    }
}
