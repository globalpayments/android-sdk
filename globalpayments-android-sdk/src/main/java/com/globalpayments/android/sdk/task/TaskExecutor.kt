package com.globalpayments.android.sdk.task

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.Future

object TaskExecutor {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val executorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2)

    /**
     * Execute a method from sdk on a background thread.
     * The result is returned on the same thread on which is executed. This is done to avoid context switching when we want to chain multiple calls.
     * @param shouldReturnToMainThread boolean that tells if the onFinished callback should be called on the main looper
     * @param taskToExecute the task we want the execute
     * @param onFinished callback on which the result is returned
     *
     * @return the return value is a future used for cancellation
     */
    fun <T> executeAsync(
        shouldReturnToMainThread: Boolean = true,
        taskToExecute: () -> T?,
        onFinished: ((TaskResult<T>) -> Unit)? = null
    ): Future<*> {
        return executorService.submit {
            val result: TaskResult<T> = try {
                val result = taskToExecute()
                if (result == null) {
                    TaskResult.Error(RuntimeException("Null result"))
                } else {
                    TaskResult.Success(result)
                }
            } catch (exception: Exception) {
                TaskResult.Error(exception)
            }
            if (shouldReturnToMainThread) {
                mainHandler.post { onFinished?.invoke(result) }
                return@submit
            }
            onFinished?.invoke(result)
        }
    }

}