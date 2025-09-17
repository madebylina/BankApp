package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Установить курс валюты")
    request {
        method 'POST'
        url '/api/exchange'
        headers {
            contentType(applicationJson())
        }
        body(
                currency: 'USD',
                value: 74.5
        )
    }

    response {
        status 200
    }
}