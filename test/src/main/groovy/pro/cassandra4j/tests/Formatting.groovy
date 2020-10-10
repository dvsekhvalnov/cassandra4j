package pro.cassandra4j.tests

class Formatting {
    def static format(List<List<Object>> table) {

        def cellWidth = maxWidth(table) + 1

        def builder = new StringBuilder()

        table.each { row ->
            row.eachWithIndex { cell, idx ->
                builder.append(toText(cell).padRight(cellWidth))

                if(idx != row.size() -1 ) {
                    builder.append("|")
                }
            }

            builder.append("\n")
        }

        builder.toString()
    }

    def static toText(Object val) {
        return val.toString()+" "
    }

    private static def maxWidth(List<List<Object>> table) {
        long max = 0

        table.each { row ->
            row.each { value ->
                //TODO: check type

                def strLength = width(value)

                if(strLength > max) {
                    max = strLength
                }
            }
        }

        return max
    }


    def static width(Object val) {
        def type = val.class

        if(Date == type) {
            return 21
        }

        return val.toString().length()
    }
}
