export function formatScore(score: number | undefined): string {
  if (score === undefined) {
    return "N/A";
  }
  return Math.round(score).toString();
}

export function getScoreClass(score: number | undefined): string {
  if (score === undefined) {
    return "";
  }
  if (score >= 80) {
    return "good"
  }
  if (score >= 40) {
    return "medium"
  }
  return "bad"
}

export function getScoreColor(score: number | undefined): string {
  const rating = getScoreClass(score);
  switch (rating) {
    case "good":
      return "#27ae60";
    case "medium":
      return "#f1c40f";
    case "bad":
      return "#e74c3c";
    default:
      return "#2980b9";
  }
}
