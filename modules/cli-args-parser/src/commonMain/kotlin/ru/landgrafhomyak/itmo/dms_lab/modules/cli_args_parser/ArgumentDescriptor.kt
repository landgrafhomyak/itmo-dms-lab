package ru.landgrafhomyak.itmo.dms_lab.modules.cli_args_parser

import ru.landgrafhomyak.itmo.dms_lab.utility.IPv4

@Suppress("RemoveRedundantQualifierName")
sealed class ArgumentDescriptor<T : Any>(
    val keyword: String,
    val isOptional: Boolean,
    val repeatable: Boolean
) {

    final override fun equals(other: Any?): Boolean = this === other

    final override fun hashCode(): Int = super.hashCode()

    @Suppress("ClassName")
    sealed class _Unique<T : Any>(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor<T>(keyword, isOptional, false)

    @Suppress("ClassName")
    sealed class _Repeatable<T : Any>(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor<T>(keyword, isOptional, true)


    @Suppress("ClassName", "SpellCheckingInspection")
    internal interface _Checkable<T : Any> {
        /**
         * Checks that passed [value] is valid
         * @return `null` if check passed or error message otherwise
         */
        fun check(value: T): String?
    }


    abstract class UniqueStringArgument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Unique<String>(keyword, isOptional), ArgumentDescriptor._Checkable<String>


    abstract class RepeatableStringArgument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Repeatable<String>(keyword, isOptional), ArgumentDescriptor._Checkable<String>


    abstract class UniqueIntArgument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Unique<Long>(keyword, isOptional), ArgumentDescriptor._Checkable<Long>

    abstract class RepeatableIntArgument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Repeatable<Long>(keyword, isOptional), ArgumentDescriptor._Checkable<Long>

    abstract class UniqueFloatArgument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Unique<Double>(keyword, isOptional), ArgumentDescriptor._Checkable<Double>

    abstract class RepeatableFloatArgument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Repeatable<Double>(keyword, isOptional), ArgumentDescriptor._Checkable<Double>

    abstract class UniqueIPv4Argument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Unique<IPv4>(keyword, isOptional)

    abstract class RepeatableIPv4Argument(keyword: String, isOptional: Boolean) :
        ArgumentDescriptor._Repeatable<IPv4>(keyword, isOptional)

    abstract class Flag(keyword: String) :
        ArgumentDescriptor._Unique<FlagValue>(keyword, true)
}