package pro.cassandra4j.tests.assertj

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import org.assertj.core.api.AbstractAssert
import org.tools4j.groovytables.GroovyTables
import org.tools4j.groovytables.Rows
import pro.cassandra4j.tests.Cassandra
import pro.cassandra4j.tests.Coercions
import pro.cassandra4j.tests.Formatting

class CassandraTableAssertion extends AbstractAssert<CassandraTableAssertion, Cassandra> {

    CassandraTableAssertion(Cassandra actual) {
        super(actual, CassandraTableAssertion.class)
    }

    CassandraTableAssertion matchesTo(Closure content) {
        actual.connect()
        def columnFamilyMeta = actual.indexTableMeta()

        def coercer = new Coercions()

        def countQuery = QueryBuilder.select().countAll().from(actual.table).allowFiltering()

        def actualCount = actual.session.execute(countQuery).one().getLong(0)

        Rows data = GroovyTables.createRows(content)

        if(actualCount!=data.size()) {
            failWithMessage("Expected: "+ data.size() +" rows, but found " + actualCount + " rows in Cassandra table.")
        }

        data.toArray().each { dataRow ->

            def selectStmt = QueryBuilder.select().from(actual.keyspace, actual.table)

            dataRow.values.eachWithIndex { value, idx ->
                def name = data.columnHeadings[idx]
                def cqlType = columnFamilyMeta[name]
                def val = coercer.coerce(value, cqlType)

                selectStmt.where(QueryBuilder.eq(name, val))
            }

            def allRows = actual.session.execute(selectStmt).all()

            if(allRows.size() > 1)
            {
                failWithMessage("Expected 1 row with values: " + dataRow + " but got: " + allRows.size() + " rows.")
            }

            if(allRows.size() <= 0)
            {
                def table = [data.columnHeadings, dataRow.values]
                failWithMessage("Unable to find row in Cassandra table:\n\n" + Formatting.format(table))
            }
        }


        actual.close()

        return this
    }

}
