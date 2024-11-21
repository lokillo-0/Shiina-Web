package dev.osunolimits.routes.ap.get.users;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dev.osunolimits.main.App;
import dev.osunolimits.models.Group;
import dev.osunolimits.models.UserInfoObject;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class ApUser extends Shiina {

    private Gson gson;

    public ApUser() {
        gson = new Gson();
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 14);

        if (!shiina.loggedIn) {
            return redirect(res, shiina, "/login");
        }

        if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            return redirect(res, shiina, "/");
        }

        String userId = req.queryParams("id");
        if(userId == null || !Validation.isNumeric(userId))  {
            return redirect(res, shiina, "/ap/users");
        }

        ResultSet user = shiina.mysql.Query("SELECT `users`.`id`, `users`.`email`, `users`.`country`, `users`.`silence_end`, `users`.`donor_end`, `users`.`creation_time`, `users`.`latest_activity`, `users`.`clan_id`, `clans`.`name` AS `clan_name`, `clans`.`tag` AS `clan_tag`, `users`.`clan_priv` FROM users LEFT JOIN `clans` ON `users`.`clan_id` = `clans`.`id` WHERE `users`.`id` = ?;", userId);
        if(!user.next()) {
            return redirect(res, shiina, "/ap/users");
        }

        UserInfoObject userInfo = gson.fromJson(App.jedisPool.get("shiina:user:" + userId), UserInfoObject.class);
        if(userInfo == null) {
            return redirect(res, shiina, "/ap/users");
        }

        ResultSet groupResult = shiina.mysql.Query("SELECT * FROM `sh_groups`");
        List<Group> groups = new ArrayList<>();
        while(groupResult.next()) {
            Group g = new Group();
            g.id = groupResult.getInt("id");
            g.name = groupResult.getString("name");
            g.emoji = groupResult.getString("emoji");
            groups.add(g);
        }

        shiina.data.put("allGroups", groups);
        shiina.data.put("id", userId);
        shiina.data.put("aname", userInfo.name);
        shiina.data.put("priv", PermissionHelper.Privileges.fromInt(userInfo.priv));
        shiina.data.put("privLevel", userInfo.priv);
        shiina.data.put("safe_name", userInfo.safe_name);
        shiina.data.put("groups", userInfo.groups);
        shiina.data.put("name", user.getString("email"));
        shiina.data.put("country", user.getString("country"));

        shiina.data.put("latest_activity", user.getString("latest_activity"));
        shiina.data.put("creation_time", user.getString("creation_time"));
        shiina.data.put("silence_end", user.getString("silence_end"));
        shiina.data.put("donor_end", user.getString("donor_end"));

        shiina.data.put("clan_id", user.getString("clan_id"));
        shiina.data.put("clan_name", user.getString("clan_name"));
        shiina.data.put("clan_tag", user.getString("clan_tag"));
        shiina.data.put("clan_priv", user.getString("clan_priv"));
        return renderTemplate("ap/users/user.html", shiina, res, req);
    }

}
