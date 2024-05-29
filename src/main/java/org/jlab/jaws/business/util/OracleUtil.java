package org.jlab.jaws.business.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleUtil {
    private static final Logger LOG = Logger.getLogger(
            OracleUtil.class.getName());

    private static final DataSource source;

    private OracleUtil() {
        // not public
    }

    static {
        try {
            source = (DataSource) new InitialContext().lookup("jdbc/jaws");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    public static void close(AutoCloseable... resources) {
        if (resources != null) {
            AutoCloseable[] var1 = resources;
            int var2 = resources.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                AutoCloseable resource = var1[var3];
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Exception var6) {
                        Exception e = var6;
                        LOG.log(Level.WARNING, "Unable to close resource", e);
                    }
                }
            }
        }
    }
}
