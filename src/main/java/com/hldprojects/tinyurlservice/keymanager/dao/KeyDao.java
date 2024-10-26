package com.hldprojects.tinyurlservice.keymanager.dao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Set;

@Repository
public class KeyDao {

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String url ;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    @Getter
    private Long totalInsertionsCounter;

    public KeyDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.totalInsertionsCounter = 0L;
    }

    public void saveKeys(Set<String> keys){
        String sql = "INSERT INTO unusedkeys (kid) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            int counter = 0;
            int batchSize = 1000;
            for(String key : keys){
                this.totalInsertionsCounter = this.totalInsertionsCounter + 1;
                preparedStatement.setString(1, key);
                preparedStatement.addBatch();
                if (counter != 0 && (counter % batchSize) == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                }
                counter += 1;
            }
            System.out.println("TOTAL KEYS INSERTIONS INTO DB"+totalInsertionsCounter);
        } catch (SQLException e) {
            System.out.println("Exception while establishing connection"+e);
        }
    }


    public String getKey() {
        String randomKey = "";
        String selectSql = "SELECT kid FROM unusedkeys ORDER BY RANDOM() LIMIT 1";
        String deleteSql = "DELETE FROM unusedkeys WHERE kid = ?";
        String insertSql = "INSERT INTO usedkeys (kid) VALUES (?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             ResultSet resultSet = selectStmt.executeQuery()) {

            // Step 1: Retrieve a random key
            if (resultSet.next()) {
                randomKey = resultSet.getString("kid");
                System.out.println("Random Key: " + randomKey);

                // Step 2: Delete the record with the retrieved key
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setString(1, randomKey);
                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Deleted key with ID: " + randomKey);
                    } else {
                        System.out.println("No key found with ID: " + randomKey);
                    }
                }

                // Step 3: Insert the record into user keys with the retrieved key
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setString(1, randomKey);
                    int rowsAffected = insertStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Inserted key into used keys table  with ID: " + randomKey);
                    }else{
                        System.out.println("Alert! Key is not inserted into used table!!"+randomKey);
                    }
                }
            } else {
                System.out.println("No records found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving or deleting key: " + e.getMessage());
        }
        return randomKey;
    }
}
