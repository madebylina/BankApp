package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Проверка валидации блокировщика")
    request {
        method GET()
        url '/api/validate'
        headers {
            header('Authorization', regex('Bearer .*'))
        }
    }
    response {
        status 200
        body(true)
        headers {
            contentType(applicationJson())
        }
    }
}

