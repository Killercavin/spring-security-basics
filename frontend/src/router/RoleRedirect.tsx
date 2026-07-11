import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { getRoleDestination } from "./constants";

export default function RoleRedirect() {
  const { user } = useAuth();
  return <Navigate to={getRoleDestination(user?.role)} replace />;
}