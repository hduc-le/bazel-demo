load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

# Shared data models that can be used across different services in the monorepo
kt_jvm_library(
    name = "shared_models",
    srcs = ["SharedModels.kt"],
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_google_code_gson_gson",
    ],
)
