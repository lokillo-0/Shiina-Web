package dev.osunolimits.routes.ap.get.groups;


import java.sql.ResultSet;

import dev.osunolimits.models.Action;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class ManageGroup extends Shiina {

    private Action action;

    public ManageGroup(Action action) {
        this.action = action;
    }


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 13);

        if(!shiina.loggedIn) {
            res.redirect("/login");
            return notFound(res, shiina);
        }

        if(!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            res.redirect("/");
            return notFound(res, shiina);
        }

        if(action == Action.CREATE) {
            return renderTemplate("ap/groups/manage.html", shiina, res, req);
        }else if(action == Action.EDIT) {

            ResultSet selectGroup = shiina.mysql.Query("SELECT * FROM `sh_groups` WHERE `id` = ?", req.queryParams("id"));
            while (selectGroup.next()) {
                shiina.data.put("id", req.queryParams("id"));
                shiina.data.put("name", selectGroup.getString("name"));
                shiina.data.put("emoji", selectGroup.getString("emoji"));
                shiina.data.put("desc", selectGroup.getString("desc"));
            }
            return renderTemplate("ap/groups/manage.html", shiina, res, req);
        }else {
            String id = req.queryParams("id");
            if(id == null) {
                return notFound(res, shiina);
            }
            shiina.mysql.Exec("DELETE FROM `sh_groups` WHERE `id` = ?", id);
            
            return redirect(res, shiina, "/ap/groups");
        }

    }
    
}
