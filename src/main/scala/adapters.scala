import scala.concurrent.Future

import validation.implicits._
import types.{
  Account,
  AccountCode,
  AccountId,
  AccountName,
  AccountRepository,
  AccountingPeriod,
  AccountingPeriodId,
  AccountingPeriodRepository,
  ConsumptionTax,
  ConsumptionTaxId,
  ConsumptionTaxRepository,
  NewAccount
}

object adapters {
  object InMemoryAccountingPeriodRepository extends AccountingPeriodRepository[Future] {
    override def find(id: AccountingPeriodId): Future[Option[AccountingPeriod]] =
      if (id == AccountingPeriodId(1)) {
        Future.successful(
          Some(
            AccountingPeriod(
              AccountingPeriodId(1)
            )
          )
        )
      } else {
        Future.successful(None)
      }
  }

  object InMemoryAccountRepository extends AccountRepository[Future] {
    override def list(id: AccountingPeriodId): Future[Seq[Account]] =
      if (id == AccountingPeriodId(1)) {
        Future.successful(
          Seq(
            Account(
              AccountId(1),
              AccountingPeriodId(1),
              AccountCode("1"),
              AccountName("account1"),
              ConsumptionTaxId(1)
            ),
            Account(
              AccountId(2),
              AccountingPeriodId(2),
              AccountCode("2"),
              AccountName("account2"),
              ConsumptionTaxId(2)
            )
          )
        )
      } else {
        Future.successful(Seq.empty)
      }

    override def store(newAccount: NewAccount): Future[Account] =
      Future.successful(
        Account(
          AccountId(3),
          newAccount.periodId,
          newAccount.code,
          newAccount.name,
          newAccount.consumptionTaxId
        )
      )
  }

  object InMemoryConsumptionTaxRepository extends ConsumptionTaxRepository[Future] {
    override def list: Future[Seq[ConsumptionTax]] =
      Future.successful(
        Seq(
          ConsumptionTax(ConsumptionTaxId(1)),
          ConsumptionTax(ConsumptionTaxId(2))
        )
      )
  }
}
