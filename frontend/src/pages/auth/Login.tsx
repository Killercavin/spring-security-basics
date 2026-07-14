import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";
import { getRoleDestination } from "../../router/constants";
import { extractErrorMessage } from "../../utils/errors";

interface LocationState {
  from?: { pathname: string };
}

export default function Login() {
  const { login, isAuthenticated, user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const emailRef = useRef<HTMLInputElement>(null);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated && user) {
      const state = location.state as LocationState;
      const destination =
        state?.from?.pathname ?? getRoleDestination(user.role);
      navigate(destination, { replace: true });
    }
  }, [isAuthenticated, user, navigate, location.state]);

  // focus email on mount
  useEffect(() => {
    emailRef.current?.focus();
  }, []);

  useEffect(() => {
    document.title = "Sign In — Gatelog";
  }, []);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!email.trim() || !password) return;

    setError(null);
    setIsLoading(true);

    try {
      await login(email.trim().toLowerCase(), password);
      // navigation handled by the useEffect above once auth state updates
    } catch (err: unknown) {
      setError(extractErrorMessage(err));
      setIsLoading(false);
    }
}

return (
  <div className="grid grid-cols-[420px_1fr] min-h-screen max-[860px]:grid-cols-1">
    {/* ── Left panel ── */}
    <div className="bg-neutral-900 relative overflow-hidden max-[860px]:hidden">
      {/* subtle grid pattern */}
      <div className="absolute inset-0 bg-[image:linear-gradient(rgba(37,168,94,0.04)_1px,transparent_1px),linear-gradient(90deg,rgba(37,168,94,0.04)_1px,transparent_1px)] bg-[size:32px_32px] pointer-events-none" />

      {/* green glow orb */}
      <div className="absolute -top-[120px] -left-[80px] w-[380px] h-[380px] rounded-full bg-[radial-gradient(circle,rgba(27,127,74,0.18)_0%,transparent_70%)] pointer-events-none" />

      <div className="relative z-10 h-full flex flex-col p-10">
        <a href="/" className="font-display text-xl font-extrabold text-white tracking-[-0.03em] no-underline flex-shrink-0">
          Gate<span className="text-green-500">log</span>
        </a>

        <div className="flex-1 flex flex-col justify-center py-12">
          <p className="text-xs font-semibold tracking-[0.1em] uppercase text-green-500 mb-4">
            Visitor Management
          </p>
          <h2 className="font-display text-[clamp(1.5rem,2.5vw,1.875rem)] font-extrabold text-white leading-[1.2] tracking-[-0.03em] mb-4">
            Every visitor. Every second. Accounted for.
          </h2>
          <p className="text-sm text-white/45 leading-[1.7] max-w-[300px] mb-10">
            The front desk tool that knows who walked in, who is still here,
            and who left without signing out.
          </p>

          <div className="flex items-center gap-6">
            <div className="flex flex-col gap-[3px]">
              <span className="font-display text-2xl font-extrabold text-white leading-none">30s</span>
              <span className="text-xs text-white/35 font-medium">Avg. check-in time</span>
            </div>
            <div className="w-px h-8 bg-white/8" />
            <div className="flex flex-col gap-[3px]">
              <span className="font-display text-2xl font-extrabold text-white leading-none">3</span>
              <span className="text-xs text-white/35 font-medium">Access levels</span>
            </div>
            <div className="w-px h-8 bg-white/8" />
            <div className="flex flex-col gap-[3px]">
              <span className="font-display text-2xl font-extrabold text-white leading-none">0</span>
              <span className="text-xs text-white/35 font-medium">Paper needed</span>
            </div>
          </div>
        </div>

        <div className="flex-shrink-0">
          <div className="inline-flex items-center gap-2 font-mono text-xs text-green-500 font-medium">
            <span className="w-1.5 h-1.5 rounded-full bg-green-500 animate-[pulse_2s_ease-in-out_infinite]" />
            System live
          </div>
        </div>
      </div>
    </div>

    {/* ── Right — form ── */}
    <div className="flex items-center justify-center p-8 bg-white max-[860px]:p-5 max-[860px:items-start max-[860px]:pt-16">
      <div className="w-full max-w-[400px]">
        {/* Mobile logo */}
        <a href="/" className="font-display text-xl font-extrabold text-black tracking-[-0.03em] no-underline flex-shrink-0 hidden max-[860px]:block mb-10">
          Gate<span className="text-green-500">log</span>
        </a>

        <div className="mb-8">
          <h1 className="font-display text-2xl font-extrabold text-neutral-900 tracking-[-0.03em] mb-2">
            Welcome back
          </h1>
          <p className="text-sm text-neutral-400">
            Sign in to your Gatelog account
          </p>
        </div>

        <form
          className="flex flex-col gap-5"
          onSubmit={handleSubmit}
          noValidate
          aria-label="Sign in form"
        >
          {/* Error banner */}
          {error && (
            <div className="flex items-center gap-3 bg-red-50 border border-red-200 rounded-sm p-3 px-4 text-sm text-red-700 leading-[1.4]" role="alert" aria-live="assertive">
              <AlertIcon />
              {error}
            </div>
          )}

          {/* Email */}
          <div className="flex flex-col gap-2">
            <label htmlFor="email" className="text-sm font-semibold text-neutral-900">
              Email address
            </label>
            <div className="relative flex items-center">
              <span className="absolute left-3 text-neutral-400 flex items-center pointer-events-none z-10" aria-hidden="true">
                <MailIcon />
              </span>
              <input
                id="email"
                ref={emailRef}
                type="email"
                className="w-full py-[0.6875rem] pl-10 pr-4 border-[1.5px] border-neutral-200 rounded-sm text-base text-neutral-900 bg-white outline-none transition-[border-color,box-shadow] duration-150 placeholder:text-neutral-400 focus:border-green-500 focus:shadow-[0_0_0_3px_rgba(37,168,94,0.12)] disabled:bg-neutral-100 disabled:cursor-not-allowed"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  setError(null);
                }}
                placeholder="you@organization.com"
                autoComplete="email"
                required
                disabled={isLoading}
                aria-describedby={error ? "login-error" : undefined}
              />
            </div>
          </div>

          {/* Password */}
          <div className="flex flex-col gap-2">
            <label htmlFor="password" className="text-sm font-semibold text-neutral-900">
              Password
            </label>
            <div className="relative flex items-center">
              <span className="absolute left-3 text-neutral-400 flex items-center pointer-events-none z-10" aria-hidden="true">
                <LockIcon />
              </span>
              <input
                id="password"
                type={showPassword ? "text" : "password"}
                className="w-full py-[0.6875rem] pl-10 pr-11 border-[1.5px] border-neutral-200 rounded-sm text-base text-neutral-900 bg-white outline-none transition-[border-color,box-shadow] duration-150 placeholder:text-neutral-400 focus:border-green-500 focus:shadow-[0_0_0_3px_rgba(37,168,94,0.12)] disabled:bg-neutral-100 disabled:cursor-not-allowed"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  setError(null);
                }}
                placeholder="••••••••"
                autoComplete="current-password"
                required
                disabled={isLoading}
              />
              <button
                type="button"
                className="absolute right-3 text-neutral-400 flex items-center p-1 rounded transition-colors duration-150 hover:text-neutral-700"
                onClick={() => setShowPassword((v) => !v)}
                aria-label={showPassword ? "Hide password" : "Show password"}
                tabIndex={-1}
              >
                {showPassword ? <EyeOffIcon /> : <EyeIcon />}
              </button>
            </div>
          </div>

          {/* Submit */}
          <button
            type="submit"
            className="flex items-center justify-center gap-2 w-full py-3 bg-green-700 text-white text-base font-semibold rounded-sm transition-[background,transform,opacity] duration-200 ease-out hover:not-disabled:bg-green-500 hover:not-disabled:-translate-y-px disabled:opacity-55 disabled:cursor-not-allowed disabled:transform-none mt-2"
            disabled={isLoading || !email || !password}
            aria-busy={isLoading}
          >
            {isLoading ? (
              <>
                <Spinner />
                Signing in…
              </>
            ) : (
              <>
                Sign In
                <ArrowRight />
              </>
            )}
          </button>
        </form>

        <p className="mt-6 text-sm text-neutral-400 text-center">
          Need access?{" "}
          <a href="/#contact" className="text-green-700 font-medium transition-colors duration-150 hover:text-green-500">
            Request a demo
          </a>
        </p>
      </div>
    </div>
  </div>
);
}

/* ── Icons ── */
function MailIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <rect x="1.5" y="3.5" width="13" height="9" rx="1.5" stroke="currentColor" strokeWidth="1.25" />
      <path d="M1.5 5.5l6.5 4 6.5-4" stroke="currentColor" strokeWidth="1.25" strokeLinecap="round" />
    </svg>
  );
}

function LockIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <rect x="3" y="7" width="10" height="7" rx="1.5" stroke="currentColor" strokeWidth="1.25" />
      <path d="M5 7V5a3 3 0 016 0v2" stroke="currentColor" strokeWidth="1.25" strokeLinecap="round" />
      <circle cx="8" cy="10.5" r="1" fill="currentColor" />
    </svg>
  );
}

function EyeIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <path d="M1 8s2.5-5 7-5 7 5 7 5-2.5 5-7 5-7-5-7-5z" stroke="currentColor" strokeWidth="1.25" />
      <circle cx="8" cy="8" r="2" stroke="currentColor" strokeWidth="1.25" />
    </svg>
  );
}

function EyeOffIcon() {
  return (
    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
      <path d="M2 2l12 12M6.7 6.8A2 2 0 009.3 9.2M4.2 4.3C2.8 5.4 1.8 6.8 1 8c1.3 3.7 4.4 6 7 6a7.5 7.5 0 004.2-1.3M6.5 2.6A7.5 7.5 0 0115 8a8.7 8.7 0 01-1.4 2.8" stroke="currentColor" strokeWidth="1.25" strokeLinecap="round" />
    </svg>
  );
}

function ArrowRight() {
  return (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M2.5 7h9M8 3.5L11.5 7 8 10.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

function AlertIcon() {
  return (
    <svg width="15" height="15" viewBox="0 0 15 15" fill="none" className="flex-shrink-0">
      <circle cx="7.5" cy="7.5" r="6.5" stroke="currentColor" strokeWidth="1.25" />
      <path d="M7.5 4.5v4" stroke="currentColor" strokeWidth="1.25" strokeLinecap="round" />
      <circle cx="7.5" cy="10.5" r="0.75" fill="currentColor" />
    </svg>
  );
}

function Spinner() {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 16 16"
      fill="none"
      className="animate-[spin_0.7s_linear_infinite]"
      aria-hidden="true"
    >
      <circle cx="8" cy="8" r="6" stroke="rgba(255,255,255,0.3)" strokeWidth="2" />
      <path d="M8 2a6 6 0 016 6" stroke="white" strokeWidth="2" strokeLinecap="round" />
    </svg>
  );
}