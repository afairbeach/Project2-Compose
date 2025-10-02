import jenkins.model.*
import hudson.security.*

def instance = Jenkins.get()

def adminId = System.getenv("JENKINS_ADMIN_ID") ?: "admin"
def adminPw = System.getenv("JENKINS_ADMIN_PASSWORD") ?: "admin123"
def buildId = System.getenv("BUILD_USER_ID") ?: "builder"
def buildPw = System.getenv("BUILD_USER_PASSWORD") ?: "builder123"

def realm = new HudsonPrivateSecurityRealm(false)
if (realm.getUser(adminId) == null) realm.createAccount(adminId, adminPw)
if (realm.getUser(buildId) == null) realm.createAccount(buildId, buildPw)
instance.setSecurityRealm(realm)

def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, adminId)

// Minimal permissions for pipeline user
strategy.add(Jenkins.READ, buildId)
strategy.add(Item.READ, buildId)
strategy.add(Item.DISCOVER, buildId)
strategy.add(Item.BUILD, buildId)
strategy.add(Item.WORKSPACE, buildId)
strategy.add(Run.READ, buildId)
strategy.add(Run.UPDATE, buildId)

instance.setAuthorizationStrategy(strategy)
instance.save()
println "Security hardened: anonymous disabled; admin + builder created."
