package dev.osunolimits.main.init;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

public class StartupSetupRedisTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        HostAndPort hostAndPort = new HostAndPort(App.env.get("REDISHOST"),
                Integer.parseInt(App.env.get("REDISPORT")));

        DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .connectionTimeoutMillis(Integer.parseInt(App.env.get("REDISTIMEOUT")))
                .database(Integer.parseInt(App.env.get("REDISDB")))
                .password(App.env.get("REDISPASS"))
                .user(App.env.get("REDISUSER"))
                .build();

        App.jedisPool = new JedisPooled(hostAndPort, clientConfig);

        logger.info("Connected to Redis: " + App.jedisPool.ping());
    }

    @Override
    public String getName() {
        return "StartupSetupRedisTask";
    }
}
