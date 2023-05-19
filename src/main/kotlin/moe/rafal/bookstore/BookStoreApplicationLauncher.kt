package moe.rafal.bookstore

fun main() {
    val application = BookStoreApplication()
        application.start()

    val runtime = Runtime.getRuntime()
        runtime.addShutdownHook(Thread(application::ditch))
}