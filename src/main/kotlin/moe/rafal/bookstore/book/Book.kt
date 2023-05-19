package moe.rafal.bookstore.book

import org.jdbi.v3.core.mapper.Nested

data class Book(var id: Int?, var isbn: String, var title: String, @Nested("author_") var author: BookAuthor)

data class BookAuthor(var id: Int?, var name: String, var surname: String)