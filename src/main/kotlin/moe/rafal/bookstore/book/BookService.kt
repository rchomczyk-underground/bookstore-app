package moe.rafal.bookstore.book

import com.github.benmanes.caffeine.cache.Caffeine
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.useHandleUnchecked
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.time.Duration

class BookService(private val jdbi: Jdbi) {

    private val bookCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
        .build<String, Book>() { title -> findBookByTitle(title) }
    private val bookAuthorCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
        .build<String, BookAuthor> { cachingIdentifyingKey -> findOrCreateBookAuthor(
            cachingIdentifyingKey.split(" ")[0],
            cachingIdentifyingKey.split(" ")[1]) }

    fun createSchema() {
        jdbi.useHandleUnchecked { handle ->
            val bookRepository = handle.attach(BookRepository::class.java)
                bookRepository.createSchema()
        }
    }

    fun getBook(title: String): Book? {
        return bookCache.get(title)
    }

    private fun findBookByTitle(title: String): Book? {
        return jdbi.withHandleUnchecked { handle -> handle.attach(BookRepository::class.java)
            .findBookByTitle(title) }
    }

    fun insertBook(isbn: String, title: String, name: String, surname: String) {
        return jdbi.withHandleUnchecked { handle -> handle.attach(BookRepository::class.java)
            .insertBook(isbn, title, bookAuthorCache.get("$name $surname")?.id!!) }
    }

    private fun findOrCreateBookAuthor(name: String, surname: String): BookAuthor {
        return jdbi.withHandleUnchecked { handle ->
            val bookRepository = handle.attach(BookRepository::class.java)
            return@withHandleUnchecked (
                bookRepository.findBookAuthorByNameAndSurname(name, surname) ?: BookAuthor(
                bookRepository.insertBookAuthor(name, surname), name, surname))
        }
    }
}