package sa.gov.nic.bio.bw.client.tools

import org.gradle.api.Action
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

class SignJars extends SourceTask
{
    final WorkerExecutor workerExecutor

    // The WorkerExecutor will be injected by Gradle at runtime
    @Inject
    public SignJars(WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor
    }

    @TaskAction
    void signJars()
    {
        // Create and submit a unit of work for each file
        source.files.each { jarFile ->
            workerExecutor.submit(SignJar.class, ({ WorkerConfiguration config ->
                // Use the minimum level of isolation
                config.isolationMode = IsolationMode.NONE

                // Constructor parameters for the unit of work implementation
                config.params jarFile, source
            } as Action<? super WorkerConfiguration>))
        }

        // Wait for all asynchronous work to complete before continuing
        workerExecutor.await()
        logger.lifecycle("Signed ${source.files.size()} jar files in ${project.relativePath(source)}")
    }
}