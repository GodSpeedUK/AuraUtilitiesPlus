package tech.aurasoftware.aurautilitiesplus.sql;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbcp.BasicDataSource;
import tech.aurasoftware.aurautilitiesplus.AuraUtilitiesPlus;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serializable;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.annotation.Ignored;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
@Getter
public class SQLDatabase implements Serializable {

    private boolean enabled = false;
    private String host = "localhost";
    private int port = 3306;
    private boolean sqLite = false;
    private String database = "database";
    private String username = "username";
    private String password = "password";

    @Ignored
    private DataSource dataSource;

    public SQLDatabase setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public SQLDatabase setHost(String host) {
        this.host = host;
        return this;
    }

    public SQLDatabase setPort(int port) {
        this.port = port;
        return this;
    }

    public SQLDatabase setDatabase(String database) {
        this.database = database;
        return this;
    }

    public SQLDatabase setUsername(String username) {
        this.username = username;
        return this;
    }

    public SQLDatabase setPassword(String password) {
        this.password = password;
        return this;
    }

    @SneakyThrows
    public void createDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        String url;
        if(sqLite) {
            File folder = new File(AuraUtilitiesPlus.getInstance().getDataFolder(), "databases");
            if(!folder.exists()){
                folder.mkdirs();
            }
            File dbFile = new File(folder, database + ".db");
            if(!dbFile.exists()){
                dbFile.createNewFile();
            }
            url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        }else {
            url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setMinIdle(10);
            dataSource.setTestOnBorrow(false);
        }
        dataSource.setUrl(url);

        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeDataSource() {
        if (dataSource instanceof BasicDataSource) {
            try {
                ((BasicDataSource) dataSource).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public SQLResponse querySync(String query, Object... values) {

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            SQLResponse response = new SQLResponse();

            int index = 0;
            while (resultSet.next()) {
                SQLRow row = new SQLRow();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    SQLColumn column = new SQLColumn(metaData.getColumnName(i), resultSet.getObject(i));
                    row.addColumn(metaData.getColumnName(i), column);
                }
                response.addRow(index, row);
                index++;
            }

            connection.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<SQLResponse> query(String query, Object... values) {
        return CompletableFuture.supplyAsync(() -> querySync(query, values));
    }

    public void updateSync(String query, Object... values) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public CompletableFuture<Void> updateMultiple(SQLQuery... sqlQueries){
        return CompletableFuture.supplyAsync(() -> {
            updateMultipleSync(sqlQueries);
            return null;
        });
    }

    public void updateMultipleSync(SQLQuery... queries){
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            for(SQLQuery sqlQuery: queries){
                try(PreparedStatement statement = connection.prepareStatement(sqlQuery.getQuery())){
                    for(int i = 0; i < sqlQuery.getValues().length; i++){
                        statement.setObject(i + 1, sqlQuery.getValues()[i]);
                    }
                    statement.executeUpdate();
                }
            }
            connection.commit();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public CompletableFuture<Void> update(String query, Object... values) {
        return CompletableFuture.supplyAsync(() -> {
            updateSync(query, values);
            return null;
        });
    }

}
