
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import hudson.util.Secret
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import jenkins.model.*

// Получаем хранилище учётных данных!!!
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

def ghcrToken = System.getenv("GHCR_TOKEN")

if (ghcrToken) {
    println "--> Creating credential: GHCR_TOKEN"
    def ghcrCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "GHCR_TOKEN",
            "GHCR token from ENV",
            Secret.fromString(ghcrToken)
    )
    store.addCredentials(Domain.global(), ghcrCred)
}