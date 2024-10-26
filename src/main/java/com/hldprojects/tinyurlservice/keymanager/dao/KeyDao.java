package com.hldprojects.tinyurlservice.keymanager.dao;

import com.hldprojects.tinyurlservice.keymanager.service.KeyManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@Repository
public class KeyDao {
    Logger logger = LoggerFactory.getLogger(KeyManagementService.class);

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String url ;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${initialkeycountinmemory}")
    private Integer initialKeyLoadLimit;



    public KeyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveKeys(Set<String> generatedKeysSet) {
        logger.info("Inserting -> " + generatedKeysSet.size() + " into unused keys table");
        String sql = "INSERT INTO unusedkeys (kid) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            int counter = 0;
            int batchSize = 1000;
            for (String key : generatedKeysSet) {
                preparedStatement.setString(1, key);
                preparedStatement.addBatch();
                counter++;

                if (counter % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                }
            }

            // Execute and commit any remaining keys in the batch
            if (counter % batchSize != 0) {
                preparedStatement.executeBatch();
                connection.commit();
            }

        } catch (SQLException e) {
            System.out.println("Exception while establishing connection: " + e.getMessage());
        }
    }


    public void processKeys(Set<String> keysSet) {
        String selectSql = "SELECT kid FROM unusedkeys LIMIT " + initialKeyLoadLimit;
        String deleteSql = "DELETE FROM unusedkeys WHERE kid = ?";
        String insertSql = "INSERT INTO usedkeys (kid) VALUES (?)";
        Set<String> tempKeySet = new HashSet<>(); //temp keyset to help in rollback during transaction failures

        // Use try-with-resources for auto-closeable resources
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

            // Begin a transaction
            connection.setAutoCommit(false);

            try (ResultSet resultSet = selectStmt.executeQuery()) {
                // Fetch keys from unusedkeys table
                while (resultSet.next()) {
                    String key = resultSet.getString("kid");
                    tempKeySet.add(key);
                }

                // Check if keys were found
                if (tempKeySet.isEmpty()) {
                    logger.info("No records found in the database.");
                    return;
                }

                // Insert keys in a batch
                for (String key : tempKeySet) {
                    insertStmt.setString(1, key);
                    insertStmt.addBatch();

                    deleteStmt.setString(1, key);
                    deleteStmt.addBatch();
                }


                logger.info("Inserted All the keys -> "+tempKeySet.size()+" into used keys table after loading them into memory");
                insertStmt.executeBatch(); // Execute all inserts at once
                logger.info("Delete All the keys -> "+tempKeySet.size()+" from unused keys table after loading them into memory");
                deleteStmt.executeBatch(); // Execute all deletes at once

                // Commit the transaction if all operations succeed
                connection.commit();
                keysSet.addAll(tempKeySet); //transaction successful add to keyset
            } catch (SQLException e) {
                // Rollback in case of an error to ensure atomicity
                connection.rollback();
                keysSet.removeAll(tempKeySet);//remove all the values collected as there is failure in transaction
                logger.error("Error while processing keys: " + e);
            }
        } catch (SQLException e) {
            logger.error("Error while establishing connection: " + e.getMessage());
        }
    }




    public Integer getAvailableUnusedKeys() {
        String availableUnusedKeysFromDbSql = "SELECT count(kid) FROM unusedkeys";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(availableUnusedKeysFromDbSql);
             ResultSet resultSet = selectStmt.executeQuery()) {

            if (resultSet.next()) {
                int count =  resultSet.getInt(1); // Get the count of unused keys
                logger.info("Total Records Found in database ->"+count);
                return count;
            }

        } catch (SQLException e) {
            System.out.println("Error while retrieving the count of keys: " + e.getMessage());
        }
        return 0;
    }

}
