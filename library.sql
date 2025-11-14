CREATE DATABASE `library` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
CREATE TABLE `books` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `year` int DEFAULT NULL,
  `plot` text,
  `image_path` varchar(255) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `pages` int DEFAULT NULL,
  `isbn` varchar(50) DEFAULT NULL,
  `stock` int DEFAULT '0',
  `category` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `fk_books_category` (`category`),
  CONSTRAINT `fk_books_category` FOREIGN KEY (`category`) REFERENCES `categories` (`category`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `users` (
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `role` enum('admin','logged_user') NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `categories` (
  `category` varchar(50) NOT NULL,
  PRIMARY KEY (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `loans` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_email` varchar(100) NOT NULL,
  `book_id` int NOT NULL,
  `status` enum('RESERVED','BORROWED','EXPIRED','RETURNED') NOT NULL DEFAULT 'RESERVED',
  `reserved_date` date DEFAULT NULL,
  `loaned_date` date DEFAULT NULL,
  `returning_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_email` (`user_email`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `loans_ibfk_1` FOREIGN KEY (`user_email`) REFERENCES `users` (`email`) ON DELETE CASCADE,
  CONSTRAINT `loans_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `purchases` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_email` varchar(100) NOT NULL,
  `book_id` int NOT NULL,
  `purchase_date` date DEFAULT NULL,
  `status` enum('RESERVED','PURCHASED') NOT NULL DEFAULT 'RESERVED',
  `approved_at` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_email` (`user_email`),
  KEY `book_id` (`book_id`),
  CONSTRAINT `purchases_ibfk_1` FOREIGN KEY (`user_email`) REFERENCES `users` (`email`) ON DELETE CASCADE,
  CONSTRAINT `purchases_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `posts` (
  `user_fk` varchar(100) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `content` text NOT NULL,
  `post_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_fk`,`post_date`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_fk`) REFERENCES `users` (`email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
