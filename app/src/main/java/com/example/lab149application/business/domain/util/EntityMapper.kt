package com.example.lab149application.business.domain.util

interface EntityMapper <Entity, DomainModel> {

    fun mapFromEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity
}
