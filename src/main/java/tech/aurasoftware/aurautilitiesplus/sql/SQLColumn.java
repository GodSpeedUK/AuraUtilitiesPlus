package tech.aurasoftware.aurautilitiesplus.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SQLColumn {

    private final String name;
    private final Object value;

    public <T> T as(Class<T> type){
        try{
            return type.cast(value);
        }catch (ClassCastException e){
            throw new ClassCastException("Cannot cast " + value.getClass().getSimpleName() + " to " + type.getSimpleName());
        }
    }

}
