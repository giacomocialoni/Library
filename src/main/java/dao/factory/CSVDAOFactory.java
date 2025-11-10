package dao.factory;

import dao.csv.CSVBookDAO;
import dao.AccountDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.PostDAO;
import dao.UserDAO;

public class CSVDAOFactory extends DAOFactory {

    @Override
    protected BookDAO createBookDAO() {
        return new CSVBookDAO();
    }

	@Override
	protected CategoryDAO createCategoryDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PostDAO createPostDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AccountDAO createAccountDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected UserDAO createUserDAO() {
		// TODO Auto-generated method stub
		return null;
	}
}