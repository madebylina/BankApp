package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получить курс валюты"
    request {
        method GET()
        urlPath('/api/exchange/USD')
        headers {
            header('Authorization', regex('Bearer .*'))
        }
    }
    response {
        status 200
        body(75.0)
        headers {
            contentType(applicationJson())
        }
    }
}
