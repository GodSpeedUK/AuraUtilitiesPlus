package tech.aurasoftware.aurautilitiesplus.configuration.serialization;

import java.util.Map;

public interface Serializable {

    default Map<String, Object> serialize(){
        return Serialization.serialize(this);
    }

}
