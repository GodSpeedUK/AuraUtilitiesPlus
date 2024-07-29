package tech.aurasoftware.aurautilitiesplus.sql;

import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SQLResponse {

    private final Map<Integer, SQLRow> sqlRows;

    public SQLResponse(){
        this.sqlRows = new HashMap<>();
    }

    public void addRow(int index, SQLRow row){
        sqlRows.put(index, row);
    }

    public SQLRow getRow(int index){
        return sqlRows.get(index);
    }

    public int getRowCount(){
        return sqlRows.size();
    }

    public boolean isEmpty(){
        return sqlRows.isEmpty();
    }

    public Collection<SQLRow> getRows(){
        return sqlRows.values();
    }

}
