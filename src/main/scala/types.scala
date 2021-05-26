import unvalidated.Unvalidated

object types {
  case class Account(
      id: AccountId,
      periodId: AccountingPeriodId,
      code: AccountCode,
      name: AccountName,
      consumptionTaxId: ConsumptionTaxId
  )

  case class AccountingPeriod(
      id: AccountingPeriodId
  )

  case class AccountId(value: Long)

  case class AccountingPeriodId(value: Long)

  case class AccountCode(value: String)

  case class AccountName(value: String)

  case class ConsumptionTax(
      id: ConsumptionTaxId
  )

  case class ConsumptionTaxId(value: Long)

  case class CreateAccountRequest(
      periodId: Unvalidated[AccountingPeriodId],
      code: Unvalidated[AccountCode],
      name: AccountName,
      consumptionTaxId: Unvalidated[ConsumptionTaxId]
  )

  case class NewAccount(
      periodId: AccountingPeriodId,
      code: AccountCode,
      name: AccountName,
      consumptionTaxId: ConsumptionTaxId
  )

  sealed trait CreateAccountError

  case class NonExistentAccountingPeriod(id: AccountingPeriodId) extends CreateAccountError

  case class DuplicateAccountCode(value: AccountCode) extends CreateAccountError

  case class NonExistentConsumptionTax(id: ConsumptionTaxId) extends CreateAccountError

  trait AccountingPeriodRepository[F[_]] {
    def find(id: AccountingPeriodId): F[Option[AccountingPeriod]]
  }

  trait AccountRepository[F[_]] {
    def list(id: AccountingPeriodId): F[Seq[Account]]

    def store(newAccount: NewAccount): F[Account]
  }

  trait ConsumptionTaxRepository[F[_]] {
    def list: F[Seq[ConsumptionTax]]
  }
}
