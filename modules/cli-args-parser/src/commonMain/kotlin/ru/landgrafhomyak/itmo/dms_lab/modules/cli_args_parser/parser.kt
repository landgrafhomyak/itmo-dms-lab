package ru.landgrafhomyak.itmo.dms_lab.modules.cli_args_parser

import ru.landgrafhomyak.itmo.dms_lab.utility.IPv4

private inline fun <T : Any> parserCheck(
    descriptor: ArgumentDescriptor<T>,
    logger: CliArgsParserLogger,
    rawValue: String,
    parsed: (String) -> T
): T? {
    @Suppress("LiftReturnOrAssignment")
    try {
        return parsed(rawValue)
    } catch (e: IllegalArgumentException) {
        logger.error(descriptor, rawValue, "Failed to parse: ${e.message}")
        return null
    }
}

private fun <T : Any, D> T?.descriptorCheck(
    descriptor: D,
    logger: CliArgsParserLogger,
    rawValue: String,
): T? where D : ArgumentDescriptor<T>, D : ArgumentDescriptor._Checkable<T> {
    val parsedValue: T = this@descriptorCheck ?: return null
    val errorMessage = descriptor.check(parsedValue)
    @Suppress("LiftReturnOrAssignment")
    if (errorMessage == null) {
        return parsedValue
    } else {
        logger.error(descriptor, rawValue, errorMessage)
        return null
    }
}

private fun <T : Any> T?.save(
    descriptor: ArgumentDescriptor._Unique<T>,
    logger: CliArgsParserLogger,
    rawValue: String,
    dst: MutableMap<ArgumentDescriptor._Unique<*>, Any>
) {
    val value = this@save ?: return
    if (dst.put(descriptor, value) != null) {
        logger.error(descriptor, rawValue, "Argument duplication")
    }
}

private fun <T : Any> T?.save(
    descriptor: ArgumentDescriptor._Repeatable<T>,
    logger: CliArgsParserLogger,
    rawValue: String,
    dst: MutableMap<ArgumentDescriptor._Repeatable<*>, MutableList<Any>>
) {
    val value = this@save ?: return
    dst.getOrPut(descriptor) { ArrayList() }.add(value)
}

fun parseCliArgs(
    args: Iterator<String>,
    logger: CliArgsParserLogger,
    expectedArgs: ArgumentDescriptorsSet
): ParsedArgumentsMap {
    val unique = HashMap<ArgumentDescriptor._Unique<*>, Any>()
    val repeatable = HashMap<ArgumentDescriptor._Repeatable<*>, MutableList<Any>>()
    val freeArgs = ArrayList<String>()

    while (args.hasNext()) {
        val rawArg = args.next()
        val (key: String, joinedRawValue: String?) = rawArg
            .split("=", limit = 2)
            .let { e -> e.first() to e.getOrNull(1) }
        val descriptor = expectedArgs[key]
        if (descriptor == null) {
            freeArgs.add(rawArg)
            continue
        }
        if (descriptor is ArgumentDescriptor.Flag) {
            unique[descriptor] = FlagValue
            continue
        }

        val rawValue: String
        @Suppress("LiftReturnOrAssignment")
        if (joinedRawValue != null) {
            rawValue = joinedRawValue
        } else {
            if (!args.hasNext()) {
                logger.fetched(descriptor, null)
                logger.error(descriptor, null, "Value not set")
                break
            }
            rawValue = args.next()
        }
        logger.fetched(descriptor, rawValue)
        when (descriptor) {
            is ArgumentDescriptor.UniqueStringArgument -> parserCheck(descriptor, logger, rawValue) { rawValue }
                .descriptorCheck(descriptor, logger, rawValue).save(descriptor, logger, rawValue, unique)

            is ArgumentDescriptor.UniqueIntArgument -> parserCheck(descriptor, logger, rawValue, String::toLong)
                .descriptorCheck(descriptor, logger, rawValue).save(descriptor, logger, rawValue, unique)

            is ArgumentDescriptor.UniqueFloatArgument -> parserCheck(descriptor, logger, rawValue, String::toDouble)
                .descriptorCheck(descriptor, logger, rawValue).save(descriptor, logger, rawValue, unique)

            is ArgumentDescriptor.UniqueIPv4Argument -> parserCheck(descriptor, logger, rawValue, IPv4::parseFromString)
                .save(descriptor, logger, rawValue, unique)

            is ArgumentDescriptor.RepeatableStringArgument -> parserCheck(descriptor, logger, rawValue) { rawValue }
                .descriptorCheck(descriptor, logger, rawValue).save(descriptor, logger, rawValue, repeatable)

            is ArgumentDescriptor.RepeatableIntArgument -> parserCheck(descriptor, logger, rawValue, String::toLong)
                .descriptorCheck(descriptor, logger, rawValue).save(descriptor, logger, rawValue, repeatable)

            is ArgumentDescriptor.RepeatableFloatArgument -> parserCheck(descriptor, logger, rawValue, String::toDouble)
                .descriptorCheck(descriptor, logger, rawValue).save(descriptor, logger, rawValue, repeatable)

            is ArgumentDescriptor.RepeatableIPv4Argument -> parserCheck(descriptor, logger, rawValue, IPv4::parseFromString)
                .save(descriptor, logger, rawValue, repeatable)

            else -> throw IllegalArgumentException("Unknown descriptor: ${descriptor::class.qualifiedName}")
        }

    }
    return ParsedArgumentsMap(expectedArgs, unique, repeatable, freeArgs)
}