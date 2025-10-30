package pl.preclaw.florafocus.domain.usecase.plant

import kotlinx.coroutines.flow.Flow
import pl.preclaw.florafocus.domain.model.Plant
import pl.preclaw.florafocus.domain.repository.PlantRepository
import javax.inject.Inject

class GetAllPlantsUseCase @Inject constructor(
    private val plantRepository: PlantRepository
) {
    operator fun invoke(): Flow<List<Plant>> {
        return plantRepository.getAllPlants()
    }
}