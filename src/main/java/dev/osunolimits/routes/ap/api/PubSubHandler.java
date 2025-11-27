package dev.osunolimits.routes.ap.api;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.pubsubs.SyncedAction;
import dev.osunolimits.modules.utils.AuditLogger;
import dev.osunolimits.plugins.events.admin.OnAddDonorEvent;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class PubSubHandler extends Shiina {

    public enum MessageType {
        RANK,
        RESTRICT,
        UNRESTRICT,
        WIPE,
        ALERT_ALL,
        GIVEDONATOR,
        ADDPRIV,
        REMOVEPRIV,
        NONE,
        RMPB,
        RMUP
    }

    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if (shiina.user == null || !shiina.loggedIn) {
            return redirect(res, shiina, "/login");
        }

        if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.STAFF)) {
            return redirect(res, shiina, "/");
        }

        MessageType type = MessageType.NONE;
        if (req.queryParams("type") != null) {
            try {
                type = MessageType.valueOf(req.queryParams("type").toUpperCase());
            } catch (Exception e) {
                type = MessageType.NONE;
            }
        }

        if (type == MessageType.NONE) {
            return raw(res, shiina, "invalid type");
        }

        AuditLogger auditLogger = new AuditLogger(shiina.mysql, type);
        
        int userId;
        int mode;
        String reason;

        switch (type) {
            case RESTRICT: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.MODERATOR)) {
                    return redirect(res, shiina, "/");
                }
               int adminId = shiina.user.id;
                reason = req.queryParams("reason");
                userId = Integer.parseInt(req.queryParams("id"));
                
                SyncedAction.restrict(userId, adminId, reason);
                auditLogger.restrictUser(shiina.user, userId,reason);
                break;
            }
            case UNRESTRICT: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.MODERATOR)) {
                    return redirect(res, shiina, "/");
                }
                int adminId = shiina.user.id;
                reason = req.queryParams("reason");
                userId = Integer.parseInt(req.queryParams("id"));
                
                SyncedAction.unrestrict(userId, adminId, reason);
                
                auditLogger.unrestrictUser(shiina.user, userId, reason);
                break;
            }
            case WIPE:
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.MODERATOR)) {
                    return redirect(res, shiina, "/");
                }
                userId = Integer.parseInt(req.queryParams("id"));
                mode = Integer.parseInt(req.queryParams("mode"));
                reason = req.queryParams("reason");

                SyncedAction.wipe(userId, mode);

                auditLogger.wipeUser(shiina.user, userId, mode, reason);
                break;
            case ALERT_ALL: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.MODERATOR)) {
                    return redirect(res, shiina, "/");
                }
                String message = req.queryParams("message");
                
                SyncedAction.alertAll(message);

                auditLogger.alertAll(shiina.user, message);
                break;
            }
            case GIVEDONATOR: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
                    return redirect(res, shiina, "/");
                }
                userId = Integer.parseInt(req.queryParams("id"));
                String duration = req.queryParams("duration");
                
                SyncedAction.addDonatorStatus(userId, duration);

                new OnAddDonorEvent(duration, userId, shiina.user.id).callListeners();
                auditLogger.giveDonator(shiina.user, userId, duration);
                res.redirect("/ap/user?id=" + userId);
                break;
            }
            case ADDPRIV: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
                    return redirect(res, shiina, "/");
                }
                userId = Integer.parseInt(req.queryParams("id"));
                String[] privs = req.queryParamsValues("privs");

                SyncedAction.addPriv(userId, privs);
              
                auditLogger.addPriv(shiina.user, userId, privs);
                res.redirect("/ap/user?id=" + userId);
                break;
            }
            case REMOVEPRIV: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
                    return redirect(res, shiina, "/");
                }
                userId = Integer.parseInt(req.queryParams("id"));
                String[] privs = req.queryParamsValues("privs");
                
                SyncedAction.removePriv(userId, privs);

                auditLogger.removePriv(shiina.user, userId, privs);
                res.redirect("/ap/user?id=" + userId);
                break;
            }

            case RMUP: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.MODERATOR)) {
                    return redirect(res, shiina, "/");
                }
                userId = Integer.parseInt(req.queryParams("id"));
                
                SyncedAction.removeUserpage(userId);

                auditLogger.removeUserpage(shiina.user, userId, req.queryParams("reason"));
                res.redirect("/ap/user?id=" + userId);
                break;
            }

            case RMPB: {
                if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.MODERATOR)) {
                    return redirect(res, shiina, "/");
                }
                userId = Integer.parseInt(req.queryParams("id"));
                String reasonPb = req.queryParams("reason");

                SyncedAction.removeProfilePicture(userId);
                
                auditLogger.removeProfilePicture(shiina.user, userId, reasonPb);
                res.redirect("/ap/user?id=" + userId);
                break;
            }

            default:
                break;
        }

        return raw(res, shiina, "success");
    }
}
