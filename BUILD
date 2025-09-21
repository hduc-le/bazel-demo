load("@bazel_gazelle//:def.bzl", "gazelle", "gazelle_binary")
load("@pip//:requirements.bzl", "all_whl_requirements", "requirement")
load("@rules_python//python:pip.bzl", "compile_pip_requirements")
load("@rules_python//python:py_library.bzl", "py_library")
load("@rules_python_gazelle_plugin//manifest:defs.bzl", "gazelle_python_manifest")
load("@rules_python_gazelle_plugin//modules_mapping:def.bzl", "modules_mapping")
load("@rules_uv//uv:pip.bzl", "pip_compile")
load("@rules_uv//uv:venv.bzl", "create_venv")

# ===================== Pip-tools based dependency compilation =====================
# make sure the requirements.txt file is exists before running this
# run bazel run //:my_pip_requirements.update to update the requirements.txt file
compile_pip_requirements(
    name = "my_pip_requirements",
    requirements_in = "requirements.in",
    requirements_txt = "requirements.txt",
)

# ===================== UV-based dependency compilation =====================
# UV-based dependency compilation (much faster than standard pip-tools)
# if you have update the `requirements.in` file, you need to update the `requirements.txt` file
# make sure the requirements.txt file is exists before running this
# run bazel run //:uv_compile_requirements to update the requirements.txt file
pip_compile(
    name = "uv_compile_requirements",
    requirements_in = "requirements.in",
    requirements_txt = "requirements.txt",
)

# Create virtual environment using UV (optional for development)
# run bazel run //:create_venv to create the virtual environment
create_venv(
    name = "create_venv",
    requirements_txt = "//:requirements.txt", # default
    destination_folder = "venv",  # Specify custom path for virtual environment
)

# Alternative: Create development environment with different path
create_venv(
    name = "create_dev_venv",
    requirements_txt = "//:requirements.txt",
    destination_folder = ".venv",  # Hidden directory (common convention)
)

# Alternative: Create production-like environment
create_venv(
    name = "create_prod_venv", 
    requirements_txt = "//:requirements.txt",  # Use traditional requirements
    destination_folder = "prod_venv",
)

# ===================== Package group targets =====================
# Group for data analysis packages
py_library(
    name = "data_analysis_deps",
    visibility = ["//visibility:public"],
    deps = [
        requirement("pandas"),
        requirement("numpy"),
        requirement("requests"),
    ],
)

# Group for FastAPI service packages
py_library(
    name = "fastapi_deps",
    visibility = ["//visibility:public"],
    deps = [
        requirement("fastapi"),
        requirement("uvicorn"),
        requirement("requests"),
    ],
)

# You can define more groups here as needed
# using py_library() and then import the build target into the app BUILD file

# ===================== Convenience alias targets for running services =====================
alias(
    name = "python_service",
    actual = "//app/python:main",
)

alias(
    name = "kotlin_service",
    actual = "//app/kotlin:kotlin_service",
)

# Build all services target
filegroup(
    name = "all_services",
    srcs = [
        "//app/kotlin:kotlin_service",
        "//app/python:main",
    ],
)

# ===================== Gazelle python extension for dependency management =====================
# This rule fetches the metadata for python packages we depend on. That data is
# required for the gazelle_python_manifest rule to update our manifest file.
modules_mapping(
    name = "modules_map",

    # include_stub_packages: bool (default: False)
    # If set to True, this flag automatically includes any corresponding type stub packages
    # for the third-party libraries that are present and used. For example, if you have
    # `boto3` as a dependency, and this flag is enabled, the corresponding `boto3-stubs`
    # package will be automatically included in the BUILD file.
    # Enabling this feature helps ensure that type hints and stubs are readily available
    # for tools like type checkers and IDEs, improving the development experience and
    # reducing manual overhead in managing separate stub packages.
    include_stub_packages = True,
    wheels = all_whl_requirements,
)

# Gazelle python extension needs a manifest file mapping from
# an import to the installed package that provides it.
# This macro produces two targets:
# - //:gazelle_python_manifest.update can be used with `bazel run`
#   to recalculate the manifest
# - //:gazelle_python_manifest.test is a test target ensuring that
#   the manifest doesn't need to be updated
gazelle_python_manifest(
    name = "gazelle_python_manifest",
    modules_mapping = ":modules_map",

    # This is what we called our `pip.parse` rule in MODULE.bazel, where third-party
    # python libraries are loaded in BUILD files.
    pip_repository_name = "pip",

    # This should point to wherever we declare our python dependencies
    # (the same as what we passed to the modules_mapping rule in WORKSPACE)
    # This argument is optional. If provided, the `.test` target is very
    # fast because it just has to check an integrity field. If not provided,
    # the integrity field is not added to the manifest which can help avoid
    # merge conflicts in large repos.
    requirements = "//:requirements.txt",
)

gazelle_binary(
    name = "gazelle_multilang",
    languages = [
        # List of language plugins.
        # If you want to generate py_proto_library targets (PR #3057), then
        # the proto language plugin _must_ come before the rules_python plugin.
        #"@bazel_gazelle//language/proto",
        "@rules_python_gazelle_plugin//python",
    ],
)

gazelle(
    name = "gazelle",
    gazelle = ":gazelle_multilang",
)
