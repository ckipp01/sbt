ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.12.12"
ThisBuild / csrCacheDirectory := (ThisBuild / baseDirectory).value / "coursier-cache"

def localCache =
  ivyPaths := IvyPaths(baseDirectory.value.toString, Some(((ThisBuild / baseDirectory).value / "ivy" / "cache").toString))

lazy val sharedResolver: Resolver = {
  val r = Resolver.defaultShared
  r withConfiguration (r.configuration withIsLocal false)
  //MavenRepository("example-shared-repo", "file:///tmp/shared-maven-repo-bad-example")
  //Resolver.file("example-shared-repo", repoDir)(Resolver.defaultPatterns)
}

lazy val common = project
  .settings(localCache)
  .settings(
    organization := "com.badexample",
    name := "badexample",
    version := "1.0-SNAPSHOT",
    publishTo := Some(sharedResolver),
    crossVersion := Disabled(),
    publishMavenStyle := (sharedResolver match {
      case repo: PatternsBasedRepository => repo.patterns.isMavenCompatible
      case _: RawRepository => false // TODO - look deeper
      case _: MavenRepository => true
      case _ => false  // TODO - Handle chain repository?
    })
  )

lazy val dependent = project
  .settings(localCache)
  .settings(
    // Ignore the inter-project resolver, so we force to look remotely.
    resolvers += sharedResolver,
    fullResolvers := fullResolvers.value.filterNot(_==projectResolver.value),
    libraryDependencies += "com.badexample" % "badexample" % "1.0-SNAPSHOT"
  )
