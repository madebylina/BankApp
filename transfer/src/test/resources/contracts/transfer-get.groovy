package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Получить список курсов валют")
    request {
        method 'GET'
        url '/api/transfer'
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body([
                [currency: 'RUB', value: 75.0],
                [currency: 'USD', value: 1.0],
                [currency: 'CNY', value: 11.0]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}