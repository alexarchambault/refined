package eu.timepit.refined
package internal

import shapeless.tag.@@

import scala.reflect.macros.blackbox

/**
 * Helper class that allows the type `T` to be inferred from calls like
 * `[[refineLit]][P](t)`.
 */
final class RefineLit[P] {
  def apply[T](t: T)(implicit p: Predicate[P, T]): T @@ P = macro RefineLit.macroImpl[P, T]
}

object RefineLit {
  def macroImpl[P: c.WeakTypeTag, T: c.WeakTypeTag](c: blackbox.Context)(t: c.Expr[T])(p: c.Expr[Predicate[P, T]]): c.Expr[T @@ P] = {
    import c.universe._

    def litValue: T = t.tree match {
      case Literal(Constant(value)) => value.asInstanceOf[T]
      case _ => c.abort(c.enclosingPosition, "refineLit only supports literals")
    }

    val predicate: Predicate[P, T] = MacroUtils.eval(c)(p)
    predicate match {
      case p0: Predicate0[P, T] =>
        if (p0.isValid)
          c.Expr(q"$t.asInstanceOf[${weakTypeOf[T @@ P]}]")
        else
          c.abort(c.enclosingPosition, "")

      case p1: Predicate1[P, T] =>
        p1.validate(litValue) match {
          case None => c.Expr(q"$t.asInstanceOf[${weakTypeOf[T @@ P]}]")
          case Some(msg) => c.abort(c.enclosingPosition, msg)
        }
    }

  }
}
