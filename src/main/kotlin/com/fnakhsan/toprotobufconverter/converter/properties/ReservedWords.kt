package com.fnakhsan.toprotobufconverter.converter.properties

internal object ReservedWords {
    private val WORDS = arrayOf(
        "abstract",
        "continue",
        "for",
        "new",
        "switch",
        "assert",
        "default",
        "if",
        "package",
        "synchronized",
        "boolean",
        "do",
        "goto",
        "private",
        "this",
        "break",
        "double",
        "implements",
        "protected",
        "throw",
        "byte",
        "else",
        "import",
        "public",
        "throws",
        "case",
        "enum",
        "instanceof",
        "return",
        "transient",
        "catch",
        "extends",
        "int",
        "short",
        "try",
        "char",
        "final",
        "interface",
        "static",
        "void",
        "class",
        "finally",
        "long",
        "strictfp",
        "volatile",
        "const",
        "float",
        "native",
        "super",
        "while"
    )
    val WORDS_SET = HashSet(WORDS.asList())
}