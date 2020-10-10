package pro.cassandra4j.tests.assertj

import org.assertj.core.api.Assertions
import pro.cassandra4j.tests.Cassandra

class CassandraAssertions extends Assertions {
    static CassandraTableAssertion assertThat(Cassandra actual) {
        new CassandraTableAssertion(actual)
    }
}
