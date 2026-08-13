import { useState } from 'react'
import { useSites } from '../../features/admin/useSites'
import type { Site, SiteRequest } from '../../features/admin/adminApi'
import {
  Building2,
  Plus,
  Pencil,
  Trash2,
  X,
  Loader2,
  AlertTriangle,
  RefreshCw,
} from 'lucide-react'
import { extractErrorMessage } from '../../utils/errors'

type ModalMode = 'create' | 'edit' | 'delete' | null

interface ModalState {
  mode: ModalMode
  site: Site | null
}

export default function SitesPage() {
  const { sites, isLoading, error, createSite, updateSite, deactivateSite, refresh } = useSites()
  const [modal, setModal] = useState<ModalState>({ mode: null, site: null })

  const openCreate = () => setModal({ mode: 'create', site: null })
  const openEdit   = (site: Site) => setModal({ mode: 'edit', site })
  const openDelete = (site: Site) => setModal({ mode: 'delete', site })
  const closeModal = () => setModal({ mode: null, site: null })

  if (isLoading) return <SitesSkeleton />

  if (error) return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] text-center">
      <AlertTriangle size={20} className="text-red-400 mb-3" />
      <p className="text-sm text-gray-500 mb-4">{error}</p>
      <button
        onClick={refresh}
        className="flex items-center gap-2 px-4 py-2 rounded-lg bg-green-700 text-white text-sm font-semibold hover:bg-green-500 transition-colors"
      >
        <RefreshCw size={13} /> Try again
      </button>
    </div>
  )

  return (
    <div className="max-w-5xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <p className="text-xs font-semibold text-green-600 uppercase tracking-wider mb-1">
            Super Admin
          </p>
          <h1 className="font-display text-2xl font-extrabold text-neutral-900 tracking-tight">
            Sites
          </h1>
          <p className="text-sm text-gray-400 mt-0.5">
            {sites.length} site{sites.length !== 1 ? 's' : ''} on the platform
          </p>
        </div>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 px-4 py-2 rounded-lg bg-green-700 text-white text-sm font-semibold hover:bg-green-500 transition-colors duration-150"
        >
          <Plus size={15} />
          New Site
        </button>
      </div>

      {/* Site list */}
      {sites.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-20 text-center bg-white rounded-xl border border-gray-100">
          <div className="w-12 h-12 rounded-full bg-gray-50 flex items-center justify-center mb-4">
            <Building2 size={20} className="text-gray-300" />
          </div>
          <p className="text-sm font-semibold text-gray-500 mb-1">No sites yet</p>
          <p className="text-xs text-gray-400 mb-5">Create your first site to get started.</p>
          <button
            onClick={openCreate}
            className="flex items-center gap-2 px-4 py-2 rounded-lg bg-green-700 text-white text-sm font-semibold hover:bg-green-500 transition-colors"
          >
            <Plus size={14} /> Create Site
          </button>
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
          <ul role="list" className="divide-y divide-gray-50">
            {sites.map((site) => (
              <li
                key={site.id}
                className="flex items-center gap-4 px-5 py-4 hover:bg-gray-50 transition-colors duration-100"
              >
                <div className="w-9 h-9 rounded-lg bg-green-50 flex-shrink-0 flex items-center justify-center">
                  <Building2 size={16} className="text-green-600" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-semibold text-neutral-900">{site.name}</p>
                  <p className="text-xs text-gray-400">
                    {site.location ?? 'No address set'}
                  </p>
                </div>
                <div className="flex items-center gap-2 flex-shrink-0">
                  <button
                    onClick={() => openEdit(site)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold border border-gray-200 text-gray-600 hover:border-green-200 hover:bg-green-50 hover:text-green-700 transition-all duration-150"
                  >
                    <Pencil size={11} /> Edit
                  </button>
                  <button
                    onClick={() => openDelete(site)}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold border border-gray-200 text-gray-600 hover:border-red-200 hover:bg-red-50 hover:text-red-600 transition-all duration-150"
                  >
                    <Trash2 size={11} /> Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Modals */}
      {modal.mode === 'create' && (
        <SiteFormModal
          mode="create"
          onSubmit={async (data) => { await createSite(data); closeModal() }}
          onClose={closeModal}
        />
      )}
      {modal.mode === 'edit' && modal.site && (
        <SiteFormModal
          mode="edit"
          site={modal.site}
          onSubmit={async (data) => { await updateSite(modal.site!.id, data); closeModal() }}
          onClose={closeModal}
        />
      )}
      {modal.mode === 'delete' && modal.site && (
        <DeleteModal
          siteName={modal.site.name}
          onConfirm={async () => { await deactivateSite(modal.site!.id); closeModal() }}
          onClose={closeModal}
        />
      )}
    </div>
  )
}

/* Site form modal */
function SiteFormModal({
  mode,
  site,
  onSubmit,
  onClose,
}: {
  mode: 'create' | 'edit'
  site?: Site
  onSubmit: (data: SiteRequest | SiteRequest) => Promise<void>
  onClose: () => void
}) {
  const [name, setName]       = useState(site?.name ?? '')
  const [location, setLocation] = useState(site?.location ?? '')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError]     = useState<string | null>(null)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!name.trim()) return
    if (!location.trim()) return
    setError(null)
    setSubmitting(true)
    try {
      await onSubmit({ name: name.trim(), location: location.trim() })
    } catch (err) {
      setError(extractErrorMessage(err))
      setSubmitting(false)
    }
  }

  return (
    <Modal onClose={onClose} title={mode === 'create' ? 'New Site' : 'Edit Site'}>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        {error && (
          <div className="flex items-center gap-2 text-sm text-red-700 bg-red-50 border border-red-200 rounded-lg px-3 py-2.5">
            <AlertTriangle size={14} className="flex-shrink-0" />
            {error}
          </div>
        )}
        <FormField label="Site Name" required>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Nairobi HQ"
            required
            className={inputCls}
            disabled={submitting}
          />
        </FormField>
        <FormField label="Address" required>
          <input
            type="text"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            placeholder="e.g. Westlands, Nairobi"
            required
            className={inputCls}
            disabled={submitting}
          />
        </FormField>
        <div className="flex justify-end gap-2 pt-2">
          <button type="button" onClick={onClose} className={ghostBtn} disabled={submitting}>
            Cancel
          </button>
          <button type="submit" className={primaryBtn} disabled={submitting || !name.trim()}>
            {submitting ? <Loader2 size={14} className="animate-spin" /> : null}
            {mode === 'create' ? 'Create Site' : 'Save Changes'}
          </button>
        </div>
      </form>
    </Modal>
  )
}

/* Delete confirmation modal */
function DeleteModal({
  siteName,
  onConfirm,
  onClose,
}: {
  siteName: string
  onConfirm: () => Promise<void>
  onClose: () => void
}) {
  const [submitting, setSubmitting] = useState(false)

  async function handleConfirm() {
    setSubmitting(true)
    try { await onConfirm() } finally { setSubmitting(false) }
  }

  return (
    <Modal onClose={onClose} title="Remove Site">
      <p className="text-sm text-gray-600 mb-6">
        Are you sure you want to remove{' '}
        <span className="font-semibold text-neutral-900">{siteName}</span>?
        This action cannot be undone.
      </p>
      <div className="flex justify-end gap-2">
        <button onClick={onClose} className={ghostBtn} disabled={submitting}>
          Cancel
        </button>
        <button
          onClick={handleConfirm}
          disabled={submitting}
          className="flex items-center gap-2 px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-semibold hover:bg-red-500 transition-colors disabled:opacity-50"
        >
          {submitting ? <Loader2 size={13} className="animate-spin" /> : null}
          Remove Site
        </button>
      </div>
    </Modal>
  )
}

/* Shared modal wrapper */
function Modal({
  title,
  onClose,
  children,
}: {
  title: string
  onClose: () => void
  children: React.ReactNode
}) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onClose}
        aria-hidden="true"
      />
      <div className="relative bg-white rounded-xl shadow-xl w-full max-w-md p-6 z-10">
        <div className="flex items-center justify-between mb-5">
          <h2 className="font-display font-bold text-base text-neutral-900">{title}</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 p-1 rounded transition-colors"
          >
            <X size={16} />
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}

/* Form field wrapper */
function FormField({
  label,
  required,
  children,
}: {
  label: string
  required?: boolean
  children: React.ReactNode
}) {
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-sm font-semibold text-neutral-900">
        {label}
        {required && <span className="text-red-500 ml-0.5">*</span>}
      </label>
      {children}
    </div>
  )
}

/* Skeleton */
function SitesSkeleton() {
  return (
    <div className="max-w-5xl mx-auto animate-pulse">
      <div className="h-8 w-32 bg-gray-100 rounded mb-2" />
      <div className="h-3 w-48 bg-gray-100 rounded mb-8" />
      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="h-16 border-b border-gray-50 bg-gray-50 last:border-0" />
        ))}
      </div>
    </div>
  )
}

/*Shared styles */
const inputCls = `
  w-full px-3 py-2.5 border border-gray-200 rounded-lg text-sm text-neutral-900
  outline-none transition-[border-color,box-shadow] duration-150 bg-white
  placeholder:text-gray-400
  focus:border-green-500 focus:shadow-[0_0_0_3px_rgba(37,168,94,0.12)]
  disabled:bg-gray-50 disabled:cursor-not-allowed
`.trim()

const primaryBtn = `
  flex items-center gap-2 px-4 py-2 rounded-lg bg-green-700 text-white
  text-sm font-semibold hover:bg-green-500 transition-colors duration-150
  disabled:opacity-50 disabled:cursor-not-allowed
`.trim()

const ghostBtn = `
  px-4 py-2 rounded-lg border border-gray-200 text-sm font-medium
  text-gray-600 hover:border-gray-300 hover:text-gray-900
  transition-colors duration-150 disabled:opacity-50
`.trim()
