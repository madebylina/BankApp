package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Получить счет пользователя по userId и валюте")
    request {
        method 'GET'
        urlPath('/api/account/value/1/USD')
        headers {
            accept(applicationJson())
        }
    }
    response {
        status 200
        body(
                id: 100,
                userId: 1,
                currency: "USD",
                value: 1500.75,
                exists:true
        )
        headers {
            contentType(applicationJson())
        }
    }
}
