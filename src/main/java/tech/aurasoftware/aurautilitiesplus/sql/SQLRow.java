package tech.aurasoftware.aurautilitiesplus.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SQLRow {

    private final Map<String, SQLColumn> values;

    public SQLRow(){
        this.values = new HashMap<>();
    }

    public void addColumn(String columnId, SQLColumn column){
        values.put(columnId, column);
    }

    public SQLColumn getColumn(String columnId){
        return values.get(columnId);
    }

    public Collection<SQLColumn> getColumns(){
        return values.values();
    }

}
