package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import mg.itu.framework.database.Database;
import mg.itu.framework.database.DatabaseConfig;

public class TestDatabase {

    public static void main(String[] args) {

        try {

            // Lecture du fichier de configuration
            DatabaseConfig config =
                DatabaseConfig.fromFile("test/database.properties");

            // Configuration du framework
            Database.configure(config);

            // Connexion à PostgreSQL
            try (Connection connexion = Database.getConnection()) {

                System.out.println("Connexion PostgreSQL réussie !");

                DatabaseMetaData metaData = connexion.getMetaData();

                System.out.println("Base de données : "
                    + metaData.getDatabaseProductName());

                System.out.println("Version : "
                    + metaData.getDatabaseProductVersion());
            }

        } catch (Exception e) {

            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}