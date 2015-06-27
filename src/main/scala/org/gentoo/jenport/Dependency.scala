package org.gentoo.jenport

import org.eclipse.aether.graph.{DependencyNode, DependencyVisitor}

class JenportDependencyVisitor extends DependencyVisitor {
  def visitEnter(dn: DependencyNode): Boolean = ???
  def visitLeave(dn: DependencyNode): Boolean = ???
}
