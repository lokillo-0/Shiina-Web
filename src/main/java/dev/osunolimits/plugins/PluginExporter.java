package dev.osunolimits.plugins;

import java.sql.ResultSet;

import dev.osunolimits.common.MySQL;
import dev.osunolimits.plugins.annotations.Table;

@Table("plugin_data")
public class PluginExporter {
    protected int userId;
    protected MySQL mysql;

    public PluginExporter(int userId, MySQL mysql) {
        this.userId = userId;
        this.mysql = mysql;
    }

    public ResultSet exportPluginData() {
        return mysql.Query("SELECT * FROM `plugin_data` WHERE `userid` = ?", userId);
    }
}
