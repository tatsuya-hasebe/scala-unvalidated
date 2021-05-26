import cats.Monad
import cats.data.EitherT
import cats.implicits._

import unvalidated.implicits._
import types.{
  Account,
  AccountRepository,
  AccountingPeriodRepository,
  ConsumptionTaxRepository,
  CreateAccountError,
  CreateAccountRequest,
  NewAccount,
  NonExistentAccountingPeriod
}
import functions.{validateAccountCode, validateConsumptionTaxId}

class CreateAccountUseCaseInteractor[F[_]: Monad](
    accountingPeriodRepository: AccountingPeriodRepository[F],
    accountRepository: AccountRepository[F],
    consumptionTaxRepository: ConsumptionTaxRepository[F]
) {
  def execute(request: CreateAccountRequest): F[Either[CreateAccountError, Account]] =
    (for {
      period <- fromEitherF(
                 request.periodId
                   .traverse(
                     periodId =>
                       accountingPeriodRepository
                         .find(periodId)
                         .map(_.toRight(NonExistentAccountingPeriod(periodId): CreateAccountError))
                   )
                   .map(_.validate(identity))
               )

      data <- fromF(
               (
                 accountRepository.list(period.id),
                 consumptionTaxRepository.list
               ).mapN((_, _))
             )

      (accounts, consumptionTaxes) = data

      validatedCode <- fromEither(
                        validateAccountCode(accounts)(request.code)
                      )

      validatedConsumptionTaxId <- fromEither(
                                    validateConsumptionTaxId(consumptionTaxes)(request.consumptionTaxId)
                                  )

      newAccount = NewAccount(period.id, validatedCode, request.name, validatedConsumptionTaxId)

      account <- fromF(
                  accountRepository.store(newAccount)
                )
    } yield account).value

  def fromEitherF[A](
      fa: F[Either[CreateAccountError, A]]
  ): EitherT[F, CreateAccountError, A] = EitherT(fa)

  def fromF[A](
      fa: F[A]
  ): EitherT[F, CreateAccountError, A] = EitherT.right(fa)

  def fromEither[A](
      fa: Either[CreateAccountError, A]
  ): EitherT[F, CreateAccountError, A] = EitherT.fromEither(fa)
}
