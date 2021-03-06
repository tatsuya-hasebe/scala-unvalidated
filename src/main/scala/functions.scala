import validation.{Unvalidated, Validated}
import validation.implicits._
import types.{Account, AccountCode, ConsumptionTax, ConsumptionTaxId, DuplicateAccountCode, NonExistentConsumptionTax}

object functions {
  def validateAccountCode(
      accounts: Seq[Account]
  )(
      unvalidatedCode: Unvalidated[AccountCode]
  ): Either[DuplicateAccountCode, Validated[AccountCode]] =
    unvalidatedCode.validate { code =>
      Either.cond(
        !accounts.exists(_.code == code),
        code,
        DuplicateAccountCode(code)
      )
    }

  def validateConsumptionTaxId(
      consumptionTaxes: Seq[ConsumptionTax]
  )(
      unvalidatedId: Unvalidated[ConsumptionTaxId]
  ): Either[NonExistentConsumptionTax, Validated[ConsumptionTaxId]] =
    unvalidatedId.validate { id =>
      Either.cond(
        consumptionTaxes.exists(_.id == id),
        id,
        NonExistentConsumptionTax(id)
      )
    }
}
