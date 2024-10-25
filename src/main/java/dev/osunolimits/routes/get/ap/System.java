package dev.osunolimits.routes.get.ap;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

import dev.osunolimits.common.Database;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class System extends Shiina {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 10);

        if (!shiina.loggedIn) {
            res.redirect("/login");
            return null;
        }

        if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            res.redirect("/");
            return null;
        }

        // System and application statistics
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // System statistics
        shiina.data.put("os_name", osBean.getName());
        shiina.data.put("os_version", osBean.getVersion());
        shiina.data.put("available_processors", osBean.getAvailableProcessors());
        shiina.data.put("system_load_average", osBean.getSystemLoadAverage());

        // JVM memory usage
        shiina.data.put("jvm_total_memory", runtime.totalMemory());
        shiina.data.put("jvm_free_memory", runtime.freeMemory());
        shiina.data.put("jvm_max_memory", runtime.maxMemory());

        shiina.data.put("sql_con", Database.currentConnections);

        // Thread statistics
        shiina.data.put("thread_count", threadMXBean.getThreadCount());
        shiina.data.put("peak_thread_count", threadMXBean.getPeakThreadCount());
        shiina.data.put("daemon_thread_count", threadMXBean.getDaemonThreadCount());

        return renderTemplate("ap/system.html", shiina, res, req);
    }
}
