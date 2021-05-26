import cats.Applicative
import cats.implicits._

object unvalidated {
  sealed trait Unvalidated[+A]

  case class UnvalidatedImpl[+A](a: A) extends Unvalidated[A]

  object Unvalidated {
    def map[A, B](f: A => B)(fa: Unvalidated[A]): Unvalidated[B] =
      fa match {
        case UnvalidatedImpl(a) => UnvalidatedImpl(f(a))
      }

    def validate[E, A, B](f: A => Either[E, B])(fa: Unvalidated[A]): Either[E, B] =
      fa match {
        case UnvalidatedImpl(a) => f(a)
      }

    def traverse[F[_]: Applicative, A, B](f: A => F[B])(fa: Unvalidated[A]): F[Unvalidated[B]] =
      fa match {
        case UnvalidatedImpl(a) => f(a).map(UnvalidatedImpl(_))
      }
  }

  object implicits {
    implicit class UnvalidatedIdOps[A](a: A) {
      def unvalidated: Unvalidated[A] = UnvalidatedImpl(a)
    }

    implicit class UnvalidatedOps[A](fa: Unvalidated[A]) {
      def map[B](f: A => B): Unvalidated[B] = Unvalidated.map(f)(fa)

      def validate[E, B](f: A => Either[E, B]): Either[E, B] = Unvalidated.validate(f)(fa)

      def traverse[F[_]: Applicative, B](f: A => F[B]): F[Unvalidated[B]] = Unvalidated.traverse(f)(fa)
    }
  }
}
