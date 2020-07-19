package com.gdavidpb.github.utils

import android.net.ConnectivityManager
import android.util.SparseArray
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.gdavidpb.github.domain.usecase.coroutines.Completable
import com.gdavidpb.github.domain.usecase.coroutines.Result
import com.google.gson.internal.bind.util.ISO8601Utils
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/* Context */

fun ConnectivityManager.isNetworkAvailable() = activeNetworkInfo?.isConnected == true

/* Live data */

typealias LiveResult<T> = MutableLiveData<Result<T>>
typealias LiveCompletable = MutableLiveData<Completable>

/* LiveResult */

@JvmName("postCompleteResult")
fun <T> LiveResult<T>.postSuccess(value: T) = postValue(Result.OnSuccess(value))

@JvmName("postThrowableResult")
fun <T> LiveResult<T>.postThrowable(throwable: Throwable) = postValue(Result.OnError(throwable))

@JvmName("postLoadingResult")
fun <T> LiveResult<T>.postLoading() = postValue(Result.OnLoading())

@JvmName("postCancelResult")
fun <T> LiveResult<T>.postCancel() = postValue(Result.OnCancel())

@JvmName("postEmptyResult")
fun <T> LiveResult<T>.postEmpty() = postValue(Result.OnEmpty())

@JvmName("postCompleteCompletable")
fun LiveCompletable.postComplete() = postValue(Completable.OnComplete)

@JvmName("postThrowableCompletable")
fun LiveCompletable.postThrowable(throwable: Throwable) = postValue(Completable.OnError(throwable))

@JvmName("postLoadingCompletable")
fun LiveCompletable.postLoading() = postValue(Completable.OnLoading)

@JvmName("postCancelCompletable")
fun LiveCompletable.postCancel() = postValue(Completable.OnCancel)

/* Observers */

fun <T, L : LiveData<T>> Fragment.observe(liveData: L, body: (T?) -> Unit) =
    liveData.observe(this, Observer(body))

/* Coroutines */

inline fun <reified T> Retrofit.create(): T = create(T::class.java) as T

fun <T> Response<T>.getOrThrow(): T = body() ?: throw HttpException(this)

/* View */

fun View.onClickOnce(onClick: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            view.setOnClickListener(null)

            also { listener ->
                CoroutineScope(Dispatchers.Main).launch {
                    onClick()

                    withContext(Dispatchers.IO) { delay(500) }

                    view.setOnClickListener(listener)
                }
            }
        }
    })
}

/* Parsing */

private val cacheFormat = SparseArray<SimpleDateFormat>()

fun Int.readableFormat() = when {
    this < 1000 -> "$this"
    this < 1000000 -> "%dk".format(this / 1000)
    else -> "%.2fM".format(this / 1000000f)
}

fun String.parseISO8601(): Date = ISO8601Utils.parse(this, ParsePosition(0))

fun Date.toISO8601(): String = ISO8601Utils.format(this)

fun Date.format(format: String): String {
    if (time == -1L) return "-"

    val key = format.hashCode()

    return (cacheFormat.get(key) ?: SimpleDateFormat(format, Locale.US).also {
        cacheFormat.put(key, it)
    }).format(this)
}

/* Fragment */

fun Fragment.longToast(@StringRes resId: Int) =
    Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG).show()

fun Fragment.requireActionBar() =
    (requireActivity() as AppCompatActivity).supportActionBar
        ?: error("Fragment $this not attached to an activity.")

fun Fragment.backCallback(callback: OnBackPressedCallback.() -> Unit) =
    object : Lazy<OnBackPressedCallback> {
        override val value: OnBackPressedCallback
            get() = requireActivity().onBackPressedDispatcher.addCallback(
                owner = this@backCallback,
                enabled = false,
                onBackPressed = callback
            )

        override fun isInitialized() = true
    }

fun Fragment.onBackPressed() = requireActivity().onBackPressedDispatcher.onBackPressed()