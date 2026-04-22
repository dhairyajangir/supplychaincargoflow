import { ensureCoordinateDatasetReady, ensureLocationCoordinate, getExactCoordinate } from './coordinateProvider'

ensureCoordinateDatasetReady()

const EARTH_RADIUS_KM = 6371

const averageSpeeds = {
  OCEAN: 40,
  AIR: 850,
  ROAD: 70,
  RAIL: 60
}

const fixedOverheadHours = {
  OCEAN: 48,
  AIR: 12,
  RAIL: 24
}

const contingencyMultipliers = {
  OCEAN: 1.15,
  RAIL: 1.15,
  AIR: 1.1,
  ROAD: 1.1
}

const toRadians = (degrees) => (degrees * Math.PI) / 180

const haversineDistance = (start, end) => {
  const [lat1, lon1] = start.map(toRadians)
  const [lat2, lon2] = end.map(toRadians)

  const dLat = lat2 - lat1
  const dLon = lon2 - lon1

  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) ** 2

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

  return EARTH_RADIUS_KM * c
}

const resolveCoordinates = (location) => ensureLocationCoordinate(location)

const inferOceanWaypoints = (origin, destination) => {
  const [olat, olon] = origin
  const [dlat, dlon] = destination
  const waypoints = []

  if (olon > 100 && dlon < -40) {
    const panama = getExactCoordinate('Panama Canal')
    if (panama) {
      waypoints.push(panama)
    }
  }

  if (olon >= -15 && olon <= 30 && dlon >= 40 && dlon <= 120) {
    const suez = getExactCoordinate('Suez Canal')
    if (suez) {
      waypoints.push(suez)
    }
  }

  if ((olat < 0 || dlat < 0) && Math.abs(olon - dlon) > 60) {
    const cape = getExactCoordinate('Cape of Good Hope')
    if (cape && !waypoints.includes(cape)) {
      waypoints.push(cape)
    }
  }

  return waypoints
}

const determineTotalDistance = (params, mode) => {
  const distanceProvided = typeof params.distance_km === 'number' && params.distance_km > 0

  if (distanceProvided) {
    return {
      totalDistance: params.distance_km,
      path: null,
      distanceSource: 'provided'
    }
  }

  const originCoords = resolveCoordinates(params.origin)
  const destinationCoords = resolveCoordinates(params.destination)

  let totalDistance = haversineDistance(originCoords, destinationCoords)
  let path = [originCoords, destinationCoords]

  if (mode === 'OCEAN') {
    const inferred = inferOceanWaypoints(originCoords, destinationCoords)
    if (inferred.length > 0) {
      totalDistance = 0
      path = [originCoords, ...inferred, destinationCoords]
      for (let i = 0; i < path.length - 1; i += 1) {
        totalDistance += haversineDistance(path[i], path[i + 1])
      }
    }
  }

  return {
    totalDistance,
    path,
    distanceSource: 'calculated'
  }
}

const computeOverhead = (mode, travelHours) => {
  if (mode === 'ROAD') {
    return Math.ceil(travelHours / 24) * 4
  }

  return fixedOverheadHours[mode] ?? 0
}

const calculateDurationDetails = (params = {}) => {
  if (typeof params !== 'object' || params === null) {
    throw new Error('Input payload must be an object.')
  }

  const mode = (params.transportationMode || '').toUpperCase()
  if (!averageSpeeds[mode]) {
    throw new Error(`Unsupported transportation mode: ${params.transportationMode}`)
  }

  if (!(typeof params.distance_km === 'number' && params.distance_km > 0)) {
    if (!params.origin || !params.destination) {
      throw new Error('Origin and destination are required when distance_km is not provided.')
    }
  }

  const { totalDistance, distanceSource, path } = determineTotalDistance(params, mode)

  const travelHours = totalDistance / averageSpeeds[mode]
  const logisticalOverhead = computeOverhead(mode, travelHours)
  const contingencyMultiplier = contingencyMultipliers[mode] ?? 1

  const bufferedHours = (travelHours + logisticalOverhead) * contingencyMultiplier
  const durationDays = Math.ceil(bufferedHours / 24)

  return {
    durationDays,
    totalDistance,
    travelHours,
    logisticalOverhead,
    bufferedHours,
    contingencyMultiplier,
    distanceSource,
    path
  }
}

const parseStartDate = (value) => {
  if (!value) {
    return new Date()
  }

  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) {
    throw new Error(`Invalid startDate supplied: ${value}`)
  }

  return parsed
}

const normaliseToUTCDate = (date) => new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()))

const addDaysUTC = (date, days) => {
  const result = normaliseToUTCDate(date)
  result.setUTCDate(result.getUTCDate() + days)
  return result
}

const formatISODate = (date) => date.toISOString().slice(0, 10)

export const estimateRouteDuration = (params) => {
  const { durationDays } = calculateDurationDetails(params)
  return { estimatedDurationDays: durationDays }
}

export const estimateShipmentDelivery = (params) => {
  const { durationDays } = calculateDurationDetails(params)
  const startDate = parseStartDate(params?.startDate)
  const deliveryDate = addDaysUTC(startDate, durationDays)
  return { estimatedDeliveryDate: formatISODate(deliveryDate) }
}

export { calculateDurationDetails }

export default {
  estimateRouteDuration,
  estimateShipmentDelivery,
  calculateDurationDetails
}
