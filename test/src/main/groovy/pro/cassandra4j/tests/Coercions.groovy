package pro.cassandra4j.tests

import com.datastax.driver.core.DataType

import java.text.ParseException
import java.text.SimpleDateFormat

class Coercions {
    def coerce(value, cqlType)
    {
        if(cqlType == DataType.NativeType.date())
        {
            return toDate(value)
        }

        if(cqlType == DataType.NativeType.time())
        {
            return toDate(value)
        }

        if(cqlType == DataType.NativeType.timestamp())
        {
            return toDate(value)
        }

        if(cqlType == DataType.uuid())
        {
            return toUuid(value)
        }

        if(cqlType == DataType.text())
        {
            return toText(value)
        }

        //can't coerce, just return what we get
        return value
    }

    static def toDate(Object val)
    {
        if(val instanceof Date)
        {
            return val
        }

        def str = toText(val)

        //attempt to parse multiformats
        def formats = ["yyyy-MM-dd' 'HH:mm:ss.SSSZ"]

        for (it in formats) {
            try {
                def pattern = new SimpleDateFormat(it)
                return new Date(pattern.parse(str).getTime())
            }
            catch (ParseException pe) {
                pe.printStackTrace()
            }
        }

        throw new IllegalArgumentException("Unable to coerce value '"+str +"' to java.util.Date")
    }

    static def toUuid(Object val) {
        return UUID.fromString(toText(val))
    }

    static def toText(Object val) {
        return val.toString()
    }
}
