package cn.disy920.oks.network.exception

/**
 * 当一个操作超时时会抛出此异常
 */
class TimeoutException(override val message: String) : Exception(message)