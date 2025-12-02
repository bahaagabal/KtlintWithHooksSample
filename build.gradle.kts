// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint)
}

subprojects {

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
        }
    }

    // apply only on Android modules
    plugins.withId("com.android.application") {
        setupGitHooksForProject(this@subprojects)
    }
    plugins.withId("com.android.library") {
        setupGitHooksForProject(this@subprojects)
    }
}

fun setupGitHooksForProject(project: Project) {
    with(project) {
        tasks.register<Copy>("copyGitHooks") {
            from("$rootDir/scripts/pre-commit")
            into("$rootDir/.git/hooks/")
        }

        tasks.register<Exec>("installGitHooks") {
            workingDir = rootDir
            commandLine("chmod", "-R", "+x", ".git/hooks/")
            dependsOn("copyGitHooks")
        }

        tasks.matching { it.name == "preBuild" }.configureEach {
            dependsOn("installGitHooks")
        }
    }
}