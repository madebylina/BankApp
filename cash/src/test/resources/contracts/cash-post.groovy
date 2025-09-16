package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Провести операцию cash с действием PUT")
    request {
        method 'POST'
        url '/api/cash'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 123,
                currency: "USD",
                value: 1000.0,
                cashAction: "PUT"
        )
    }
    response {
        status 200
    }
}
