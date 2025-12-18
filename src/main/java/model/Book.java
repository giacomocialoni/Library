package model;

import java.util.Objects;

public class Book {

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

    public Book() {}

    public Book(
            int id,
            String title,
            String author,
            String category,
            int year,
            String publisher,
            int pages,
            String isbn,
            int stock,
            String plot,
            String imagePath,
            double price
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.year = year;
        this.publisher = publisher;
        this.pages = pages;
        this.isbn = isbn;
        this.stock = stock;
        this.plot = plot;
        this.imagePath = imagePath;
        this.price = price;
    }

    // --- GETTER & SETTER ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getPlot() { return plot; }
    public void setPlot(String plot) { this.plot = plot; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // --- EQUALITY ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}