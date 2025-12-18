package bean;

import exception.IncorrectDataException;

public class BookBean {

    private int id;
    private String title;
    private String author;
    private String category;
    private int year;
    private String publisher;
    private int pages;
    private String isbn;
    private int stock;
    private String plot;
    private String imagePath;
    private double price;

    public BookBean() {}

    // ================== GETTER & SETTER ==================

    public int getId() {
        return id;
    }

    public void setId(int id) throws IncorrectDataException {
        if (id <= 0)
            throw new IncorrectDataException("Book id non valido");
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws IncorrectDataException {
        if (title == null || title.isBlank())
            throw new IncorrectDataException("Titolo libro non valido");
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) throws IncorrectDataException {
        if (author == null || author.isBlank())
            throw new IncorrectDataException("Autore libro non valido");
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws IncorrectDataException {
        if (category == null || category.isBlank())
            throw new IncorrectDataException("Categoria libro non valida");
        this.category = category;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) throws IncorrectDataException {
        if (year <= 0)
            throw new IncorrectDataException("Anno di pubblicazione non valido");
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) throws IncorrectDataException {
        if (publisher == null || publisher.isBlank())
            throw new IncorrectDataException("Editore non valido");
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) throws IncorrectDataException {
        if (pages <= 0)
            throw new IncorrectDataException("Numero di pagine non valido");
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) throws IncorrectDataException {
        if (isbn == null || isbn.isBlank())
            throw new IncorrectDataException("ISBN non valido");
        this.isbn = isbn;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws IncorrectDataException {
        if (stock < 0)
            throw new IncorrectDataException("Stock non valido");
        this.stock = stock;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) throws IncorrectDataException {
        if (plot == null)
            throw new IncorrectDataException("Trama non valida");
        this.plot = plot;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) throws IncorrectDataException {
        if (imagePath == null || imagePath.isBlank())
            throw new IncorrectDataException("Percorso immagine non valido");
        this.imagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws IncorrectDataException {
        if (price < 0)
            throw new IncorrectDataException("Prezzo non valido");
        this.price = price;
    }
}