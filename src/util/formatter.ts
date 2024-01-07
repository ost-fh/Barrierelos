export function formatScore(score: number | undefined): string {
  if (score == null) {
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
  if (score >= 50) {
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

export function formatViolationRatio(n: number, digits: number): string {
  const decimalFactor = Math.pow(10, digits);
  n = parseFloat((n * decimalFactor).toFixed(11));
  return String(Math.round(n) / decimalFactor);
}

export function compareStrings(a: string, b: string): number {
  return a.localeCompare(b);
}
