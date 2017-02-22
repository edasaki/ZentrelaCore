package com.edasaki.core.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.SakiCore;

public class SQLManager extends AbstractManagerCore {

    private static int count1 = (int) (Math.random() * 10);
    private static int count2 = (int) (Math.random() * 10);

    public static ConnectionPoolManager manager;

    public SQLManager(SakiCore plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        if (!SakiCore.OFFLINE)
            manager = new ConnectionPoolManager();
    }

    public static void close(AutoCloseable... toClose) {
        if (manager != null)
            manager.close(toClose);
    }

    public static void disconnect() {
        if (manager != null)
            manager.disconnect();
    }

    /**
     * Returns a prepared statement. Values must be manually set after retrieved from this method.
     * @return an array of [PreparedStatement, Connection], which MUST be closed at some point
     */
    public static AutoCloseable[] prepare(String statement) {
        try {
            Connection conn = manager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);
            return new AutoCloseable[] { ps, conn };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Execute any SQL query and cleans up the connection afterwards.
     * @param ac - an array of [PreparedStatement, Connection]
     * @return true if statement was successfully run, false if an error occurred
     */
    public static boolean execute(AutoCloseable[] ac) {
        if (ac == null || ac.length != 2 || !(ac[0] instanceof PreparedStatement) || !(ac[1] instanceof Connection)) {
            try {
                throw new Exception("Invalid SQL execute() input");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        long start = System.currentTimeMillis();
        try {
            ((PreparedStatement) ac[0]).execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ac);
        }
        if (count1++ % 10 == 0)
            System.out.println("Ran statement in " + (System.currentTimeMillis() - start) + "ms");
        return true;
    }

    /**
     * Execute an SQL query that returns a ResultSet
     * @return array of [ResultSet, PreparedStatement, Connection], which MUST be closed at some point
     */
    public static AutoCloseable[] executeQuery(PreparedStatement statement) {
        long start = System.currentTimeMillis();
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = manager.getConnection();
            rs = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (count2++ % 10 == 0)
            System.out.println("Ran query in " + (System.currentTimeMillis() - start) + "ms");
        return new AutoCloseable[] { rs, statement, conn };
    }

}
