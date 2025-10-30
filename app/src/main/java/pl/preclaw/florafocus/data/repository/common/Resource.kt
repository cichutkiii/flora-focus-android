package pl.preclaw.florafocus.data.repository.common

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Exception? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

// Extension functions for easy mapping
fun <T> T.toSuccess(): Resource<T> = Resource.Success(this)
fun String.toError(exception: Exception? = null): Resource<Nothing> = Resource.Error(this, exception)