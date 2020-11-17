# scala-big-data-topics

>Manage a list of big data topics using Scala, Akka, and HTTP

## Links

- [Service]()
- [Repo](https://github.com/denisecase/scala-big-data-topics)

## Prerequisities

- OpenJDK (8 or higher)
- sbt (current)
- (Optional, if Windows) Chocolatey package manager

```PowerShell
choco install openjdk -y
choco install sbt -y
choco upgrade sbt -y
choco list --local
refreshenv
sbt --version
sbt scalaVersion
```

## Creating From Scratch

### Initialize a new repo

1. In GitHub, add a new repo
1. Add a default README.md
1. (Optional) Add License
1. Add a .gitignore using Scala template
1. Clone down to your machine

### Create standard Scala project

1. Add build.sbt
1. Add project\build.properties
1. Add project\plugins.sbt
1. Add src\main\scala\Main.scala
1. Add src\test\scala\MainSpec.scala
1. Add src\main\resources\application.conf
1. Add src\main\resources\logback.xml

Add VS Code extesions:

 - Scala Syntax (official)
 - Scala (sbt) by LightBend

## Add dependencies and static analysis

- Add dependencies to build.sbt
- Add sbt.version to project/build.properties
- In project/plugins.sbt add Scalastyle
- Open PowerShell as Admin and run `sbt scalastyleGenerateConfig` to create config file
- Edit scalastyle-config.xml as desired (e.g. file header)

## Create the first route in Main.scala

1. Complete src/main/scala/Main.scala
2. Open PowerShell as Admin and run `sbt scalastyle`
3. Run `sbt run`

## Create the first test in MainSpec.scala

1. Complete src/test/scala/MainSpec.scala
2. Open PowerShell as Admin and run `sbt scalastyle`
3. Run run `sbt test`

## Resources

1. [sbt](https://www.scala-sbt.org/index.html)
2. [Akka](https://akka.io/)
3. [Akka docs](https://akka.io/docs/)
4. [akkahttp-quickstart](https://github.com/Codemunity/akkahttp-quickstart)
5. [ScalaTest](https://www.scalatest.org/at_a_glance/WordSpec)
6. [Scala on Command Line](https://docs.scala-lang.org/getting-started/sbt-track/getting-started-with-scala-and-sbt-on-the-command-line.html)
7. [Akka HTTP Quickstart](https://doc.akka.io/docs/akka-http/current/introduction.html#)
8. [Scalastyle](http://www.scalastyle.org/)
9. [Logging](https://doc.akka.io/docs/akka/2.6/typed/logging.html#logback)









