package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Успешный перевод между счетами")
    request {
        method 'POST'
        url '/api/account/transfer'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 1,
                fromExchange: [
                        currency: "USD",
                        value: 100.00
                ],
                toUserId: 2,
                toExchange: [
                        currency: "USD",
                        value: 100.00
                ],
                value: 100.00
        )
    }
    response {
        status 204
        body('')
    }
}