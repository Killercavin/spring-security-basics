interface ApiError {
  response?: {
    status?: number;
    data?: {
      message?: string;
      fieldErrors?: Record<string, string>;
    };
  };
  message?: string;
}

export function extractErrorMessage(err: unknown): string {
  const error = err as ApiError;

  // no response at all — network failure, server down, CORS block
  if (!error.response) {
    return "Unable to reach the server. Check your connection and try again.";
  }

  const { status, data } = error.response;

  // use the backend's own message if it exists — your GlobalExceptionHandler
  // always returns a structured { message: string } body
  const backendMessage = data?.message;

  switch (status) {
    case 400:
      // validation failure — check for field-level errors first
      if (data?.fieldErrors) {
        const first = Object.values(data.fieldErrors)[0];
        return first ?? backendMessage ?? "Please check your input and try again.";
      }
      return backendMessage ?? "Invalid request. Please check your input.";

    case 401:
      // your InvalidCredentialsException maps to 401
      return backendMessage ?? "Invalid email or password.";

    case 403:
      // AccountDisabledException maps to 403
      return backendMessage ?? "Your account has been deactivated. Contact your administrator.";

    case 404:
      return backendMessage ?? "The requested resource was not found.";

    case 409:
      // ConflictException — duplicate email etc
      return backendMessage ?? "A conflict occurred. Please try again.";

    case 422:
      // InvalidStateException
      return backendMessage ?? "This action cannot be completed in the current state.";

    case 429:
      return "Too many attempts. Please wait a moment and try again.";

    case 500:
    case 502:
    case 503:
      return "The server encountered an error. Please try again in a moment.";

    default:
      return backendMessage ?? "Something went wrong. Please try again later.";
  }
}