package dev.osunolimits.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import dev.osunolimits.main.App;
import dev.osunolimits.utils.Validation;

public final class MySQL {

	private final int COLUMN_WIDTH = 20;

    private void printResultSet(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                System.out.printf("%-" + COLUMN_WIDTH + "s", columnName);
            }
            System.out.println();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnValue = resultSet.getString(i);
                    System.out.printf("%-" + COLUMN_WIDTH + "s", columnValue);
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	private Connection currentCon;

	public MySQL(Connection currentCon) {

		this.currentCon = currentCon;
	}

	public ResultSet Query(String sql, Object... args) {
		try {
			PreparedStatement stmt = currentCon.prepareStatement(sql);
			for (int i = 0; i < args.length; i++)
                if(Validation.isNumeric(args[i].toString())) {
                    stmt.setInt(i + 1, Integer.parseInt(args[i].toString()));
                } else {
                    stmt.setString(i + 1, (String) args[i]);
                }

			logSQL(stmt.toString());
			return stmt.executeQuery();
		} catch (Exception ex) {

			return null;
		}
	}

	public void printQuery(String sql, String... args) {
		ResultSet rs = Query(sql, (Object[]) args);
		printResultSet(rs);
	}

	public ResultSet Query(String sql, List<String> args) {
		try {
			PreparedStatement stmt = currentCon.prepareStatement(sql);
			for (int i = 0; i < args.size(); i++)
				stmt.setString(i + 1, args.get(i));
                logSQL(stmt.toString());
			return stmt.executeQuery();
		} catch (Exception ex) {

			return null;
		}
	}

	public int Exec(String sql, String... args) {
		try {
			PreparedStatement stmt = currentCon.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < args.length; i++) {
				stmt.setString(i + 1, args[i]);
			}
	
			int rowsAffected = stmt.executeUpdate();
			logSQL(stmt.toString());

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int generatedKey = rs.getInt(1);
				return generatedKey;
			} else {
				return rowsAffected;
			}
		} catch (Exception ex) {
			logSQL(ex.getMessage());
			return -1;
		}
	}
	

	public void close() {
		try {
			if (!currentCon.isClosed()) {
				Database.currentConnections--;
				currentCon.close();
			}
		} catch (Exception ex) {
			logSQL("Failed to close connection");
		}

	}

    
    private void logSQL(String message) {
        if(App.loggerEnv.get("MYSQL_LOG").equalsIgnoreCase("TRUE")) {
            App.log.info(message);
        }
    }
}