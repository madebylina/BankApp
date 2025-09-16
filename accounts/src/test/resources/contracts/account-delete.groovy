package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Удалить аккаунт по ID")
    request {
        method 'DELETE'
        url '/api/account/101'
    }
    response {
        status 204
    }
}

