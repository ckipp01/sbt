// tests that errors are properly propagated for dependsOn, map, and flatMap

import sbt.TupleSyntax.*
lazy val root = (project in file(".")).settings(
  a := (baseDirectory mapN (b => if ((b / "succeed").exists) () else sys.error("fail"))).value,
  // deprecated?
  // b := (a.task(at => nop dependsOn(at))).value,
  c := (a mapN { _ => () }).value,
  d := (a flatMapN { _ => task { () } }).value
)
lazy val a = taskKey[Unit]("")
lazy val b = taskKey[Unit]("")
lazy val c = taskKey[Unit]("")
lazy val d = taskKey[Unit]("")

lazy val input = (project in file("input")).settings(
  f := (if (Def.spaceDelimited().parsed.head == "succeed") () else sys.error("fail")),
  j := sys.error("j"),
  g := f.dependsOnTask(j).evaluated,
  h := (f map { _ => IO.touch(file("h")) }).evaluated
)
lazy val f = inputKey[Unit]("")
lazy val g = inputKey[Unit]("")
lazy val h = inputKey[Unit]("")
lazy val j = taskKey[Unit]("")
