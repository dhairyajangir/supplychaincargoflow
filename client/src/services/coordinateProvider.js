import countriesStatesCities from '@/data/json/countries+states+cities.json'
import manualCoordinates from '@/data/manualCoordinates'

const PRIORITY_JSON = 1
const PRIORITY_MANUAL = 2

const stripDiacritics = (value) => {
  if (typeof value !== 'string') return ''
  return value
    .normalize('NFKD')
    .replace(/[\u0300-\u036f]/g, '')
}

const toLowerTrimmed = (value) => stripDiacritics(value).toLowerCase().trim()

const normalizeKey = (value) => toLowerTrimmed(value)
const normalizeLoose = (value) => toLowerTrimmed(value).replace(/[^a-z0-9]+/g, ' ').replace(/\s+/g, ' ').trim()
const simplifyKey = (value) => toLowerTrimmed(value).replace(/[^a-z0-9]+/g, '')

const toCoords = (lat, lon) => {
  const latNum = typeof lat === 'number' ? lat : Number.parseFloat(lat)
  const lonNum = typeof lon === 'number' ? lon : Number.parseFloat(lon)
  if (!Number.isFinite(latNum) || !Number.isFinite(lonNum)) {
    return null
  }
  return [latNum, lonNum]
}

const implicitAliases = (name) => {
  const aliases = new Set()
  if (typeof name !== 'string') return []

  const parenthetical = name.replace(/\(([^)]+)\)/g, (_, group) => {
    const trimmed = group.trim()
    if (trimmed.length > 2) {
      aliases.add(trimmed)
    }
    return ' '
  })

  parenthetical.split(',').forEach((part) => {
    const trimmed = part.trim()
    if (trimmed.length > 2 && trimmed !== name) {
      aliases.add(trimmed)
    }
  })

  name.split(/[\/-|]/).forEach((part) => {
    const trimmed = part.trim()
    if (trimmed.length > 2 && trimmed !== name) {
      aliases.add(trimmed)
    }
  })

  const portMatch = name.match(/port of\s+(.+)/i)
  if (portMatch && portMatch[1]) {
    const trimmed = portMatch[1].trim()
    if (trimmed.length > 2) {
      aliases.add(trimmed)
    }
  }

  const noPortSuffix = name.replace(/\bport\b/gi, '').trim()
  if (noPortSuffix.length > 2 && noPortSuffix !== name) {
    aliases.add(noPortSuffix)
  }

  return Array.from(aliases)
}

const COMMON_PREFIXES = [/^port of\s+/i, /^city of\s+/i]
const COMMON_SUFFIXES = [/\sport$/i, /\scity$/i, /\sairport$/i, /\sterminal$/i]

const generateCandidateQueries = (rawLocation) => {
  const variants = new Set()
  if (!rawLocation || typeof rawLocation !== 'string') return []
  const trimmed = rawLocation.trim()
  if (!trimmed) return []

  variants.add(trimmed)

  const withoutParens = trimmed.replace(/\(([^)]+)\)/g, ' $1 ').replace(/\s+/g, ' ').trim()
  if (withoutParens) variants.add(withoutParens)

  COMMON_PREFIXES.forEach((regex) => {
    if (regex.test(trimmed)) {
      variants.add(trimmed.replace(regex, '').trim())
    }
  })

  COMMON_SUFFIXES.forEach((regex) => {
    if (regex.test(trimmed)) {
      variants.add(trimmed.replace(regex, '').trim())
    }
  })

  trimmed.split(',').forEach((segment) => {
    const part = segment.trim()
    if (part) variants.add(part)
  })

  return Array.from(variants).filter(Boolean)
}

const createCoordinateIndex = () => {
  const coordinateIndex = new Map()
  const entryByName = new Map()
  const suggestionEntries = new Map()
  const searchEntries = []
  let suggestionNames = []
  let isBuilt = false

  const attachKey = (key, entry) => {
    if (!key) return
    const existing = coordinateIndex.get(key)
    if (!existing || entry.priority >= existing.priority) {
      coordinateIndex.set(key, entry)
    }
  }

  const ensureEntry = (name, coords, priority, includeInSuggestions = true) => {
    const current = entryByName.get(name)
    if (current) {
      if (priority >= current.priority) {
        current.coords = coords
        current.priority = priority
        current.lower = toLowerTrimmed(name)
        current.simplified = simplifyKey(name)
      }
      if (includeInSuggestions && !suggestionEntries.has(name)) {
        suggestionEntries.set(name, current)
      }
      return current
    }

    const entry = {
      name,
      coords,
      priority,
      lower: toLowerTrimmed(name),
      simplified: simplifyKey(name)
    }
    entryByName.set(name, entry)
    searchEntries.push(entry)
    if (includeInSuggestions) {
      suggestionEntries.set(name, entry)
    }
    return entry
  }

  const linkNameToEntry = (name, entry, includeInSuggestions = false) => {
    const normalized = normalizeKey(name)
    if (normalized) attachKey(normalized, entry)
    const loose = normalizeLoose(name)
    if (loose) attachKey(loose, entry)
    const simplified = simplifyKey(name)
    if (simplified) attachKey(simplified, entry)
    if (includeInSuggestions && !suggestionEntries.has(name)) {
      suggestionEntries.set(name, entry)
    }
  }

  const registerCoordinate = (name, lat, lon, priority = PRIORITY_JSON, options = {}) => {
    if (!name) return
    const coords = toCoords(lat, lon)
    if (!coords) return

    const entry = ensureEntry(name, coords, priority, options.includeInSuggestions ?? true)
    linkNameToEntry(name, entry, true)

    const aliasList = new Set([...(options.aliases ?? []), ...implicitAliases(name)])
    aliasList.forEach((alias) => {
      if (!alias || alias === name) return
      const trimmed = alias.trim()
      if (trimmed.length < 2) return
      linkNameToEntry(trimmed, entry, options.exposeAliasesInSuggestions === true)
    })
  }

  const buildFromCountriesDataset = () => {
    countriesStatesCities.forEach((country) => {
      registerCoordinate(country.name, country.latitude, country.longitude, PRIORITY_JSON, {
        aliases: [country.iso2, country.iso3, country.capital].filter(Boolean)
      })

      if (Array.isArray(country.states)) {
        country.states.forEach((state) => {
          registerCoordinate(state.name, state.latitude, state.longitude, PRIORITY_JSON, {
            aliases: [`${state.name}, ${country.name}`]
          })

          if (Array.isArray(state.cities)) {
            state.cities.forEach((city) => {
              registerCoordinate(city.name, city.latitude, city.longitude, PRIORITY_JSON, {
                aliases: [
                  `${city.name}, ${state.name}`,
                  `${city.name}, ${country.name}`,
                  `${city.name}, ${state.name}, ${country.name}`
                ]
              })
            })
          }
        })
      }
    })
  }

  const buildFromManualOverrides = () => {
    Object.entries(manualCoordinates).forEach(([name, coords]) => {
      if (!Array.isArray(coords) || coords.length < 2) return
      registerCoordinate(name, coords[0], coords[1], PRIORITY_MANUAL, {
        includeInSuggestions: true,
        exposeAliasesInSuggestions: true
      })
    })
  }

  const ensureReady = () => {
    if (isBuilt) return
    buildFromCountriesDataset()
    buildFromManualOverrides()
    suggestionNames = Array.from(suggestionEntries.keys()).sort((a, b) => a.localeCompare(b))
    isBuilt = true
  }

  const lookupDirect = (location) => {
    ensureReady()
    const normalized = normalizeKey(location)
    if (normalized && coordinateIndex.has(normalized)) {
      return coordinateIndex.get(normalized)
    }
    const loose = normalizeLoose(location)
    if (loose && coordinateIndex.has(loose)) {
      return coordinateIndex.get(loose)
    }
    const simple = simplifyKey(location)
    if (simple && coordinateIndex.has(simple)) {
      return coordinateIndex.get(simple)
    }
    return null
  }

  const fallbackMatch = (location) => {
    ensureReady()
    const lower = toLowerTrimmed(location)
    const simplified = simplifyKey(location)

    for (const entry of searchEntries) {
      if (entry.lower === lower || entry.simplified === simplified) {
        return entry
      }
    }

    for (const entry of searchEntries) {
      if (entry.lower.includes(lower) || lower.includes(entry.lower)) {
        return entry
      }
      if (entry.simplified.includes(simplified) || simplified.includes(entry.simplified)) {
        return entry
      }
    }

    return null
  }

  const resolve = (location) => {
    if (!location) return null
    const candidates = generateCandidateQueries(location)
    for (const candidate of candidates) {
      const direct = lookupDirect(candidate)
      if (direct) {
        return direct.coords
      }
    }

    for (const candidate of candidates) {
      const fallback = fallbackMatch(candidate)
      if (fallback) {
        return fallback.coords
      }
    }

    return null
  }

  const ensureCoordinate = (location) => {
    const coords = resolve(location)
    if (!coords) {
      throw new Error(`Coordinates not found for location: ${location}`)
    }
    return coords
  }

  const getExact = (location) => {
    if (!location) return null
    const direct = lookupDirect(location)
    return direct ? direct.coords : null
  }

  const listNames = () => {
    ensureReady()
    return suggestionNames
  }

  return {
    ensureReady,
    resolve,
    ensure: ensureCoordinate,
    getExact,
    listNames
  }
}

const coordinateStore = createCoordinateIndex()
coordinateStore.ensureReady()

export const ensureCoordinateDatasetReady = () => {
  coordinateStore.ensureReady()
}

export const getAllLocationNames = () => coordinateStore.listNames()

export const resolveLocationCoordinate = (location) => coordinateStore.resolve(location)

export const ensureLocationCoordinate = (location) => coordinateStore.ensure(location)

export const getExactCoordinate = (location) => coordinateStore.getExact(location)

export default {
  ensureCoordinateDatasetReady,
  getAllLocationNames,
  resolveLocationCoordinate,
  ensureLocationCoordinate,
  getExactCoordinate
}
