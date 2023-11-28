package ch.barrierelos.backend.service

import ch.barrierelos.backend.model.scanner.AnalysisResult
import ch.barrierelos.backend.repository.ScoringRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
public class ScoringService {
    @Autowired
    private lateinit var scoringRepository: ScoringRepository

    /**
     * Gets called after the analysis result from the scanner is stored in the database.
     */
    public fun onReceiveResult(analysisResult: AnalysisResult) {
        val score = scoringRepository.calculateScore(analysisResult.id)
    }
}
