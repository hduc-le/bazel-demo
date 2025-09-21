load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")

# Kotlin HTTP service
kt_jvm_library(
    name = "kotlin_service_lib",
    srcs = ["KotlinService.kt"],
    deps = [
        "//app/kotlin/src/main/kotlin/com/demo/models:shared_models",
        "@maven//:com_google_code_gson_gson",
        "@maven//:com_squareup_okhttp3_okhttp",
    ],
)

kt_jvm_binary(
    name = "kotlin_service",
    main_class = "com.demo.service.KotlinServiceKt",
    visibility = ["//visibility:public"],
    runtime_deps = [":kotlin_service_lib"],
)
