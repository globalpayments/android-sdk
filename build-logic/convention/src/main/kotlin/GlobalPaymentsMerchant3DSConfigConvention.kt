import com.android.build.api.dsl.ApplicationExtension
import com.globalpayments.android.sdk.configureMerchant3DSApp
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class GlobalPaymentsMerchant3DSConfigConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                configureMerchant3DSApp(this)
            }
        }
    }
}
