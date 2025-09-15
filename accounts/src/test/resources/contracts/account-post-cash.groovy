package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Успешная операция пополнения или снятия средств")
    request {
        method 'POST'
        url '/api/account/cash'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 2,
                currency: "USD",
                value: 100.00,
                cashAction: "PUT"
        )
    }
    response {
        status 204
        body('')
    }
}