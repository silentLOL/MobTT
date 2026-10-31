package at.stefanirndorfer.mobtt.core.xttv

import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.Environment
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs

class XttvModuleTest {
    @Test
    fun resolvesTheXttvBackedAssociationRepositoryFromAnAppConfig() {
        val koin = koinApplication {
            modules(module { single { AppConfig(Environment.Prod, appVersion = "1.0") } }, xttvModule)
        }.koin

        assertIs<XttvAssociationRepository>(koin.get<AssociationRepository>())
        koin.close()
    }
}
