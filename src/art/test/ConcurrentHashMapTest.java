package art.test;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {
    public static void main(String[] args){
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("first-key", 1);
        System.out.println(map.size());
    }
}
