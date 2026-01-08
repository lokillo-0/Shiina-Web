package dev.osunolimits.routes.api.get.auth.ap;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.routes.api.get.ShiinaAPIHandler;
import dev.osunolimits.routes.get.modular.ModuleRegister;
import dev.osunolimits.utils.osu.PermissionHelper;
import lombok.Data;
import spark.Request;
import spark.Response;

public class SetModuleSortAdmin extends Shiina {
    
    @Data
    public static class ModuleSortRequest {
        private String page;
        private List<String> modules;
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
            // Parse JSON from request body
            Gson gson = new Gson();
            ModuleSortRequest sortRequest = gson.fromJson(req.body(), ModuleSortRequest.class);
            
            if (sortRequest.getPage() == null || sortRequest.getModules() == null) {
                return shiinaAPIHandler.renderJSON(Map.of("success", false, "error", "Missing page or modules"), shiina, res);
            }
            
            // Apply the new sort order
            ModuleRegister.setSortedModules(sortRequest.getPage(), sortRequest.getModules());
            
            return shiinaAPIHandler.renderJSON(Map.of("success", true), shiina, res);
        } catch (Exception e) {
            return shiinaAPIHandler.renderJSON(Map.of("success", false, "error", e.getMessage()), shiina, res);
        }
    }
}
