package at.stefanirndorfer.mobtt.core.di

import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.Environment
import at.stefanirndorfer.mobtt.core.xttv.XttvAssociationRepository
import at.stefanirndorfer.mobtt.domain.club.Association
import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs

class RepositoryModulesTest {
    private val fakeAssociations = object : AssociationRepository {
        override suspend fun getAssociation(id: String): Association? = null

        override suspend fun getAssociations(): List<Association> = emptyList()
    }

    private fun associationRepository(environment: Environment, vararg appModules: Module): AssociationRepository? {
        val config = AppConfig(environment, appVersion = "1.0")
        // Apps bind their modules first, as initKoin does, so the repository modules come last.
        val koin = koinApplication {
            modules(module { single { config } })
            modules(appModules.toList())
            modules(repositoryModules())
        }.koin
        return koin.getOrNull<AssociationRepository>().also { koin.close() }
    }

    @Test
    fun everyEnvironmentReadsXttv() {
        Environment.entries.forEach { environment ->
            assertIs<XttvAssociationRepository>(associationRepository(environment), environment.key)
        }
    }

    @Test
    fun xttvTakesPrecedenceOverAFakeAnAppBoundFirst() {
        val fake = module { single<AssociationRepository> { fakeAssociations } }

        Environment.entries.forEach { environment ->
            assertIs<XttvAssociationRepository>(associationRepository(environment, fake), environment.key)
        }
    }
}
