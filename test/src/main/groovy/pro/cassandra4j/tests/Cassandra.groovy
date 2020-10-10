package pro.cassandra4j.tests

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session

class Cassandra {
    String keyspace
    String table
    List<String> tables = []
    Map<String, String> options
    Session session
    Cluster cluster


    static Cassandra table(keyspace, table, Map<String, String> opts = [:]) {
        new Cassandra(keyspace: keyspace, table: table, options: opts)
    }

    static Cassandra tables(String keyspace, Map<String, String> opts = [:], String ...tables) {
        new Cassandra(keyspace: keyspace, tables: tables, options: opts)
    }

    def connect() {
        cluster = Cluster.builder()
                .withClusterName(options.get("clusterName", "Test Cluster"))
                .addContactPoint(options.get("host", "127.0.0.1")).build()

        session = cluster.connect(keyspace)

        session
    }

    def indexTableMeta() {
        def definitions = session.cluster.metadata.getKeyspace(keyspace).getTable(table).columns

        //index metadata
        definitions.collectEntries { [it.name, it.type] }
    }

    def close() {
        session.close()
        cluster.close()
    }
}
