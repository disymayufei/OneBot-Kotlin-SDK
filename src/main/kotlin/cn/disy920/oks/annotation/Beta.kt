package cn.disy920.oks.annotation

/**
 * 用于标记一个类或方法处于不稳定阶段
 * 这意味着在使用该类或方法时，可能会出现不可预知的行为，或者在不同的平台上可能出现不同的表现
 * 除非你知道你在做什么，否则尽量不要使用携带有该注解的类或方法
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Beta(val reason: String)
