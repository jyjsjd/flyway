/*
 * Copyright 2010-2018 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.internal.database.sqlite;

import org.flywaydb.core.internal.database.Delimiter;
import org.flywaydb.core.internal.database.SqlStatementBuilder;
import org.flywaydb.core.internal.util.StringUtils;

/**
 * SqlStatementBuilder supporting H2-specific delimiter changes.
 */
public class SQLiteSqlStatementBuilder extends SqlStatementBuilder {
    /**
     * Holds the beginning of the statement.
     */
    private String statementStart = "";

    SQLiteSqlStatementBuilder(Delimiter defaultDelimiter) {
        super(defaultDelimiter);
    }

    @Override
    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        if (StringUtils.countOccurrencesOf(statementStart, " ") < 8) {
            statementStart += line;
            statementStart += " ";
            statementStart = statementStart.replaceAll("\\s+", " ");
        }
        boolean createTriggerStatement = statementStart.matches("CREATE( TEMP| TEMPORARY)? TRIGGER.*");

        if (createTriggerStatement && !line.endsWith("END;")) {
            return null;
        }
        return defaultDelimiter;
    }

    @Override
    protected String cleanToken(String token) {
        if (token.startsWith("X'")) {
            // blob literal
            return token.substring(token.indexOf("'"));
        }
        return token;
    }

}