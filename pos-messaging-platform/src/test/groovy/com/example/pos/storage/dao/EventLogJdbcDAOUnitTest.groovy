package com.example.pos.storage.dao

import com.example.pos.storage.dao.EventLogJdbcDAO
import com.example.pos.storage.model.EventLog
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.*
import java.time.LocalDateTime

class EventLogJdbcDAOUnitTest extends Specification {

    def "save() prepares correct SQL statement and sets fields"() {
        given: "A DAO with mocked dependencies"
        def dataSource = Mock(DataSource)
        def connection = Mock(Connection)
        def preparedStatement = Mock(PreparedStatement)
        def resultSet = Mock(ResultSet)

        and: "Mock object returns for connection"
        dataSource.getConnection() >> connection
        connection.prepareStatement(_, Statement.RETURN_GENERATED_KEYS) >> preparedStatement
        preparedStatement.executeUpdate() >> 1
        preparedStatement.getGeneratedKeys() >> resultSet
        resultSet.next() >> true
        resultSet.getLong(1) >> 100L

        def dao = new EventLogJdbcDAO(dataSource)
        def event = new EventLog(
                topic: "unit",
                eventType: "UNIT_TESTED",
                payload: "{}",
                status: "NEW",
                createdAt: LocalDateTime.now(),
                updatedAt: null
        )

        when: "Calling save()"
        def id = dao.save(event)

        then: "Prepared statement is used correctly"
        1 * preparedStatement.setString(1, "unit")
        1 * preparedStatement.setString(2, "UNIT_TESTED")
        1 * preparedStatement.setString(3, "{}")
        1 * preparedStatement.setString(4, "NEW")
        1 * preparedStatement.setObject(5, _ as LocalDateTime)
        1 * preparedStatement.setObject(6, null)
        1 * resultSet.getLong(1)
        id == 100L
    }
}

