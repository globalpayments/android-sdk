import com.android.build.api.dsl.ApplicationExtension
import com.globalpayments.android.sdk.configureSampleApp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class GlobalPaymentsSampleAppConfigConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                configureSampleApp(this)
            }
        }
    }
}
