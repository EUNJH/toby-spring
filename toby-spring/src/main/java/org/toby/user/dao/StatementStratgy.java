package org.toby.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStratgy {

    PreparedStatement makePreparedStatement(Connection c) throws SQLException;

}
