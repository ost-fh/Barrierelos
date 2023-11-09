package ch.barrierelos.backend.scoring

import ch.barrierelos.backend.model.scanner.AnalysisResult
import org.springframework.stereotype.Service

@Service
public class ScoringService
{
  /**
   * Gets called after the analysis result from the scanner is stored in the database.
   */
  public fun onReceiveResult(analysisResult: AnalysisResult)
  {
    // TODO: implement
  }
}
