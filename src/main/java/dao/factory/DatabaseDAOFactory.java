package dao.factory;

import dao.AccountDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.PostDAO;
import dao.UserDAO;
import dao.database.DBConnection;
import dao.database.DatabaseAccountDAO;
import dao.database.DatabaseBookDAO;
import dao.database.DatabaseCategoryDAO;
import dao.database.DatabasePostDAO;
import dao.database.DatabaseUserDAO;

public class DatabaseDAOFactory extends DAOFactory {

    private final DBConnection dbConnection;

    public DatabaseDAOFactory(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    public DBConnection getDbConnection() {
    	return dbConnection;
    }

    @Override
    protected BookDAO createBookDAO() {
        return new DatabaseBookDAO(dbConnection);
    }

    @Override
    protected CategoryDAO createCategoryDAO() {
        return new DatabaseCategoryDAO(dbConnection);
    }

    @Override
    protected PostDAO createPostDAO() {
        return new DatabasePostDAO(dbConnection);
    }

	@Override
	protected AccountDAO createAccountDAO() {
		return new DatabaseAccountDAO(dbConnection);
	}

	@Override
	protected UserDAO createUserDAO() {
		return new DatabaseUserDAO(dbConnection);
	}
}