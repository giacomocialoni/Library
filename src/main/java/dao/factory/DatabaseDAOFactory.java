package dao.factory;

import dao.AccountDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.LoanDAO;
import dao.PostDAO;
import dao.PurchaseDAO;
import dao.UserDAO;
import dao.WishlistDAO;
import dao.database.DBConnection;
import dao.database.DatabaseAccountDAO;
import dao.database.DatabaseBookDAO;
import dao.database.DatabaseCategoryDAO;
import dao.database.DatabaseLoanDAO;
import dao.database.DatabasePostDAO;
import dao.database.DatabasePurchaseDAO;
import dao.database.DatabaseUserDAO;
import dao.database.DatabaseWishlistDAO;

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
	
	@Override
	protected LoanDAO createLoanDAO() {
	    return new DatabaseLoanDAO(dbConnection);
	}

	@Override
	protected PurchaseDAO createPurchaseDAO() {
	    return new DatabasePurchaseDAO(dbConnection);
	}
	
	@Override
	protected WishlistDAO createWishlistDAO() {
	    return new DatabaseWishlistDAO(dbConnection);
	}
}