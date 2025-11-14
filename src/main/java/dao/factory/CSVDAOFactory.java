package dao.factory;

import dao.csv.CSVBookDAO;
import dao.AccountDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.LoanDAO;
import dao.PostDAO;
import dao.PurchaseDAO;
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

	@Override
	protected LoanDAO createLoanDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PurchaseDAO createPurchaseDAO() {
		// TODO Auto-generated method stub
		return null;
	}
}