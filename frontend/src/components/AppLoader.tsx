export function AppLoader() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-500" />
        <p className="mt-2 text-sm text-gray-500">Loading...</p>
      </div>
    </div>
  )
}