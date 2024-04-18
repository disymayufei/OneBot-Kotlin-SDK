package cn.disy920.okapi.annotation

/**
 * 用于标记一个类或方法的调用行为是未定义的
 * 这意味着在使用该类或方法时，可能会出现不可预知的行为
 * 这通常是由于LLOneBot未实现，或未按预期实现某一接口导致的
 * 除非你知道你在做什么，否则不要使用携带有该注解的类或方法
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Undefined(val reason: String)
