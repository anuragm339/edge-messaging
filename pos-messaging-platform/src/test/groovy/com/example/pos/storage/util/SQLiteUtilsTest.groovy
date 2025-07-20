package com.example.pos.storage.util
import spock.lang.Specification

class SQLiteUtilsTest extends Specification {

    def "can apply valid SQLite PRAGMA statements"() {
        when:
        def pragmas = SQLiteUtils.getWALPragmaSQL()

        then:
        pragmas.any { it.contains("PRAGMA journal_mode = WAL") }
    }
}

