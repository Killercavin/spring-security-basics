export default function NotFound() {
  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", height: "100vh", gap: "1rem", fontFamily: "var(--font-body)" }}>
      <span style={{ fontSize: "3rem", fontWeight: 800, color: "var(--neutral-900)", fontFamily: "var(--font-display)" }}>404</span>
      <p style={{ color: "var(--neutral-600)" }}>This page does not exist.</p>
      <a href="/" style={{ color: "var(--green-700)", fontWeight: 600, fontSize: "0.875rem" }}>← Back to home</a>
    </div>
  );
}