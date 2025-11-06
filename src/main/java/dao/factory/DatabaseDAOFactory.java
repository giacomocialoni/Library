package dao.factory;

import dao.AccountDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.PostDAO;
import dao.database.DBConnection;
import dao.database.DatabaseAccountDAO;
import dao.database.DatabaseBookDAO;
import dao.database.DatabaseCategoryDAO;
import dao.database.DatabasePostDAO;

public class DatabaseDAOFactory extends DAOFactory {

    private final DBConnection dbConnection;

    public DatabaseDAOFactory(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
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

    public DBConnection getDbConnection() {
        return dbConnection;
    }

	@Override
	protected AccountDAO createAccountDAO() {
		return new DatabaseAccountDAO(dbConnection);
	}
}