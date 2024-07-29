package tech.aurasoftware.aurautilitiesplus.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Manager<K, V> {

    private final Map<K, V> map;

    public Manager(){
        this.map = new HashMap<>();
    }
    public abstract K getKey(V obj);

   public void insert(V obj){
       map.put(getKey(obj), obj);
   }

   public V get(K key){
       return map.get(key);
   }

   public Collection<K> getAllKeys(){
       return map.keySet();
   }

   public Collection<V> getAllValues() {
       return map.values();
   }

    public void remove(K key){
         map.remove(key);
    }


}
