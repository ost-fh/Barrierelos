export function formatScore(score: number | undefined): string {
  if (score === undefined) {
    return "N/A";
  }
  return Math.round(score).toString();
}
