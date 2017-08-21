package sa.gov.nic.bio.bw.client.tools

import javax.inject.Inject

class SignJar implements Runnable
{
    File jarFile
    File outputDirectory

    @Inject
    public SignJar(File jarFile, File outputDirectory)
    {
        this.jarFile = jarFile
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void run()
    {
        ant.signjar(
                jar: jarFile,
                destDir: outputDirectory,
                keystore: "tools-resources/bw-keystore.p12",
                storetype: "PKCS12",
                storepass: "bw#456321",
                alias: "bw",
                preservelastmodified: "true")
    }
}