package pro.cassandra4j.tests

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import org.tools4j.groovytables.GroovyTables
import org.tools4j.groovytables.Rows

class CassandraTableBuilder {

    static given(Cassandra cassandra, Closure content) {

        cassandra.connect()
        def columnFamilyMeta = cassandra.indexTableMeta()

        def coercer = new Coercions()

        //parse data and build CQL insert statements
        Rows data = GroovyTables.createRows(content)

        data.toArray().each { dataRow ->
            def insertStmt = QueryBuilder.insertInto(cassandra.keyspace, cassandra.table)

            dataRow.values.eachWithIndex { value, idx ->
                def name = data.columnHeadings[idx]
                def cqlType = columnFamilyMeta[name]
                def val = coercer.coerce(value, cqlType)

                insertStmt.value(name,  val)
            }

            //fire insert statement
            cassandra.session.execute(insertStmt)
        }

        cassandra.close()
    }
}
