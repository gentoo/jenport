# jenport
jenport is in development.  The primary goal is to generate and possibly bump ebuilds for
some Java Virtual Machine build systems.  Some popular build systems that are difficult
to deal with for Gentoo packaging include:

* Maven - popular for Java programming language projects
* sbt - Scala Build Tool - popular for Scala programming language projects
* leiningen - popular for Clojure programming language projects

Initially maven is targetted as due to its popularity, it causes the most trouble.

## maven overlay followed by 2011 Google Summer of Code Gentoo Maven Integration

http://permalink.gmane.org/gmane.linux.gentoo.java/2191
Gentoo Maven Integration - Final Report

It references the "Developer and User guide for Maven in Gentoo" which I can
not find.

There are 2 patches in the file:
https://google-summer-of-code-2011-gentoo.googlecode.com/files/Kasun_Gajasinghe.tar.gz

One of which looks like a patch from an earlier copy of the java overlay with the
diffs to the gsoc-maven-overlay.

## Jenport development

jenport is written in the Scala programming language.

jenport uses the Aether library to download maven pom.xml (Project Object Model) files
from maven central, and to calculate the dependencies.

http://www.eclipse.org/aether/

Other than the detailed information about the dependencies, Aether only provides very
limited information about other fields in the pom.  Aether will need to use Maven APIs
to read the downloaded pom.xml file to obtain information about other fields that are
not covered by the Aether API.

Twirl templates from the Play framework are used to generate the ebuild and metadata:

https://github.com/gentoo/jenport/blob/master/src/main/twirl/MavenEbuild.scala.txt

Line 2 references Scala classes:

https://github.com/gentoo/jenport/blob/master/src/main/scala/org/gentoo/jenport/G.scala
https://github.com/gentoo/jenport/blob/master/src/main/scala/org/gentoo/jenport/E.scala
https://github.com/gentoo/jenport/blob/master/src/main/scala/org/gentoo/jenport/D.scala

Which are filled in (currently with hard coded junk) and passed to the twirl
template txt.MavenBuild(g, e, ds) in Jenport.scala.  The ebuild Twirl template
is mostly blank.  The eclass has not yet been written.

https://www.playframework.com/documentation/2.4.x/ScalaTemplates

## Maven ebuild sketch

A sketch of an ebuild that uses maven in the build is here:

http://dev.gentoo.org/~gienah/proof-of-concept/dev-java/aether/aether-1.0.2.20150114.ebuild

It is just a sketch for discussion.  It is broken in multiple ways, including:

* Uses maven
* DEPEND missing
* Uses hard coded specific version dependencies instead of a range
* "${WORKDIR}"/m2/repository needs to be populated somehow
* It uses the currently select jdk, instead of the jdk specified in DEPEND
* Does not use any java eclass

### Uses maven

Although this ebuild demonstrates that for some simple projects, maven might
actually be able to build the project, the earlier java-maven-2.eclass also
supported the dual strategy of either using maven or using ant to build the
project to workaround broken or fragile maven builds.

So jenport may need to help support this dual maven or ant strategy.

### DEPEND missing

The DEPEND is missing all the Java dependencies, which are instead tar'd up in
http://dev.gentoo.org/~gienah/snapshots/${P}-maven-deps.tar.bz2

The plan is for jenport to generate the dependencies from the pom which are then
included in DEPEND, something like:

```bash
declare -a MAVEN_DEPS=(		# GroupId	ArtifactId	Version
  "dev-java/bndlib:1.50.0"	# biz.aQute	bndlib		1.50.0	
)

DEPEND="${MAVEN_DEPS[@]}"
```

### Uses hard coded specific version dependencies instead of a range

Another broken feature of the Java eco-system.  Lots of projects use
hard coded dependency version numbers.  The maven documentation and
website does not mention "semantic versioning" anywhere, so there is
no guidelines to avoid chaos:

https://maven.apache.org/guides/mini/guide-naming-conventions.html

"**version** if you distribute it then you can choose any typical version
with numbers and dots (1.0, 1.1, 1.0.1, ...). Don't use dates as they
are usually associated with SNAPSHOT (nightly) builds. If it's a third
party artifact, you have to use their version number whatever it is,
and as strange as it can look.

eg. 2.0, 2.0.1, 1.3.1"

leading to:

* Since using hard coded version numbers for dependencies are so common, and
with no guidelines to say that semantic versioning should be used, it seems
to encourage the broken thinking that breaking the ABI with a minor version
bump is ok.

* With the lack of semantic versioning, and the use of hard coded version numbers
for dependencies, it is difficult to fix security bugs in dependencies

* An explosion in the number of ebuilds and installed packages.  Lots of slots
for the different ABIs.

* It may be difficult or impossible to find a consistent set of packages.

http://avandeursen.com/2014/10/09/semantic-versioning-in-maven-central-breaking-changes/

An idea is that jenport or a Python script could patch the pom.xml to loosen
the dependencies.  However since the type system in Java is not very strong,
it may compile ok, but then fail at runtime.  If upstream provide a testsuite
that works then we could run the testsuite.

We could use a tool such as japicmp to compare 2 versions of a jar to see if
they are ABI compatible or not.  This could be used to determine which slot to
place a new ebuild in.

https://github.com/siom79/japicmp

### "${WORKDIR}"/m2/repository needs to be populated somehow

Some ideas:

* If we install stuff into a local Maven repository, say /usr/share/m2/repository, then jenport
could generate metadata that the eclass (which does not yet exist, to be written) could use
to copy the files, or symlink them somehow, to "${WORKDIR}"/m2/repository

* Or if we do not have a local Maven repository, then maybe jenport could somehow figure out how
to generate metadata to copy the files of the dependencies to "${WORKDIR}"/m2/repository

### It uses the currently select jdk, instead of the jdk specified in DEPEND

The earlier approach was to rewrite the pom.xml in

https://gitweb.gentoo.org/proj/javatoolkit.git/tree/src/py/javatoolkit/maven/MavenPom.py

Patched with Kasun_Gajasinghe-javatoolkit.patch from 

https://google-summer-of-code-2011-gentoo.googlecode.com/files/Kasun_Gajasinghe.tar.gz

and this was run from the eclass/java-maven-2.eclass that is patched in

Kasun_Gajasinghe-gsoc-maven-overlay.patch

### Does not use any java eclass

As this is also to be done.

## Contributing

Please add your name (in 2 places, it may require some reformatting to
break the lines) to LICENSE.

Please join us in channel `#gentoo-java` on IRC freenode.
