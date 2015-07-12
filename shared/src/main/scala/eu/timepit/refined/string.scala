package eu.timepit.refined

import eu.timepit.refined.InferenceRule.==>
import eu.timepit.refined.string._
import shapeless.Witness

object string extends StringPredicates with StringInferenceRules {

  /** Predicate that checks if a `String` ends with the suffix `S`. */
  trait EndsWith[S]

  /** Predicate that checks if a `String` matches the regular expression `R`. */
  trait MatchesRegex[R]

  /** Predicate that checks if a `String` is a valid regular expression. */
  trait Regex

  /** Predicate that checks if a `String` starts with the prefix `S`. */
  trait StartsWith[S]

  /** Predicate that checks if a `String` is a valid URI. */
  trait Uri

  /** Predicate that checks if a `String` is a valid URL. */
  trait Url
}

private[refined] trait StringPredicates {

  implicit def endsWithPredicate[R <: String](implicit wr: Witness.Aux[R]): Predicate1[EndsWith[R], String] =
    Predicate1.instance(_.endsWith(wr.value), t => s""""$t".endsWith("${wr.value}")""")

  implicit def matchesRegexPredicate[R <: String](implicit wr: Witness.Aux[R]): Predicate1[MatchesRegex[R], String] =
    Predicate1.instance(_.matches(wr.value), t => s""""$t".matches("${wr.value}")""")

  implicit val regexPredicate: Predicate1[Regex, String] =
    Predicate1.fromPartial(_.r, t => s"""isValidRegex("$t")""")

  implicit def startsWithPredicate[R <: String](implicit wr: Witness.Aux[R]): Predicate1[StartsWith[R], String] =
    Predicate1.instance(_.startsWith(wr.value), t => s""""$t".startsWith("${wr.value}")""")

  implicit val uriPredicate: Predicate1[Uri, String] =
    Predicate1.fromPartial(new java.net.URI(_), t => s"""isValidUri("$t")""")

  implicit val urlPredicate: Predicate1[Url, String] =
    Predicate1.fromPartial(new java.net.URL(_), t => s"""isValidUrl("$t")""")
}

private[refined] trait StringInferenceRules {

  implicit def endsWithInference[A <: String, B <: String](implicit wa: Witness.Aux[A], wb: Witness.Aux[B]): EndsWith[A] ==> EndsWith[B] =
    InferenceRule(wa.value.endsWith(wb.value), s"endsWithInference(${wa.value}, ${wb.value})")

  implicit def startsWithInference[A <: String, B <: String](implicit wa: Witness.Aux[A], wb: Witness.Aux[B]): StartsWith[A] ==> StartsWith[B] =
    InferenceRule(wa.value.startsWith(wb.value), s"startsWithInference(${wa.value}, ${wb.value})")
}
