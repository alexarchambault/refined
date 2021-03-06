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

  /** Predicate that checks if a `String` is a valid UUID. */
  trait Uuid

  /** Predicate that checks if a `String` is valid XML. */
  trait Xml

  /** Predicate that checks if a `String` is a valid XPath expression. */
  trait XPath
}

private[refined] trait StringPredicates {

  implicit def endsWithPredicate[R <: String](implicit wr: Witness.Aux[R]): Predicate[EndsWith[R], String] =
    Predicate.instance(_.endsWith(wr.value), t => s""""$t".endsWith("${wr.value}")""")

  implicit def matchesRegexPredicate[R <: String](implicit wr: Witness.Aux[R]): Predicate[MatchesRegex[R], String] =
    Predicate.instance(_.matches(wr.value), t => s""""$t".matches("${wr.value}")""")

  implicit def regexPredicate: Predicate[Regex, String] =
    Predicate.fromPartial(new scala.util.matching.Regex(_), "Regex")

  implicit def startsWithPredicate[R <: String](implicit wr: Witness.Aux[R]): Predicate[StartsWith[R], String] =
    Predicate.instance(_.startsWith(wr.value), t => s""""$t".startsWith("${wr.value}")""")

  implicit def uriPredicate: Predicate[Uri, String] =
    Predicate.fromPartial(new java.net.URI(_), "Uri")

  implicit def urlPredicate: Predicate[Url, String] =
    Predicate.fromPartial(new java.net.URL(_), "Url")

  implicit def uuidPredicate: Predicate[Uuid, String] =
    Predicate.fromPartial(java.util.UUID.fromString, "Uuid")

  implicit def xmlPredicate: Predicate[Xml, String] =
    Predicate.fromPartial(scala.xml.XML.loadString, "Xml")

  implicit def xpathPredicate: Predicate[XPath, String] =
    Predicate.fromPartial(javax.xml.xpath.XPathFactory.newInstance().newXPath().compile, "XPath")
}

private[refined] trait StringInferenceRules {

  implicit def endsWithInference[A <: String, B <: String](implicit wa: Witness.Aux[A], wb: Witness.Aux[B]): EndsWith[A] ==> EndsWith[B] =
    InferenceRule(wa.value.endsWith(wb.value), s"endsWithInference(${wa.value}, ${wb.value})")

  implicit def startsWithInference[A <: String, B <: String](implicit wa: Witness.Aux[A], wb: Witness.Aux[B]): StartsWith[A] ==> StartsWith[B] =
    InferenceRule(wa.value.startsWith(wb.value), s"startsWithInference(${wa.value}, ${wb.value})")
}
