package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.dto.CustomerDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    // TESTS

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val customer = builderCustomerDto()
        val customerSaved: Customer = customerRepository.save(customer.toEntity())

        val creditDto: CreditDto = builderCreditDto(customerId = customerSaved.id!!)
        val creditDtoAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditDtoAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(creditDto.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(creditDto.numberOfInstallments))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.status")
                    .value(me.dio.credit.application.system.enummeration.Status.IN_PROGRESS.name)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `findAllByCustomerId returns 200 status`() {
        //given
        val customer = builderCustomerDto()
        val customerSaved: Customer = customerRepository.save(customer.toEntity())

        val creditDto1: CreditDto = builderCreditDto(customerId = customerSaved.id!!)
        val creditDto2: CreditDto = builderCreditDto(customerId = customerSaved.id!!)
        val creditDto3: CreditDto = builderCreditDto(customerId = customerSaved.id!!)
        creditRepository.saveAll(listOf(creditDto1.toEntity(), creditDto2.toEntity(), creditDto3.toEntity()))

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get(URL)
                .queryParam("customerId", "${customerSaved.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.[*].creditCode").isNotEmpty)
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.[*].creditValue").isNotEmpty
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.[*].numberOfInstallments").isNotEmpty
            )
            .andDo(MockMvcResultHandlers.print())
    }


    @Test
    fun `findAllByCreditCode returns 200 status`() {
        // given
        val customer = builderCustomerDto()
        val customerSaved: Customer = customerRepository.save(customer.toEntity())

        val creditDto1: CreditDto = builderCreditDto(customerId = customerSaved.id!!)

        val credit1: Credit = creditRepository.save(creditDto1.toEntity())

        // when
        // then
        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/${credit1.creditCode}")
                .queryParam("customerId", "${customerSaved.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(creditDto1.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(creditDto1.numberOfInstallments))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.status")
                    .value(me.dio.credit.application.system.enummeration.Status.IN_PROGRESS.name)
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andDo(MockMvcResultHandlers.print())
    }


    // BUILDER

    private fun builderCreditDto(
        customerId: Long = 1L,
        numberOfInstallments: Int = 1,
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.MAX,

        ): CreditDto {
        return CreditDto(
            customerId = customerId,
            numberOfInstallments = numberOfInstallments,
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment
        )
    }

    private fun builderCustomerDto(
        firstName: String = "Vinicius",
        lastName: String = "Bohrer",
        cpf: String = "28475934625",
        email: String = "vbsanttos@email.com",
        income: BigDecimal = BigDecimal.valueOf(5000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua Floriano Peixoto",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )
}