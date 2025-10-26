package pl.preclaw.florafocus.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Dummy entity to satisfy Room's requirement of at least one entity
 *
 * DELETE THIS when you add your first real entity (e.g., UserPlantEntity)
 */
@Entity(tableName = "dummy")
internal data class DummyEntity(
    @PrimaryKey
    val id: Int = 1
)