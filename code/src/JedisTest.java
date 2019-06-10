import redis.clients.jedis.Jedis;

public class JedisTest {

    public static void main(String[] args) {
        //Connecting to Redis on localhost
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //adding a new key
        jedis.set("key", "value");
        //getting the key value
        System.out.println(jedis.get("key"));

    }
}
