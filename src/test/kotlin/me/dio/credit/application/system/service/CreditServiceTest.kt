package me.dio.credit.application.system.service

import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enummeration.Status
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class CreditServiceTest {
    @Mock
    lateinit var creditRepository: CreditRepository

    @Mock
    lateinit var customerService: CustomerService

    @InjectMocks
    lateinit var creditService: CreditService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should save credit`() {
        // given
        val customerId = 123L
        val fakeCustomer = buildCustomer(id = customerId)
        val fakeCredit = buildCredit(customerId = customerId)
        Mockito.`when`(customerService.findById(customerId)).thenReturn(fakeCustomer)
        Mockito.`when`(creditRepository.save(Mockito.any())).thenReturn(fakeCredit)

        // when
        val result = creditService.save(fakeCredit)

        // then
        Assertions.assertEquals(fakeCredit, result)
        Mockito.verify(customerService, Mockito.times(1)).findById(customerId)
        Mockito.verify(creditRepository, Mockito.times(1)).save(fakeCredit)
    }

    @Test
    fun `should find all credits by customer id`() {
        // given
        val customerId = 123L
        val credits = listOf(
            buildCredit(customerId = customerId),
            buildCredit(customerId = customerId),
            buildCredit(customerId = customerId)
        )
        Mockito.`when`(creditRepository.findAllByCustomerId(customerId)).thenReturn(credits)

        // when
        val result = creditService.findAllByCustomer(customerId)

        // then
        Assertions.assertEquals(credits, result)
        Mockito.verify(creditRepository, Mockito.times(1)).findAllByCustomerId(customerId)
    }

    @Test
    fun `should find credit by customer id and credit code`() {
        // given
        val customerId = 123L
        val creditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(creditCode = creditCode, customerId = customerId)
        Mockito.`when`(creditRepository.findByCreditCode(creditCode)).thenReturn(fakeCredit)

        // when
        val result = creditService.findByCreditCode(customerId, creditCode)

        // then
        Assertions.assertEquals(fakeCredit, result)
        Mockito.verify(creditRepository, Mockito.times(1)).findByCreditCode(creditCode)
    }

    @Test
    fun `should throw BusinessException when credit code not found`() {
        // given
        val customerId = 123L
        val creditCode = UUID.randomUUID()
        Mockito.`when`(creditRepository.findByCreditCode(creditCode)).thenReturn(null)

        // when/then
        Assertions.assertThrows(BusinessException::class.java) {
            creditService.findByCreditCode(customerId, creditCode)
        }
        Mockito.verify(creditRepository, Mockito.times(1)).findByCreditCode(creditCode)
    }

    @Test
    fun `should throw IllegalArgumentException when customer id does not match credit customer id`() {
        // given
        val customerId = 123L
        val creditCode = UUID.randomUUID()
        val credit = buildCredit(creditCode = creditCode, customerId = 456L)
        Mockito.`when`(creditRepository.findByCreditCode(creditCode)).thenReturn(credit)

        // when/then
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            creditService.findByCreditCode(customerId, creditCode)
        }
        Mockito.verify(creditRepository, Mockito.times(1)).findByCreditCode(creditCode)
    }

    private fun buildCustomer(
        firstName: String = "Vinícius",
        lastName: String = "Bohrer",
        cpf: String = "28475934625",
        email: String = "vbsanttos@gmail.com",
        password: String = "12345",
        zipCode: String = "54321",
        street: String = "Rua Floriando Peixoto",
        income: BigDecimal = BigDecimal.valueOf(5000.0),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        income = income,
        id = id
    )

    private fun buildCredit(
        id: Long = 1L,
        creditCode: UUID = UUID.randomUUID(),
        customerId: Long = 1L,
        dayFirstInstallment: LocalDate = LocalDate.EPOCH,
        numberOfInstallments: Int = 1,
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
    ): Credit {
        return Credit(
            id = id,
            creditCode = creditCode,
            customer = buildCustomer(id = customerId),
            dayFirstInstallment = dayFirstInstallment,
            status = Status.IN_PROGRESS,
            numberOfInstallments = numberOfInstallments,
            creditValue = creditValue
        )
    }

}
