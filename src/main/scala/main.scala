import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import cats.implicits._

import adapters.{InMemoryAccountRepository, InMemoryAccountingPeriodRepository, InMemoryConsumptionTaxRepository}
import types.{AccountCode, AccountName, AccountingPeriodId, ConsumptionTaxId, CreateAccountRequest}
import validation.implicits._

object main extends App {
  val createAccountUseCase = new CreateAccountUseCaseInteractor(
    InMemoryAccountingPeriodRepository,
    InMemoryAccountRepository,
    InMemoryConsumptionTaxRepository
  )

  val response = createAccountUseCase.execute(
    CreateAccountRequest(
      AccountingPeriodId(1).unvalidated,
      AccountCode("3").unvalidated,
      AccountName("account3"),
      ConsumptionTaxId(1).unvalidated
    )
  )

  Await.result(response, Duration.Inf)
  println(response) // should be success

  val response2 = createAccountUseCase.execute(
    CreateAccountRequest(
      AccountingPeriodId(9).unvalidated, // non-existent
      AccountCode("3").unvalidated,
      AccountName("account3"),
      ConsumptionTaxId(1).unvalidated
    )
  )

  Await.result(response2, Duration.Inf)
  println(response2) // should be failure

  val response3 = createAccountUseCase.execute(
    CreateAccountRequest(
      AccountingPeriodId(1).unvalidated,
      AccountCode("1").unvalidated, // duplicated
      AccountName("account3"),
      ConsumptionTaxId(1).unvalidated
    )
  )

  Await.result(response3, Duration.Inf)
  println(response3) // should be failure
}
