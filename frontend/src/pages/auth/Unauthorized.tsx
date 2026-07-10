import { useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";
import { getRoleDestination } from "../../router/Router";

export default function Unauthorized() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function goHome() {
    if (user) navigate(getRoleDestination(user.role), { replace: true });
    else navigate("/", { replace: true });
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", height: "100vh", gap: "1rem", fontFamily: "var(--font-body)", padding: "2rem", textAlign: "center" }}>
      <span style={{ fontSize: "2.5rem", fontWeight: 800, color: "var(--neutral-900)", fontFamily: "var(--font-display)" }}>Access Denied</span>
      <p style={{ color: "var(--neutral-600)", maxWidth: "360px", lineHeight: 1.6 }}>You do not have permission to view this page.</p>
      <div style={{ display: "flex", gap: "1rem", marginTop: "0.5rem" }}>
        <button onClick={goHome} style={{ background: "var(--green-700)", color: "#fff", border: "none", borderRadius: "6px", padding: "0.625rem 1.25rem", fontWeight: 600, fontSize: "0.875rem", cursor: "pointer" }}>
          Go to my dashboard
        </button>
        <button onClick={() => logout()} style={{ background: "none", color: "var(--neutral-600)", border: "1px solid var(--neutral-200)", borderRadius: "6px", padding: "0.625rem 1.25rem", fontWeight: 600, fontSize: "0.875rem", cursor: "pointer" }}>
          Sign out
        </button>
      </div>
    </div>
  );
}