package moe.rafal.bookstore.book

import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlScript
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface BookRepository {

    @SqlScript(
        """
        CREATE TABLE IF NOT EXISTS `books_authors`
        (
            `id` INT(11) PRIMARY KEY AUTO_INCREMENT,
            `name` VARCHAR(64) NOT NULL,
            `surname` VARCHAR(128) NOT NULL
        );    
        """)
    @SqlScript(
        """
        CREATE TABLE IF NOT EXISTS `books`
        (
            `id` INT(11) PRIMARY KEY AUTO_INCREMENT,
            `isbn` VARCHAR(16) NOT NULL,
            `title` VARCHAR(256) NOT NULL,
            `author_id` INT(11),
            FOREIGN KEY (`author_id`) REFERENCES `books_authors`(`id`)
        );    
        """)
    fun createSchema()

    @SqlQuery(
        """
        SELECT 
            `books`.`id`,
            `books`.`isbn`,
            `books`.`title`,
            `books_authors`.`id` AS `author_id`,
            `books_authors`.`name` AS `author_name`,
            `books_authors`.`surname` AS `author_surname`
        FROM
            `books`,
            `books_authors`
        WHERE
            `books`.`author_id` = `books_authors`.`id`
        AND
            `books`.`title` = ?;
        """)
    fun findBookByTitle(title: String): Book?

    @SqlUpdate("INSERT INTO `books` (`isbn`, `title`, `author_id`) VALUES (?, ?, ?);")
    @GetGeneratedKeys("id")
    fun insertBook(isbn: String, title: String, authorId: Int): Int

    @SqlQuery(
        """
        SELECT
            `books_authors`.`id`,
            `books_authors`.`name`,
            `books_authors`.`surname`
        FROM 
            `books_authors`
        WHERE
            `books_authors`.`name` = ?
        AND
            `books_authors`.`surname` = ?;
        """)
    fun findBookAuthorByNameAndSurname(name: String, surname: String): BookAuthor?

    @SqlUpdate("INSERT INTO `books_authors` (`name`, `surname`) VALUES (?, ?);")
    @GetGeneratedKeys("id")
    fun insertBookAuthor(name: String, surname: String): Int
}
