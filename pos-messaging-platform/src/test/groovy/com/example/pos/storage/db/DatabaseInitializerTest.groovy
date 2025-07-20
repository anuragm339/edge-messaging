package com.example.pos.storage.db


import spock.lang.Specification
import javax.sql.DataSource
import java.sql.Connection
import java.sql.Statement
import com.example.pos.storage.db.DatabaseInitializer

class DatabaseInitializerTest extends Specification {

    def "should run schema creation without exceptions"() {
        given:
        def statement = Mock(Statement)
        def connection = Mock(Connection)
        def dataSource = Mock(DataSource)

        and:
        dataSource.getConnection() >> connection
        connection.createStatement() >> statement

        def initializer = new DatabaseInitializer(dataSource)

        when: "Simulate application start"
        initializer.onStartup(null)

        then:
        1 * statement.executeUpdate({ it.contains("CREATE TABLE") })
        1 * statement.executeUpdate({ it.contains("CREATE INDEX") })
    }

    def "should fail if DB connection fails"() {
        given:
        def dataSource = Mock(DataSource)
        def exception = new RuntimeException("connection failed")

        and:
        dataSource.getConnection() >> { throw exception }
        def initializer = new DatabaseInitializer(dataSource)

        when:
        initializer.onStartup(null)

        then:
        def e = thrown(RuntimeException)
        e.message.contains("DB schema init failed")
    }
}

