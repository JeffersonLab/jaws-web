package org.jlab.jaws.integration;

import oracle.jdbc.pool.OracleDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;

public class TestOracleDataSource implements AutoCloseable {
    private final InitialContext initCtx;
    private final Context envCtx;
    private final OracleDataSource ods;

    public TestOracleDataSource() throws NamingException, SQLException {
        initCtx = new InitialContext();
        envCtx = (Context) initCtx.lookup("java:comp/env");

        ods = new OracleDataSource();

        String user = System.getenv("ORACLE_USER");
        String pass = System.getenv("ORACLE_PASS");
        String service = System.getenv("ORACLE_SERVICE");
        String server = System.getenv("ORACLE_SERVER");

        String url = "jdbc:oracle:thin:" + user + "/" + pass + "@" + server + "/" + service;

        ods.setURL(url);

        envCtx.rebind("jdbc/jaws", ods);
    }

    @Override
    public void close() throws IOException {
        // No close method on ods?
    }
}
