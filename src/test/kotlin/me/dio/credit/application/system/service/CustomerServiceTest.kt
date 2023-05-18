package me.dio.credit.application.system.service

import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CustomerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
class CustomerServiceTest {
    @Mock
    lateinit var customerRepository: CustomerRepository

    @InjectMocks
    lateinit var customerService: CustomerService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should create customer`() {
        //given
        val fakeCustomer: Customer = buildCustomer()
        Mockito.`when`(customerRepository.save(Mockito.any())).thenReturn(fakeCustomer)

        //when
        val actual: Customer = customerService.save(fakeCustomer)

        //then
        Assertions.assertNotNull(actual)
        Assertions.assertSame(actual, fakeCustomer)
        Mockito.verify(customerRepository, Mockito.times(1)).save(fakeCustomer)
    }

    @Test
    fun `should find customer by id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        Mockito.`when`(customerRepository.findById(fakeId)).thenReturn(Optional.of(fakeCustomer))

        //when
        val actual: Customer = customerService.findById(fakeId)

        //then
        Assertions.assertNotNull(actual)
        Assertions.assertTrue(actual is Customer)
        Assertions.assertSame(actual, fakeCustomer)
        Mockito.verify(customerRepository, Mockito.times(1)).findById(fakeId)
    }

    @Test
    fun `should not find customer by invalid id and throw BusinessException`() {
        //given
        val fakeId: Long = Random().nextLong()
        Mockito.`when`(customerRepository.findById(fakeId)).thenReturn(Optional.empty())

        //when
        //then
        Assertions.assertThrows(BusinessException::class.java) {
            customerService.findById(fakeId)
        }
        Mockito.verify(customerRepository, Mockito.times(1)).findById(fakeId)
    }

    @Test
    fun `should delete customer by id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        Mockito.`when`(customerRepository.findById(fakeId)).thenReturn(Optional.of(fakeCustomer))

        //when
        customerService.delete(fakeId)

        //then
        Mockito.verify(customerRepository, Mockito.times(1)).findById(fakeId)
        Mockito.verify(customerRepository, Mockito.times(1)).delete(fakeCustomer)
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
}
