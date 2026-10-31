package at.stefanirndorfer.mobtt.domain.user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** A DataStore scope the test can cancel to release the file, so a second store can open it. */
fun newStoreScope() = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
