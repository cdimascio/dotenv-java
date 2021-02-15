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
mvn deploy -Dregistry=https://maven.pkg.github.com/cdimascio -Dtoken=XXXX
```
https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages
