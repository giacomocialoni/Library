package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import controller.observer.WishlistEmailObserver;
import controller.observer.WishlistObservable;
import dao.BookDAO;
import dao.database.DBConnection;
import dao.database.DatabaseBookDAO;
import dao.factory.DAOFactory;
import dao.factory.DatabaseDAOFactory;

public class Main {
    public static void main(String[] args) {
        String viewType;
        String dataSourceType;
        WishlistObservable wishlistObservable = new WishlistObservable();

        // --- Lettura del file start.properties ---
        try (InputStream input = new FileInputStream("src/main/resources/start.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            viewType = properties.getProperty("view.type");
            dataSourceType = properties.getProperty("data.source");

        } catch (IOException e) {
            throw new RuntimeException("Errore durante la lettura del file start.properties", e);
        }

        // --- Configurazione del tipo di sorgente dati ---
        DAOFactory factory;

        if ("DEMO".equalsIgnoreCase(dataSourceType)) {
        	//TODO parte demo con memoria temporanea
        	factory = DAOFactory.getFactory("DEMO", null);
        } else if ("DB".equalsIgnoreCase(dataSourceType)) {
            // Leggo i dati della connessione dal db.properties
            Properties dbProps = new Properties();
            try (InputStream dbInput = new FileInputStream("src/main/resources/db.properties")) {
                dbProps.load(dbInput);
            } catch (IOException e) {
                throw new RuntimeException("Errore durante la lettura del file db.properties", e);
            }

            String url = dbProps.getProperty("db.url");
            String user = dbProps.getProperty("db.user");
            String password = dbProps.getProperty("db.password");

            DBConnection dbConnection = new DBConnection(url, user, password);
            
            // Crea il BookDAO con l'observable
            DatabaseBookDAO bookDAO = new DatabaseBookDAO(dbConnection, wishlistObservable);
            
            // Crea una factory custom completa
            factory = new CustomDatabaseDAOFactory(dbConnection, bookDAO);
        } else {
            factory = DAOFactory.getFactory("CSV", null);
        }

        // Imposto la factory globale
        DAOFactory.setActiveFactory(factory);

        // Ora è sicuro creare l'Observer
        wishlistObservable.addObserver(new WishlistEmailObserver());

        // --- Avvio interfaccia scelta ---
        if ("GUI".equalsIgnoreCase(viewType)) {
            ApplicationGUI.setDaoFactory(factory);
            ApplicationGUI.launchApp(args);
        } else if ("CLI".equalsIgnoreCase(viewType)) {
            System.out.println("Avvio CLI...");
            // ApplicationCLI.start();
        } else {
            throw new IllegalArgumentException("Tipo di view non valido: " + viewType);
        }
    }
    
    // Factory custom che estende DatabaseDAOFactory
    static class CustomDatabaseDAOFactory extends DatabaseDAOFactory {
        private final BookDAO customBookDAO;
        
        public CustomDatabaseDAOFactory(DBConnection dbConnection, BookDAO customBookDAO) {
            super(dbConnection);
            this.customBookDAO = customBookDAO;
        }
        
        @Override
        public BookDAO getBookDAO() {
            return customBookDAO;  // Restituisce il BookDAO con l'observable
        }
    }
}