package at.stefanirndorfer.batmate.core.di

import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.core.xttv.XttvAssociationRepository
import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertIs

class RepositoryModulesTest {
    private val fakeAssociations = object : AssociationRepository {
        override suspend fun getAssociation(id: String): Association? = null
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
