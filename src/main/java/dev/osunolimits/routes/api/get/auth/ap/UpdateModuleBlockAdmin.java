package dev.osunolimits.routes.api.get.auth.ap;

import java.util.Map;

import com.google.gson.Gson;

import dev.osunolimits.models.Action;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.routes.api.get.ShiinaAPIHandler;
import dev.osunolimits.routes.get.modular.ModuleRegister;
import dev.osunolimits.utils.osu.PermissionHelper;
import lombok.Data;
import spark.Request;
import spark.Response;

public class UpdateModuleBlockAdmin extends Shiina {
    private Action action;

    @Data
    public static class ModuleBlockRequest {
        private String page;
        private String module;
    }

    public UpdateModuleBlockAdmin(Action action) {
        this.action = action;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
         ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if (shiina.user == null) {
            res.redirect("/login");
            return notFound(res, shiina);
        }

        if(!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            res.redirect("/");
            return notFound(res, shiina);
        }
        
        ShiinaAPIHandler shiinaAPIHandler = new ShiinaAPIHandler();

        try {
            Gson gson = new Gson();
            ModuleBlockRequest blockRequest = gson.fromJson(req.body(), ModuleBlockRequest.class);

            if (blockRequest.getPage() == null || blockRequest.getModule() == null) {
                return shiinaAPIHandler.renderJSON(Map.of("success", false, "error", "Missing page or module"), shiina, res);
            }

            if (action == Action.DELETE) {
                ModuleRegister.blockModule(blockRequest.getPage(), blockRequest.getModule());
            } else if (action == Action.CREATE) {
                ModuleRegister.unblockModule(blockRequest.getPage(), blockRequest.getModule());
            }

            return shiinaAPIHandler.renderJSON(Map.of("success", true), shiina, res);
        } catch (Exception e) {
            return shiinaAPIHandler.renderJSON(Map.of("success", false, "error", e.getMessage()), shiina, res);
        }
    }
}
