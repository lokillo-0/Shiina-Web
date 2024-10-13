package dev.osunolimits.modules;

import java.util.HashMap;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;
import spark.Request;
import spark.Response;

public class ShiinaRoute {

    public class ShiinaRequest {
        public MySQL mysql;
        public HashMap<String, Object> data = new HashMap<>();
    }

    public ShiinaRequest handle(Request req, Response res) throws Exception {
        ShiinaRequest request = new ShiinaRequest();
        request.mysql = Database.getConnection();
        return request;
    }
    
}
