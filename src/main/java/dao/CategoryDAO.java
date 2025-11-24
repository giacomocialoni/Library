package dao;

import exception.RecordNotFoundException;
import exception.DuplicateRecordException;
import model.Category;

import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {

    List<Category> getAllCategories() throws SQLException;
    void addCategory(Category category) throws SQLException, DuplicateRecordException;
    void deleteCategory(String category) throws SQLException, RecordNotFoundException;
}