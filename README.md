# 🗝️ dotenv-java 

![Build Status](https://github.com/cloudsimplus/cloudsimplus/actions/workflows/build.yml/badge.svg) [![Maven Central](https://img.shields.io/maven-central/v/io.github.cdimascio/dotenv-java.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.cdimascio/dotenv-java) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/66b8195f0da544f1ad9ed1352c0ea66f)](https://app.codacy.com/app/cdimascio/dotenv-java?utm_source=github.com&utm_medium=referral&utm_content=cdimascio/dotenv-java&utm_campaign=Badge_Grade_Dashboard) ![](https://img.shields.io/ossf-scorecard/github.com/cdimascio/dotenv-java?label=openssf%20scorecard&style=flat) [![OpenSSF Best Practices](https://www.bestpractices.dev/projects/9407/badge)](https://www.bestpractices.dev/projects/9407) [![](https://img.shields.io/gitter/room/cdimascio-oss/community?color=%23eb205a)](https://gitter.im/cdimascio-oss/community) [![All Contributors](https://img.shields.io/badge/all_contributors-6-orange.svg?style=flat-square)](#contributors-) [![](https://img.shields.io/badge/doc-javadoc-blue)](https://cdimascio.github.io/dotenv-java/docs/javadoc/index.html) ![](https://img.shields.io/badge/license-Apache%202.0-blue.svg)

A no-dependency, pure Java port of the Ruby dotenv project. Load environment variables from a `.env` file.

<p align="center">
	<img src="https://raw.githubusercontent.com/cdimascio/dotenv-java/master/assets/dotenv-java-logo.png" alt="dotenv" width="550"/> 
</p>

_dotenv-java also powers the popular [dotenv-kotlin](https://github.com/cdimascio/dotenv-kotlin) library._

Why dotenv?

>Storing configuration in the environment is one of the tenets of a [twelve-factor](http://12factor.net/config) app. Anything that is likely to change between deployment environments–such as resource handles for databases or credentials for external services–should be extracted from the code into environment variables.

>But it is not always practical to set environment variables on development machines or continuous integration servers where multiple projects are run. Dotenv load variables from a .env file into ENV when the environment is bootstrapped.

>-- [Brandon Keepers](https://github.com/bkeepers/dotenv)


Environment variables listed in the host environment override those in `.env`.  

Use `dotenv.get("...")` instead of Java's `System.getenv(...)`.  

## Install

_Requires Java 11 or greater._

_Still using Java 8? Use version 2.3.2_

### Maven
```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.2.0</version>
</dependency>
```

### Gradle <4.10

```groovy
compile 'io.github.cdimascio:dotenv-java:3.2.0'
```

### Gradle >=4.10

```groovy
implementation 'io.github.cdimascio:dotenv-java:3.2.0'
```

### Gradle Kotlin DSL

```kotlin
implementation("io.github.cdimascio:dotenv-java:3.2.0")
```

Looking for the Kotlin variant? **get [dotenv-kotlin](https://github.com/cdimascio/dotenv-kotlin)**.

## Usage
Use `dotenv.get("...")` instead of Java's `System.getenv(...)`. Here's [why](#faq).

Create a `.env` file in the root of your project

```dosini
# formatted as key=value
MY_ENV_VAR1=some_value
MY_EVV_VAR2=some_value #some value comment
MULTI_LINE="some
multi line
value"
```


```java
Dotenv dotenv = Dotenv.load();
dotenv.get("MY_ENV_VAR1")
```

## Android Usage

- Create an assets folder
- Add `env` *(no dot)* to the assets folder

	<img src="assets/android-dotenv.png" width="350">

- Configure dotenv to search `/assets` for a file with name `env`

	```java
    Dotenv dotenv = Dotenv.configure()
       .directory("/assets")
       .filename("env") // instead of '.env', use 'env'
       .load();

	dotenv.get("MY_ENV_VAR1");
	```

**Note:** The above configuration is required because dot files in `/assets` do not appear to resolve on Android. *(Seeking recommendations from the Android community on how `dotenv-java` configuration should work in order to provide the best experience for Android developers)*

Alternatively, if you are using Provider `android.resource` you may specify

```
 .directory("android.resource://com.example.dimascio.myapp/raw")
```

## Advanced Usage

### Configure
Configure `dotenv-java` once in your application. 

```java
Dotenv dotenv = Dotenv.configure()
        .directory("./some/path")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();
```

- see [configuration options](#configuration-options)

### Get environment variables
Note, environment variables specified in the host environment take precedence over those in `.env`.

```java
dotenv.get("HOME");
dotenv.get("MY_ENV_VAR1", "default value");
```

### Iterate over environment variables
Note, environment variables specified in the host environment take precedence over those in `.env`.

```java
for (DotenvEntry e : dotenv.entries()) {
    System.out.println(e.getKey());
    System.out.println(e.getValue());
}
```

## Configuration options

### *optional* `directory` 
* `path` specifies the directory containing `.env`. Dotenv first searches for `.env` using the given path on the filesystem. If not found, it searches the given path on the classpath. If `directory` is not specified it defaults to searching the current working directory on the filesystem. If not found, it searches the current directory on the classpath.

	**example**
	
	```java
	Dotenv
	  .configure()
	  .directory("/some/path")
	  .load();
	```

### *optional* `filename`

* Use a filename other than `.env`. Recommended for use with Android (see [details](#android-usage)) 

	**example**
	
	```java
	Dotenv
	  .configure()
	  .filename("myenv")
	  .load();
	```

### *optional* `ignoreIfMalformed`

* Do not throw when `.env` entries are malformed. Malformed entries are skipped.

	**example**
	
	```java
	Dotenv
	  .configure()
	  .ignoreIfMalformed()
	  .load();
	```

### *optional* `ignoreIfMissing` 

* Do not throw when `.env` does not exist. Dotenv will continue to retrieve environment variables that are set in the environment e.g. `dotenv["HOME"]`

	**example**
	
	```java
	Dotenv
	  .configure()
	  .ignoreIfMissing()
	  .load();
	```

### *optional* `systemProperties` 

* Load environment variables into System properties, thus making all environment variables accessible via `System.getProperty(...)`

	**example**
	
	```java
	Dotenv
	  .configure()
	  .systemProperties()
	  .load();
	```

## Examples
- with [Maven (simple)](examples/maven-simple)
- see [Java tests](./src/test/java/tests/DotenvTests.java) 

## Powered by dotenv-java
- [spring-dotenv](https://github.com/paulschwarz/spring-dotenv) - dotenv-java as a Spring PropertySource
- [dotenv-kotlin](https://github.com/cdimascio/dotenv-kotlin) - a Kotlin DSL for dotenv-java

## [Javadoc](https://cdimascio.github.io/dotenv-java/docs/javadoc/)

see [javadoc](https://cdimascio.github.io/dotenv-java/docs/javadoc/)

## FAQ

**Q:** Should I deploy a `.env` to e.g. production?

**A**: Tenet III of the [12 factor app methodology](https://12factor.net/config) states "The twelve-factor app stores config in environment variables". Thus, it is not recommended to provide the .env file to such environments. dotenv, however, is super useful in e.g a local development environment as it enables a developer to manage the environment via a file which is more convenient.

Using dotenv in production would be cheating. This type of usage, however is an anti-pattern.

**Q:** Why should I use `dotenv.get("MY_ENV_VAR")` instead of `System.getenv("MY_ENV_VAR")`

**A**: Since Java does not provide a way to set environment variables on a currently running process, vars listed in `.env` cannot be set and thus cannot be retrieved using `System.getenv(...)`.

**Q**: Can I use `System.getProperty(...)` to retrieve environment variables?

**A**: Sure. Use the `systemProperties` option. Or after initializing dotenv set each env var into system properties manually. For example:

example
```java
Dotenv dotenv = Dotenv.configure().load();
dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
System.getProperty("MY_VAR");
```

**Q:** Should I have multiple .env files?

**A**: No. We strongly recommend against having a "main" .env file and an "environment" .env file like .env.test. Your config should vary between deploys, and you should not be sharing values between environments.

> In a twelve-factor app, env vars are granular controls, each fully orthogonal to other env vars. They are never grouped together as “environments”, but instead are independently managed for each deploy. This is a model that scales up smoothly as the app naturally expands into more deploys over its lifetime.

>– The Twelve-Factor App

**Q**: Should I commit my `.env` file?

**A**: No. We strongly recommend against committing your `.env` file to version control. It should only include environment-specific values such as database passwords or API keys. Your production database should have a different password than your development database.

**Q**: What happens to environment variables that were already set?

**A**: dotenv-java will never modify any environment variables that have already been set. In particular, if there is a variable in your `.env` file which collides with one that already exists in your environment, then that variable will be skipped. This behavior allows you to override all `.env` configurations with a machine-specific environment, although it is not recommended.

**Q**: What about variable expansion in `.env`? 

**A**: We haven't been presented with a compelling use case for expanding variables and believe it leads to env vars that are not "fully orthogonal" as [The Twelve-Factor App outlines](https://12factor.net/config). Please open an issue if you have a compelling use case.

**Q**: Can I supply a multi-line value?

**A**: dotenv-java exhibits the same behavior as Java's `System.getenv(...)`, thus if a multi-line value is needed you might consider encoding it via e.g. Base64. see this [comment](https://github.com/cdimascio/dotenv-java/issues/28#issuecomment-489443975) for details.


**Note and reference**: The FAQs present on [motdotla's dotenv](https://github.com/motdotla/dotenv#faq) node project page are so well done that I've included those that are relevant in the FAQs above.

see [CONTRIBUTING.md](CONTRIBUTING.md)

## License

see [LICENSE](LICENSE) ([Apache 2.0](LICENSE))

<a href="https://www.buymeacoffee.com/m97tA5c" target="_blank"><img src="https://bmc-cdn.nyc3.digitaloceanspaces.com/BMC-button-images/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Mooninaut"><img src="https://avatars.githubusercontent.com/u/1463364?v=4?s=100" width="100px;" alt="Clement Cherlin"/><br /><sub><b>Clement Cherlin</b></sub></a><br /><a href="https://github.com/cdimascio/dotenv-java/commits?author=Mooninaut" title="Code">💻</a> <a href="https://github.com/cdimascio/dotenv-java/commits?author=Mooninaut" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/alexbraga"><img src="https://avatars.githubusercontent.com/u/61568124?v=4?s=100" width="100px;" alt="Alex Braga"/><br /><sub><b>Alex Braga</b></sub></a><br /><a href="https://github.com/cdimascio/dotenv-java/commits?author=alexbraga" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/c00ler"><img src="https://avatars.githubusercontent.com/u/1210272?v=4?s=100" width="100px;" alt="Alexey Venderov"/><br /><sub><b>Alexey Venderov</b></sub></a><br /><a href="https://github.com/cdimascio/dotenv-java/commits?author=c00ler" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/yassenb"><img src="https://avatars.githubusercontent.com/u/531223?v=4?s=100" width="100px;" alt="yassenb"/><br /><sub><b>yassenb</b></sub></a><br /><a href="https://github.com/cdimascio/dotenv-java/commits?author=yassenb" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://sidney.beekhoven.nl"><img src="https://avatars.githubusercontent.com/u/903673?v=4?s=100" width="100px;" alt="Sidney Beekhoven"/><br /><sub><b>Sidney Beekhoven</b></sub></a><br /><a href="https://github.com/cdimascio/dotenv-java/commits?author=dizney" title="Code">💻</a> <a href="https://github.com/cdimascio/dotenv-java/commits?author=dizney" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://youtube.com/@manoelcamposdev"><img src="https://avatars.githubusercontent.com/u/261605?v=4?s=100" width="100px;" alt="Manoel Campos"/><br /><sub><b>Manoel Campos</b></sub></a><br /><a href="https://github.com/cdimascio/dotenv-java/commits?author=manoelcampos" title="Code">💻</a> <a href="https://github.com/cdimascio/dotenv-java/commits?author=manoelcampos" title="Tests">⚠️</a> <a href="#infra-manoelcampos" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
