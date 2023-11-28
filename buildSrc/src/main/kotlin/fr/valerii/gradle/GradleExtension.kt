package fr.valerii.gradle

import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

object GradleExtension {

    fun getGeneralKotlinConfigure(
        jvmTarget: String = SdkVersionProperties.DEFAULT.jvmTarget,
    ): Action<KotlinJvmOptions> = Action { this.jvmTarget = jvmTarget }
}
