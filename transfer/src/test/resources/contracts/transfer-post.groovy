package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Перевод денег")
    request {
        method 'POST'
        url '/api/transfer'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 123,
                fromExchange: [
                        currency: 'USD',
                        value: 0.0 // Значение обычно заменяется в контроллере
                ],
                toExchange: [
                        currency: 'RUB',
                        value: 0.0
                ]
        )
    }
    response {
        status 200
    }
}