package dev.osunolimits.common;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.osunolimits.main.App;


public class Database {
    private HikariConfig hikariConfig;
    public static HikariDataSource dataSource;
    private int connectionTimeout;
    private int maximumPoolSize;
    public static int currentConnections;



    /**
     * Set the connection timeout value.
     *
     * @param connectionTimeout The connection timeout value in milliseconds.
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        hikariConfig.setConnectionTimeout(this.connectionTimeout);
    }

    /**
     * Get the maximum pool size.
     *
     * @return The maximum pool size.
     */
    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    /**
     * Set the maximum pool size.
     *
     * @param maximumPoolSize The maximum pool size.
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        hikariConfig.setMaximumPoolSize(100);
    }

    /**
     * Set the default connection settings.
     *
     * @param cachePrepStmts        Whether to cache prepared statements.
     * @param prepStmtCacheSize     The size of the prepared statement cache.
     * @param prepStmtCacheSqlLimit The SQL limit for the prepared statement cache.
     * @param autoReconnect         Whether to enable auto-reconnect.
     */
    public void setDefaultSettings(boolean cachePrepStmts, int prepStmtCacheSize, int prepStmtCacheSqlLimit,
            boolean autoReconnect) {
        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        hikariConfig.addDataSourceProperty("autoReconnect", autoReconnect);
    }

    /**
     * Set the default connection settings with default values.
     * The default values are cachePrepStmts=true, prepStmtCacheSize=250, prepStmtCacheSqlLimit=2048, autoReconnect=true.
     */
    public void setDefaultSettings() {
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("autoReconnect", true);
    }

    /**
     * Constructs a new Database object with default settings.
     */
    public Database() {
        this.hikariConfig = new HikariConfig();
    }

    /**
     * Represents the server timezone for the MySQL connection.
     */
    public enum ServerTimezone {
        UTC("UTC"), GMT("GMT");

        private final String code;

        /**
         * Constructs a new ServerTimezone enum with the specified code.
         *
         * @param code The code representing the server timezone.
         */
        ServerTimezone(String code) {
            this.code = code;
        }

        /**
         * Returns the code representing the server timezone.
         *
         * @return The code representing the server timezone.
         */
        @Override
        public String toString() {
            return code;
        }
    }

    /**
     * Connects to a MySQL database using the specified connection parameters.
     *
     * @param host           The host of the MySQL server.
     * @param user           The username for the database connection.
     * @param password       The password for the database connection.
     * @param database       The name of the database to connect to.
     * @param serverTimezone The server timezone for the MySQL connection.
     */
    public void connectToMySQL(String host, String user, String password, String database,
            ServerTimezone serverTimezone) {
        String url = "jdbc:mysql://" + host + ":3306/" + database + "?serverTimezone=" + serverTimezone;
        hikariConfig
                .setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);

            dataSource = new HikariDataSource(hikariConfig);

            try (Connection connection = dataSource.getConnection()) {
                App.log.info("Connected to Database (" + url + ")");
                connection.close();
            } catch (SQLException e) {
                App.log.error("Error while connecting to MySQL database " + e.getMessage());
            }
        }
    
    
    /**
     * Get a connection to the MySQL database.
     *
     * @return A connection to the MySQL database.
     */
    public static MySQL getConnection() {
        MySQL connection = null;
        try {
            connection = new MySQL(dataSource.getConnection());
            Database.currentConnections++;
            return connection;
        } catch (SQLException e) {
            App.log.error("Error while obtaining a connection from the pool.", e);
            return null;
        }
    }
}
