package tech.aurasoftware.aurautilitiesplus.sql;

import lombok.experimental.UtilityClass;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.annotation.Ignored;
import tech.aurasoftware.aurautilitiesplus.sql.annotation.UniqueKey;
import tech.aurasoftware.aurautilitiesplus.sql.annotation.UpdateOnDuplicate;

import java.lang.reflect.Field;
import java.util.*;

@UtilityClass
public class SQLUtility {

    public <T> List<T> getData(Class<T> tClass, SQLDatabase sqlDatabase, SQLRequirement... sqlRequirements) {
        List<T> data = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + tClass.getSimpleName().toLowerCase());

        List<Object> values = new ArrayList<>();

        if (sqlRequirements.length > 0) {
            queryBuilder.append(" WHERE ");
            for (int i = 0; i < sqlRequirements.length; i++) {
                SQLRequirement sqlRequirement = sqlRequirements[i];
                values.add(sqlRequirement.getValue());
                queryBuilder.append(sqlRequirement.getName()).append(" = ").append("?");
                if (i != sqlRequirements.length - 1) {
                    queryBuilder.append(" AND ");
                }
            }
        }

        SQLResponse response = sqlDatabase.querySync(queryBuilder.toString(), values.toArray());

        if (response.isEmpty()) {
            return data;
        }

        for (SQLRow row : response.getRows()) {
            T instance;
            try {
                instance = tClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return data;
            }

            for (SQLColumn column : row.getColumns()) {
                try {

                    Field field;
                    try{
                        field = tClass.getDeclaredField(column.getName());
                    }catch (NoSuchFieldException e) {
                        continue;
                    }
                    field.setAccessible(true);
                    if (field.getType().isAssignableFrom(UUID.class)) {
                        field.set(instance, UUID.fromString((String) column.getValue()));
                        continue;
                    }
                    field.set(instance, column.getValue());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return data;
                }
            }

            data.add(instance);
        }

        return data;
    }

    public SQLQuery getDeleteQuery(Class<?> clazz, SQLRequirement... sqlRequirements) {
        StringBuilder queryBuilder = new StringBuilder("DELETE FROM " + clazz.getSimpleName().toLowerCase());

        List<Object> values = new ArrayList<>();

        if (sqlRequirements.length > 0) {
            queryBuilder.append(" WHERE ");
            for (int i = 0; i < sqlRequirements.length; i++) {
                SQLRequirement sqlRequirement = sqlRequirements[i];
                values.add(sqlRequirement.getValue());
                queryBuilder.append(sqlRequirement.getName()).append(" = ").append("?");
                if (i != sqlRequirements.length - 1) {
                    queryBuilder.append(" AND ");
                }
            }
        }

        return new SQLQuery(queryBuilder.toString(), values.toArray());
    }

    public SQLQuery getSaveQuery(Object object) {
        Class<?> tClass = object.getClass();

        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> updateFields = new HashMap<>();

        for (Field field : tClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Ignored.class) != null) {
                continue;
            }

            if (field.getAnnotation(UpdateOnDuplicate.class) != null) {
                try {
                    updateFields.put(field.getName(), field.get(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }

            try {

                Object fieldObject = field.get(object);

                if (fieldObject instanceof UUID) {
                    String uuidString = ((UUID) fieldObject).toString();
                    fields.put(field.getName(), uuidString);
                } else {
                    fields.put(field.getName(), fieldObject);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String name = tClass.getSimpleName().toLowerCase();

        StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + name + " (");
        StringBuilder valueBuilder = new StringBuilder(") VALUES (");
        StringBuilder updateFieldBuilder = new StringBuilder(" ON DUPLICATE KEY UPDATE ");

        for (int i = 0; i < fields.size(); i++) {
            queryBuilder.append(fields.keySet().toArray()[i]);
            valueBuilder.append("?");
            if (i != fields.size() - 1) {
                queryBuilder.append(", ");
                valueBuilder.append(", ");
            }
        }

        List<Object> values = new ArrayList<>(fields.values());


        queryBuilder.append(valueBuilder).append(")");

        if (!updateFields.isEmpty()) {
            for (int i = 0; i < updateFields.size(); i++) {
                updateFieldBuilder.append(updateFields.keySet().toArray()[i]).append(" = ?");
                values.add(updateFields.values().toArray()[i]);
                if (i != updateFields.size() - 1) {
                    updateFieldBuilder.append(", ");
                }
            }
            queryBuilder.append(updateFieldBuilder);
        }


        SQLQuery sqlQuery = new SQLQuery(queryBuilder.toString(), values.toArray());
        return sqlQuery;
    }

    public void saveData(Object object, SQLDatabase sqlDatabase) {
        SQLQuery sqlQuery = getSaveQuery(object);
        sqlDatabase.updateSync(sqlQuery.getQuery(), sqlQuery.getValues());
    }

    public void batchSave(SQLDatabase sqlDatabase, SQLQuery... queries) {
        sqlDatabase.updateMultipleSync(queries);
    }

    public void createTable(Class<?> tClass, SQLDatabase sqlDatabase) {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tClass.getSimpleName().toLowerCase() + " (");
        StringBuilder uniqueKeyBuilder = new StringBuilder("UNIQUE KEY `unique_key_")
                .append(tClass.getSimpleName().toLowerCase())
                .append("`")
                .append("(");

        Field[] fields = tClass.getDeclaredFields();

        queryBuilder.append("id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");

        boolean foundUniqueKey = false;
        int keyId = 0;

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (field.getAnnotation(Ignored.class) != null) {
                continue;
            }

            if (i != fields.length - 1 && i != 0) {
                queryBuilder.append(", ");
            }

            if (field.getAnnotation(UniqueKey.class) != null) {
                foundUniqueKey = true;
                if (keyId != 0) {
                    uniqueKeyBuilder.append(", ");
                }
                uniqueKeyBuilder.append(field.getName());
                keyId++;
            }

            if (field.getType().isAssignableFrom(String.class)) {
                queryBuilder.append(field.getName()).append(" VARCHAR(255) NOT NULL");
            } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                queryBuilder.append(field.getName()).append(" INT NOT NULL");
            } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                queryBuilder.append(field.getName()).append(" BIGINT NOT NULL");
            } else if (field.getType().equals(UUID.class)) {
                queryBuilder.append(field.getName()).append(" VARCHAR(36) NOT NULL");
            } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                queryBuilder.append(field.getName()).append(" BOOLEAN NOT NULL");
            } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                queryBuilder.append(field.getName()).append(" DOUBLE NOT NULL");
            }


        }

        if (foundUniqueKey) {
            uniqueKeyBuilder.append(")");
            queryBuilder.append(", ").append(uniqueKeyBuilder);
        }

        queryBuilder.append(")");

        sqlDatabase.update(queryBuilder.toString());
    }

}
