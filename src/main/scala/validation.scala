import scala.language.implicitConversions

import cats.Applicative
import cats.implicits._

object validation {
  sealed trait Unvalidated[+A]

  private case class UnvalidatedImpl[+A](a: A) extends Unvalidated[A]

  sealed trait Validated[+A]

  private case class ValidatedImpl[+A](a: A) extends Validated[A]

  object Unvalidated {
    def map[A, B](f: A => B)(fa: Unvalidated[A]): Unvalidated[B] =
      fa match {
        case UnvalidatedImpl(a) => UnvalidatedImpl(f(a))
      }

    def validate[E, A, B](f: A => Either[E, B])(fa: Unvalidated[A]): Either[E, Validated[B]] =
      fa match {
        case UnvalidatedImpl(a) => f(a).map(ValidatedImpl(_))
      }

    def traverse[F[_]: Applicative, A, B](f: A => F[B])(fa: Unvalidated[A]): F[Unvalidated[B]] =
      fa match {
        case UnvalidatedImpl(a) => f(a).map(UnvalidatedImpl(_))
      }
  }

  object Validated {
    def map[A, B](f: A => B)(fa: Validated[A]): Validated[B] =
      fa match {
        case ValidatedImpl(a) => ValidatedImpl(f(a))
      }
  }

  object implicits {
    implicit class UnvalidatedIdOps[A](a: A) {
      def unvalidated: Unvalidated[A] = UnvalidatedImpl(a)
    }

    implicit class UnvalidatedOps[A](fa: Unvalidated[A]) {
      def map[B](f: A => B): Unvalidated[B] = Unvalidated.map(f)(fa)

      def validate[E, B](f: A => Either[E, B]): Either[E, Validated[B]] = Unvalidated.validate(f)(fa)

      def traverse[F[_]: Applicative, B](f: A => F[B]): F[Unvalidated[B]] = Unvalidated.traverse(f)(fa)
    }

    implicit def fromValidated[A](fa: Validated[A]): A =
      fa match {
        case ValidatedImpl(a) => a
      }

    implicit class ValidatedOps[A](fa: Validated[A]) {
      def map[B](f: A => B): Validated[B] = Validated.map(f)(fa)
    }
  }
}
