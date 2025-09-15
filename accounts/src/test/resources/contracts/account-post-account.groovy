package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Создать или обновить счет")
    request {
        method 'POST'
        url '/api/account'
        headers {
            contentType(applicationJson())
        }
        body(
                id: null,
                userId: 1,
                currency: "USD",
                value: 1000.00,
                exists: true
        )
    }
    response {
        status 200
        body(
                id: 101,
                userId: 1,
                currency: "USD",
                value: 1000.00,
                exists: true
        )
        headers {
            contentType(applicationJson())
        }
    }
}