# Contributing

## Develop

1. Fork this repo

2. Build the project

  ```shell
  # java >=8 required
  export JAVA_HOME=/path/to/java8/home

  mvn build
  ```

3. Make a change

  Fix a bug, add a feature, update the doc, etc

4. Run the Tests

  ```shell
  mvn test
  ```

5. Create a Pull Request (PR)

## Add yourself as a contributor

Once your PR has been merged, add the following comment to your PR:

>@all-contributors please add @username for code and test!

_Replace code and test with doc or test or infra or some combination depending on your contribution._

## Package

Run the following to ensure the package step succeeds.

```shell
mvn clean test jacoco:report package 
```

## Release Process

### Build

Build sources and javadoc
```shell
mvn clean test jacoco:report package 
```

### Publish to Maven Central

Deploy
```shell
mvn clean test jacoco:report package deploy -DperformRelease=true
```
When first publishing to staging repos, you most close and release from OSS Sonatype. To do this
- navigate to https://oss.sonatype.org/#stagingRepositories
- select repository
- press the `close` button
- press the `release` button

#### Artifacts upload

- Generate signed artifacts locally
  ```shell
  mvn verify -P release-sign-artifacts -DperformRelease=true
  ```

- Upload change log
  ```shell
  gh release create v3.0.2 -F CHANGELOG.md
  ```

- Attach 'signed' artifacts (needed for OpenSSF Security Score)
  ```shell
  gh release upload v3.0.2 target/*.jar.asc --clobber       
  ```

### Publish to Github Packages

_Note: This step can only be run by maintainers._

Add `distributionManagement` to `pom.xml`

```xml
  <distributionManagement>
    <repository>
      <id>github</id>
      <name>Carmine M DiMascio</name>
      <url>https://maven.pkg.github.com/cdimascio/dotenv-java</url>
    </repository>
  </distributionManagement>
```

```shell
# deploy to github packages
# comment out sonatype plugin sonatype repository from pom.xml, then
mvn deploy -Dregistry=https://maven.pkg.github.com/cdimascio -Dtoken=XXXX
# or
mvn clean test jacoco:report package deploy  -Dregistry=https://maven.pkg.github.com/cdimascio -Dtoken=XXXX
```

## Notes


### Publish to MavenCentral

Contributors are not responsible for deploying to mavencentral.

**Maven Central**

- Publish with Maven - https://central.sonatype.org/publish/publish-maven/
- GPG Setup - https://central.sonatype.org/publish/requirements/gpg/
- https://oss.sonatype.org/#profile;User%20Token
  - get oss.sonatype token

To publish a gpg key:

```shell
gpg --send-keys 5BE1414D5EAF81B48F2E77E1999F818C080AF9C1
## search keys
gpg --keyserver keyserver.ubuntu.com --search-keys 5BE1414D5EAF81B48F2E77E1999F818C080AF9C1
````

where `5BE1414D5EAF81B48F2E77E1999F818C080AF9C1` is the public key


```shell
mvn clean test jacoco:report package deploy -DperformRelease=true
```

Generate signed artifacts locally without deploying

```shell
mvn verify -P release-sign-artifacts -DperformRelease=true
```

Navigate to https://oss.sonatype.org/#stagingRepositories, select repository, then press the `close` button, then  `release`


https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages


### OpenSSF Security Scorecard
- Get Analysis Result: https://api.securityscorecards.dev/#/results/getResult
  - enter platform=github.com, org=cdimascio, repo=dotenv-java
- Step Security - Secure Your Repo Analysis + auto PR - https://app.stepsecurity.io/securerepo
- Step Security - For Repo - https://app.stepsecurity.io/github/cdimascio/actions/dashboard
- OpenSSF Badget Analysis https://www.bestpractices.dev/en/projects/9407


### GPG Key notes

-  `cat ~/.gnupg/gpg.conf`
Note that the keyserver is specified
```
auto-key-retrieve
no-emit-version
keyserver hkp://keyserver.ubuntu.com
```

```

Send and validate keys are present on the keyserver
``shell
# send public key
gpg --keyserver keyserver.ubuntu.com --send-keys D4E6A3593F7EC1BBC039AC99896F36215850D4C7

# search public key
gpg --keyserver keyserver.ubuntu.com --search-keys D4E6A3593F7EC1BBC039AC99896F36215850D4C7

```
