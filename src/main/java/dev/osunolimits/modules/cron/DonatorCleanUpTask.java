package dev.osunolimits.modules.cron;

import java.sql.ResultSet;

import com.google.gson.Gson;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;
import dev.osunolimits.main.App;
import dev.osunolimits.models.UserInfoObject;
import dev.osunolimits.modules.cron.engine.RunnableCronTask;
import dev.osunolimits.utils.osu.PermissionHelper;
import dev.osunolimits.utils.osu.PermissionHelper.Privileges;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public class DonatorCleanUpTask extends RunnableCronTask {

    @Override
    public String getName() {
        return "DonatorCleanUpTask";
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        JedisPooled jedis = App.jedisPool;
        MySQL mysql = Database.getConnection();

        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams params = new ScanParams().match("shiina:user:[0-9]*");

        do {
            ScanResult<String> scanResult = jedis.scan(cursor, params);
            for (String key : scanResult.getResult()) {
                if ("string".equals(jedis.type(key))) {
                    String value = jedis.get(key);
                    if (value != null && value.trim().startsWith("{")) {
                        try {
                            UserInfoObject userInfo = gson.fromJson(value, UserInfoObject.class);
                            if (PermissionHelper.hasPrivileges(userInfo.priv, Privileges.SUPPORTER)) {
                                ResultSet rs = mysql.Query("SELECT `donor_end`, `priv` FROM `users` WHERE `id` = ?", userInfo.id);
                                
                                try {
                                    if (rs.next()) {
                                        long donorEnd = rs.getLong("donor_end");
                                        int priv = rs.getInt("priv");
                                        long currentTime = System.currentTimeMillis() / 1000L;

                                        if(donorEnd == 0) {
                                            int newPriv = Privileges.removePrivilege(userInfo.priv, Privileges.SUPPORTER);
                                            userInfo.priv = newPriv;
                                            String updatedValue = gson.toJson(userInfo);
                                            jedis.set(key, updatedValue);
                                            logger.info("Updated user ID " + userInfo.id + ": removed SUPPORTER privilege.");
                                            continue;
                                        }

                                        if (donorEnd < currentTime && PermissionHelper.hasPrivileges(priv, Privileges.SUPPORTER)) {
                                            // Donator status has expired, update the user
                                            int newPriv = Privileges.removePrivilege(priv, Privileges.SUPPORTER);
                                            mysql.Exec("UPDATE `users` SET `priv` = ? WHERE `id` = ?", newPriv, userInfo.id);

                                            // Update the cached user info
                                            userInfo.priv = newPriv;
                                            String updatedValue = gson.toJson(userInfo);
                                            jedis.set(key, updatedValue);

                                            logger.info("Updated user ID " + userInfo.id + ": removed SUPPORTER privilege.");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception ex) {
                            logger.error("Failed to parse JSON for key: " + key + ", value: " + value, ex);
                        }
                    } 
                }
            }
            cursor = scanResult.getCursor();
        } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

        mysql.close();
    }
}
