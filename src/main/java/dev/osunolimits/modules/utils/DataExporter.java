package dev.osunolimits.modules.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;

public class DataExporter {

    private int userId;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ZipOutputStream zipOutputStream;
    private CSVWriter csvWriter;

    private static Logger logger = LoggerFactory.getLogger("DataExporter");

    public DataExporter(int userId) {
        this.userId = userId;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        this.csvWriter = new CSVWriter(new OutputStreamWriter(zipOutputStream));
    }

    public byte[] exportData() {
        MySQL mysql = Database.getConnection();
        try {
            // TODO: Make data more readable

            ResultSet rs = mysql.Query("SELECT * FROM `client_hashes` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("client_hashes", rs);

            rs = mysql.Query("SELECT * FROM `comments` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("comments", rs);

            rs = mysql.Query("SELECT * FROM `favourites` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("favourites", rs);

            rs = mysql.Query("SELECT * FROM `ingame_logins` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("ingame_logins", rs);

            rs = mysql.Query("SELECT * FROM `logs` WHERE `from` = ? OR `to` = ?", userId, userId);
            exportResultSetAsCSVZip("logs", rs);

            rs = mysql.Query("SELECT * FROM `mail` WHERE `from_id` = ? OR `to_id` = ?", userId, userId);
            exportResultSetAsCSVZip("mail", rs);

            rs = mysql.Query("SELECT * FROM `ratings` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("ratings", rs);

            rs = mysql.Query("SELECT * FROM `relationships` WHERE user1 = ? OR user2 = ?", userId, userId);
            exportResultSetAsCSVZip("relationships", rs);

            rs = mysql.Query("SELECT * FROM `scores` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("scores", rs);

            rs = mysql.Query("SELECT * FROM `sh_audit` WHERE `user_id` = ? OR `target_id` = ?", userId, userId);
            exportResultSetAsCSVZip("sh_audit", rs);

            rs = mysql.Query("SELECT * FROM `sh_clan_denied` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("sh_clan_denied", rs);

            rs = mysql.Query("SELECT * FROM `sh_clan_pending` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("sh_clan_pending", rs);

            rs = mysql.Query("SELECT * FROM `sh_detections` WHERE `user` = ? OR `target` = ?", userId, userId);
            exportResultSetAsCSVZip("sh_detections", rs);

            rs = mysql.Query("SELECT * FROM `sh_groups_users` WHERE `user_id` = ?", userId);
            exportResultSetAsCSVZip("sh_groups_users", rs);

            rs = mysql.Query("SELECT * FROM `sh_payments` WHERE `user_id` = ?", userId);
            exportResultSetAsCSVZip("sh_payments", rs);

            rs = mysql.Query("SELECT * FROM `sh_recovery` WHERE `user` = ?", userId);
            exportResultSetAsCSVZip("sh_recovery", rs);

            rs = mysql.Query("SELECT * FROM `stats` WHERE `id` = ?", userId);
            exportResultSetAsCSVZip("stats", rs);

            rs = mysql.Query("SELECT * FROM `userpages` WHERE `user_id` = ?", userId);
            exportResultSetAsCSVZip("userpages", rs);

            rs = mysql.Query("SELECT * FROM `users` WHERE `id` = ?", userId);
            exportResultSetAsCSVZip("users", rs);

            rs = mysql.Query("SELECT * FROM `user_achievements` WHERE `userid` = ?", userId);
            exportResultSetAsCSVZip("user_achievements", rs);
            
            zipOutputStream.finish();

        } catch (Exception e) {
            logger.error("Error exporting data for user ID " + userId, e);
            try {
                zipOutputStream.close();
            } catch (IOException ioException) {
                logger.error("Error closing ZIP output stream", ioException);
            }
        }
        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("Error closing CSV writer", e);
        }

        mysql.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void exportResultSetAsCSVZip(String name, ResultSet rs) throws Exception {
        zipOutputStream.putNextEntry(new ZipEntry(name + ".csv"));

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Write header
        String[] header = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            header[i - 1] = metaData.getColumnName(i);
        }
        csvWriter.writeNext(header);

        // Write rows
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = sanitizeForCSV(rs.getString(i));
            }
            csvWriter.writeNext(row);
        }

        csvWriter.flush(); // Important: flush but do NOT close
        zipOutputStream.closeEntry(); // Close only the ZIP entry, not the ZIP itself
    }

    private String sanitizeForCSV(String value) {
        if (value == null)
            return "";
        if (value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@")) {
            value = "'" + value;
        }
        return value;
    }

}
