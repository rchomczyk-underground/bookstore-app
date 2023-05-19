package moe.rafal.bookstore

import moe.rafal.bookstore.book.BookService
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin

class BookStoreApplication {

    private lateinit var jdbi: Jdbi

    fun start() {
        jdbi = Jdbi.create("jdbc:mysql://127.0.0.1:3306/bookstore", "root", "")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        val bookService = BookService(jdbi)
            bookService.createSchema()

        bookService.insertBook("9788383223445", "Czysty kod. Podręcznik dobrego programisty", "Robert", "C. Martin")
    }

    fun ditch() {
        // There is nothing to do in this place, yet.
    }
}